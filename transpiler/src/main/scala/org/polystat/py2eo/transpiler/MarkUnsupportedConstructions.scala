package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression
import org.polystat.py2eo.parser.Expression.{
  AnonFun, Assignment, Await, Binop, CallIndex, CollectionComprehension, CollectionCons, DictComprehension,
  DictCons, DoubleStar, EllipsisLiteral, FloatLiteral, FreakingComparison, GeneratorComprehension, ImagLiteral,
  IntLiteral, SimpleComparison, Slice, Star, StringLiteral, T, UnsupportedExpr, Yield, YieldFrom
}

object MarkUnsupportedConstructions {
  def mkUnsupportedExpr(e: Expression.T): T = {
    def mkUnsupportedExprInner(original : Expression.T): UnsupportedExpr = {
      new UnsupportedExpr(original, AnalysisSupport.childrenE(original), original.ann.pos)
    }
    def supportedCompOp(op : Expression.Compops.T) =
      try {
        PrintEO.compop(op); true
      } catch {
        case _: Throwable => false
      }
    val e1 = e match {
      case CallIndex(isCall, _, args, _) if !isCall || args.exists(x => x._1.nonEmpty) =>
        mkUnsupportedExprInner(e)
      case StringLiteral(value, ann) if value.exists(
        s => (s.head != '\'' && s.head != '"') || "\\\\[^\"'\\\\]".r.findFirstMatchIn(s).nonEmpty
      ) => mkUnsupportedExprInner(e)
      case ImagLiteral(_, _) => mkUnsupportedExprInner(e)
      case FloatLiteral(value, ann)
        if value.contains("e") || value.contains("E") || value.endsWith(".") || value.startsWith(".") =>
        mkUnsupportedExprInner(e)
      case IntLiteral(value, ann) if value < -(BigInt(1) << 31) || value > (BigInt(1) << 31) - 1 => mkUnsupportedExprInner(e)
      case Star(_, _) | DoubleStar(_, _) | CollectionComprehension(_, _, _, _) | DictComprehension(_, _, _) |
        Yield(_, _) | YieldFrom(_, _) | Slice(_, _, _, _) | AnonFun(_, _, _, _, _) | CollectionCons(_, _, _) |
        DictCons(_, _) | ImagLiteral(_, _) | EllipsisLiteral(_) | GeneratorComprehension(_, _, _) |
        Await(_, _) | Assignment(_, _, _) =>
        mkUnsupportedExprInner(e)
      case FreakingComparison(List(op), _, ann) if !supportedCompOp(op) => mkUnsupportedExprInner(e)
      case FreakingComparison(ops, l, ann) if (l.length != 2) => mkUnsupportedExprInner(e)
      case SimpleComparison(op, _, _, _) if (!supportedCompOp(op)) => mkUnsupportedExprInner(e)
      case Binop(op, _, _, _) if (
        try {
          PrintEO.binop(op); false
        } catch {
          case _: Throwable => true
        }
      ) => mkUnsupportedExprInner(e)
      case _ => e
    }
    e1
  }
}
