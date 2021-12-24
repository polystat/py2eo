package org.polystat.py2eo

import Expression.{CallIndex, CollectionKind, Compops, Ident}
import Python3Parser._
import org.antlr.v4.runtime.{ANTLRInputStream, ParserRuleContext, Token}

import java.io.{File, FileReader}
import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap

case class Position(line : Int, char : Int) {
  override def toString = s"$line:$char"
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
    def ofContext(c : Comp_opContext): Compops.Value = {
      if (c.EQUALS() != null) Eq else
      if (c.NOT_EQ_1() != null || c.NOT_EQ_2() != null) Neq else
      if (c.GREATER_THAN() != null) Gt else
      if (c.GT_EQ() != null) Ge else
      if (c.LESS_THAN() != null) Lt else
      if (c.LT_EQ() != null) Le else
      if (c.IN() != null) if (c.NOT() != null) NotIn else In else
      if (c.IS() != null) if (c.NOT() != null) IsNot else Is else
        ???
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

  case class Parameter(name : String, kind : ArgKind.T, paramAnn : Option[T], default : Option[T], ann : GeneralAnnotation)

  case class Assignment(ident : String, rhs : T, ann : GeneralAnnotation) extends T
  case class Await(what : T, ann : GeneralAnnotation) extends T
  case class IntLiteral(value : BigInt, ann : GeneralAnnotation) extends T
  case class FloatLiteral(value : String, ann : GeneralAnnotation) extends T
  case class ImagLiteral(value : String, ann : GeneralAnnotation) extends T
  case class StringLiteral(value : String, ann : GeneralAnnotation) extends T
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

  def ofContext(c : AugassignContext): AugOps.Value = {
    if (c.ADD_ASSIGN() != null) Plus else
    if (c.SUB_ASSIGN() != null) Minus else
    if (c.MULT_ASSIGN() != null) Mul else
    if (c.AT_ASSIGN() != null) At else
    if (c.DIV_ASSIGN() != null) Div else
    if (c.MOD_ASSIGN() != null) Mod else
    if (c.IDIV_ASSIGN() != null) FloorDiv else
    if (c.AND_ASSIGN() != null) And else
    if (c.OR_ASSIGN() != null) Or else
    if (c.XOR_ASSIGN() != null) Xor else
    if (c.LEFT_SHIFT_ASSIGN() != null) Shl else
    if (c.RIGHT_SHIFT_ASSIGN() != null) Shr else
    if (c.POWER_ASSIGN() != null) Pow else
      ???
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
  val Local, NonLocal, ImplicitNonLocal, Arg, Global /*, Dynamic dynamic vars don't exist in python! only dynamic attributes do */  = Value
}

object ArgKind extends Enumeration {
  type T = Value
  val Positional, Keyword, PosOrKeyword = Value
}

sealed trait Statement {
  val ann : GeneralAnnotation
}

import Expression.{T => ET}

case class If(conditioned : List[(ET, Statement)], eelse : Statement, ann : GeneralAnnotation) extends Statement
case class IfSimple(cond : ET, yes : Statement, no : Statement, ann : GeneralAnnotation) extends Statement
case class While(cond : ET, body : Statement, eelse : Statement, ann : GeneralAnnotation) extends Statement
case class For(what : ET, in : ET, body : Statement, eelse : Statement, isAsync : Boolean, ann : GeneralAnnotation) extends Statement
case class Suite(l : List[Statement], ann : GeneralAnnotation) extends Statement
case class AugAssign(op : AugOps.T, lhs : ET, rhs : ET, ann : GeneralAnnotation) extends Statement
case class AnnAssign(lhs : ET, rhsAnn : ET, rhs : Option[ET], ann : GeneralAnnotation) extends Statement
case class Assign(l : List[ET], ann : GeneralAnnotation) extends Statement
case class CreateConst(name : String, value : ET, ann : GeneralAnnotation) extends Statement
case class Pass(ann : GeneralAnnotation) extends Statement
case class Break(ann : GeneralAnnotation) extends Statement
case class Continue(ann : GeneralAnnotation) extends Statement
case class Return(x : Option[ET], ann : GeneralAnnotation) extends Statement
case class Assert(x : List[ET], ann : GeneralAnnotation) extends Statement {
  def this(x : ET, ann : GeneralAnnotation) = this(List(x), ann)
}
case class Raise(e : Option[ET], from : Option[ET], ann : GeneralAnnotation) extends Statement
case class Del(l : ET, ann : GeneralAnnotation) extends Statement
case class Decorators(l : List[ET])
case class FuncDef(name : String, args : List[Expression.Parameter], otherPositional : Option[(String, Option[ET])],
         otherKeyword : Option[(String, Option[ET])], returnAnnotation : Option[ET], body : Statement, decorators: Decorators,
         accessibleIdents : HashMap[String, (VarScope.T, GeneralAnnotation)], isAsync : Boolean, ann : GeneralAnnotation) extends Statement {
//  lazy val variablesClassification = SimpleAnalysis.classifyFunctionVariables(args, body, false)
}

case class ClassDef(name : String, bases : List[(Option[String], ET)], body : Statement, decorators: Decorators, ann : GeneralAnnotation) extends Statement

// this cannot be expressed explicitly in python, but in EO
case class SimpleObject(name : String, fields : List[(String, ET)], ann : GeneralAnnotation) extends Statement

case class NonLocal(l : List[String], ann : GeneralAnnotation) extends Statement
case class Global(l : List[String], ann : GeneralAnnotation) extends Statement
case class ImportModule(what : List[String], as : Option[String], ann : GeneralAnnotation) extends Statement
case class ImportAllSymbols(from : List[String], ann : GeneralAnnotation) extends Statement
case class ImportSymbol(from : List[String], what : String, as : String, ann : GeneralAnnotation) extends Statement
case class With(cm : ET, target : Option[ET], body : Statement, isAsync : Boolean, ann : GeneralAnnotation) extends Statement
case class Try(ttry : Statement, excepts : List[(Option[(ET, Option[String])], Statement)],
               eelse : Statement, ffinally : Statement, ann : GeneralAnnotation) extends Statement
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

object MapStatements {

  import MapExpressions._

  def mapDottedName(x : Dotted_nameContext): List[String] = asScala(x.l).toList.map(x => x.getText)

  def mapImportModule(s : Import_stmtContext) : Statement = s match {
    case s : ImportNameContext =>
      val l = asScala(s.import_name().dotted_as_names().l).map(x => {
        val what = mapDottedName(x.dotted_name())
        val as = Option(x.NAME()).map(_.getText)
        ImportModule(what, as, new GeneralAnnotation(x))
      }).toList
      Suite(l, new GeneralAnnotation(s))

    case s : ImportFromContext =>
      val from = mapDottedName(s.import_from().dotted_name())
      if (s.import_from().import_as_names() == null)
        ImportAllSymbols(from, new GeneralAnnotation(s))
      else {
        val symbols = asScala(s.import_from().import_as_names().l).toList
        Suite(symbols.map(x => ImportSymbol(from, x.what.getText,
          if (x.aswhat != null) x.aswhat.getText else x.what.getText, new GeneralAnnotation(x)
        )), new GeneralAnnotation(s))
      }
  }

  def mapNullableSuite(s : SuiteContext) : Statement = if (s == null) Pass(new GeneralAnnotation(s)) else
    s match {
      case b : SuiteBlockStmtsContext => Suite(asScala(b.l).map(mapStmt).toList, new GeneralAnnotation(b))
      case s : SuiteSimpleStmtContext => mapSimpleStmt(s.simple_stmt())
    }

  def mapDecorators(c : DecoratorsContext): Decorators = Decorators(
    asScala(c.decorator()).toList.map(x => {
      val dname = asScala(x.dotted_name().l).toList
      // todo: decorators in the parser do not conform to the newest reference:
      // it is an assignment expression now https://docs.python.org/3/reference/compound_stmts.html#grammar-token-decorators
      val d = dname.tail.foldLeft(Ident(dname.head.getText, new GeneralAnnotation(dname.head)) : ET)((acc, token) =>
          Expression.Field(acc, token.getText, new GeneralAnnotation(token)))
      if (x.OPEN_PAREN() != null) CallIndex(isCall = true, d, mapArglistNullable(x.arglist()), new GeneralAnnotation(x))
      else d
    }))

  def mapTfparg(kind : ArgKind.T, c : TfpargContext) : Expression.Parameter = {
    val default = Option(c.test()).map(mapTest)
    Expression.Parameter(c.tfpdef().NAME().getText, kind, Option(c.tfpdef().test()).map(mapTest), default, new GeneralAnnotation(c))
  }

  def mapTypedargslistNopos(c : Typedargslist_noposContext): (List[Expression.Parameter], Option[(String, Option[ET])], Option[(String, Option[ET])]) = {
    (asScala(c.l).toList.map(x => mapTfparg(ArgKind.Keyword, x)),
      // todo: the next line may be wrong, it may break the method with which python forces all args to be keyword only (4.8.3 in tutorial)
      Option(c.tfptuple().tfpdef()).map(x => (x.NAME().getText, Option(x.test()).map(mapTest))),
      Option(c.tfpdict()).map(x => (x.tfpdef().NAME().getText, Option(x.tfpdef().test()).map(mapTest)))
    )
  }

  def mapTypedargslist(c : TypedargslistContext): (List[Expression.Parameter], Option[(String, Option[ET])], Option[(String, Option[ET])]) = {
    if (c == null) (List(), None, None) else {
      val tail =
        if (c.typedargslist_nopos() != null) mapTypedargslistNopos(c.typedargslist_nopos()) else
        if (c.tfpdict() != null) (List(), None,
          Some(c.tfpdict().tfpdef().NAME().getText, Option(c.tfpdict().tfpdef().test()).map(mapTest))) else
          (List(), None, None)
      val l = asScala(c.l).toList
      val (posOnly, others) = l.splitAt(l.indexWhere(c => c.tfpdef().DIV() != null))
      val (posAndKword, kwordOnly) = others.splitAt({
        val pos = others.indexWhere(c => c.tfpdef().STAR() != null)
        if (-1 == pos) others.length else pos
      })
      def f(kind : ArgKind.T, l : List[TfpargContext]) = l.flatMap(x =>
        if (x.tfpdef().NAME() == null) List() else List(mapTfparg(kind, x)))
      (f(ArgKind.Positional, posOnly) ++ f(ArgKind.PosOrKeyword, posAndKword) ++ f(ArgKind.Keyword, kwordOnly) ++
        tail._1, tail._2, tail._3)
    }
  }

  def mapFuncDef(s : FuncdefContext, decorators: Decorators, isAsync : Boolean): FuncDef = {
    val z = mapTypedargslist(s.parameters().typedargslist())
    FuncDef(s.NAME().getText, z._1, z._2, z._3, Option(s.test()).map(mapTest),
      mapNullableSuite(s.suite()), decorators, HashMap(), isAsync, new GeneralAnnotation(s))
  }

  def mapClassDef(s : ClassdefContext, decorators: Decorators): ClassDef = {
    ClassDef(
      s.NAME().getText, mapArglistNullable(s.arglist()),
        mapNullableSuite(s.suite()), decorators, new GeneralAnnotation(s)
    )
  }

  def mapFor(s : For_stmtContext, isAsync : Boolean): For = {
    For(mapExprList(s.exprlist()), mapTestList(s.testlist()),
      mapNullableSuite(s.body), mapNullableSuite(s.eelse), isAsync, new GeneralAnnotation(s))
  }

  def mapIf(s : If_stmtContext): If = {
    If(
      asScala(s.conds).map(mapAssignmentExpression).zip(asScala(s.bodies).map(mapNullableSuite)).toList,
      mapNullableSuite(s.eelse), new GeneralAnnotation(s)
    )
  }

  def mapWith(s : With_stmtContext, isAsync : Boolean): Statement = {
    asScala(s.l).toList.foldRight(mapNullableSuite(s.suite()))((x, s) =>
      With(mapTest(x.test()), Option(x.expr()).map(mapExpr), s, isAsync, s.ann.pos)
    )
  }

  def mapStmt(s : StmtContext) : Statement = s match {
    case s : StmtSimpleContext => mapSimpleStmt(s.simple_stmt())
    case s : StmtCompoundContext => s.compound_stmt() match {
      case s : CompIfContext => mapIf(s.if_stmt())
      case s : CompWhileContext =>
        While(
          mapAssignmentExpression(s.while_stmt().cond),
          mapNullableSuite(s.while_stmt().body),
          mapNullableSuite(s.while_stmt().eelse),
          new GeneralAnnotation(s)
        )
      case s : CompForContext => mapFor(s.for_stmt(), isAsync = false)
      case s : CompTryContext =>
        val t = s.try_stmt()
        val es = asScala(t.except_clause()).toList.zip(asScala(t.exceptSuites).toList)
          .map(ex => (if (ex._1.test() == null) None else Some((mapTest(ex._1.test()), Option(ex._1.NAME()).map(_.getText))),
            mapNullableSuite(ex._2)))
        Try(mapNullableSuite(t.trySuite), es, mapNullableSuite(t.elseSuite),
          if (t.finallySuite.size() == 0)
            Pass(new GeneralAnnotation(s))
          else
            mapNullableSuite(t.finallySuite.get(0))
          , new GeneralAnnotation(s))
      case s : CompWithContext => mapWith(s.with_stmt(), isAsync = false)
      case s : CompFuncDefContext => mapFuncDef(s.funcdef(), Decorators(List()), isAsync = false)
      case s : CompClassDefContext => mapClassDef(s.classdef(), Decorators(List()))
      case s : CompDecoratedContext =>
        val s1 = s.decorated()
        val decorators = mapDecorators(s1.decorators())
        if (s1.classdef() != null) mapClassDef(s1.classdef(), decorators) else
        if (s1.funcdef() != null) mapFuncDef(s1.funcdef(), decorators, isAsync = false) else
        if (s1.async_funcdef() != null) mapFuncDef(s1.async_funcdef().funcdef(), decorators, isAsync = true) else
          throw new AssertionError("nothing after decorators. This must not be parseable with the golden python compiler")
      case s : CompAsyncContext =>
        if (s.async_stmt().with_stmt() != null) mapWith(s.async_stmt().with_stmt(), isAsync = true) else
        if (s.async_stmt().for_stmt() != null) mapFor(s.async_stmt().for_stmt(), isAsync = true) else
        if (s.async_stmt().funcdef() != null) mapFuncDef(s.async_stmt().funcdef(), Decorators(List()), isAsync = true)
        else ???
    }
  }

  def mapSimpleStmt(context: Simple_stmtContext): Suite =
    Suite(asScala(context.l).map(mapSmallStmt).toList, new GeneralAnnotation(context))

  def mapSmallStmt(s : Small_stmtContext) : Statement = s match {
    case s : SmallNonLocalContext => NonLocal(asScala(s.nonlocal_stmt().l).map(_.getText).toList, new GeneralAnnotation(s))
    case s : SmallGlobalContext   => Global(asScala(s.global_stmt().l).map(_.getText).toList, new GeneralAnnotation(s))
    case s : SmallAssertContext => Assert(asScala(s.assert_stmt().test()).toList.map(mapTest), new GeneralAnnotation(s))
    case s : SmallExprContext =>
      val lhs = mapTestlistStarExpr(CollectionKind.Tuple, s.expr_stmt().testlist_star_expr())
      s.expr_stmt().expr_stmt_right() match {
        case r : AugAssignLabelContext =>
          val op = AugOps.ofContext(r.augassign())
          AugAssign(op, lhs, mapTestList(r.testlist()), new GeneralAnnotation(r))
        case a : JustAssignContext =>
          val rhss = asScala(a.l).map(mapRhsAssign).toList
          Assign(lhs :: rhss, new GeneralAnnotation(a))
        case x : AnnAssignLabelContext =>
          AnnAssign(lhs, mapTest(x.annassign().ann), Option(x.annassign().value).map(mapTest), new GeneralAnnotation(x))
      }
    case s : SmallDelContext => Del(mapExprList(s.del_stmt().exprlist()), new GeneralAnnotation(s))
    case s : SmallPassContext => Pass(new GeneralAnnotation(s))
    case s : SmallFlowContext => s.flow_stmt() match {
      case _ : FlowBreakContext => Break(new GeneralAnnotation(s))
      case _ : FlowContinueContext => Continue(new GeneralAnnotation(s))
      case r : FlowReturnContext =>
        Return(Option(r.return_stmt().testlist_star_expr()).map(mapTestlistStarExpr(CollectionKind.Tuple, _)),
          new GeneralAnnotation(r))
      case r : FlowRaiseContext =>
        val tests = asScala(r.raise_stmt().test()).toList.map(mapTest)
        Raise(tests.headOption, tests.lift(1), new GeneralAnnotation(r))
      case y : FlowYieldContext =>
        Assign(List(mapYieldExpr(y.yield_stmt().yield_expr())), new GeneralAnnotation(y))
    }
    case s : SmallImportContext => mapImportModule(s.import_stmt())
  }

  def mapRhsAssign(x : RhsassignContext): ET = x match {
    case l : RhsTestlistContext => mapTestlistStarExpr(CollectionKind.Tuple, l.testlist_star_expr())
    case y : RhsYieldContext => mapYieldExpr(y.yield_expr())
  }

  def mapFile(parsingBuiltins : Boolean, x : File_inputContext): Suite = {
    Suite(
      if (parsingBuiltins) List() else asScala(x.stmt()).map(mapStmt).toList,
      new GeneralAnnotation(x)
    )
  }

}

object MapExpressions {

  def string2num(x : String, c : ParserRuleContext) : Expression.T = {
    if (x.last == 'j') Expression.ImagLiteral(x.init, new GeneralAnnotation(c)) else
    if (x.exists(c => ((c == 'e' || c == 'E') && !x.startsWith("0x") && !x.startsWith("0X")) || c == '.' || c == '+' || c == '-'))
      Expression.FloatLiteral(x, new GeneralAnnotation(c))
    else {
      val int =
        if (x.startsWith("0x") || x.startsWith("0X"))
          x.substring(2, x.length).foldLeft(BigInt(0))((acc, ch) => {
            if (ch != '_')
              acc * 16 + (
                if (ch >= '0' && ch <= '9') ch.toInt - '0'.toInt else
                if (ch >= 'a' && ch <= 'f') ch.toInt - 'a'.toInt + 10 else
                if (ch >= 'A' && ch <= 'F') ch.toInt - 'A'.toInt + 10 else
                  throw new NumberFormatException()
                )
            else acc
          })
        else if (x.startsWith("0o") || x.startsWith("0O"))
          x.substring(2, x.length).foldLeft(BigInt(0))((acc, ch) =>
            if (ch != '_')
              acc * 8 + (if (ch >= '0' && ch <= '7') ch.toInt - '0'.toInt else throw new NumberFormatException())
            else acc
          )
        else if (x.startsWith("0b") || x.startsWith("0B"))
          x.substring(2, x.length).foldLeft(BigInt(0))((acc, ch) =>
            if (ch != '_')
              acc * 2 + (if (ch >= '0' && ch <= '1') ch.toInt - '0'.toInt else throw new NumberFormatException())
            else acc
          )
        else
          BigInt(x.filter(_ != '_'))
      Expression.IntLiteral(int, new GeneralAnnotation(c))
    }
  }

  import Expression._

  def mapAssignmentExpression(c : Assignment_expressionContext): ET = {
    if (c.ASSIGN_IN_EXPR() == null) mapTest(c.test())
    else Assignment(c.IDENT().getText, mapTest(c.test()), new GeneralAnnotation(c))
  }

  def mapDictEltDoubleStar(c : Dict_elt_double_starContext): Either[(ET, ET), ET] =
    if (c.expr() != null) Right(mapExpr(c.expr())) else Left((mapTest(c.test(0)), mapTest(c.test(1))))

  def mapYieldExpr(y1 : Yield_exprContext) : T = {
    if (y1.yield_arg() == null) Yield(None, new GeneralAnnotation(y1)) else {
      if (y1.yield_arg().FROM() == null)
        Yield(Some(mapTestlistStarExpr(CollectionKind.Tuple, y1.yield_arg().testlist_star_expr())), new GeneralAnnotation(y1))
      else
        YieldFrom(mapTest(y1.yield_arg().test()), new GeneralAnnotation(y1))
    }
  }

  def mapAtom(a : AtomContext) : T  = {
      if (a.OPEN_PAREN() != null || a.OPEN_BRACK() != null) {
        val collectionKind = if (a.OPEN_BRACK() != null) CollectionKind.List else CollectionKind.Tuple
        if (a.testlist_comp() != null) {
          mapTestList_comp(collectionKind, a.testlist_comp())
        } else
        if (a.yield_expr() != null) mapYieldExpr(a.yield_expr()) else
        CollectionCons(collectionKind, List(), new GeneralAnnotation(a))
      } else
      if (a.OPEN_BRACE() != null) {
        // An empty set cannot be constructed with {}; this literal constructs an empty dictionary. https://docs.python.org/3/reference/expressions.html#set-displays
        if (a.dictorsetmaker() == null) DictCons(List(), new GeneralAnnotation(a)) else
        if (a.dictorsetmaker().testlist_comp() != null) {
          mapTestList_comp(CollectionKind.Set, a.dictorsetmaker().testlist_comp())
        } else {
          if (a.dictorsetmaker().comp_for() != null)
            DictComprehension(mapDictEltDoubleStar(a.dictorsetmaker().dict_elt_double_star(0)),
              mapCompFor(a.dictorsetmaker().comp_for()), new GeneralAnnotation(a))
          else
            DictCons(asScala(a.dictorsetmaker().dict_elt_double_star()).toList.map(mapDictEltDoubleStar), new GeneralAnnotation(a))
        }
      } else
      if (a.NUMBER() != null) string2num(a.NUMBER().getText, a) else
      if (a.NAME() != null) Ident(a.NAME().getText, new GeneralAnnotation(a)) else
      if (a.TRUE() != null) BoolLiteral(value = true, new GeneralAnnotation(a)) else
      if (a.FALSE() != null) BoolLiteral(value = false, new GeneralAnnotation(a)) else
      if (a.NONE() != null) NoneLiteral(new GeneralAnnotation(a)) else {
      if (a.ELLIPSIS() != null) EllipsisLiteral(new GeneralAnnotation(a)) else
        StringLiteral(asScala(a.STRING()).map(_.getText).mkString(" "), new GeneralAnnotation(a))
      }
      /*    else {  todo: incorrect string literal processing?
            println(a.STRING())
            println(a.yield_expr())
            println(a.testlist_comp())
            println(a.dictorsetmaker(), a.ELLIPSIS(), a.NONE(), a.TRUE(), a.FALSE())
            ???
          }*/
  }

  def mapExprStarExpr(e : Expr_star_exprContext): ET = if (e.expr() != null) mapExpr(e.expr()) else mapStarExpr(e.star_expr())
  def mapExprList(e : ExprlistContext): ET = asScala(e.l).map(mapExprStarExpr).toList match {
    case List(x) if e.COMMA().isEmpty => x
    case l => CollectionCons(CollectionKind.Tuple, l, new GeneralAnnotation(e))
  }

  def mapVfparg(kind : ArgKind.T, c : VfpargContext) : Parameter = {
    val default = Option(c.test()).map(mapTest)
    Parameter(c.vfpdef().NAME().getText, kind, None, default, new GeneralAnnotation(c))
  }

  def mapVarargslistNopos(c : Varargslist_noposContext): (List[Parameter], Option[String], Option[String]) = {
    (asScala(c.l).toList.map(x => mapVfparg(ArgKind.Keyword, x)),
      // todo: the next line may be wrong, it may break the method with which python forces all args to be keyword only (4.8.3 in tutorial)
      if (c.vfptuple().vfpdef() == null) None else Some(c.vfptuple().vfpdef().NAME().getText),
      Option(c.vfpdict()).map(_.vfpdef().NAME().getText)
    )
  }

  def mapVarargslist(c : VarargslistContext): (List[Parameter], Option[String], Option[String]) = {
    if (c == null) (List(), None, None) else {
      val tail =
        if (c.varargslist_nopos() != null) mapVarargslistNopos(c.varargslist_nopos()) else
          if (c.vfpdict() != null) (List(), None, Some(c.vfpdict().vfpdef().NAME().getText)) else
            (List(), None, None)
      val l = asScala(c.l).toList
      val (posOnly, others) = l.splitAt(l.indexWhere(c => c.vfpdef().DIV() != null))
      val (posAndKword, kwordOnly) = others.splitAt({
        val pos = others.indexWhere(c => c.vfpdef().STAR() != null)
        if (-1 == pos) others.length else pos
      })
      def f(kind : ArgKind.T, l : List[VfpargContext]) = l.flatMap(x =>
        if (x.vfpdef().NAME() == null) List() else List(mapVfparg(kind, x)))
      (f(ArgKind.Positional, posOnly) ++ f(ArgKind.PosOrKeyword, posAndKword) ++ f(ArgKind.Keyword, kwordOnly) ++
           tail._1, tail._2, tail._3)
    }
  }



  def mapTest(e : TestContext) : T = e match {
    case e : TestOrTestContext =>
      if (e.test() == null)
        mapOrTest(e.or_test(0))
      else
        Cond(mapOrTest(e.or_test(1)), mapOrTest(e.or_test(0)), mapTest(e.test()), new GeneralAnnotation(e))
    case e : TestLambdefContext =>
      val (args, o1, o2) = mapVarargslist(e.lambdef().varargslist())
      AnonFun(
        args, o1, o2,
        mapTest(e.lambdef().test()),
        new GeneralAnnotation(e)
      )
  }

  def mapTestList_comp(collectionKind: CollectionKind.T, e : Testlist_compContext) : T =
    if (e.test_star_expr().size() > 1)
      CollectionCons(collectionKind, asScala(e.test_star_expr()).map(mapTestStarExpr).toList, new GeneralAnnotation(e)) else
    if (e.comp_for() == null) {
      val x = mapTestStarExpr(e.test_star_expr(0))
      if (!e.COMMA().isEmpty || collectionKind != CollectionKind.Tuple)
        CollectionCons(collectionKind, List(x), new GeneralAnnotation(e))
      else x
    }
    else {
      assert(e.test_star_expr().size() == 1)
      CollectionComprehension(collectionKind, mapTestStarExpr(e.test_star_expr(0)), mapCompFor(e.comp_for()), new GeneralAnnotation(e))
    }

  def mapCompFor(cf : Comp_forContext) : List[Comprehension] = {
    ForComprehension(mapExprList(cf.exprlist()), mapOrTest(cf.or_test()), cf.ASYNC() != null) ::
      mapCompIterNullable(cf.comp_iter())
  }

  def mapCompIterNullable(context: Python3Parser.Comp_iterContext) : List[Comprehension] =
    if (context == null) List() else
    if (context.comp_for() != null) mapCompFor(context.comp_for())
    else mapCompIf(context.comp_if())

  def mapCompIf(context: Python3Parser.Comp_ifContext) : List[Comprehension] =
    IfComprehension(mapTestNocond(context.test_nocond())) :: mapCompIterNullable(context.comp_iter())

  def mapTestNocond(context: Python3Parser.Test_nocondContext) : T =
    if (context.or_test() != null) mapOrTest(context.or_test()) else {
      val (args, o1, o2) = mapVarargslist(context.lambdef_nocond().varargslist())
      AnonFun(args, o1, o2,
        mapTestNocond(context.lambdef_nocond().test_nocond()),
        new GeneralAnnotation(context)
      )
    }

  def mapTestList(e : TestlistContext): ET = asScala(e.l).map(mapTest).toList match {
    case List(x) if e.COMMA().isEmpty => x
    case l => CollectionCons(CollectionKind.Tuple, l, new GeneralAnnotation(e))
  }

  def mapStarExpr(e : Star_exprContext): Star = Star(mapExpr(e.expr()), new GeneralAnnotation(e))

  def mapTestStarExpr(e : Test_star_exprContext) : T = e match {
    case e : TestNotStarContext => mapTest(e.test())
    case e : StarNotTestContext => mapStarExpr(e.star_expr())
  }

  def mapTestlistStarExpr(collectionKind : CollectionKind.T, e : Testlist_star_exprContext): ET = {
    asScala(e.l).map(mapTestStarExpr).toList match {
      case List(x) if e.COMMA().size() == 0 => x
      case l => CollectionCons(collectionKind, l, new GeneralAnnotation(e))
    }
  }

  def mapSubscript(e : Subscript_Context) : T = e match {
    case i : SubIndexContext => mapTest(i.test())
    case slice : SubSliceContext => Slice(
      if (slice.start == null) None else Some(mapTest(slice.start)),
      if (slice.stop == null) None else Some(mapTest(slice.stop)),
      if (slice.step == null) None else Some(mapTest(slice.step)),
      new GeneralAnnotation(e)
    )
  }

  def mapArgument(e : ArgumentContext) : (Option[String], T) = {
    if (e.STAR() != null) (None, Star(mapTest(e.test(0)), new GeneralAnnotation(e))) else
    if (e.POWER() != null) (None, DoubleStar(mapTest(e.test(0)), new GeneralAnnotation(e))) else
    if (e.ASSIGN() != null) {
      val Ident(keyword, _) = mapTest(e.test(0))
      (Some(keyword), mapTest(e.test(1)))
    } else {
      val a = mapTest(e.test(0))
      if (e.comp_for() != null) {
        val comp = mapCompFor(e.comp_for())
        (None, GeneratorComprehension(a, comp, new GeneralAnnotation(e)))
      } else
        (None, a)
    }
  }
  def mapArglistNullable(l : ArglistContext): List[(Option[String], ET)] = {
    if (l == null) List() else asScala(l.l).map(mapArgument).toList
  }

  def mapSubscriptList(c : SubscriptlistContext): ET = asScala(c.l).toList match {
    case List(x) if c.COMMA().isEmpty => mapSubscript(x)
    case l => CollectionCons(CollectionKind.Tuple, l.map(mapSubscript), new GeneralAnnotation(c))
  }

  def mapAtomExpr(e : Atom_exprContext): ET = {
    val noawait = {
      val whom = mapAtom(e.atom())
      asScala(e.trailer()).foldLeft(whom)((acc, trailer) => trailer match {
        case call: TrailerCallContext => CallIndex(isCall = true, acc, mapArglistNullable(call.arglist()), new GeneralAnnotation(call))
        case ind: TrailerSubContext => CallIndex(isCall = false, acc,
          List((None, mapSubscriptList(ind.subscriptlist()))), new GeneralAnnotation(ind))
        case field: TrailerFieldContext => Field(acc, field.NAME().getText, new GeneralAnnotation(field))
      })
    }
    if (e.AWAIT() != null) Await(noawait, noawait.ann.pos) else noawait
  }

  def mapPower(e : PowerContext): ET = {
    if (e.factor() != null) Binop(Binops.Pow, mapAtomExpr(e.atom_expr()), mapFactor(e.factor()), new GeneralAnnotation(e))
    else mapAtomExpr(e.atom_expr())
  }

  def mapFactor(e : FactorContext) : T = e match {
    case p : FactorPowerContext => mapPower(p.power())
    case u : UnaryContext =>
      val x = mapFactor(u.factor())
      Unop(Unops.ofString(u.op.getText), x, new GeneralAnnotation(e))
  }

  def mapNotTest(e : Not_testContext) : T = e match {
    case e : NotNotContext => Unop(Unops.LNot, mapNotTest(e.not_test()), new GeneralAnnotation(e))
    case e : NotComparisonContext =>
      val ops0 = asScala(e.comparison().ops)
      val ops = ops0.map(Compops.ofContext).toList
      val args = asScala(e.comparison().args).map(mapExpr).toList
      if (ops.isEmpty) args.head else FreakingComparison(ops, args, new GeneralAnnotation(e))
  }

  def mapBinop[TT](args : java.util.List[TT], ops : java.util.List[Token], mapa : TT => T): ET = {
    val sargs = asScala(args)
    val sops  = asScala(ops)
    val head = sargs.head
    sops.zip(sargs.tail).foldLeft(mapa(head))((acc, x) => {
      Binop(Binops.ofString(x._1.getText), acc, mapa(x._2), new GeneralAnnotation(x._1))
    })
  }

  def mapTerm(e : TermContext): ET = mapBinop(e.args, e.ops, mapFactor)
  def mapArithExpr(e : Arith_exprContext): ET = mapBinop(e.args, e.ops, mapTerm)
  def mapShiftExpr(e : Shift_exprContext): ET = mapBinop(e.args, e.ops, mapArithExpr)
  def mapAndExpr(e : And_exprContext): ET = mapBinop(e.args, e.ops, mapShiftExpr)
  def mapXorExpr(e : Xor_exprContext): ET = mapBinop(e.args, e.ops, mapAndExpr)
  def mapExpr(e : ExprContext): ET = mapBinop(e.args, e.ops, mapXorExpr)
  def mapOrTest(e : Or_testContext): ET = {
    def inner(l : List[And_testContext]) : T = l match {
      case List(x) => mapAndTest(x)
      case h :: t => LazyLOr(mapAndTest(h), inner(t), new GeneralAnnotation(h))
    }
    inner(asScala(e.args).toList)
  }
  def mapAndTest(e : And_testContext): ET = {
    def inner(l : List[Not_testContext]) : T = l match {
      case List(x) => mapNotTest(x)
      case h :: t => LazyLAnd(mapNotTest(h), inner(t), new GeneralAnnotation(h))
    }
    inner(asScala(e.args).toList)
  }

}

object Parse {
  import org.antlr.v4.runtime.CommonTokenStream

  def parse(file : File, debugPrinter : (Statement, String) => Unit) : Statement = {
    assert(file.getName.endsWith(".py"))

    val input = new FileReader(file)

    val inputStream = new ANTLRInputStream(input)
    val lexer = new Python3Lexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new Python3Parser(tokenStream)

    val e = parser.file_input()

    val t = MapStatements.mapFile(file.getName == "builtins", e)
    debugPrinter(t, "afterParser")
    t
  }

}
