package org.polystat.py2eo

import org.antlr.v4.runtime
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.{ParserRuleContext, Token}
import org.polystat.py2eo.PythonParser._

import scala.collection.JavaConverters.asScala

object MapExpressions1 {

  import MapExpressions.string2num

  import Expression._

  private def ga(c : ParserRuleContext) = new GeneralAnnotation(c)
  private def toList[T](l : java.util.List[T]) = asScala(l).toList
  private def toListNullable[T](l : java.util.List[T]) = if (l == null) List() else toList(l)

  private def mapBinop[TT](args: java.util.List[TT], ops : java.util.List[TerminalNode], mapa: TT => T): T = {
    val sargs = asScala(args)
    val sops = asScala(ops).toList.map(_.getSymbol)
    val head = sargs.head
    sops.zip(sargs.tail).foldLeft(mapa(head))((acc, x) => {
      Binop(Binops.ofString(x._1.getText), acc, mapa(x._2), new GeneralAnnotation(x._1))
    }
    )
  }

  def mapLazyLogic[TT <: runtime.ParserRuleContext](args : java.util.List[TT], cons : (T, T, GeneralAnnotation) => T, mapa : TT => T) : T = {
    def inner(l : List[TT]) : T =
      if (l.length == 1) mapa(l.head) else
      cons(inner(l.init), mapa(l.last), ga(l.last))
    inner(toList(args))
  }

  def mapDisjunction(context: PythonParser.DisjunctionContext) : T =
    mapLazyLogic(context.conjunction(), LazyLOr, mapConjunction)

  def mapConjunction(c : ConjunctionContext) : T =
    mapLazyLogic(c.inversion(), LazyLAnd, mapInversion)

  def mapInversion(c : InversionContext) : T =
    if (c.NOT() != null) mapComparison(c.comparison()) else
    Unop(Unops.LNot, mapInversion(c.inversion()), ga(c))

  def mapComparison(context: PythonParser.ComparisonContext) : T = {
    if (context.compare_op_bitwise_or_pair() == null) {
      mapBitwiseOr(context.bitwise_or())
    } else {
      val l = toList(context.compare_op_bitwise_or_pair())
      val l1 = l.map(
        c => {
          if (c.eq_bitwise_or() != null) (Compops.Eq, mapBitwiseOr(c.eq_bitwise_or().bitwise_or())) else
          if (c.noteq_bitwise_or() != null) (Compops.Neq, mapBitwiseOr(c.noteq_bitwise_or().bitwise_or())) else
          if (c.lte_bitwise_or() != null) (Compops.Le, mapBitwiseOr(c.lte_bitwise_or().bitwise_or())) else
          if (c.lt_bitwise_or() != null) (Compops.Lt, mapBitwiseOr(c.lt_bitwise_or().bitwise_or())) else
          if (c.gte_bitwise_or() != null) (Compops.Ge, mapBitwiseOr(c.gte_bitwise_or().bitwise_or())) else
          if (c.gt_bitwise_or() != null) (Compops.Gt, mapBitwiseOr(c.gt_bitwise_or().bitwise_or())) else
          if (c.notin_bitwise_or() != null) (Compops.NotIn, mapBitwiseOr(c.notin_bitwise_or().bitwise_or())) else
          if (c.in_bitwise_or() != null) (Compops.In, mapBitwiseOr(c.in_bitwise_or().bitwise_or())) else
          if (c.isnot_bitwise_or() != null) (Compops.IsNot, mapBitwiseOr(c.isnot_bitwise_or().bitwise_or())) else
          if (c.is_bitwise_or() != null) (Compops.Is, mapBitwiseOr(c.is_bitwise_or().bitwise_or())) else
          throw new RuntimeException("Unsupported comparison operation?")
        }
      )
      FreakingComparison(l1.map(_._1), mapBitwiseOr(context.bitwise_or()) :: l1.map(_._2), ga(context))
    }
  }

  def mapBitwiseOr(c : Bitwise_orContext) : T = {
    if (c.bitwise_or() != null) Binop(Binops.Or, mapBitwiseOr(c.bitwise_or()), mapBitwiseXOr(c.bitwise_xor()), ga(c)) else
    mapBitwiseXOr(c.bitwise_xor())
  }

  def mapBitwiseXOr(context: PythonParser.Bitwise_xorContext) : T = {
    if (context.bitwise_xor() != null) {
      Binop(Binops.Xor, mapBitwiseXOr(context.bitwise_xor()), mapBitwiseAnd(context.bitwise_and()), ga(context))
    } else {
      mapBitwiseAnd(context.bitwise_and())
    }
  }

