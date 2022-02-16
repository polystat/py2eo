package org.polystat.py2eo

import Expression.{CallIndex, CollectionKind, Compops, Ident}
import org.antlr.v4.runtime.{ANTLRInputStream, ParserRuleContext, Token}

import java.io.{File, FileReader}
import scala.collection.immutable.HashMap

case class Position(line : Int, char : Int) {
  override def toString : String = s"$line:$char"
}
// annotates all the AST nodes
case class GeneralAnnotation(start : Option[Position], stop : Option[Position]) {
  def this(c : ParserRuleContext) = {
    this(
      if (c != null) Some(GeneralAnnotation.token2StartPos(c.start)) else None,
      if (c != null) Some(GeneralAnnotation.token2StopPos(c.stop)) else None
    )
  }
  def this(t : Token) = this(Some(GeneralAnnotation.token2StartPos(t)), Some(GeneralAnnotation.token2StopPos(t)))
  def this() = this(None, None)
  override def toString: String = {
    def s(o : Option[Position]) = o match { case None => "???" case Some(x) => x.toString }
    s(start) + "-" + s(stop)
  }
  def rangeTo(to : GeneralAnnotation): GeneralAnnotation = GeneralAnnotation(start, to.stop)
  val pos: GeneralAnnotation = this // maybe some other annotations will be added later, so lets have an explicit method to copy position
}
object GeneralAnnotation{
  private def token2StartPos(t : Token) = Position(t.getLine, t.getCharPositionInLine)
  private def token2StopPos(t : Token) = Position(t.getLine, t.getStopIndex - t.getStartIndex + t.getCharPositionInLine)
}

object Expression {

  sealed trait T {
    val ann : GeneralAnnotation
  }

  // ops staring with L are logical, others are arithmetical, sometimes bitwise
  object Binops extends Enumeration {
    type T = Value
    val Plus, Minus, Mul, Div, FloorDiv, Pow, Shl, Shr, Xor, And, Or, Mod, At = Value
    def ofString(s : String) : T = s match {
      case "+" => Plus
      case "-" => Minus
      case "*" => Mul
      case "/" => Div
      case "//" => FloorDiv
      case "**" => Pow
      case "<<" => Shl
      case ">>" => Shr
      case "^"  => Xor
      case "&"  => And
      case "|"  => Or
      case "%"  => Mod
      case "@" => At
    }
    def toString(x : T): String = x match {
      case Plus => "+"
      case Minus =>"-"
      case Mul =>"*"
      case Div =>"/"
      case Mod =>"%"
      case And =>"&"
      case Or =>"|"
      case Xor =>"^"
      case Shl =>"<<"
      case Shr =>">>"
      case Pow =>"**"
      case FloorDiv =>"//"
      case At => "@"
    }

  }

  // ops staring with L are logical, others are arithmetical, sometimes bitwise
  object Compops extends Enumeration {
    type T = Value
    val  Eq, Neq, Gt, Ge, Lt, Le, In, NotIn, Is, IsNot = Value
    def ofString(s : String) : T = s match {
      case "==" => Eq
      case "!=" => Neq
      case ">"  => Gt
      case ">=" => Ge
      case "<"  => Lt
      case "<=" => Le
      case "in" => In
      case "is" => Is
    }
    def toString(x : T): String = x match {
      case Eq => "=="
      case Neq =>"!="
      case Gt =>">"
      case Ge =>">="
      case Lt =>"<"
      case Le =>"<="
      case In =>"in"
      case NotIn =>"not in"
      case Is =>"is"
      case IsNot =>"is not"
    }
  }

  object Unops extends  Enumeration {
    type T = Value
    val Plus, Minus, Neg, LNot = Value
    def ofString(s : String) : T = s match {
      case "+" => Plus
      case "-" => Minus
      case "~" => Neg
      case "not" => LNot
    }
    def toString(x : T): String = x match {
      case Plus => "+"
      case Minus =>"-"
      case Neg =>"~"
      case LNot =>"not "
    }
  }

  object CollectionKind extends Enumeration {
    type T = Value
    val Tuple, List, Set = Value
    def toBraks(x : T): (String, String) = x match {
      case Tuple => ("(", ")")
      case List =>  ("[", "]")
      case Set =>   ("{", "}")
    }
  }

  case class Parameter(name : String, kind : ArgKind.T, paramAnn : Option[T], default : Option[T], ann : GeneralAnnotation) {
    def withAnnDefault(ann : Option[T], default : Option[T]): Parameter = Parameter(name, kind, ann, default, this.ann.pos)
  }

