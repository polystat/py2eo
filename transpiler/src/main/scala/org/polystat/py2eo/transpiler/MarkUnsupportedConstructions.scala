package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.{ArgKind, Expression, GeneralAnnotation, Statement}
import org.polystat.py2eo.parser.Expression.{
  AnonFun, Assignment, Await, Binop, CallIndex, CollectionComprehension, CollectionCons, DictComprehension,
  DictCons, DoubleStar, EllipsisLiteral, FloatLiteral, FreakingComparison, GeneratorComprehension, Ident,
  ImagLiteral, IntLiteral, Parameter, SimpleComparison, Slice, Star, StringLiteral, T, UnsupportedExpr, Yield, YieldFrom
}
import org.polystat.py2eo.parser.Statement.{
  AnnAssign, Assert, Assign, AugAssign, Break, ClassDef, Continue, Decorators, Del, For, FuncDef, Global,
  ImportAllSymbols, ImportModule, ImportSymbol, NonLocal, Pass, Raise, Return, Try, Unsupported, While, With
}
import org.polystat.py2eo.transpiler.GenericStatementPasses.NamesU

object MarkUnsupportedConstructions {
  def expressions(e: Expression.T): T = {
    def inner(original : Expression.T): UnsupportedExpr = {
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
        inner(e)
      case StringLiteral(value, ann) if value.length > 1 || value.exists(
        s => (s.head != '\'' && s.head != '"') || "\\\\[^\"'\\\\]".r.findFirstMatchIn(s).nonEmpty
      ) => inner(e)
      case ImagLiteral(_, _) => inner(e)
      case FloatLiteral(value, ann)
        if value.contains("e") || value.contains("E") || value.endsWith(".") || value.startsWith(".") =>
        inner(e)
      case IntLiteral(value, ann) if value < -(BigInt(1) << 31) || value > (BigInt(1) << 31) - 1 => inner(e)
      case Star(_, _) | DoubleStar(_, _) | CollectionComprehension(_, _, _, _) | DictComprehension(_, _, _) |
        Yield(_, _) | YieldFrom(_, _) | Slice(_, _, _, _) | AnonFun(_, _, _, _, _) | CollectionCons(_, _, _) |
        DictCons(_, _) | ImagLiteral(_, _) | EllipsisLiteral(_) | GeneratorComprehension(_, _, _) |
        Await(_, _) | Assignment(_, _, _) =>
        inner(e)
      case FreakingComparison(List(op), _, ann) if !supportedCompOp(op) => inner(e)
      case FreakingComparison(ops, l, ann) if (l.length != 2) => inner(e)
      case SimpleComparison(op, _, _, _) if (!supportedCompOp(op)) => inner(e)
      case Binop(op, _, _, _) if (
        try {
          PrintEO.binop(op); false
        } catch {
          case _: Throwable => true
        }
      ) => inner(e)
      case _ => e
    }
    e1
  }

  def statements(s: Statement.T, ns: NamesU): (Statement.T, NamesU) = {
    def inner(original: Statement.T, declareVars: List[String], ann: GeneralAnnotation): Unsupported = {
      new Unsupported(original, declareVars, AnalysisSupport.childrenS(original)._2, AnalysisSupport.childrenS(original)._1, ann)
    }
    (s match {
    case Assign(List(_), _) => s
    case Assign(List(Ident(_, _), _), _) => s
    case Assign(l, ann) => inner(s, l.init.flatMap { case Ident(s, _) => List(s) case _ => List() }, ann.pos)
    case FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation, body, decorators, accessibleIdents, isAsync, ann)
      if decorators.l.nonEmpty || otherKeyword.nonEmpty || otherPositional.nonEmpty || isAsync || returnAnnotation.nonEmpty ||
        args.exists(x => x.default.nonEmpty || x.paramAnn.nonEmpty || x.kind == ArgKind.Keyword) =>
      val body1 = inner(body, List(), body.ann.pos)
      FuncDef(name, args.map(a => Parameter(a.name, ArgKind.Positional, None, None, a.ann.pos)), None, None, None, body1,
        Decorators(List()), accessibleIdents, false, ann.pos)
    case While(_, _, None, _) => s
    case While(_, _, Some(Pass(_)), _) => s
    case For(_, _, _, _, _, _) | AugAssign(_, _, _, _) | Continue(_) | Break(_) | _: ClassDef | _: AnnAssign |
      Assert(_, _, _) | Raise(_, _, _) | Del(_, _) | Global(_, _) | NonLocal(_, _) | With(_, _, _, _) | Try(_, _, _, _, _) |
      ImportAllSymbols(_, _) | Return(_, _) | While(_, _, _, _) => inner(s, List(), s.ann.pos)
    case ImportModule(what, as, _) => inner(s, as.toList, s.ann.pos)
    case ImportSymbol(from, what, as, _) => inner(s, as.toList, s.ann.pos)
    case _ => s
  }, ns)
  }
}
