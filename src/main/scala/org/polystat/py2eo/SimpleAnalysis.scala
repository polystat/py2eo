package org.polystat.py2eo

import scala.collection.immutable.HashMap

object SimpleAnalysis {

  import Expression._

  private def childrenComprehension(c : Comprehension) = c match {
    case IfComprehension(cond) => List(cond)
    case ForComprehension(what, in, _) => List(what, in)
  }

  private def childrenDictEltDoubleStar(x : DictEltDoubleStar) = x match {
    case Right(value) => List(value)
    case Left((a, b)) => List(a, b)
  }

  def childrenE(e : T) : List[T] = e match {
    case Assignment(_, rhs, _) => List(rhs)
    case Await(what, _) => List(what)
    case AnonFun(_, _, _, body, _) => List(body)
    case Binop(_, l, r, _) =>List(l, r)
    case SimpleComparison(_, l, r, _) => List(l, r)
    case LazyLAnd(l, r, _) => List(l, r)
    case LazyLOr(l, r, _) => List(l, r)
    case FreakingComparison(_, l, _) => l
    case Unop(_, x, _) => List(x)
    case Star(e, _) => List(e)
    case DoubleStar(e, _) => List(e)
    case CollectionCons(_, l, _) => l
    case DictCons(l, _) => l.flatMap(childrenDictEltDoubleStar)
    case Slice(from, to, by, _) => from.toList ++ to.toList ++ by.toList
    case CallIndex(_, whom, args, _) => whom :: args.map(_._2)
    case Field(whose, _, _) => List(whose)
    case Cond(cond, yes, no, _) => List(cond, yes, no)
    case x : UnsupportedExpr => x.children
    case IntLiteral(_, _) | FloatLiteral(_, _) | StringLiteral(_, _) | BoolLiteral(_, _)
      | NoneLiteral(_) | ImagLiteral(_, _) | Ident(_, _) | EllipsisLiteral(_) =>
      List()
    case CollectionComprehension(_, base, l, _) => base :: l.flatMap(childrenComprehension)
    case GeneratorComprehension(base, l, ann) => base :: l.flatMap(childrenComprehension)
    case DictComprehension(base, l, _) =>
      childrenDictEltDoubleStar(base) ++ l.flatMap(childrenComprehension)
    case Yield(l, _) => l.toList
    case YieldFrom(e, _) => List(e)
  }

  def foldEE[A](f0 : (A, T) => A)(acc0 : A, e : T) : A = {
    val f = foldEE(f0)(_, _)
    val acc = f0(acc0, e)
    childrenE(e).foldLeft(acc)(f)
  }

