package org.polystat.py2eo.checker.mutator

import org.polystat.py2eo.parser.{Expression, GeneralAnnotation, Statement}

import scala.collection.immutable.HashMap

object Convert {

  private val nullAnnotation = GeneralAnnotation(None, None)

  def apply(node: Statement.T): Node = {
    node match {
      case Statement.If(conditioned, eelse, ann) => ???
      case Statement.IfSimple(cond, yes, no, ann) => ???
      case Statement.While(cond, body, eelse, ann) => ???
      case Statement.For(what, in, body, eelse, isAsync, ann) => ???
      case Statement.Suite(body, _) => Node("Suite", body.map(apply))
      case Statement.AugAssign(op, lhs, rhs, ann) => ???
      case Statement.AnnAssign(lhs, rhsAnn, rhs, ann) => ???
      case Statement.Assign(l, ann) => ???
      case Statement.CreateConst(name, value, ann) => ???

      case Statement.Pass(_) => Node("Pass")
      case Statement.Break(_) => Node("Break")
      case Statement.Continue(_) => Node("Continue")
      case Statement.Return(value, _) => Node("Return", value.map(apply).toList)

      case Statement.Assert(what, param, ann) => ???
      case Statement.Raise(e, from, ann) => ???
      case Statement.Del(l, ann) => ???
      case Statement.FuncDef(name, _, _, _, _, body, _, _, _, _) => Node("FuncDef", name, List(apply(body)))
      case Statement.ClassDef(name, bases, body, decorators, ann) => ???
      case Statement.SimpleObject(name, decorates, fields, ann) => ???
      case Statement.NonLocal(l, ann) => ???
      case Statement.Global(l, ann) => ???
      case Statement.ImportModule(what, as, ann) => ???
      case Statement.ImportAllSymbols(from, ann) => ???
      case Statement.ImportSymbol(from, what, as, ann) => ???
      case Statement.With(cms, body, isAsync, ann) => ???
      case Statement.Try(ttry, excepts, eelse, ffinally, ann) => ???
      case Statement.Unsupported(original, declareVars, es, sts, ann) => ???
    }
  }

  def apply(node: Expression.T): Node = {
    node match {
      case Expression.Assignment(ident, rhs, ann) => ???
      case Expression.Await(what, ann) => ???
      case Expression.IntLiteral(value, _) => Node("IntLiteral", value.toString)
      case Expression.FloatLiteral(value, _) => Node("FloatLiteral", value)
      case Expression.ImagLiteral(value, ann) => ???
      case Expression.StringLiteral(value, _) => Node("StringLiteral", value.mkString(" "))
      case Expression.BoolLiteral(value, _) => Node("BoolLiteral", value.toString)
      case Expression.NoneLiteral(_) => Node("None")
      case Expression.EllipsisLiteral(_) => Node("Ellipsis")

      case Expression.Binop(op, lhs, rhs, _) =>
        Node("Binop", Expression.Binops.toString(op), List(lhs, rhs).map(apply))

      case Expression.SimpleComparison(op, l, r, ann) => ???
      case Expression.LazyLAnd(l, r, ann) => ???
      case Expression.LazyLOr(l, r, ann) => ???
      case Expression.FreakingComparison(ops, l, ann) => ???
      case Expression.Unop(op, x, ann) => ???
      case Expression.Ident(name, ann) => ???
      case Expression.Star(e, ann) => ???
      case Expression.DoubleStar(e, ann) => ???
      case Expression.CollectionCons(kind, l, ann) => ???
      case Expression.CollectionComprehension(kind, base, l, ann) => ???
      case Expression.GeneratorComprehension(base, l, ann) => ???
      case Expression.DictCons(l, ann) => ???
      case Expression.DictComprehension(base, l, ann) => ???
      case Expression.Slice(from, to, by, ann) => ???
      case Expression.CallIndex(isCall, whom, args, ann) => ???
      case Expression.Field(whose, name, ann) => ???
      case Expression.Cond(cond, yes, no, ann) => ???
      case Expression.AnonFun(args, otherPositional, otherKeyword, body, ann) => ???
      case Expression.Yield(l, ann) => ???
      case Expression.YieldFrom(e, ann) => ???
    }
  }

  def apply(node: Node): Statement.T = {
    node match {
      case Node("Return", _, children) =>
        Statement.Return(children.headOption.map(parseExpression), nullAnnotation)

      case Node("Suite", _, children) =>
        Statement.Suite(children.map(apply), nullAnnotation)

      case Node("FuncDef", Some(name), body :: _) =>
        Statement.FuncDef(name, List(), None, None, None, apply(body), Statement.Decorators(List.empty), HashMap.empty, isAsync = false, nullAnnotation)
    }
  }

  private def parseExpression(node: Node): Expression.T = {
    node match {
      case Node("IntLiteral", Some(value), _) => Expression.IntLiteral(value.toInt, nullAnnotation)
      case Node("Binop", Some(op), lhs :: rhs :: _) =>
        Expression.Binop(Expression.Binops.ofString(op), parseExpression(lhs), parseExpression(rhs), nullAnnotation)
    }
  }
}
