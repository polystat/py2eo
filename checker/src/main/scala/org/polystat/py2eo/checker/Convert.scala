package org.polystat.py2eo.checker

import org.cqfn.astranaut.base.{DraftNode, Node}
import org.polystat.py2eo.parser.{Expression, GeneralAnnotation, Statement}

import java.util
import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.collection.immutable.HashMap
import scala.jdk.CollectionConverters.CollectionHasAsScala

object Convert {

  private val nullAnnotation = GeneralAnnotation(None, None)

  def apply(node: Statement.T): Node = {
    node match {
      case Statement.If(conditioned, eelse, ann) => ???
      case Statement.IfSimple(cond, yes, no, ann) => ???
      case Statement.While(cond, body, eelse, ann) => ???
      case Statement.For(what, in, body, eelse, isAsync, ann) => ???
      case Statement.Suite(l, ann) => {
        val result = new DraftNode.Constructor()
        result.setName("Suite")
        result.setChildrenList(l.map(apply).asJava)
        result.createNode
      }
      case Statement.AugAssign(op, lhs, rhs, ann) => ???
      case Statement.AnnAssign(lhs, rhsAnn, rhs, ann) => ???
      case Statement.Assign(l, ann) => ???
      case Statement.CreateConst(name, value, ann) => ???
      case Statement.Pass(ann) => ???
      case Statement.Break(ann) => ???
      case Statement.Continue(ann) => ???
      case Statement.Return(x, _) => {
        val result = new DraftNode.Constructor()
        result.setName("Return")
        result.setChildrenList(x match {
          case Some(value) => util.Arrays.asList(apply(value))
          case None => util.Arrays.asList()
        })
        result.createNode
      }
      case Statement.Assert(what, param, ann) => ???
      case Statement.Raise(e, from, ann) => ???
      case Statement.Del(l, ann) => ???
      case Statement.FuncDef(name, _, _, _, _, body, _, _, _, _) => {
        val result = new DraftNode.Constructor()
        result.setName("FuncDef")
        result.setData(name)
        result.setChildrenList(util.Arrays.asList(apply(body)))
        result.createNode
      }
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
      case Expression.IntLiteral(value, _) => {
        val result = new DraftNode.Constructor()
        result.setName("IntLiteral")
        result.setData(value.toString)
        result.createNode
      }
      case Expression.FloatLiteral(value, ann) => ???
      case Expression.ImagLiteral(value, ann) => ???
      case Expression.StringLiteral(value, ann) => ???
      case Expression.BoolLiteral(value, ann) => ???
      case Expression.NoneLiteral(ann) => ???
      case Expression.EllipsisLiteral(ann) => ???
      case Expression.Binop(op, l, r, _) => {
        val result = new DraftNode.Constructor()
        result.setName("Binop")
        result.setData(Expression.Binops.toString(op))
        result.setChildrenList(util.Arrays.asList(apply(l), apply(r)))
        result.createNode
      }
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
    node.getTypeName match {
      case "Return" => {
        if (node.getChildCount == 1) {
          Statement.Return(Some(parseExpression(node.getChild(0))), nullAnnotation)
        } else {
          Statement.Return(None, nullAnnotation)
        }
      }

      case "FuncDef" => {
        val decorators = Statement.Decorators(List.empty)
        Statement.FuncDef(node.getData, List(), None, None, None, apply(node.getChild(0)), decorators, HashMap.empty, isAsync = false, nullAnnotation)
      }

      case "Suite" => {
        val children = node.getChildrenList.asScala.map(apply)
        Statement.Suite(children.toList, nullAnnotation)
      }
    }
  }

  private def parseExpression(node: Node): Expression.T = {
    node.getTypeName match {
      case "IntLiteral" => Expression.IntLiteral(node.getData.toInt, nullAnnotation)
      case "Binop" =>
        val op = Expression.Binops.ofString(node.getData)
        Expression.Binop(op, parseExpression(node.getChild(0)), parseExpression(node.getChild(1)), nullAnnotation)
    }
  }

}
