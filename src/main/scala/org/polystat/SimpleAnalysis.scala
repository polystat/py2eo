import scala.collection.immutable.HashMap

object SimpleAnalysis {

  import Expression._

  def childrenComprehension(c : Comprehension) = c match {
    case IfComprehension(cond) => List(cond)
    case ForComprehension(what, in) => List(what, in)
  }

  def childrenDictEltDoubleStar(x : DictEltDoubleStar) = x match {
    case Right(value) => List(value)
    case Left((a, b)) => List(a, b)
  }

  def childrenE(e : T) : List[T] = e match {
    case Binop(op, l, r) =>List(l, r)
    case SimpleComparison(op, l, r) => List(l, r)
    case LazyLAnd(l, r) => List(l, r)
    case LazyLOr(l, r) => List(l, r)
    case FreakingComparison(ops, l) => l
    case Unop(op, x) => List(x)
    case Star(e) => List(e)
    case DoubleStar(e) => List(e)
    case CollectionCons(wasList, l) => l
    case DictCons(l) => l.flatMap(childrenDictEltDoubleStar)
    case Slice(from, to, by) => from.toList ++ to.toList ++ by.toList
    case CallIndex(isCall, whom, args) => whom :: args.map(_._2)
    case Field(whose, name) => List(whose)
    case Cond(cond, yes, no) => List(cond, yes, no)
    case x : UnsupportedExpr => x.children
    case IntLiteral(_) | FloatLiteral(_) | StringLiteral(_) | BoolLiteral(_) | NoneLiteral() | ImagLiteral(_) |
         Ident(_) => List()
    case CollectionComprehension(kind, base, l) => base :: l.flatMap(childrenComprehension)
    case DictComprehension(base, l) =>
      childrenDictEltDoubleStar(base) ++ l.flatMap(childrenComprehension)
  }

  def foldEE[A](f0 : (A, T) => A)(acc0 : A, e : T) : A = {
    val f = foldEE(f0)(_, _)
    val acc = f0(acc0, e)
    childrenE(e).foldLeft(acc)(f)
  }

  def childrenS(s : Statement) : (List[Statement], List[(Boolean, T)]) = {
    def isRhs(e : T) = (false, e)
    s match {
      case Yield(l) => (List(), l.map(x => (false, x)).toList)
      case With(cm, target, body) => (List(body), (false, cm) :: target.map(x => (true, x)).toList)
      case For(what, in, body, eelse) => (List(body, eelse), List((false, what), (false, in)))
      case Del(e) => (List(), List((false, e)))
      case If(conditioned, eelse) => (eelse :: conditioned.map(_._2), conditioned.map(x => (false, x._1)))
      case IfSimple(cond, yes, no) => (List(yes, no), List((false, cond)))
      case Try(ttry, excepts, eelse, ffinally) =>
        ((ttry :: excepts.map(_._2)) :+ eelse :+ ffinally, excepts.flatMap(p => p._1.map(x => (false, x._1)).toList))
      case While(cond, body, eelse) => (List(body, eelse), List(isRhs(cond)))
      case Suite(l) => (l, List())
      case AugAssign(op, lhs, rhs) => (List(), List((true, lhs), (false, rhs)))
      case Assign(l) => (List(), (false, l.head) :: l.tail.map(isRhs))
      case CreateConst(name, value) => (List(), List(isRhs(value)))
      case Return(x) => (List(), List(isRhs(x)))
      case Assert(x) => (List(), List(isRhs(x)))
      case Raise(e, None) => (List(), e.toList.map(isRhs))
      case ClassDef(name, bases, body, decorators) => (List(body), (bases ++ decorators.l).map(isRhs))
      case FuncDef(name, args, _, _, body, decorators, _) => (List(body), decorators.l.map(isRhs))
      case NonLocal(_) | WithoutArgs(_) | Global(_) | ImportModule(_, _) | ImportSymbol(_, _, _) | ImportAllSymbols(_) => (List(), List())
      case x : Unsupported => (x.sts, x.es)
    }
  }

