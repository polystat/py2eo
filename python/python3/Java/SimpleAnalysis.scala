import scala.collection.immutable.{HashMap, HashSet}

object SimpleAnalysis {

  import Expression._

  def childrenE(e : T) : List[T] = e match {
    case Binop(op, l, r) =>List(l, r)
    case SimpleComparison(op, l, r) => List(l, r)
    case LazyLAnd(l, r) => List(l, r)
    case LazyLOr(l, r) => List(l, r)
    case FreakingComparison(ops, l) => l
    case Unop(op, x) => List(x)
    case Star(e) => List(e)
    case CollectionCons(wasList, l) => l
    case Slice(from, to, by) => from.toList ++ to.toList ++ by.toList
    case CallIndex(isCall, whom, args) => whom :: args.map(_._2)
    case Field(whose, name) => List(whose)
    case Cond(cond, yes, no) => List(cond, yes, no)
    case _ => List()
  }

  def foldEE[A](f0 : (A, T) => A)(acc0 : A, e : T) : A = {
    val f = foldEE(f0)(_, _)
    val acc = f0(acc0, e)
    childrenE(e).foldLeft(acc)(f)
  }

  def childrenS(s : Statement) : (List[Statement], List[T]) = s match {
    case Del(Ident(name)) => (List(), List(Ident(name)))
    case If(conditioned, eelse) => (eelse :: conditioned.map(_._2), conditioned.map(_._1))
    case IfSimple(cond, yes, no) => (List(yes, no), List(cond))
    case Try(ttry, excepts, eelse, ffinally) => ((ttry :: excepts.map(_._2)) :+ eelse :+ ffinally, excepts.flatMap(p => p._1.map(_._1).toList))
    case While(cond, body, eelse) => (List(body, eelse), List(cond))
    case Suite(l) => (l, List())
    case AugAssign(op, lhs, rhs) => (List(), List(lhs, rhs))
    case Assign(l) => (List(), l)
    case Return(x) => (List(), List(x))
    case Assert(x) => (List(), List(x))
    case Raise(e, None) => (List(), e.toList)
    case ClassDef(name, bases, body, decorators) => (List(body), bases ++ decorators.l)
    case FuncDef(name, args, _, _, body, decorators) => (List(body), decorators.l)
    case NonLocal(_) | WithoutArgs(_) | Global(_) | ImportModule(_, _) | ImportSymbol(_, _, _) | ImportAllSymbols(_) => (List(), List())
  }

  def foldSE[A](f : (A, T) => A, mayVisit : Statement => Boolean)(acc0 : A, s : Statement) : A = {
    if (mayVisit(s)) {
      val (ls, le) = childrenS(s)
      val acc = le.foldLeft(acc0)(foldEE(f))
      ls.foldLeft(acc)(foldSE(f, mayVisit))
    } else acc0
  }

  // almost a typical fold, but f may disallow it to visit children of certain nodes
  def foldSS[A](f : (A, Statement) => (A, Boolean))(acc0 : A, s : Statement) : A = {
    val (acc, procChildren) = f(acc0, s)
    if (!procChildren) acc else {
      val (ls, _) = childrenS(s)
      ls.foldLeft(acc)(foldSS(f))
    }
  }

  def classifyFunctionVariables(args : List[(String, ArgKind.T, Option[T])], body : Statement, visitChildren : Boolean)
  : HashMap[String, VarScope.T] = {
    def dontVisitOtherBlocks(s : Statement) : Boolean = s match {
      case FuncDef(_, _, _, _, _, _) | ClassDef(_, _, _, _) => visitChildren
      case _ => true
    }
    type H = HashMap[String, VarScope.T]
    val h1 = args.foldLeft(HashMap[String, VarScope.T]())((h, name) => h.+((name._1, VarScope.Local)))
    val h2 = foldSS[H]((h, st) => st match {
      case (NonLocal(l)) => (l.foldLeft(h)((h, name) => h.+((name, VarScope.NonLocal))), false)
      case (Global(l))  => (l.foldLeft(h)((h, name) => h.+((name, VarScope.Global))), false)
      case _ => (h, dontVisitOtherBlocks(st))
    })(h1, body)

    val h3 = foldSS[H]((h, st) => {
      def add(name : String) : H = if (h.contains(name)) h else h.+((name, VarScope.Local))
      st match {
        case ClassDef(name, bases, body, _) => (add(name), false)
        case FuncDef(name, args, body, _, _, _) => (add(name), false)
        case Assign(List(CollectionCons(_, _), _)) => throw new Throwable("run this analysis after all assignment simplification passes!")
        case Assign(l) if l.size > 2 => throw new Throwable("run this analysis after all assignment simplification passes!")
        case Assign(List(Ident(name), _)) => (add(name), true)
        case _ => (h, true)
      }
    })(h2, body)

    foldSE[H]((h, e) => e match {
      case Ident(name) => if (h.contains(name)) h else {
//        if (!BuiltinFunctions.fs.contains(name))  // todo: this should be the builtin module's global namespace
//          h.+((name, VarScope.Global))
//        else
        h
      }
      case _ => h
    }, dontVisitOtherBlocks)(h3, body)

  }

  private def assertStatementIsSimplified(acc : Unit, s : Statement) : (Unit, Boolean) = s match {
    case Del(Ident(_)) | IfSimple(_, _, _) | Try(_, List((None, _)), _, _) | While(_, _, _) |
       Suite(_) | AugAssign(_, _, _) | Assign(List(_)) | Assign(List(Ident(_), _)) | Return(_) |
       Raise(_, None) | ClassDef(_, _, _, Decorators(List())) | FuncDef(_, _, _, _, _, Decorators(List())) |
       NonLocal(_) | WithoutArgs(_) | Global(_) | ImportModule(_, _) | ImportSymbol(_, _, _) | ImportAllSymbols(_) => (acc, true)
  }

  private def assertExpressionIsSimplified(acc : Unit, e : T) : Unit = e match {
    case FreakingComparison(List(_), List(_, _)) => ()
    case Star(_) | DoubleStar(_) | CollectionComprehension(_, _, _) | DictComprehension(_, _)
         | CallIndex(false, _, _) | FreakingComparison(_, _) | AnonFun(_, _) =>
      throw new Throwable("these must never happen after all passes: " + PrintPython.printExpr(e))
    case _ => ()
  }

  def checkIsSimplified(s : Statement) : Unit = {
    foldSS(assertStatementIsSimplified)((), s)
    foldSE(assertExpressionIsSimplified, _ => true)((), s)
  }

}