  def mapBitwiseAnd(context: PythonParser.Bitwise_andContext) : T = {
    if (context.shift_expr() != null) {
      Binop(Binops.And, mapBitwiseAnd(context.bitwise_and()), mapShiftExpr(context.shift_expr()), ga(context))
    } else {
      mapShiftExpr(context.shift_expr())
    }
  }

  def mapShiftExpr(context: PythonParser.Shift_exprContext) : T = {
    if (context.LEFTSHIFT() != null) {
      Binop(Binops.Shl, mapShiftExpr(context.shift_expr()), mapSum(context.sum()), ga(context))
    } else
    if (context.RIGHTSHIFT() != null) {
      Binop(Binops.Shr, mapShiftExpr(context.shift_expr()), mapSum(context.sum()), ga(context))
    } else {
      mapSum(context.sum())
    }
  }

  def mapSum(context: PythonParser.SumContext) : T = {
    if (context.PLUS() != null) {
      Binop(Binops.Plus, mapSum(context.sum()), mapTerm(context.term()), ga(context))
    } else
    if (context.MINUS() != null) {
      Binop(Binops.Minus, mapSum(context.sum()), mapTerm(context.term()), ga(context))
    } else {
      mapTerm(context.term())
    }
  }

  def mapTerm(context: PythonParser.TermContext) : T = {
    if (context.STAR() != null) {
      Binop(Binops.Mul, mapTerm(context.term()), mapFactor(context.factor()), ga(context))
    } else
    if (context.SLASH() != null) {
      Binop(Binops.Div, mapTerm(context.term()), mapFactor(context.factor()), ga(context))
    } else
    if (context.DOUBLESLASH() != null) {
      Binop(Binops.FloorDiv, mapTerm(context.term()), mapFactor(context.factor()), ga(context))
    } else
    if (context.PERCENT() != null) {
      Binop(Binops.Mod, mapTerm(context.term()), mapFactor(context.factor()), ga(context))
    } else
    if (context.AT() != null) {
      Binop(Binops.At, mapTerm(context.term()), mapFactor(context.factor()), ga(context))
    } else {
      mapFactor(context.factor())
    }
  }

  def mapFactor(context: PythonParser.FactorContext) : T = {
    if (context.PLUS() != null) Unop(Unops.Plus, mapFactor(context.factor()), ga(context)) else
    if (context.MINUS() != null) Unop(Unops.Minus, mapFactor(context.factor()), ga(context)) else
    if (context.TILDE() != null) Unop(Unops.Neg, mapFactor(context.factor()), ga(context)) else
    mapPower(context.power())
  }

  def mapPower(context: PythonParser.PowerContext) : T = {
    if (context.factor() != null) {
      Binop(Binops.Pow, mapAwaitPrimary(context.await_primary()), mapFactor(context.factor()), ga(context))
    } else {
      mapAwaitPrimary(context.await_primary())
    }
  }

  def mapAwaitPrimary(context: PythonParser.Await_primaryContext) : T = {
    if (context.AWAIT() != null) Await(mapPrimary(context.primary()), ga(context)) else mapPrimary(context.primary())
  }

  def mapPrimary(context: PythonParser.PrimaryContext) : T = {
    if (context.NAME() != null) Field(mapPrimary(context.primary()), context.NAME().getText, ga(context)) else
    if (context.genexp() != null) ??? else
    if (context.arguments() != null) {
      CallIndex(true, mapPrimary(context.primary()), mapArgs(context.arguments().args()), ga(context))
    } else
    if (context.slices() != null) {
      CallIndex(false, mapPrimary(context.primary()), mapSlices(context.slices()).map(x => (None, x)), ga(context))
    } else
    mapAtom(context.atom())
  }

