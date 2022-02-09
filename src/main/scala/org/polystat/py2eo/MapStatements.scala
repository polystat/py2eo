package org.polystat.py2eo

import org.antlr.v4.runtime
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.{ParserRuleContext, Token}
import org.polystat.py2eo.Common.ASTMapperException
import org.polystat.py2eo.Expression.{CallIndex, CollectionCons, CollectionKind, Field, Ident, Parameter, T => ET}
import org.polystat.py2eo.MapExpressions.{
  ga, mapExpression, mapNamedExpression, mapSlices, mapStarExpression, mapStarExpressions,
  mapStarTarget, mapStarTargets, mapTPrimary, mapYieldExpr, toList, toListNullable
}
import org.polystat.py2eo.PythonParser.{
  Annotated_rhsContext, Class_def_rawContext, Compound_stmtContext, DecoratorsContext, Del_targetContext,
  Del_targetsContext, Dotted_nameContext, Elif_stmtContext, Except_blockContext, FileContext, Function_defContext,
  Import_fromContext, Import_nameContext, ParamContext, Param_maybe_defaultContext, Param_no_defaultContext,
  Param_with_defaultContext, Simple_stmtContext, Single_targetContext, Slash_no_defaultContext,
  Slash_with_defaultContext, Star_expressionsContext, StatementContext
}

import scala.collection.immutable.HashMap

object MapStatements {

  def mapFile(c : FileContext) : Statement =
    if (c.statements() == null) Pass(ga(c)) else mapStatements(c.statements())

  def mapStatements(context: PythonParser.StatementsContext) : Statement =
    Suite(toList(context.statement()).map(mapStatement), ga(context))

  def mapStatement(c : StatementContext) : Statement = {
    if (c.compound_stmt() != null) mapCompoundStmt(c.compound_stmt()) else
    { Suite(mapSimpleStmts(c.simple_stmts()), ga(c)) }
  }

  def mapCompoundStmt(c : Compound_stmtContext) : Statement = {
    if (c.function_def() != null) mapFunctionDef(c.function_def()) else
    if (c.if_stmt() != null) mapIfStmt(c.if_stmt()) else
    if (c.class_def() != null) mapClassDef(c.class_def()) else
    if (c.with_stmt() != null) mapWithStmt(c.with_stmt()) else
    if (c.for_stmt() != null) mapForStmt(c.for_stmt()) else
    if (c.try_stmt() != null) mapTryStmt(c.try_stmt()) else
    if (c.while_stmt() != null) mapWhileStmt(c.while_stmt()) else
    if (c.match_stmt() != null) ??? else
    { throw new ASTMapperException("mapCompoundStmt") }
  }

  def mapTryStmt(context: PythonParser.Try_stmtContext) : Try = Try(
    mapBlock(context.block()),
    toListNullable(context.except_block()).map(mapExceptBlock),
    Option(context.else_block()).map(x => mapBlock(x.block())),
    Option(context.finally_block()).map(x => mapBlock(x.block())),
    ga(context)
  )

  def mapExceptBlock(c : Except_blockContext): (Option[(Expression.T, Option[String])], Statement) = (
    Option(c.expression()).map(c1 => (mapExpression(c1), Option(c.NAME()).map(_.getText))),
    mapBlock(c.block())
  )

  def mapForStmt(context: PythonParser.For_stmtContext) : For = {
    For(
      mapStarTargets(context.star_targets()),
      mapStarExpressions2Expression(context.star_expressions()),
      mapBlock(context.block()),
      Option(context.else_block()).map(c => mapBlock(c.block())),
      context.ASYNC() != null,
      ga(context)
    )
  }

  def mapWithStmt(context: PythonParser.With_stmtContext) : With = {
    if (context.TYPE_COMMENT() != null) ???
    val async = context.ASYNC() != null
    val cms = toList(context.with_item()).map(
      c => (mapExpression(c.expression()), Option(c.star_target()).map(mapStarTarget))
    )
    With(cms, mapBlock(context.block()), async, ga(context))
  }

  def mapClassDef(context: PythonParser.Class_defContext) : ClassDef = {
    mapClassDefRaw(mapDecorators(context.decorators()))(context.class_def_raw())
  }

  def mapClassDefRaw(d : Decorators)(c : Class_def_rawContext) : ClassDef = {
    val bases = if (c.arguments() != null) MapExpressions.mapArgs(c.arguments().args()) else List()
    ClassDef(c.NAME().getText, bases, mapBlock(c.block()), d, ga(c))
  }