  def childrenS(s : Statement) : (List[Statement], List[(Boolean, T)]) = {
    def isRhs(e : T) = (false, e)
    s match {
      case SimpleObject(_, fields, _) => (List(), fields.map(x => (false, x._2)))
      case With(cms, body, _, _) =>
        (List(body), cms.flatMap(x => (false, x._1) :: x._2.map(x => (true, x)).toList))
      case For(what, in, body, eelse, _, _) => (body :: eelse.toList, List((false, what), (false, in)))
      case Del(e, _) => (List(), List((false, e)))
      case If(conditioned, eelse, _) => (eelse.toList ++ conditioned.map(_._2), conditioned.map(x => (false, x._1)))
      case IfSimple(cond, yes, no, _) => (List(yes, no), List((false, cond)))
      case Try(ttry, excepts, eelse, ffinally, _) =>
        ((ttry :: excepts.map(_._2)) ++ eelse.toList ++ ffinally.toList, excepts.flatMap(p => p._1.map(x => (false, x._1)).toList))
      case While(cond, body, eelse, _) => (body :: eelse.toList, List(isRhs(cond)))
      case Suite(l, _) => (l, List())
      case AugAssign(_, lhs, rhs, _) => (List(), List((true, lhs), (false, rhs)))
      case Assign(l, _) => (List(), (false, l.head) :: l.tail.map(isRhs))
      case AnnAssign(lhs, rhsAnn, rhs, _) => (List(), (true, lhs) :: (List(rhsAnn) ++ rhs.toList).map(isRhs))
      case CreateConst(_, value, _) => (List(), List(isRhs(value)))
      case Return(x, _) => (List(), x.toList.map(isRhs))
      case Assert(what, param, _) => (List(), (what :: param.toList).map(isRhs))
      case Raise(e, from, _) => (List(), (e.toList ++ from.toList).map(isRhs))
      case ClassDef(_, bases, body, decorators, _) => (List(body), (bases.map(_._2) ++ decorators.l).map(isRhs))
      case FuncDef(_, args, _, _, returnAnnotation, body, decorators, _, _, _) => (
        List(body),
        (decorators.l ++ returnAnnotation.toList ++ args.flatMap(p => p.paramAnn.toList ++ p.default.toList)).map(isRhs)
      )
      case NonLocal(_, _) | Pass(_) | Break(_) | Continue(_) | Global(_, _) | ImportModule(_, _, _)
        | ImportSymbol(_, _, _, _) | ImportAllSymbols(_, _) =>
        (List(), List())
      case x : Unsupported => (x.sts, x.es)
    }
  }

  def foldSE[A](f : (A, T) => A, mayVisit : Statement => Boolean)(acc0 : A, s : Statement) : A = {
    if (mayVisit(s)) {
      val (ls, le) = childrenS(s)
      val acc = le.map(_._2).foldLeft(acc0)(foldEE(f))
      ls.foldLeft(acc)(foldSE(f, mayVisit))
    } else {
      acc0
    }
  }

  // almost a typical fold, but f may disallow it to visit children of certain nodes
  def foldSS[A](f : (A, Statement) => (A, Boolean))(acc0 : A, s : Statement) : A = {
    val (acc, procChildren) = f(acc0, s)
    if (!procChildren) acc else {
      val (ls, _) = childrenS(s)
      ls.foldLeft(acc)(foldSS(f))
    }
  }

  private def classifyVariablesAssignedInFunctionBody(args : List[Parameter], body : Statement)
  : HashMap[String, (VarScope.T, GeneralAnnotation)] = {
    type H = HashMap[String, (VarScope.T, GeneralAnnotation)]
    def dontVisitOtherBlocks(s : Statement) : Boolean = s match {
      case FuncDef(_, _, _, _, _, _, _, _, _, _) | ClassDef(_, _, _, _, _) => false
      case _ => true
    }
    val allNonLocalAndGlobalNames = foldSS[H](
      (h, st) => st match {
        case NonLocal(l, ann) => (l.foldLeft(h)((h, name) => h.+((name, (VarScope.NonLocal, ann.pos)))), false)
        case Global(l, ann)   => (l.foldLeft(h)((h, name) => h.+((name, (VarScope.Global, ann.pos)))), false)
        case _                => (h, dontVisitOtherBlocks(st))
      }
    )(HashMap[String, (VarScope.T, GeneralAnnotation)](), body)
    val allNonLocalAndGlobalAndLocalNames = foldSS[H](
      (h, st) => {
        def add0(h : H, name : String, ann : GeneralAnnotation) : H =
          if (h.contains(name)) h else h.+((name, (VarScope.Local, ann.pos)))
        def add(name : String, ann : GeneralAnnotation) = add0(h, name, ann)
        st match {
          case ClassDef(name, _, _, _, ann) => (add(name, ann), false)
          case SimpleObject(name, _, ann) => (add(name, ann), false)
          case FuncDef(name, _, _, _, _, _, _, _, _, ann)  => (add(name, ann), false)
          case Assign(List(CollectionCons(_, _, _), _), _) =>
            throw new RuntimeException("run this analysis after all assignment simplification passes!")
          case Assign(l, _) if l.size > 2 =>
            throw new RuntimeException("run this analysis after all assignment simplification passes!")
          case Assign(List(Ident(name, _), _), ann) => (add(name, ann), true)
          case AnnAssign(Ident(name, _), _, _, ann) => (add(name, ann), true)
          case u : Unsupported => (u.declareVars.foldLeft(h)(add0(_, _, u.ann)), true)
          case CreateConst(name, _, ann) => (add(name, ann), true)
          case _ => (h, true)
        }
      }
    )(allNonLocalAndGlobalNames, body)
    args.foldLeft(allNonLocalAndGlobalAndLocalNames)((h, name) => h.+((name.name, (VarScope.Arg, name.ann))))
  }

