package org.polystat.py2eo

import org.polystat.py2eo.Expression.{CollectionKind, Ident}
import org.polystat.py2eo.Python3Parser._

import scala.collection.JavaConverters.asScala
import scala.collection.immutable.HashMap

import Expression.{T => ET}
import Expression.CallIndex

object MapStatements {

  import MapExpressions._

  def mapDottedName(x: Dotted_nameContext): List[String] = asScala(x.l).toList.map(x => x.getText)

  def mapImportModule(s: Import_stmtContext): Statement = s match {
    case s: ImportNameContext =>
      val l = asScala(s.import_name().dotted_as_names().l).map(x => {
        val what = mapDottedName(x.dotted_name())
        val as = Option(x.NAME()).map(_.getText)
        ImportModule(what, as, new GeneralAnnotation(x))
      }).toList
      Suite(l, new GeneralAnnotation(s))

    case s: ImportFromContext =>
      val from = mapDottedName(s.import_from().dotted_name())
      if (s.import_from().import_as_names() == null) {
        ImportAllSymbols(from, new GeneralAnnotation(s))
      } else {
        val symbols = asScala(s.import_from().import_as_names().l).toList
        Suite(symbols.map(x => ImportSymbol(from, x.what.getText,
          if (x.aswhat != null) x.aswhat.getText else x.what.getText, new GeneralAnnotation(x)
        )), new GeneralAnnotation(s))
      }
  }

  def mapNullableSuite(s: SuiteContext): Statement =
    if (s == null) {
      Pass(new GeneralAnnotation(s))
    } else {
      s match {
        case b: SuiteBlockStmtsContext => Suite(asScala(b.l).map(mapStmt).toList, new GeneralAnnotation(b))
        case s: SuiteSimpleStmtContext => mapSimpleStmt(s.simple_stmt())
      }
    }

  def mapDecorators(c: DecoratorsContext): Decorators = Decorators(
    asScala(c.decorator()).toList.map(x => {
      val dname = asScala(x.dotted_name().l).toList
      // todo: decorators in the parser do not conform to the newest reference:
      // it is an assignment expression now https://docs.python.org/3/reference/compound_stmts.html#grammar-token-decorators
      val d = dname.tail.foldLeft(Ident(dname.head.getText, new GeneralAnnotation(dname.head)): ET)((acc, token) =>
        Expression.Field(acc, token.getText, new GeneralAnnotation(token)))
      if (x.OPEN_PAREN() != null) {
        CallIndex(isCall = true, d, mapArglistNullable(x.arglist()), new GeneralAnnotation(x))
      }
      else {
        d
      }
    }
    )
  )

  def mapTfparg(kind: ArgKind.T, c: TfpargContext): Expression.Parameter = {
    val default = Option(c.test()).map(mapTest)
    Expression.Parameter(c.tfpdef().NAME().getText, kind, Option(c.tfpdef().test()).map(mapTest), default, new GeneralAnnotation(c))
  }

  def mapTypedargslistNopos(c: Typedargslist_noposContext): (List[Expression.Parameter], Option[(String, Option[ET])], Option[(String, Option[ET])]) = {
    (asScala(c.l).toList.map(x => mapTfparg(ArgKind.Keyword, x)),
      // todo: the next line may be wrong, it may break the method with which python forces all args to be keyword only (4.8.3 in tutorial)
      Option(c.tfptuple().tfpdef()).map(x => (x.NAME().getText, Option(x.test()).map(mapTest))),
      Option(c.tfpdict()).map(x => (x.tfpdef().NAME().getText, Option(x.tfpdef().test()).map(mapTest)))
    )
  }