  def mapElifStmt(c : Elif_stmtContext) : (List[(ET, Statement)], Option[Statement]) = {
    if (c.elif_stmt() != null) {
      val (l, last) = mapElifStmt(c.elif_stmt())
      ((mapNamedExpression(c.named_expression()), mapBlock(c.block())) :: l, last)
    } else {
      (
        List((mapNamedExpression(c.named_expression()), mapBlock(c.block()))),
        Option(c.else_block()).map(x => mapBlock(x.block()))
      )
    }
  }

  def mapIfStmt(context: PythonParser.If_stmtContext) : If = {
    if (context.elif_stmt() != null) {
      val (l, last) = mapElifStmt(context.elif_stmt())
      If((mapNamedExpression(context.named_expression()), mapBlock(context.block())) :: l, last, ga(context))
    } else {
      If(
        List((mapNamedExpression(context.named_expression()), mapBlock(context.block()))),
        Option(context.else_block()).map(x => mapBlock(x.block())), ga(context)
      )
    }
  }

  def mapWhileStmt(context: PythonParser.While_stmtContext) : While =
    While(
      mapNamedExpression(context.named_expression()),
      mapBlock(context.block()),
      Option(context.else_block()).map(x => mapBlock(x.block())),
      ga(context)
    )

  def mapParam(c : ParamContext) : (String, Option[ET]) =
    (c.NAME().getText, Option(c.annotation()).map(x => mapExpression(x.expression())))

  def mapParamMaybeDefault(kind : ArgKind.T)(c : Param_maybe_defaultContext) : Parameter = {
    val (name, typeAnn) = mapParam(c.param())
    val default = Option(c.default_assignment()).map(x => mapExpression(x.expression()))
    Parameter(name, kind, typeAnn, default, ga(c))
  }

  def mapParamWithDefault(kind : ArgKind.T)(c : Param_with_defaultContext) : Parameter = {
    val (name, typeAnn) = mapParam(c.param())
    val default = Some(mapExpression(c.default_assignment().expression()))
    Parameter(name, kind, typeAnn, default, ga(c))
  }

  def mapParamNoDefault(kind : ArgKind.T)(c : Param_no_defaultContext) : Parameter = {
    val (name, typeAnn) = mapParam(c.param())
    Parameter(name, kind, typeAnn, None, ga(c))
  }

  def mapSlashNoDefault(c : Slash_no_defaultContext) : List[Parameter] = {
    if (c == null) List() else {
      val l = toListNullable(c.param_no_default())
      l.map(mapParamNoDefault(ArgKind.Positional))
    }
  }

  def mapSlashWithDefault(c : Slash_with_defaultContext) : List[Parameter] = {
    if (c == null) List() else {
      toListNullable(c.param_no_default()).map(mapParamNoDefault(ArgKind.Positional)) ++
        toListNullable(c.param_with_default()).map(mapParamWithDefault(ArgKind.Positional))
    }
  }

  def mapFunctionDefRaw(decorators : Decorators)(context: PythonParser.Function_def_rawContext) : FuncDef = {
    if (context.func_type_comment() != null) ???
    val body = mapBlock(context.block())
    val returnType = Option(context.expression()).map(mapExpression)
    val (params, otherPositional, otherKeywords) = if (context.params() != null) {
      val params = context.params().parameters()
      val snd = mapSlashNoDefault(params.slash_no_default())
      val swd = mapSlashWithDefault(params.slash_with_default())
      val pnd = toListNullable(params.param_no_default()).map(mapParamNoDefault(ArgKind.PosOrKeyword))
      val pwd = toListNullable(params.param_with_default()).map(mapParamWithDefault(ArgKind.PosOrKeyword))
      val otherPositional = Option(params.star_etc()).flatMap(
        x => Option(x.param_no_default()).map(x => mapParam(x.param()))
      )
      val otherKeywords = Option(params.star_etc()).flatMap(
        x => Option(x.kwds()).map(x => mapParam(x.param_no_default().param()))
      )
      val rest = Option(params.star_etc()).toList.flatMap(
        x => toListNullable(x.param_maybe_default()).map(mapParamMaybeDefault(ArgKind.Keyword))
      )
      (snd ++ swd ++ pnd ++ pwd ++ rest, otherPositional, otherKeywords)
    } else {
      (List(), None, None)
    }
    FuncDef(
      context.NAME().getText, params, otherPositional, otherKeywords,
      returnType, body, decorators, HashMap(), context.ASYNC() != null, ga(context)
    )
  }

