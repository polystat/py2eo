package org.polystat.py2eo.transpiler

import scala.collection.immutable.HashMap
import org.polystat.py2eo.transpiler.Common.ASTAnalysisException
import org.polystat.py2eo.parser.Expression.{
  AnonFun, Assignment, Await, Binop, BoolLiteral, CallIndex, CollectionComprehension, CollectionCons, Comprehension,
  Cond, DictComprehension, DictCons, DictEltDoubleStar, DoubleStar, EllipsisLiteral, Field, FloatLiteral,
  ForComprehension, FreakingComparison, GeneratorComprehension, Ident, IfComprehension, ImagLiteral, IntLiteral,
  LazyLAnd, LazyLOr, NoneLiteral, Parameter, SimpleComparison, Slice, Star, StringLiteral, T, Unop,
  UnsupportedExpr, Yield, YieldFrom
}
import org.polystat.py2eo.parser.{GeneralAnnotation, PrintPython, Statement, VarScope}
import org.polystat.py2eo.transpiler.StatementPasses.{Names, NamesU}
import org.polystat.py2eo.parser.Statement.{
  AnnAssign, Assert, Assign, AugAssign, Break, ClassDef, Continue, CreateConst, Decorators, Del, For, FuncDef,
  Global, If, IfSimple, ImportAllSymbols, ImportModule, ImportSymbol, NonLocal, Pass, Raise, Return, SimpleObject,
  Suite, Try, Unsupported, While, With
}

object AnalysisSupport {

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

  def childrenS(s : Statement.T) : (List[Statement.T], List[(Boolean, T)]) = {
    def isRhs(e : T) = (false, e)
    s match {
      case SimpleObject(_, decorates, fields, _) => (List(), fields.map(x => (false, x._2)) ++ decorates.map((false, _)))
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

  def foldSE[A](f : (A, T) => A, mayVisit : Statement.T => Boolean)(acc0 : A, s : Statement.T) : A = {
    if (mayVisit(s)) {
      val (ls, le) = childrenS(s)
      val acc = le.map(_._2).foldLeft(acc0)(foldEE(f))
      ls.foldLeft(acc)(foldSE(f, mayVisit))
    } else {
      acc0
    }
  }

  // almost a typical fold, but f may disallow it to visit children of certain nodes
  def foldSS[A](f : (A, Statement.T) => (A, Boolean))(acc0 : A, s : Statement.T) : A = {
    val (acc, procChildren) = f(acc0, s)
    if (!procChildren) acc else {
      val (ls, _) = childrenS(s)
      ls.foldLeft(acc)(foldSS(f))
    }
  }

}