  def mapAtom(context: PythonParser.AtomContext) : T = {
    if (context.NAME() != null) Ident(context.NAME().getText, ga(context)) else
    if (context.TRUE() != null) BoolLiteral(true, ga(context)) else
    if (context.FALSE() != null) BoolLiteral(false, ga(context)) else
    if (context.NONE() != null) NoneLiteral(ga(context)) else
    if (context.strings() != null) StringLiteral(toList(context.strings().STRING()).map(_.getText), ga(context)) else
    if (context.NUMBER() != null) MapExpressions.string2num(context.NUMBER().getText, context) else
    if (context.tuple() != null) mapTuple(context.tuple()) else
    if (context.group() != null) {
      if (context.group().yield_expr() != null) mapYieldExpr(context.group().yield_expr()) else
      if (context.group().named_expression() != null) mapNamedExpression(context.group().named_expression()) else
      throw new RuntimeException("context.group problem")
    } else
    if (context.list() != null) mapList(context.list()) else
    if (context.listcomp() != null) mapListcomp(context.listcomp()) else
    if (context.dict() != null) mapDict(context.dict()) else
    if (context.set() != null) mapSet(context.set()) else
    if (context.dictcomp() != null) mapDictcomp(context.dictcomp()) else
    if (context.setcomp() != null) mapSetcomp(context.setcomp()) else
    if (context.ELLIPSIS() != null) EllipsisLiteral(ga(context)) else
    throw new RuntimeException("wrong alternative in mapAtom")
  }

  def mapYieldExpr(context: PythonParser.Yield_exprContext) : T = {
    if (context.FROM() != null) YieldFrom(mapExpression(context.expression()), ga(context)) else
    if (context.star_expressions() == null) Yield(None, ga(context)) else {
      val l = mapStarExpressions(context.star_expressions())
      Yield(Some(CollectionCons(CollectionKind.Tuple, l, ga(context))), ga(context))
    }
  }

  def mapStarExpressions(context: PythonParser.Star_expressionsContext) : List[T] = {
    toList(context.l).map(mapStarExpression)
  }

  def mapStarExpression(c : Star_expressionContext) : T = {
    if (c.bitwise_or() != null) Star(mapBitwiseOr(c.bitwise_or()), ga(c)) else
    mapExpression(c.expression())
  }

  def mapSetcomp(context: PythonParser.SetcompContext) : CollectionComprehension = {
    CollectionComprehension(
      CollectionKind.Set, mapNamedExpression(context.named_expression()),
      mapForIfClauses(context.for_if_clauses()), ga(context)
    )
  }

  def mapDictcomp(context: PythonParser.DictcompContext) : DictComprehension = {
    DictComprehension(Left(mapKvpair(context.kvpair())), mapForIfClauses(context.for_if_clauses()), ga(context))
  }

  def mapDict(context: PythonParser.DictContext) : DictCons = {
    DictCons(toList(context.double_starred_kvpairs().double_starred_kvpair()).map(mapDoubleStarredKvpair), ga(context))
  }

  def mapDoubleStarredKvpair(c : Double_starred_kvpairContext) : DictEltDoubleStar = {
    if (c.bitwise_or() != null) Right(mapBitwiseOr(c.bitwise_or())) else
    Left(mapKvpair(c.kvpair()))
  }

  def mapKvpair(context: PythonParser.KvpairContext) : (T, T) =
    (mapExpression(context.expression(0)), mapExpression(context.expression(1)))

  def mapTuple(context: PythonParser.TupleContext) : T = {
    val l = if (context.star_named_expressions() == null) List() else mapStarNamedExpressions(context.star_named_expressions())
    val head = mapStarNamedExpression(context.star_named_expression())
    CollectionCons(CollectionKind.Tuple, head :: l, ga(context))
  }

  def mapSet(context: PythonParser.SetContext) : CollectionCons = {
    CollectionCons(CollectionKind.Set, mapStarNamedExpressions(context.star_named_expressions()), ga(context))
  }

  def mapList(context: PythonParser.ListContext) : T = {
    val l = if (context.star_named_expressions() != null) {
      mapStarNamedExpressions(context.star_named_expressions())
    } else {
      List()
    }
    CollectionCons(CollectionKind.List, l, ga(context))
  }

  def mapListcomp(context: PythonParser.ListcompContext) : T = {
    CollectionComprehension(
      CollectionKind.List, mapNamedExpression(context.named_expression()),
      mapForIfClauses(context.for_if_clauses()), ga(context)
    )
  }

  def mapForIfClauses(context: PythonParser.For_if_clausesContext) : List[Comprehension] = {
    toList(context.for_if_clause()).flatMap(mapForIfClause)
  }

  def mapForIfClause(c : For_if_clauseContext) : List[Comprehension] = {
    val isAsync = c.ASYNC() != null
    val ffor = ForComprehension(mapStarTargets(c.star_targets()), mapDisjunction(c.disjunction(0)), isAsync)
    val iff = if (c.IF() == null) List() else List(IfComprehension(mapDisjunction(c.disjunction(1))))
    ffor :: iff
  }