  def foldSE[A](f : (A, T) => A, mayVisit : Statement => Boolean)(acc0 : A, s : Statement) : A = {
    if (mayVisit(s)) {
      val (ls, le) = childrenS(s)
      val acc = le.map(_._2).foldLeft(acc0)(foldEE(f))
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

  private def classifyVariablesAssignedInFunctionBody(args : List[(String, ArgKind.T, Option[T])], body : Statement)
  : HashMap[String, VarScope.T] = {
    def dontVisitOtherBlocks(s : Statement) : Boolean = s match {
      case FuncDef(_, _, _, _, _, _, _) | ClassDef(_, _, _, _) => false
      case _ => true
    }
    type H = HashMap[String, VarScope.T]
    val h2 = foldSS[H]((h, st) => st match {
      case (NonLocal(l)) => (l.foldLeft(h)((h, name) => h.+((name, VarScope.NonLocal))), false)
      case (Global(l))  => (l.foldLeft(h)((h, name) => h.+((name, VarScope.Global))), false)
      case _ => (h, dontVisitOtherBlocks(st))
    })(HashMap[String, VarScope.T](), body)

    val h3 = foldSS[H]((h, st) => {
      def add0(h : H, name : String) : H = if (h.contains(name)) h else h.+((name, VarScope.Local))
      def add(name : String) = add0(h, name)
      st match {
        case ClassDef(name, bases, body, _) => (add(name), false)
        case FuncDef(name, args, body, _, _, _, _) => (add(name), false)
        case Assign(List(CollectionCons(_, _), _)) => throw new Throwable("run this analysis after all assignment simplification passes!")
        case Assign(l) if l.size > 2 => throw new Throwable("run this analysis after all assignment simplification passes!")
        case Assign(List(Ident(name), _)) => (add(name), true)
        case u : Unsupported => (u.declareVars.foldLeft(h)(add0), true)
        case CreateConst(name, _) => (add(name), true)
        case _ => (h, true)
      }
    })(h2, body)

    args.foldLeft(h3)((h, name) => h.+((name._1, VarScope.Arg)))

//    foldSE[H]((h, e) => e match {
//      case Ident(name) => if (h.contains(name)) h else {
////        if (!BuiltinFunctions.fs.contains(name))  // todo: this should be the builtin module's global namespace
//          h.+((name, VarScope.Global))
////        else
////        h
//      }
//      case _ => h
//    }, dontVisitOtherBlocks)(h3, body)
//
  }

  private def computeAccessibleIdentsF(upperVars : HashMap[String, VarScope.T], f : FuncDef) : FuncDef = {
    val v = classifyVariablesAssignedInFunctionBody(f.args, f.body)
    val vUpper = upperVars.map(x => if (x._2 == VarScope.Local || x._2 == VarScope.Arg) (x._1, VarScope.ImplicitNonLocal) else x)
    val merged = v.foldLeft(vUpper)((acc, z) => acc.+(z))
    val (body, _) = SimplePass.procStatementGeneral((s, ns) => s match {
      case f : FuncDef => (computeAccessibleIdentsF(merged, f), ns, false)
      case _ => (s, ns, true)
    })(f.body, new SimplePass.Names())
    FuncDef(f.name, f.args, f.otherPositional, f.otherKeyword, body, f.decorators, merged)
  }

  def computeAccessibleIdents(s : Statement) : Statement = {
    SimplePass.procStatementGeneral((s, ns) => s match {
      case f : FuncDef => (computeAccessibleIdentsF(HashMap(), f), ns, false)
      case _ => (s, ns, true)
    })(s, new SimplePass.Names())._1
  }

  private def assertStatementIsSimplified(acc : Unit, s : Statement) : (Unit, Boolean) = s match {
    case
//      Del(Ident(_)) | Try(_, List((None, _)), _, _) | AugAssign(_, _, _) |
//      Raise(_, None) | ClassDef(_, _, _, Decorators(List())) | Global(_) |
      IfSimple(_, _, _) | While(_, _, _) |
       Suite(_) | Assign(List(_)) | Assign(List(Ident(_), _)) | Return(_) |
       FuncDef(_, _, _, _, _, Decorators(List()), _) |
       NonLocal(_) | WithoutArgs(_) | ImportModule(_, _) | ImportSymbol(_, _, _) | ImportAllSymbols(_) => (acc, true)
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