  case class Assignment(ident : String, rhs : T, ann : GeneralAnnotation) extends T
  case class Await(what : T, ann : GeneralAnnotation) extends T
  case class IntLiteral(value : BigInt, ann : GeneralAnnotation) extends T
  case class FloatLiteral(value : String, ann : GeneralAnnotation) extends T
  case class ImagLiteral(value : String, ann : GeneralAnnotation) extends T
  case class StringLiteral(value : List[String], ann : GeneralAnnotation) extends T
  case class BoolLiteral(value : Boolean, ann : GeneralAnnotation) extends T
  case class NoneLiteral(ann : GeneralAnnotation) extends T
  case class EllipsisLiteral(ann : GeneralAnnotation) extends T
  case class Binop(op : Binops.T, l : T, r : T, ann : GeneralAnnotation) extends T
  case class SimpleComparison(op : Compops.T, l : T, r : T, ann : GeneralAnnotation) extends T
  case class LazyLAnd(l : T, r : T, ann : GeneralAnnotation) extends T
  case class LazyLOr(l : T, r : T, ann : GeneralAnnotation) extends T
  case class FreakingComparison(ops : List[Compops.T], l : List[T], ann : GeneralAnnotation) extends T
  case class Unop(op : Unops.T, x : T, ann : GeneralAnnotation) extends T
  case class Ident(name : String, ann : GeneralAnnotation) extends T
  case class Star(e : T, ann : GeneralAnnotation) extends T
  case class DoubleStar(e : T, ann : GeneralAnnotation) extends T
  case class CollectionCons(kind : CollectionKind.T, l : List[T], ann : GeneralAnnotation) extends T
  case class CollectionComprehension(kind : CollectionKind.T, base : T, l : List[Comprehension], ann : GeneralAnnotation) extends T
  case class GeneratorComprehension(base : T, l : List[Comprehension], ann : GeneralAnnotation) extends T
  type DictEltDoubleStar = Either[(T, T), T]
  case class DictCons(l : List[DictEltDoubleStar], ann : GeneralAnnotation) extends T
  case class DictComprehension(base : DictEltDoubleStar, l : List[Comprehension], ann : GeneralAnnotation) extends T
  case class Slice(from : Option[T], to : Option[T], by : Option[T], ann : GeneralAnnotation) extends T
  case class CallIndex(isCall : Boolean, whom : T, args : List[(Option[String], T)], ann : GeneralAnnotation) extends T {
    def this(whom : T, args : List[T], isCall : Boolean, ann : GeneralAnnotation) = {
       this (isCall, whom, args.map(x => (None, x)), ann)
    }
  }
  case class Field(whose : T, name : String, ann : GeneralAnnotation) extends T
  case class Cond(cond : T, yes : T, no : T, ann : GeneralAnnotation) extends T
  case class AnonFun(args : List[Parameter], otherPositional : Option[String],
                     otherKeyword : Option[String], body : T, ann : GeneralAnnotation) extends T
  case class Yield(l : Option[T], ann : GeneralAnnotation) extends T
  case class YieldFrom(e : T, ann : GeneralAnnotation) extends T
  class UnsupportedExpr(original0 : T, children0 : List[T], ann0 : GeneralAnnotation) extends T {
    val original: T = original0
    val children: List[T] = children0
    val ann: GeneralAnnotation = ann0
    def this(original : T) = this(original, SimpleAnalysis.childrenE(original), original.ann.pos)
  }
  object UnsupportedExpr {
    def unapply(e : UnsupportedExpr): Option[(T, List[T])] = Some((e.original, e.children))
  }

  sealed trait Comprehension
  case class IfComprehension(cond : T) extends Comprehension
  case class ForComprehension(what : T, in : T, isAsync : Boolean) extends Comprehension

  def mapRef(f: String => String)(x: T): T = {
    val m = mapRef(f)(_)
    x match {
      case Ident(name, ann) => Ident(f(name), ann.pos)
      case Binop(op, l, r, ann) => Binop(op, m(l), m(r), ann.pos)
      case FreakingComparison(ops, l, ann) => FreakingComparison(ops, l.map(m), ann.pos)
      case Unop(op, x, ann) => Unop(op, m(x), ann.pos)
      case Star(e, ann) => Star(m(e), ann.pos)
      case CollectionCons(b, l, ann) => CollectionCons(b, l.map(m), ann.pos)
      case Slice(from, to, by, ann) => Slice(from.map(m), to.map(m), by.map(m), ann.pos)
      case CallIndex(isCall, whom, args, ann) => CallIndex(isCall, m(whom),
        args.map(p => (p._1, m(p._2))), ann.pos)
      case Field(whose, name, ann) => Field(m(whose), name, ann.pos)
      case Cond(cond, yes, no, ann) => Cond(m(cond), m(yes), m(no), ann.pos)
    }
  }