  def mapStarTargets(context: PythonParser.Star_targetsContext) : CollectionCons = {
    CollectionCons(CollectionKind.Tuple, toList(context.l).map(mapStarTarget), ga(context))
  }

  def mapStarTarget(c : Star_targetContext) : T = {
    if (c.star_target() != null) Star(mapStarTarget(c.star_target()), ga(c)) else
    mapTargetWithStarAtom(c.target_with_star_atom())
  }

  def mapTargetWithStarAtom(c : Target_with_star_atomContext) : T = {
    if (c.NAME() != null) Field(mapTPrimary(c.t_primary()), c.NAME().getText, ga(c)) else
    if (c.slices() != null) CallIndex(
      false, mapTPrimary(c.t_primary()), mapSlices(c.slices()).map(x => (None, x)), ga(c)
    ) else
    mapStarAtom(c.star_atom())
  }

  def mapStarTargetsTupleSeq(context: PythonParser.Star_targets_tuple_seqContext) : CollectionCons = {
    val l = toList(context.l).map(mapStarTarget)
    CollectionCons(CollectionKind.Tuple, l, ga(context))
  }

  def mapStarTargetsListSeq(context: PythonParser.Star_targets_list_seqContext) : CollectionCons = {
    val l = toList(context.star_target()).map(mapStarTarget)
    CollectionCons(CollectionKind.List, l, ga(context))
  }

  def mapStarAtom(context: PythonParser.Star_atomContext) : T = {
    if (context.NAME() != null) Ident(context.NAME().getText, ga(context)) else
    if (context.target_with_star_atom() != null) mapTargetWithStarAtom(context.target_with_star_atom()) else
    if (context.star_targets_tuple_seq() != null) mapStarTargetsTupleSeq(context.star_targets_tuple_seq()) else
    if (context.star_targets_list_seq() != null) mapStarTargetsListSeq(context.star_targets_list_seq()) else
    throw new RuntimeException("mapStarAtom")
  }

  def mapStarNamedExpression(context: PythonParser.Star_named_expressionContext) : T = {
    if (context.bitwise_or() != null) Star(mapBitwiseOr(context.bitwise_or()), ga(context)) else
    mapNamedExpression(context.named_expression())
  }

  def mapNamedExpression(context: PythonParser.Named_expressionContext) : T = {
    if (context.assignment_expression() != null) mapAssignmentExpression(context.assignment_expression()) else
    mapExpression(context.expression())
  }

  def mapStarNamedExpressions(context: PythonParser.Star_named_expressionsContext) : List[T] = {
    toList(context.l).map(mapStarNamedExpression)
  }

  def mapExpression(c : ExpressionContext) : T = {
    if (c.expression() != null) {
      Cond(
        mapDisjunction(c.disjunction(1)), mapDisjunction(c.disjunction(0)),
        mapExpression(c.expression()), ga(c)
      )
    } else
    if (c.lambdef() != null) mapLambdef(c.lambdef()) else
    mapDisjunction(c.disjunction(0))
  }

  def mapLambdaParamMaybeDefault(kind : ArgKind.T)(c : Lambda_param_maybe_defaultContext) : Parameter = {
    val name = c.lambda_param().NAME().getText
    val default = Option(c.default_assignment()).map(x => mapExpression(x.expression()))
    Parameter(name, kind, None, default, ga(c))
  }

  def mapLambdaParamWithDefault(kind : ArgKind.T)(c : Lambda_param_with_defaultContext) : Parameter = {
    val name = c.lambda_param().NAME().getText
    val default = Some(mapExpression(c.default_assignment().expression()))
    Parameter(name, kind, None, default, ga(c))
  }

  def mapLambdaParamNoDefault(kind : ArgKind.T)(c : Lambda_param_no_defaultContext) : Parameter = {
    val name = c.lambda_param().NAME().getText
    Parameter(name, kind, None, None, ga(c))
  }

  def mapLambdaSlashNoDefault(c : Lambda_slash_no_defaultContext) : List[Parameter] = {
    val l = toListNullable(c.lambda_param_no_default())
    l.map(mapLambdaParamNoDefault(ArgKind.Positional))
  }

  def mapLambdaSlashWithDefault(c : Lambda_slash_with_defaultContext) : List[Parameter] = {
    toListNullable(c.lambda_param_no_default()).map(mapLambdaParamNoDefault(ArgKind.Positional)) ++
    toListNullable(c.lambda_param_with_default()).map(mapLambdaParamWithDefault(ArgKind.Positional))
  }

