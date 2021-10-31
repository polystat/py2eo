import java.io.{File, FileReader, FileWriter}
import java.math.BigInteger

import Common.HashStack
import Expression.{CallIndex, CollectionCons, CollectionKind, DictCons, Ident, NoneLiteral}
import Python3Parser.{And_exprContext, And_testContext, ArglistContext, ArgumentContext, Arith_exprContext, AtomContext, Atom_exprContext, AugAssignContext, AugassignContext, ClassdefContext, CompAsyncContext, CompClassDefContext, CompDecoratedContext, CompForContext, CompFuncDefContext, CompIfContext, CompTryContext, CompWhileContext, CompWithContext, Comp_forContext, Comp_opContext, DecoratorsContext, Dict_elt_double_starContext, Dotted_nameContext, ExprContext, Expr_star_exprContext, Expr_stmtContext, ExprlistContext, FactorContext, FactorPowerContext, File_inputContext, FlowBreakContext, FlowContinueContext, FlowRaiseContext, FlowReturnContext, FlowYieldContext, FuncdefContext, ImportFromContext, ImportNameContext, Import_stmtContext, JustAssignContext, NotComparisonContext, NotNotContext, Not_testContext, Or_testContext, PowerContext, RhsTestlistContext, RhsassignContext, Shift_exprContext, Simple_stmtContext, SmallAssertContext, SmallDelContext, SmallExprContext, SmallFlowContext, SmallGlobalContext, SmallImportContext, SmallNonLocalContext, SmallPassContext, Small_stmtContext, StarNotTestContext, Star_exprContext, StmtCompoundContext, StmtContext, StmtSimpleContext, SubIndexContext, SubSliceContext, Subscript_Context, SuiteBlockStmtsContext, SuiteContext, SuiteSimpleStmtContext, TermContext, TestContext, TestLambdefContext, TestNotStarContext, TestOrTestContext, Test_star_exprContext, TestlistContext, Testlist_compContext, Testlist_star_exprContext, TfpargContext, TrailerCallContext, TrailerFieldContext, TrailerSubContext, TypedargslistContext, Typedargslist_noposContext, UnaryContext, Xor_exprContext, Yield_stmtContext}
import org.antlr.v4.runtime.{ANTLRInputStream, Token}

import collection.JavaConverters._
import scala.collection.immutable.HashMap
import scala.reflect.internal.util.FileUtils
import scala.sys.process.ProcessLogger

object Expression {