  def map(f : T => T)(x : T) : T = {
    def m(e : T) : T = map(f)(e)
    def mD(x : DictEltDoubleStar) = x match {
      case Right(value) => Right(m(value))
      case Left(value) => Left(m(value._1), m(value._2))
    }
    def mC(x : Comprehension) = x match {
      case IfComprehension(cond) => IfComprehension(m(cond))
      case ForComprehension(what, in, isAsync) => ForComprehension(m(what), m(in), isAsync)
    }
    val x1 = x match {
      case Assignment(ident, rhs, ann) => Assignment(ident, m(rhs), ann)
      case Await(what, ann) => Await(m(what), ann)
      case IntLiteral(value, ann) => x
      case FloatLiteral(value, ann) => x
      case ImagLiteral(value, ann) => x
      case StringLiteral(value, ann) => x
      case BoolLiteral(value, ann) => x
      case NoneLiteral(ann) => x
      case EllipsisLiteral(ann) => x
      case Binop(op, l, r, ann) => Binop(op, m(l), m(r), ann)
      case SimpleComparison(op, l, r, ann) => SimpleComparison(op, m(l), m(r), ann)
      case LazyLAnd(l, r, ann) => LazyLAnd(m(l), m(r), ann)
      case LazyLOr(l, r, ann) => LazyLOr(m(l), m(r), ann)
      case FreakingComparison(ops, l, ann) => FreakingComparison(ops, l.map(m), ann)
      case Unop(op, x, ann) => Unop(op, m(x), ann)
      case Ident(name, ann) => x
      case Star(e, ann) => Star(m(e), ann)
      case DoubleStar(e, ann) => DoubleStar(m(e), ann)
      case CollectionCons(kind, l, ann) => CollectionCons(kind, l.map(m), ann)
      case CollectionComprehension(kind, base, l, ann) => CollectionComprehension(kind, m(base), l.map(mC), ann)
      case GeneratorComprehension(base, l, ann) => GeneratorComprehension(m(base), l.map(mC), ann)
      case DictCons(l, ann) => DictCons(l.map(mD), ann)
      case DictComprehension(base, l, ann) => DictComprehension(mD(base), l.map(mC), ann)
      case Slice(from, to, by, ann) => Slice(from.map(m), to.map(m), by.map(m), ann)
      case CallIndex(isCall, whom, args, ann) => CallIndex(isCall, m(whom), args.map(x => (x._1, m(x._2))), ann)
      case Field(whose, name, ann) => Field(m(whose), name, ann)
      case Cond(cond, yes, no, ann) => Cond(m(cond), m(yes), m(no), ann)
      case AnonFun(args, otherPositional, otherKeyword, body, ann) =>
        AnonFun(args.map(p => p.withAnnDefault(p.paramAnn.map(m), p.default.map(m))), otherPositional, otherKeyword, m(body), ann)
      case Yield(l, ann) => Yield(l.map(m), ann)
      case YieldFrom(e, ann) => YieldFrom(m(e), ann)
      case expr: UnsupportedExpr => new UnsupportedExpr(m(expr.original), expr.children.map(m), expr.ann)
    }
    f(x1)
  }

  def isLiteral(e : T) : Boolean = e match {
    case _ : NoneLiteral | _ : StringLiteral | _ : IntLiteral | _ : FloatLiteral | _ : ImagLiteral |
         _ : BoolLiteral | _ : EllipsisLiteral => true
    case _ => false
  }

}

object AugOps extends Enumeration {
  type T = Value
  val Plus, Minus, Mul, At, Div, Mod, And, Or, Xor, Shl, Shr, Pow, FloorDiv = Value
  def ofString(s : String): AugOps.Value = s match {
    case  "+=" => Plus
    case  "-=" =>  Minus
    case  "*=" =>  Mul
    case  "@=" =>  At
    case  "/=" =>  Div
    case  "%=" =>  Mod
    case  "&=" =>  And
    case  "|=" =>  Or
    case  "^=" =>  Xor
    case "<<=" =>  Shl
    case  ">>=" =>  Shr
    case  "**=" =>  Pow
    case  "//=" => FloorDiv
  }