  def mapLambdef(context: PythonParser.LambdefContext) : AnonFun = {
    val body = mapExpression(context.expression())
    val z = context.lambda_params().lambda_parameters()
    val snd = mapLambdaSlashNoDefault(z.lambda_slash_no_default())
    val swd = mapLambdaSlashWithDefault(z.lambda_slash_with_default())
    val pnd = toListNullable(z.lambda_param_no_default()).map(mapLambdaParamNoDefault(ArgKind.PosOrKeyword))
    val pwd = toListNullable(z.lambda_param_with_default()).map(mapLambdaParamWithDefault(ArgKind.PosOrKeyword))
    val otherPositional = Option(z.lambda_star_etc()).flatMap(
      x => Option(x.lambda_param_no_default()).map(x => x.lambda_param().NAME().getText)
    )
    val otherKeywords = Option(z.lambda_star_etc()).flatMap(
      x => Option(x.lambda_kwds()).map(x => x.lambda_param_no_default().lambda_param().NAME().getText)
    )
    val rest = Option(z.lambda_star_etc()).toList.flatMap(
      x => toListNullable(x.lambda_param_maybe_default()).map(mapLambdaParamMaybeDefault(ArgKind.Keyword))
    )
    AnonFun(snd ++ swd ++ pnd ++ pwd ++ rest, otherPositional, otherKeywords, body, ga(context))
  }

  def mapSlice(c : SliceContext) : T = {
    if (c.named_expression() != null) mapNamedExpression(c.named_expression()) else {
      val from = Option(c.expression(0)).map(mapExpression)
      val to = Option(c.expression(0)).map(mapExpression)
      val by = Option(c.expression(0)).map(mapExpression)
      Slice(from, to, by, ga(c))
    }
  }

  def mapSlices(c : SlicesContext) : List[T] = {
    if (c.l != null) asScala(c.l).toList.map(mapSlice) else
    List(mapSlice(c.slice))
  }

  def mapTPrimary(c : T_primaryContext) : T = {
    if (c.NAME() != null) Field(mapTPrimary(c.t_primary()), c.NAME().getText, ga(c)) else
    if (c.slices() != null) CallIndex(false, mapTPrimary(c.t_primary()), mapSlices(c.slices()).map(x => (None, x)), ga(c)) else
    if (c.genexp() != null) ??? else
    if (c.arguments() != null) CallIndex(
      true, mapTPrimary(c.t_primary()),
      if (c.arguments() == null) List() else mapArgs(c.arguments().args()),
      ga(c)
    ) else
      mapAtom(c.atom())
  }

  def mapArgs(context: PythonParser.ArgsContext) : List[(Option[String], T)] = {
    val args = if (context.arg() != null) toList(context.arg()).map(mapArg) else List()
    val kwargs = if (context.kwargs() != null) mapKwargs(context.kwargs()) else List()
    args.map(x => (None, x)) ++ kwargs
  }

  def mapArg(c : ArgContext) : T = {
    if (c.starred_expression() != null) mapStarredExpression(c.starred_expression()) else
    if (c.assignment_expression() != null) mapAssignmentExpression(c.assignment_expression()) else
    mapExpression(c.expression())
  }

  def mapAssignmentExpression(context: PythonParser.Assignment_expressionContext) : Assignment = {
    Assignment(context.NAME().getText, mapExpression(context.expression()), ga(context))
  }

  def mapStarredExpression(context: PythonParser.Starred_expressionContext) : Star =
    Star(mapExpression(context.expression()), ga(context))

  def mapKwargs(context: PythonParser.KwargsContext) : List[(Option[String], T)] = {
    val starred = if (context.kwarg_or_starred() == null) List() else toList(context.kwarg_or_starred()).map(mapKwargOrStarred)
    val doubleStarred = if (context.kwarg_or_double_starred() == null) List() else
      toList(context.kwarg_or_double_starred()).map(mapKwargOrDoubleStarred)
    starred ++ doubleStarred
  }

  def mapKwargOrStarred(c : Kwarg_or_starredContext) : (Option[String], T) = {
    if (c.expression() != null) (Some(c.NAME().getText), mapExpression(c.expression())) else
    (None, mapStarredExpression(c.starred_expression()))
  }

  def mapKwargOrDoubleStarred(c : Kwarg_or_double_starredContext) : (Option[String], T) = {
    if (c.NAME() != null) (Some(c.NAME().getText), mapExpression(c.expression())) else
      (None, DoubleStar(mapExpression(c.expression()), ga(c)))
  }

}