  def mapTypedargslist(c: TypedargslistContext): (List[Expression.Parameter], Option[(String, Option[ET])], Option[(String, Option[ET])]) = {
    if (c == null) (List(), None, None) else {
      val tail =
        if (c.typedargslist_nopos() != null) mapTypedargslistNopos(c.typedargslist_nopos()) else if (c.tfpdict() != null) {
          (List(), None, Some(c.tfpdict().tfpdef().NAME().getText, Option(c.tfpdict().tfpdef().test()).map(mapTest)))
        } else {
          (List(), None, None)
        }
      val l = asScala(c.l).toList
      val (posOnly, others) = l.splitAt(l.indexWhere(c => c.tfpdef().DIV() != null))
      val (posAndKword, kwordOnly) = others.splitAt({
        val pos = others.indexWhere(c => c.tfpdef().STAR() != null)
        if (-1 == pos) others.length else pos
      })

      def f(kind: ArgKind.T, l: List[TfpargContext]) = l.flatMap(x =>
        if (x.tfpdef().NAME() == null) List() else List(mapTfparg(kind, x)))

      (
        f(ArgKind.Positional, posOnly) ++
          f(ArgKind.PosOrKeyword, posAndKword) ++
          f(ArgKind.Keyword, kwordOnly) ++
          tail._1, tail._2, tail._3
      )
    }
  }

  def mapFuncDef(s: FuncdefContext, decorators: Decorators, isAsync: Boolean): FuncDef = {
    val z = mapTypedargslist(s.parameters().typedargslist())
    FuncDef(
      s.NAME().getText, z._1, z._2, z._3, Option(s.test()).map(mapTest),
      mapNullableSuite(s.suite()), decorators, HashMap(), isAsync, new GeneralAnnotation(s)
    )
  }

  def mapClassDef(s: ClassdefContext, decorators: Decorators): ClassDef = {
    ClassDef(
      s.NAME().getText, mapArglistNullable(s.arglist()),
      mapNullableSuite(s.suite()), decorators, new GeneralAnnotation(s)
    )
  }

  def mapFor(s: For_stmtContext, isAsync: Boolean): For = {
    For(
      mapExprList(s.exprlist()), mapTestList(s.testlist()),
      mapNullableSuite(s.body), mapNullableSuite(s.eelse), isAsync, new GeneralAnnotation(s)
    )
  }

  def mapIf(s: If_stmtContext): If = {
    If(
      asScala(s.conds).map(mapAssignmentExpression).zip(asScala(s.bodies).map(mapNullableSuite)).toList,
      mapNullableSuite(s.eelse), new GeneralAnnotation(s)
    )
  }

  def mapWith(s: With_stmtContext, isAsync: Boolean): Statement = {
    asScala(s.l).toList.foldRight(mapNullableSuite(s.suite()))(
      (x, s) => With(mapTest(x.test()), Option(x.expr()).map(mapExpr), s, isAsync, s.ann.pos)
    )
  }

  def mapStmt(s: StmtContext): Statement = s match {
    case s: StmtSimpleContext => mapSimpleStmt(s.simple_stmt())
    case s: StmtCompoundContext => s.compound_stmt() match {
      case s: CompIfContext => mapIf(s.if_stmt())
      case s: CompWhileContext =>
        While(
          mapAssignmentExpression(s.while_stmt().cond),
          mapNullableSuite(s.while_stmt().body),
          mapNullableSuite(s.while_stmt().eelse),
          new GeneralAnnotation(s)
        )
      case s: CompForContext => mapFor(s.for_stmt(), isAsync = false)
      case s: CompTryContext =>
        val t = s.try_stmt()
        val es = asScala(t.except_clause()).toList.zip(asScala(t.exceptSuites).toList)
          .map(ex => (if (ex._1.test() == null) None else Some((mapTest(ex._1.test()), Option(ex._1.NAME()).map(_.getText))),
            mapNullableSuite(ex._2)))
        Try(
          mapNullableSuite(t.trySuite), es, mapNullableSuite(t.elseSuite),
          if (t.finallySuite.size() == 0) {
            Pass(new GeneralAnnotation(s))
          } else {
            mapNullableSuite(t.finallySuite.get(0))
          }
          , new GeneralAnnotation(s)
        )
      case s: CompWithContext => mapWith(s.with_stmt(), isAsync = false)
      case s: CompFuncDefContext => mapFuncDef(s.funcdef(), Decorators(List()), isAsync = false)
      case s: CompClassDefContext => mapClassDef(s.classdef(), Decorators(List()))
      case s: CompDecoratedContext =>
        val s1 = s.decorated()
        val decorators = mapDecorators(s1.decorators())
        if (s1.classdef() != null) mapClassDef(s1.classdef(), decorators) else if (s1.funcdef() != null) mapFuncDef(s1.funcdef(), decorators, isAsync = false) else if (s1.async_funcdef() != null) mapFuncDef(s1.async_funcdef().funcdef(), decorators, isAsync = true) else {
          throw new AssertionError("nothing after decorators. This must not be parseable with the golden python compiler")
        }
      case s: CompAsyncContext =>
        if (s.async_stmt().with_stmt() != null) mapWith(s.async_stmt().with_stmt(), isAsync = true) else if (s.async_stmt().for_stmt() != null) mapFor(s.async_stmt().for_stmt(), isAsync = true) else if (s.async_stmt().funcdef() != null) mapFuncDef(s.async_stmt().funcdef(), Decorators(List()), isAsync = true) else {
          ???
        }
    }
  }