  def toString(s : T) : String = s match {
    case Plus => "+="
    case Minus =>"-="
    case Mul =>"*="
    case At =>"@="
    case Div =>"/="
    case Mod =>"%="
    case And =>"&="
    case Or =>"|="
    case Xor =>"^="
    case Shl =>"<<="
    case Shr =>">>="
    case Pow =>"**="
    case FloorDiv =>"//="
  }
}

object VarScope extends Enumeration {
  type T = Value
  val Local, NonLocal, ImplicitNonLocal, Arg, Global = Value
}

object ArgKind extends Enumeration {
  type T = Value
  val Positional, Keyword, PosOrKeyword = Value
}

sealed trait Statement {
  val ann : GeneralAnnotation
}

import Expression.{T => ET}

case class If(conditioned : List[(ET, Statement)], eelse : Option[Statement], ann : GeneralAnnotation) extends Statement
case class IfSimple(cond : ET, yes : Statement, no : Statement, ann : GeneralAnnotation) extends Statement
case class While(cond : ET, body : Statement, eelse : Option[Statement], ann : GeneralAnnotation) extends Statement
case class For(what : ET, in : ET, body : Statement, eelse : Option[Statement], isAsync : Boolean, ann : GeneralAnnotation) extends Statement
case class Suite(l : List[Statement], ann : GeneralAnnotation) extends Statement
case class AugAssign(op : AugOps.T, lhs : ET, rhs : ET, ann : GeneralAnnotation) extends Statement
case class AnnAssign(lhs : ET, rhsAnn : ET, rhs : Option[ET], ann : GeneralAnnotation) extends Statement
case class Assign(l : List[ET], ann : GeneralAnnotation) extends Statement
case class CreateConst(name : String, value : ET, ann : GeneralAnnotation) extends Statement
case class Pass(ann : GeneralAnnotation) extends Statement
case class Break(ann : GeneralAnnotation) extends Statement
case class Continue(ann : GeneralAnnotation) extends Statement
case class Return(x : Option[ET], ann : GeneralAnnotation) extends Statement
case class Assert(what : ET, param : Option[ET], ann : GeneralAnnotation) extends Statement {
  def this(x : ET, ann : GeneralAnnotation) = this(x, None, ann)
}
case class Raise(e : Option[ET], from : Option[ET], ann : GeneralAnnotation) extends Statement
case class Del(l : ET, ann : GeneralAnnotation) extends Statement
case class Decorators(l : List[ET])
case class FuncDef(name : String, args : List[Expression.Parameter], otherPositional : Option[(String, Option[ET])],
         otherKeyword : Option[(String, Option[ET])], returnAnnotation : Option[ET], body : Statement, decorators: Decorators,
         accessibleIdents : HashMap[String, (VarScope.T, GeneralAnnotation)], isAsync : Boolean, ann : GeneralAnnotation) extends Statement

case class ClassDef(name : String, bases : List[(Option[String], ET)], body : Statement, decorators: Decorators, ann : GeneralAnnotation) extends Statement

// this cannot be expressed explicitly in python, but in EO
case class SimpleObject(name : String, decorates : Option[ET], fields : List[(String, ET)], ann : GeneralAnnotation) extends Statement

case class NonLocal(l : List[String], ann : GeneralAnnotation) extends Statement
case class Global(l : List[String], ann : GeneralAnnotation) extends Statement
case class ImportModule(what : List[String], as : Option[String], ann : GeneralAnnotation) extends Statement
case class ImportAllSymbols(from : List[String], ann : GeneralAnnotation) extends Statement
case class ImportSymbol(from : List[String], what : String, as : Option[String], ann : GeneralAnnotation) extends Statement
case class With(cms : List[(ET, Option[ET])], body : Statement, isAsync : Boolean, ann : GeneralAnnotation) extends Statement
case class Try(ttry : Statement, excepts : List[(Option[(ET, Option[String])], Statement)],
               eelse : Option[Statement], ffinally : Option[Statement], ann : GeneralAnnotation) extends Statement
class Unsupported(original0 : Statement, declareVars0 : List[String],
                  es0 : List[(Boolean, Expression.T)], sts0 : List[Statement], ann0 : GeneralAnnotation) extends Statement {
  val original: Statement = original0
  val declareVars: List[String] = declareVars0
  val es: List[(Boolean, ET)] = es0
  val sts: List[Statement] = sts0
  val ann: GeneralAnnotation = ann0
  def this(original : Statement, declareVars : List[String], ann : GeneralAnnotation) =
    this(original, declareVars, SimpleAnalysis.childrenS(original)._2, SimpleAnalysis.childrenS(original)._1, ann)
}