  def mapDecorators(c : DecoratorsContext) : Decorators = {
    Decorators(if (c == null) List() else toList(c.named_expression()).map(MapExpressions.mapNamedExpression))
  }

  def mapFunctionDef(c : Function_defContext) : FuncDef =
    mapFunctionDefRaw(mapDecorators(c.decorators()))(c.function_def_raw())

  def mapBlock(context: PythonParser.BlockContext) : Statement = {
    Suite (
      if (context.simple_stmts() != null) {
        mapSimpleStmts(context.simple_stmts())
      } else {
        toList(context.statements().statement()).map(mapStatement)
      },
      ga(context)
    )
  }

  def mapSimpleStmts(context: PythonParser.Simple_stmtsContext) : List[Statement] =
    toList(context.simple_stmt()).map(mapSimpleStmt)

  def mapSimpleStmt(c : Simple_stmtContext) : Statement = {
    if (c.assignment() != null) mapAssignment(c.assignment()) else
    if (c.star_expressions() != null) {
      val l = MapExpressions.mapStarExpressions(c.star_expressions())
      Assign(List(
        if (c.star_expressions().COMMA().size() > 1) CollectionCons(CollectionKind.Tuple, l, ga(c)) else l.head
      ), ga(c))
    } else
    if (c.return_stmt() != null) mapReturnStmt(c.return_stmt()) else
    if (c.import_stmt() != null) mapImportStmt(c.import_stmt()) else
    if (c.raise_stmt() != null) mapRaiseStmt(c.raise_stmt()) else
    if (c.PASS() != null) Pass(ga(c)) else
    if (c.del_stmt() != null) mapDelStmt(c.del_stmt()) else
    if (c.yield_stmt() != null) mapYieldStmt(c.yield_stmt()) else
    if (c.assert_stmt() != null) mapAssertStmt(c.assert_stmt()) else
    if (c.BREAK() != null) Break(ga(c)) else
    if (c.CONTINUE() != null) Continue(ga(c)) else
    if (c.global_stmt() != null) mapGlobalStmt(c.global_stmt()) else
    if (c.nonlocal_stmt() != null) mapNonlocalStmt(c.nonlocal_stmt()) else
    { throw new ASTMapperException("mapSimpleStmt") }
  }

  def mapDelTargets(c : Del_targetsContext) : List[ET] = toList(c.del_target()).map(mapDelTarget)

  def mapDelTarget(c : Del_targetContext) : ET = {
    if (c.NAME() != null) Field(mapTPrimary(c.t_primary()), c.NAME().getText, ga(c)) else
    if (c.slices() != null) CallIndex(false, mapTPrimary(c.t_primary()), List((None, mapSlices(c.slices()))), ga(c)) else
    { mapDelTAtom(c.del_t_atom()) }
  }

  def mapDelTAtom(context: PythonParser.Del_t_atomContext) : ET = {
    if (context.NAME() != null) Ident(context.NAME().getText, ga(context)) else
    if (context.del_target() != null) mapDelTarget(context.del_target()) else {
      CollectionCons(
        if (context.OPEN_BRACK() != null) CollectionKind.List else CollectionKind.Tuple,
        if (context.del_targets() != null) mapDelTargets(context.del_targets()) else List(),
        ga(context)
      )
    }
  }

  def mapDelStmt(context: PythonParser.Del_stmtContext) : Del =
    Del(CollectionCons(CollectionKind.Tuple, mapDelTargets(context.del_targets()), ga(context)), ga(context))

  def mapYieldStmt(context: PythonParser.Yield_stmtContext) : Assign =
    Assign(List(mapYieldExpr(context.yield_expr())), ga(context))

  def mapAssertStmt(context: PythonParser.Assert_stmtContext) : Assert = {
    val l = toList(context.expression()).map(mapExpression)
    Assert(l.head, if (l.length == 1) None else Some(l(1)), ga(context))
  }

  def mapGlobalStmt(context: PythonParser.Global_stmtContext) : Global =
    Global(toList(context.NAME()).map(_.getText), ga(context))

  def mapNonlocalStmt(context: PythonParser.Nonlocal_stmtContext) : NonLocal =
    NonLocal(toList(context.NAME()).map(_.getText), ga(context))