  private def computeAccessibleIdentsF(upperVars : HashMap[String, (VarScope.T, GeneralAnnotation)], f : FuncDef) : FuncDef = {
    val v = classifyVariablesAssignedInFunctionBody(f.args, f.body)
    val vUpper = upperVars.map(
      x => if (x._2._1 == VarScope.Local || x._2._1 == VarScope.Arg) (x._1, (VarScope.ImplicitNonLocal, x._2._2)) else x
    )
    val merged = v.foldLeft(vUpper)((acc, z) => acc.+(z))
    val (body, _) = SimplePass.procStatementGeneral(
      (s, ns) => s match {
        case f : FuncDef => (computeAccessibleIdentsF(merged, f), ns, false)
        case _ => (s, ns, true)
      }
    )(f.body, new SimplePass.Names())
    FuncDef(
      f.name, f.args, f.otherPositional, f.otherKeyword, f.returnAnnotation,
      body, f.decorators, merged, f.isAsync, f.ann.pos
    )
  }

  def computeAccessibleIdents(s : Statement) : Statement = {
    SimplePass.procStatementGeneral(
      (s, ns) => s match {
        case f : FuncDef => (computeAccessibleIdentsF(HashMap(), f), ns, false)
        case _ => (s, ns, true)
      }
    )(s, new SimplePass.Names())._1
  }

  private def assertStatementIsSimplified(acc : Unit, s : Statement) : (Unit, Boolean) = s match {
    case
      IfSimple(_, _, _, _) | While(_, _, _, _) | Suite(_, _) | Assign(List(_), _)
      | Return(_, _) | FuncDef(_, _, _, _, _, _, Decorators(List()), _, _, _)
      | NonLocal(_, _) | Pass(_) | Break(_) | Continue(_) | ImportModule(_, _, _)
      | ImportSymbol(_, _, _, _) | ImportAllSymbols(_, _) =>
      (acc, true)
    case Assign(List(lhs, _), _) if PrintLinearizedMutableEOWithCage.seqOfFields(lhs).isDefined  => (acc, true)
    case ClassDef(_, List(), body, Decorators(List()), _) =>{
      val (Suite(defs, _), _) = SimplePass.procStatement(SimplePass.unSuite)(body, SimplePass.Names(HashMap()))
      assert(defs.forall{ case Assign(List(Ident(_, _), _), _) => true case _ => false })
      (acc, true)
    }
  }

  private def assertExpressionIsSimplified(acc : Unit, e : T) : Unit = e match {
    case FreakingComparison(List(_), List(_, _), _) => ()
    case Star(_, _) | DoubleStar(_, _) | CollectionComprehension(_, _, _, _) | DictComprehension(_, _, _)
      | CallIndex(false, _, _, _) | FreakingComparison(_, _, _) | AnonFun(_, _, _, _, _) =>
      throw new RuntimeException("these must never happen after all passes: " + PrintPython.printExpr(e))
    case _ => ()
  }

  def checkIsSimplified(s : Statement) : Unit = {
    foldSS(assertStatementIsSimplified)((), s)
    foldSE(assertExpressionIsSimplified, _ => true)((), s)
  }

}