  def mapSimpleStmt(context: Simple_stmtContext): Suite =
    Suite(asScala(context.l).map(mapSmallStmt).toList, new GeneralAnnotation(context))

  def mapSmallStmt(s: Small_stmtContext): Statement = s match {
    case s: SmallNonLocalContext => NonLocal(asScala(s.nonlocal_stmt().l).map(_.getText).toList, new GeneralAnnotation(s))
    case s: SmallGlobalContext => Global(asScala(s.global_stmt().l).map(_.getText).toList, new GeneralAnnotation(s))
    case s: SmallAssertContext => Assert(asScala(s.assert_stmt().test()).toList.map(mapTest), new GeneralAnnotation(s))
    case s: SmallExprContext =>
      val lhs = mapTestlistStarExpr(CollectionKind.Tuple, s.expr_stmt().testlist_star_expr())
      s.expr_stmt().expr_stmt_right() match {
        case r: AugAssignLabelContext =>
          val op = AugOps.ofContext(r.augassign())
          AugAssign(op, lhs, mapTestList(r.testlist()), new GeneralAnnotation(r))
        case a: JustAssignContext =>
          val rhss = asScala(a.l).map(mapRhsAssign).toList
          Assign(lhs :: rhss, new GeneralAnnotation(a))
        case x: AnnAssignLabelContext =>
          AnnAssign(lhs, mapTest(x.annassign().ann), Option(x.annassign().value).map(mapTest), new GeneralAnnotation(x))
      }
    case s: SmallDelContext => Del(mapExprList(s.del_stmt().exprlist()), new GeneralAnnotation(s))
    case s: SmallPassContext => Pass(new GeneralAnnotation(s))
    case s: SmallFlowContext => s.flow_stmt() match {
      case _: FlowBreakContext => Break(new GeneralAnnotation(s))
      case _: FlowContinueContext => Continue(new GeneralAnnotation(s))
      case r: FlowReturnContext =>
        Return(Option(r.return_stmt().testlist_star_expr()).map(mapTestlistStarExpr(CollectionKind.Tuple, _)),
          new GeneralAnnotation(r))
      case r: FlowRaiseContext =>
        val tests = asScala(r.raise_stmt().test()).toList.map(mapTest)
        Raise(tests.headOption, tests.lift(1), new GeneralAnnotation(r))
      case y: FlowYieldContext =>
        Assign(List(mapYieldExpr(y.yield_stmt().yield_expr())), new GeneralAnnotation(y))
    }
    case s: SmallImportContext => mapImportModule(s.import_stmt())
  }

  def mapRhsAssign(x: RhsassignContext): ET = x match {
    case l: RhsTestlistContext => mapTestlistStarExpr(CollectionKind.Tuple, l.testlist_star_expr())
    case y: RhsYieldContext => mapYieldExpr(y.yield_expr())
  }

  def mapFile(parsingBuiltins: Boolean, x: File_inputContext): Suite = {
    Suite(
      if (parsingBuiltins) List() else asScala(x.stmt()).map(mapStmt).toList,
      new GeneralAnnotation(x)
    )
  }

}