  def mapRaiseStmt(context: PythonParser.Raise_stmtContext) : Raise = {
    val l = toListNullable(context.expression()).map(mapExpression)
    l match {
      case List() => Raise(None, None, ga(context))
      case List(what) => Raise(Some(what), None, ga(context))
      case List(what, from) => Raise(Some(what), Some(from), ga(context))
    }
  }

  def mapDottedName(c : Dotted_nameContext) : List[String] = {
    if (c == null) List() else
    if (c.dotted_name() != null) mapDottedName(c.dotted_name()) :+ c.NAME().getText else {
      List(c.NAME().getText)
    }
  }

  def mapImportName(c : Import_nameContext) : Statement = {
    Suite(
      toList(c.dotted_as_names().dotted_as_name()).map(
        c => ImportModule(mapDottedName(c.dotted_name()), Option(c.NAME()).map(_.getText), ga(c))
      ),
      ga(c)
    )
  }

  def mapImportFrom(c : Import_fromContext) : Statement = {
    val nprefixDots = c.ELLIPSIS().size() * 3 + c.DOT().size()
    val from = List.fill(nprefixDots)("") ++ mapDottedName(c.dotted_name())
    if (c.import_from_targets().STAR() != null) ImportAllSymbols(from, ga(c)) else {
      Suite(
        toList(c.import_from_targets().import_from_as_names().import_from_as_name()).map(
          c => ImportSymbol(from, c.NAME(0).getText, Option(c.NAME(1)).map(_.getText), ga(c))
        ),
        ga(c)
      )
    }
  }

  def mapImportStmt(context: PythonParser.Import_stmtContext) : Statement = {
    if (context.import_from() != null) mapImportFrom(context.import_from()) else mapImportName(context.import_name())
  }

  def mapReturnStmt(context: PythonParser.Return_stmtContext) : Return = {
    Return(Option(context.star_expressions()).map(mapStarExpressions2Expression), ga(context))
  }

  def mapStarExpressions2Expression(c : Star_expressionsContext) : ET = {
    if (c.COMMA().size() == 0) mapStarExpression(c.star_expression(0)) else
    { CollectionCons(CollectionKind.Tuple, mapStarExpressions(c), ga(c)) }
  }

  def mapAnnotatedRhs(c : Annotated_rhsContext) : ET = {
    if (c.yield_expr() != null) {
      mapYieldExpr(c.yield_expr())
    } else {
      mapStarExpressions2Expression(c.star_expressions())
    }
  }

  def mapSingleSubscriptAttributeTarget(context: PythonParser.Single_subscript_attribute_targetContext) : ET = {
    if (context.NAME() != null) Field(mapTPrimary(context.t_primary()), context.NAME().getText, ga(context)) else
    { CallIndex(false, mapTPrimary(context.t_primary()), List((None, mapSlices(context.slices()))), ga(context)) }
  }

  def mapSingleTarget(c : Single_targetContext) : ET = {
    if (c.single_subscript_attribute_target() != null) mapSingleSubscriptAttributeTarget(c.single_subscript_attribute_target()) else
    if (c.NAME() != null) Ident(c.NAME().getText, ga(c)) else
    { mapSingleTarget(c.single_target()) }
  }

  def mapAssignment(context: PythonParser.AssignmentContext) : Statement = {
    if (context.NAME() != null) {
      AnnAssign(
        Ident(context.NAME().getText, new GeneralAnnotation(context.NAME().getSymbol)),
        mapExpression(context.expression()),
        Option(context.annotated_rhs()).map(mapAnnotatedRhs),
        ga(context)
      )
    } else
    if (context.augassign() != null) {
      AugAssign(
        AugOps.ofString(context.augassign().getText),
        mapSingleTarget(context.single_target()),
        if (context.yield_expr() != null) mapYieldExpr(context.yield_expr()) else mapStarExpressions2Expression(context.star_expressions()),
        ga(context)
      )
    } else
    if (context.star_targets().size() > 0) {
      val l = toList(context.star_targets()).map(mapStarTargets)
      val rhs = if (context.yield_expr() != null) {
        mapYieldExpr(context.yield_expr())
      } else {
        mapStarExpressions2Expression(context.star_expressions())
      }
      Assign(l :+ rhs, ga(context))
    } else {
      val lhs = if (context.single_target() != null) {
        mapSingleTarget(context.single_target())
      } else {
        mapSingleSubscriptAttributeTarget(context.single_subscript_attribute_target())
      }
      AnnAssign(
        lhs, mapExpression(context.expression()),
        Option(context.annotated_rhs()).map(mapAnnotatedRhs),
        ga(context)
      )
    }
  }

}