  sealed trait T;

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
    def toString(x : T) = x match {
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
    def ofContext(c : Comp_opContext) = {
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
    def toString(x : T) = x match {
      case Plus => "+"
      case Minus =>"-"
      case Neg =>"~"
      case LNot =>"not "
    }
  }

  object CollectionKind extends Enumeration {
    type T = Value
    val Tuple, List, Set = Value
    def toBraks(x : T) = x match {
      case Tuple => ("(", ")")
      case List =>  ("[", "]")
      case Set =>   ("{", "}")
    }
  }

  case class IntLiteral(value : BigInt) extends T
  case class FloatLiteral(value : Double) extends T
  case class StringLiteral(value : String) extends T
  case class BoolLiteral(value : Boolean) extends T
  case class NoneLiteral() extends T
  case class Binop(op : Binops.T, l : T, r : T) extends T
  case class SimpleComparison(op : Compops.T, l : T, r : T) extends T
  case class LazyLAnd(l : T, r : T) extends T
  case class LazyLOr(l : T, r : T) extends T
  case class FreakingComparison(ops : List[Compops.T], l : List[T]) extends T
  case class Unop(op : Unops.T, x : T) extends T
  case class Ident(name : String) extends T
  case class Star(e : T) extends T
  case class DoubleStar(e : T) extends T
  case class CollectionCons(kind : CollectionKind.T, l : List[T]) extends T
  case class CollectionComprehension(kind : CollectionKind.T, base : T, l : List[Comprehension]) extends T
  type DictEltDoubleStar = Either[(T, T), T]
  case class DictCons(l : List[DictEltDoubleStar]) extends T
  case class DictComprehension(base : DictEltDoubleStar, l : List[Comprehension]) extends T
  case class Slice(from : Option[T], to : Option[T], by : Option[T]) extends T
  case class CallIndex(isCall : Boolean, whom : T, args : List[(Option[String], T)]) extends T {
    def this(whom : T, args : List[T], isCall : Boolean) = {
       this (isCall, whom, args.map(x => (None, x)))
    }
  }
  case class Field(whose : T, name : String) extends T
  case class Cond(cond : T, yes : T, no : T) extends T
  case class AnonFun(args : List[String], body : T) extends T

  sealed trait Comprehension
  case class IfComprehension(cond : T) extends Comprehension
  case class ForComprehension(what : T, in : T) extends Comprehension

  def mapRef(f: String => String)(x: T): T = {
    val m = mapRef(f)(_)
    x match {
      case Ident(name) => Ident(f(name))
      case Binop(op, l, r) => Binop(op, m(l), m(r))
      case FreakingComparison(ops, l) => FreakingComparison(ops, l.map(m))
      case Unop(op, x) => Unop(op, m(x))
      case Star(e) => Star(m(e))
      case CollectionCons(b, l) => CollectionCons(b, l.map(m))
      case Slice(from, to, by) => Slice(from.map(m), to.map(m), by.map(m))
      case CallIndex(isCall, whom, args) => CallIndex(isCall, m(whom),
        args.map(p => (p._1, m(p._2))))
      case Field(whose, name) => Field(m(whose), name)
      case Cond(cond, yes, no) => Cond(m(cond), m(yes), m(no))
    }
  }

}

object AugOps extends Enumeration {
  type T = Value
  val Plus, Minus, Mul, At, Div, Mod, And, Or, Xor, Shl, Shr, Pow, FloorDiv = Value
  def ofString(s : String) = s match {
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

  def ofContext(c : AugassignContext) = {
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


object StatementsWithoutArgs extends Enumeration {
  type T = Value
  val Pass, Break, Continue = Value
  def toString(x : T) = x match {
    case Pass =>"pass"
    case Break =>"break"
    case Continue =>"continue"
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

sealed trait Statement

import Expression.{T => ET}

case class If(conditioned : List[(ET, Statement)], eelse : Statement) extends Statement
case class IfSimple(cond : ET, yes : Statement, no : Statement) extends Statement
case class While(cond : ET, body : Statement, eelse : Statement) extends Statement
case class For(what : ET, in : ET, body : Statement, eelse : Statement) extends Statement
case class Suite(l : List[Statement]) extends Statement
case class AugAssign(op : AugOps.T, lhs : ET, rhs : ET) extends Statement
case class Assign(l : List[ET]) extends Statement
case class WithoutArgs(s : StatementsWithoutArgs.T) extends Statement
object WithoutArgs {
  val pass = WithoutArgs(StatementsWithoutArgs.Pass)
  val brk = WithoutArgs(StatementsWithoutArgs.Break)
}
case class Return(x : ET) extends Statement
case class Yield(l : Option[ET]) extends Statement
case class Assert(x : ET) extends Statement
case class Raise(e : Option[ET], from : Option[ET]) extends Statement
case class Del(l : ET) extends Statement
case class Decorators(l : List[ET])
case class FuncDef(name : String, args : List[(String, ArgKind.T, Option[ET])], otherPositional : Option[String],
         otherKeyword : Option[String], body : Statement, decorators: Decorators, accessibleIdents : HashMap[String, VarScope.T]) extends Statement {
//  lazy val variablesClassification = SimpleAnalysis.classifyFunctionVariables(args, body, false)
}
class ClassDef(name : String, bases0 : List[ET], body : Statement, decorators: Decorators)
  extends FuncDef(name, List(), None, None, body, decorators, HashMap()) {
    val bases = bases0
}
object ClassDef{
  def unapply(arg: ClassDef) = Some((arg.name, arg.bases, arg.body, arg.decorators))
}
case class NonLocal(l : List[String]) extends Statement
case class Global(l : List[String]) extends Statement
case class ImportModule(what : List[String], as : String) extends Statement
case class ImportAllSymbols(from : List[String]) extends Statement
case class ImportSymbol(from : List[String], what : String, as : String) extends Statement
case class With(cm : ET, target : Option[ET], body : Statement) extends Statement
case class Try(ttry : Statement, excepts : List[(Option[(ET, Option[String])], Statement)],
               eelse : Statement, ffinally : Statement) extends Statement

object MapStatements {

  import MapExpressions._

  def mapDottedName(x : Dotted_nameContext) = asScala(x.l).toList.map(x => x.getText)

  def mapImportModule(s : Import_stmtContext) : Statement = s match {
    case s : ImportNameContext =>
      val l = asScala(s.import_name().dotted_as_names().l).map(x => {
        val what = mapDottedName(x.dotted_name())
        val as = if (x.NAME() == null) what.last else x.NAME().getText
        ImportModule(what, as)
      }).toList
      Suite(l)

    case s : ImportFromContext =>
      val symbols = asScala(s.import_from().import_as_names().l).toList
      val from = mapDottedName(s.import_from().dotted_name())
      Suite(symbols.map(x => ImportSymbol(from, x.what.getText,
        if (x.aswhat != null) x.aswhat.getText else x.what.getText
      )))
  }

  def mapNullableSuite(s : SuiteContext) : Statement = if (s == null) pass else
    s match {
      case b : SuiteBlockStmtsContext => Suite(asScala(b.l).map(mapStmt).toList)
      case s : SuiteSimpleStmtContext => mapSimpleStmt(s.simple_stmt())
    }

  def mapDecorators(c : DecoratorsContext) = Decorators(
    asScala(c.decorator()).toList.map(x => {
      val dname = mapDottedName(x.dotted_name())
      // todo: decorators in the parser do not conform to the newest reference:
      // it is an assignment expression now https://docs.python.org/3/reference/compound_stmts.html#grammar-token-decorators
      val d = dname.tail.foldLeft(Ident(dname.head) : ET)(Expression.Field)
      if (x.arglist()!= null) CallIndex(true, d, mapArglistNullable(x.arglist()))
      else d
    }))

  def mapTfparg(kind : ArgKind.T, c : TfpargContext) : (String, ArgKind.T, Option[ET]) =
    (c.tfpdef().NAME().getText, kind, Option(c.test()).map(mapTest)) // todo: default value is ignored
  def mapTypedargslistNopos(c : Typedargslist_noposContext) =
    (asScala(c.l).toList.map(x => (mapTfparg(ArgKind.Keyword, x))),
      Some(c.tfptuple().tfpdef().NAME().getText),
      Option(c.tfpdict()).map(_.tfpdef().NAME().getText)
    )
  def mapTypedargslist(c : TypedargslistContext) = {
    if (c == null) (List(), None, None) else {
      val tail =
        if (c.typedargslist_nopos() != null) mapTypedargslistNopos(c.typedargslist_nopos()) else
        if (c.tfpdict() != null) (List(), None, Some(c.tfpdict().tfpdef().NAME().getText)) else
          (List(), None, None)
      val head = asScala(c.l).toList.map(x => (mapTfparg(ArgKind.PosOrKeyword, x)))
      (head ++ tail._1, tail._2, tail._3)
    }
  }

  def mapFuncDef(s : FuncdefContext, decorators: Decorators) = {
    val z = mapTypedargslist(s.parameters().typedargslist())
    FuncDef(s.NAME().getText, z._1, z._2, z._3, mapNullableSuite(s.suite()), decorators, HashMap())
  }

  def mapClassDef(s : ClassdefContext, decorators: Decorators) = new ClassDef(
    s.NAME().getText, mapArglistNullable(s.arglist()).map{case (None, e) => e}, mapNullableSuite(s.suite()), decorators
  )

  val pass = WithoutArgs(StatementsWithoutArgs.Pass)

  def mapStmt(s : StmtContext) : Statement = s match {
    case s : StmtSimpleContext => mapSimpleStmt(s.simple_stmt())
    case s : StmtCompoundContext => s.compound_stmt() match {
      case s : CompIfContext => If(
        asScala(s.if_stmt().conds).map(mapTest).zip(asScala(s.if_stmt().bodies).map(mapNullableSuite)).toList,
        mapNullableSuite(s.if_stmt().eelse)
      )
      case s : CompWhileContext =>
        While(
          mapTest(s.while_stmt().cond),
          mapNullableSuite(s.while_stmt().body),
          mapNullableSuite(s.while_stmt().eelse)
        )
      case s : CompForContext =>
        For(mapExprList(s.for_stmt().exprlist()), mapTestList(s.for_stmt().testlist()),
          mapNullableSuite(s.for_stmt().body), mapNullableSuite(s.for_stmt().eelse))
      case s : CompTryContext =>
        val t = s.try_stmt()
        val es = asScala(t.except_clause()).toList.zip(asScala(t.exceptSuites).toList)
          .map(ex => ((if (ex._1.test() == null) None else Some((mapTest(ex._1.test()), Option(ex._1.NAME()).map(_.getText)))),
            mapNullableSuite(ex._2)))
        Try(mapNullableSuite(t.trySuite), es, mapNullableSuite(t.elseSuite),
          if (t.finallySuite.size() == 0) pass else mapNullableSuite(t.finallySuite.get(0)))
      case s : CompWithContext =>
        asScala(s.with_stmt().l).toList.foldRight(mapNullableSuite(s.with_stmt().suite()))((x, s) =>
          With(mapTest(x.test()), Option(x.expr()).map(mapExpr), s)
        )
      case s : CompFuncDefContext => mapFuncDef(s.funcdef(), Decorators(List()))
      case s : CompClassDefContext => mapClassDef(s.classdef(), Decorators(List()))
      case s : CompDecoratedContext =>
        val s1 = s.decorated()
        val decorators = mapDecorators(s1.decorators())
        if (s1.classdef() != null) mapClassDef(s1.classdef(), decorators) else
        if (s1.funcdef() != null) mapFuncDef(s1.funcdef(), decorators) else
        if (s1.async_funcdef() != null) ??? else
          throw new AssertionError("nothing after decorators. This must not be parseable with the golden python compiler")
      case s : CompAsyncContext => ???
    }
  }

  def mapSimpleStmt(context: Simple_stmtContext) =
    Suite(asScala(context.l).map(mapSmallStmt).toList)

  def mapSmallStmt(s : Small_stmtContext) : Statement = s match {
    case s : SmallNonLocalContext => NonLocal(asScala(s.nonlocal_stmt().l).map(_.getText).toList)
    case s : SmallGlobalContext   => Global(asScala(s.global_stmt().l).map(_.getText).toList)
    case s : SmallAssertContext => Assert(mapTest(s.assert_stmt().test(0)))
    case s : SmallExprContext => {
      val lhs = mapTestlistStarExpr(CollectionKind.Tuple, s.expr_stmt().testlist_star_expr())
      s.expr_stmt().expr_stmt_right() match {
        case r : AugAssignContext =>
          val op = AugOps.ofContext(r.augassign())
          AugAssign(op, lhs, mapTestList(r.testlist()))
        case a : JustAssignContext =>
          val rhss = asScala(a.l).map(mapRhsAssign).toList
          Assign(lhs :: rhss)
      }
    }
    case s : SmallDelContext => Del(mapExprList(s.del_stmt().exprlist()))
    case s : SmallPassContext => WithoutArgs(StatementsWithoutArgs.Pass)
    case s : SmallFlowContext => s.flow_stmt() match {
      case _ : FlowBreakContext => WithoutArgs(StatementsWithoutArgs.Break)
      case _ : FlowContinueContext => WithoutArgs(StatementsWithoutArgs.Continue)
      case r : FlowReturnContext =>
        Return(if (r.return_stmt().testlist() == null)
          Expression.CollectionCons(CollectionKind.Tuple, List()) else
          mapTestList(r.return_stmt().testlist()))
      case r : FlowRaiseContext =>
        val tests = asScala(r.raise_stmt().test()).toList.map(mapTest)
        assert(tests.size <= 1)
        Raise(tests.headOption, tests.lift(1))
      case y : FlowYieldContext =>
        val y1 = y.yield_stmt().yield_expr()
        if (y1.yield_arg() == null) Yield(None) else {
          assert(y1.yield_arg().FROM() == null)
          Yield(Some(mapTestList(y1.yield_arg().testlist())))
        }
    }
    case s : SmallImportContext => mapImportModule(s.import_stmt())
  }

  def mapRhsAssign(x : RhsassignContext) = x match {
    case l : RhsTestlistContext => mapTestlistStarExpr(CollectionKind.Tuple, l.testlist_star_expr())
  }

  def mapFile(parsingBuiltins : Boolean, x : File_inputContext) = {
    val bins = "builtins"
    Suite(
    (if (parsingBuiltins) List() else List(ImportModule(List(bins), bins), ImportAllSymbols(List(bins)))) ++
      ( ImportModule(List("sys"), "sys") ::
        asScala(x.stmt()).map(mapStmt).toList)
    )
  }

}

object MapExpressions {

  def string2num(x : String) : Either[BigInt, Double] = {
    if (x.exists(c => c == '.' || c == '+' || c == '-')) Right(x.toDouble) else {
      val int =
      if (x.length >= 2 && x.substring(0, 2) == "0x")
        x.substring(2, x.length).foldLeft(BigInt(0))((acc, ch) => {
          acc * 16 + (
            if (ch >= '0' && ch <= '9') (ch.toInt - '0'.toInt) else if (ch >= 'a' && ch <= 'f') ch.toInt - 'a'.toInt else if (ch >= 'A' && ch <= 'F') ch.toInt - 'A'.toInt else
              throw new NumberFormatException()
            )
        })
      else if (x.length >= 2 && x.substring(0, 2) == "0o")
        x.substring(2, x.length).foldLeft(BigInt(0))((acc, ch) =>
          acc * 8 + (if (ch >= '0' && ch <= '7') (ch.toInt - '0'.toInt) else throw new NumberFormatException())
        )
      else
        BigInt(x)
      Left(int)
    }
  }

  import Expression._

  def mapDictEltDoubleStar(c : Dict_elt_double_starContext) =
    if (c.expr() != null) Right(mapExpr(c.expr())) else Left((mapTest(c.test(0)), mapTest(c.test(1))))

  def mapAtom(a : AtomContext) : T  = {
    if (a.OPEN_PAREN() != null || a.OPEN_BRACK() != null) {
      val collectionKind = if (a.OPEN_BRACK() != null) CollectionKind.List else CollectionKind.Tuple
      if (a.testlist_comp() != null) {
        mapTestList_comp(collectionKind, a.testlist_comp())
      } else
      if (a.yield_expr() != null) ??? else
      CollectionCons(collectionKind, List())
    } else
    if (a.OPEN_BRACE() != null) {
      // An empty set cannot be constructed with {}; this literal constructs an empty dictionary. https://docs.python.org/3/reference/expressions.html#set-displays
      if (a.dictorsetmaker() == null) DictCons(List()) else
      if (a.dictorsetmaker().testlist_comp() != null) {
        mapTestList_comp(CollectionKind.Set, a.dictorsetmaker().testlist_comp())
      } else {
        if (a.dictorsetmaker().comp_for() != null)
          DictComprehension(mapDictEltDoubleStar(a.dictorsetmaker().dict_elt_double_star(0)), mapCompFor(a.dictorsetmaker().comp_for()))
        else
          DictCons(asScala(a.dictorsetmaker().dict_elt_double_star()).toList.map(mapDictEltDoubleStar))
      }
    } else
    if (a.NUMBER() != null) string2num(a.NUMBER().getText) match {
      case Left(value) => IntLiteral(value)
      case Right(value) => FloatLiteral(value)
    } else
    if (a.NAME() != null) Ident(a.NAME().getText) else
    if (a.TRUE() != null) BoolLiteral(true) else
    if (a.FALSE() != null) BoolLiteral(false) else
    if (a.NONE() != null) NoneLiteral() else
      StringLiteral(asScala(a.STRING()).map(_.getText).mkString(""))
/*    else {  todo: incorrect string literal processing?
      println(a.STRING())
      println(a.yield_expr())
      println(a.testlist_comp())
      println(a.dictorsetmaker(), a.ELLIPSIS(), a.NONE(), a.TRUE(), a.FALSE())
      ???
    }*/
  }

  def mapExprStarExpr(e : Expr_star_exprContext) = if (e.expr() != null) mapExpr(e.expr()) else mapStarExpr(e.star_expr())
  def mapExprList(e : ExprlistContext) = asScala(e.l).map(mapExprStarExpr).toList match {
    case List(x) => x
    case l => CollectionCons(CollectionKind.Tuple, l)
  }

  def mapTest(e : TestContext) : T = e match {
    case e : TestOrTestContext =>
      if (e.test() == null) mapOrTest(e.or_test(0)) else Cond(mapOrTest(e.or_test(1)), mapOrTest(e.or_test(0)), mapTest(e.test()))
    case e : TestLambdefContext => AnonFun(
      if (e.lambdef().varargslist() == null) List() else
        asScala(e.lambdef().varargslist().l).toList.map(_.NAME().getText),
      mapTest(e.lambdef().test())
    )
  }

  def mapTestList_comp(collectionKind: CollectionKind.T, e : Testlist_compContext) : T =
    if (e.test_star_expr().size() > 1) CollectionCons(collectionKind, asScala(e.test_star_expr()).map(mapTestStarExpr).toList) else
    if (e.comp_for() == null) {
      val x = mapTestStarExpr(e.test_star_expr(0))
      if (!e.COMMA().isEmpty || collectionKind != CollectionKind.Tuple)
        CollectionCons(collectionKind, List(x))
      else x
    }
    else {
      assert(e.test_star_expr().size() == 1)
      CollectionComprehension(collectionKind, mapTestStarExpr(e.test_star_expr(0)), mapCompFor(e.comp_for()))
    }

  def mapCompFor(cf : Comp_forContext) : List[Comprehension] = {
    assert(cf.ASYNC() == null)
    ForComprehension(mapExprList(cf.exprlist()), mapOrTest(cf.or_test())) :: mapCompIterNullable(cf.comp_iter())
  }

  def mapCompIterNullable(context: Python3Parser.Comp_iterContext) : List[Comprehension] =
    if (context == null) List() else
    if (context.comp_for() != null) mapCompFor(context.comp_for())
    else mapCompIf(context.comp_if())

  def mapCompIf(context: Python3Parser.Comp_ifContext) : List[Comprehension] =
    IfComprehension(mapTestNocond(context.test_nocond())) :: mapCompIterNullable(context.comp_iter())

  def mapTestNocond(context: Python3Parser.Test_nocondContext) : T =
    if (context.or_test() != null) mapOrTest(context.or_test()) else
    AnonFun(
      if (context.lambdef_nocond().varargslist() == null) List() else
        asScala(context.lambdef_nocond().varargslist().l).toList.map(_.NAME().getText),
      mapTestNocond(context.lambdef_nocond().test_nocond())
    )

  def mapTestList(e : TestlistContext) = (asScala(e.l).map(mapTest).toList) match {
    case List(x) => x
    case l => CollectionCons(CollectionKind.Tuple, l)
  }

  def mapStarExpr(e : Star_exprContext) = Star(mapExpr(e.expr()))

  def mapTestStarExpr(e : Test_star_exprContext) : T = e match {
    case e : TestNotStarContext => mapTest(e.test())
    case e : StarNotTestContext => mapStarExpr(e.star_expr())
  }

  def mapTestlistStarExpr(collectionKind : CollectionKind.T, e : Testlist_star_exprContext) = {
    (asScala(e.l).map(mapTestStarExpr).toList) match {
      case List(x) => x
      case l => CollectionCons(collectionKind, l)
    }
  }

  def mapSubscript(e : Subscript_Context) : T = e match {
    case i : SubIndexContext => mapTest(i.test())
    case slice : SubSliceContext => Slice(
      if (slice.test(0) == null) None else Some(mapTest(slice.test(0))),
      if (slice.test(1) == null) None else Some(mapTest(slice.test(1))),
      if (slice.test(2) == null) None else Some(mapTest(slice.test(2))),
    )
  }

  def mapArgument(e : ArgumentContext) : (Option[String], T) = {
    if (e.STAR() != null) (None, Star(mapTest(e.test(0)))) else
    if (e.POWER() != null) (None, DoubleStar(mapTest(e.test(0)))) else
    if (e.ASSIGN() != null) {
      val Ident(keyword) = mapTest(e.test(0))
      (Some(keyword), mapTest(e.test(1)))
    } else {
      assert(e.comp_for() == null)
      (None, mapTest(e.test(0)))
    }
  }
  def mapArglistNullable(l : ArglistContext) = {
    if (l == null) List() else asScala(l.l).map(mapArgument).toList
  }



  def mapAtomExpr(e : Atom_exprContext) = {
    val whom = mapAtom(e.atom())
    asScala(e.trailer()).foldLeft(whom)((acc, trailer) => trailer match {
      case call : TrailerCallContext => CallIndex(true, acc, mapArglistNullable(call.arglist()))
      case ind  : TrailerSubContext => CallIndex(false, acc,
        asScala(ind.subscriptlist().l).map(x => (None, mapSubscript(x))).toList)
      case field : TrailerFieldContext => Field(acc, field.NAME().getText)
    })
  }

  def mapPower(e : PowerContext) = {
    if (e.factor() != null) Binop(Binops.Pow, mapAtomExpr(e.atom_expr()), mapFactor(e.factor()))
    else mapAtomExpr(e.atom_expr())
  }

  def mapFactor(e : FactorContext) : T = e match {
    case p : FactorPowerContext => mapPower(p.power())
    case u : UnaryContext =>
      val x = mapFactor(u.factor())
      Unop(Unops.ofString(u.op.getText), x)
  }

  def mapNotTest(e : Not_testContext) : T = e match {
    case e : NotNotContext => Unop(Unops.LNot, mapNotTest(e.not_test()))
    case e : NotComparisonContext => {
      val ops0 = asScala(e.comparison().ops)
      val ops = ops0.map(Compops.ofContext).toList
      val args = asScala(e.comparison().args).map(mapExpr).toList
      if (ops.isEmpty) args.head else FreakingComparison(ops, args)
    }
  }

  def mapBinop[TT](args : java.util.List[TT], ops : java.util.List[Token], mapa : TT => T) = {
    val sargs = asScala(args)
    val sops  = asScala(ops)
    val head = sargs.head
    sops.zip(sargs.tail).foldLeft(mapa(head))((acc, x) => {
      Binop(Binops.ofString(x._1.getText), acc, mapa(x._2))
    })
  }

  def mapTerm(e : TermContext) = mapBinop(e.args, e.ops, mapFactor)
  def mapArithExpr(e : Arith_exprContext) = mapBinop(e.args, e.ops, mapTerm)
  def mapShiftExpr(e : Shift_exprContext) = mapBinop(e.args, e.ops, mapArithExpr)
  def mapAndExpr(e : And_exprContext) = mapBinop(e.args, e.ops, mapShiftExpr)
  def mapXorExpr(e : Xor_exprContext) = mapBinop(e.args, e.ops, mapAndExpr)
  def mapExpr(e : ExprContext) = mapBinop(e.args, e.ops, mapXorExpr)
  def mapOrTest(e : Or_testContext) = {
    def inner(l : List[And_testContext]) : T = l match {
      case List(x) => mapAndTest(x)
      case h :: t => LazyLOr(mapAndTest(h), inner(t))
    }
    inner(asScala(e.args).toList)
  }
  def mapAndTest(e : And_testContext) = {
    def inner(l : List[Not_testContext]) : T = l match {
      case List(x) => mapNotTest(x)
      case h :: t => LazyLAnd(mapNotTest(h), inner(t))
    }
    inner(asScala(e.args).toList)
  }

}

object Parse {
  import org.antlr.v4.runtime.CommonTokenStream

  def toFile(t : Statement, dir : String, test : String) = {
    val output = new FileWriter(dir + "/" + test + ".py")
    output.write(PrintPython.printSt(t, ""))
    output.close()
  }

  def parse(path : String, fileName : String) : (Statement, SimplePass.Names) = {
    val test = fileName
    def output(t : Statement, dir : String) = toFile(t, dir, test)

    val fullName = path + "/" + test + ".py"
    println(s"parsing $fullName")
    val input = new FileReader(fullName)

    val inputStream = new ANTLRInputStream(input);
    val lexer = new Python3Lexer(inputStream);
    val tokenStream = new CommonTokenStream(lexer);
    val parser = new Python3Parser(tokenStream);

    val e = parser.file_input()

    val t = MapStatements.mapFile(fileName == "builtins", e)
    output(t, path + "afterParser")

    val t1 = SimplePass.procStatement((a, b) => (a, b))(t, new SimplePass.Names())
    output(t1._1, path + "afterEmptyProcStatement")

    val tsimplifyIf = SimplePass.procStatement(SimplePass.simplifyIf)(t1._1, t1._2)
    output(tsimplifyIf._1, path + "afterSimplifyIf")

    tsimplifyIf
  }

}

object ExplicitHeapTest extends App {
  val name = "trivial"
  val y = Parse.parse("./", name)

  val textractAllCalls = SimplePass.procExprInStatement(
    SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
  Parse.toFile(textractAllCalls._1, "afterExtractAllCalls", name)

  val x = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
  val Suite(theFun :: _) = x._1
  Parse.toFile(theFun, "afterRemoveControlFlow", name)

  val z = ExplicitHeap.explicitStackHeap(theFun, x._2)

  val hacked = Suite(List(
    ImportAllSymbols(List("closureRuntime")),
    Assign(List(Ident("hiddenHeap"), CollectionCons(CollectionKind.List, List()))),
    Assign(List(Ident("theTestPtr"), CallIndex(true, Ident("mkNew"),
      List((None, Ident("hiddenHeap")), (None, NoneLiteral()))))),
    z._1,
    Assign(List(CallIndex(true, Ident("tmpFun"),
      List((None, Ident("hiddenHeap")), (None, DictCons(List()))))))
  ))

  Parse.toFile(hacked, "afterExplicitStackHeap", name)
}
