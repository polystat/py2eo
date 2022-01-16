package org.polystat.py2eo

import org.antlr.v4.runtime.{ParserRuleContext, Token}
import org.polystat.py2eo.Python3Parser._

import scala.collection.JavaConverters.asScala

import Expression.{T => ET}

object MapExpressions {

  def string2num(x: String, c: ParserRuleContext): ET = {
    if (x.last == 'j') Expression.ImagLiteral(x.init, new GeneralAnnotation(c)) else if (x.exists(c => ((c == 'e' || c == 'E') && !x.startsWith("0x") && !x.startsWith("0X")) || c == '.' || c == '+' || c == '-')) {
      Expression.FloatLiteral(x, new GeneralAnnotation(c))
    } else {
      val int =
        if (x.startsWith("0x") || x.startsWith("0X")) {
          x.substring(2, x.length).foldLeft(BigInt(0))((acc, ch) => {
            if (ch != '_') {
              acc * 16 + (
                if (ch >= '0' && ch <= '9') ch.toInt - '0'.toInt else if (ch >= 'a' && ch <= 'f') ch.toInt - 'a'.toInt + 10 else if (ch >= 'A' && ch <= 'F') ch.toInt - 'A'.toInt + 10 else {
                  throw new NumberFormatException()
                }
                )
            } else {
              acc
            }
          }
          )
        } else if (x.startsWith("0o") || x.startsWith("0O")) {
          x.substring(2, x.length).foldLeft(BigInt(0))((acc, ch) =>
            if (ch != '_') {
              acc * 8 + (if (ch >= '0' && ch <= '7') ch.toInt - '0'.toInt else throw new NumberFormatException())
            } else {
              acc
            }
          )
        } else if (x.startsWith("0b") || x.startsWith("0B")) {
          x.substring(2, x.length).foldLeft(BigInt(0))((acc, ch) =>
            if (ch != '_') {
              acc * 2 + (if (ch >= '0' && ch <= '1') ch.toInt - '0'.toInt else throw new NumberFormatException())
            } else {
              acc
            }
          )
        } else {
          BigInt(x.filter(_ != '_'))
        }
      Expression.IntLiteral(int, new GeneralAnnotation(c))
    }
  }

  import Expression._

  def mapAssignmentExpression(c: Assignment_expressionContext): ET = {
    if (c.ASSIGN_IN_EXPR() == null) {
      mapTest(c.test())
    }
    else {
      Assignment(c.IDENT().getText, mapTest(c.test()), new GeneralAnnotation(c))
    }
  }

  def mapDictEltDoubleStar(c: Dict_elt_double_starContext): Either[(ET, ET), ET] =
    if (c.expr() != null) Right(mapExpr(c.expr())) else Left((mapTest(c.test(0)), mapTest(c.test(1))))

  def mapYieldExpr(y1: Yield_exprContext): T = {
    if (y1.yield_arg() == null) Yield(None, new GeneralAnnotation(y1)) else {
      if (y1.yield_arg().FROM() == null) {
        Yield(Some(mapTestlistStarExpr(CollectionKind.Tuple, y1.yield_arg().testlist_star_expr())), new GeneralAnnotation(y1))
      } else {
        YieldFrom(mapTest(y1.yield_arg().test()), new GeneralAnnotation(y1))
      }
    }
  }

  def mapAtom(a: AtomContext): T = {
    if (a.OPEN_PAREN() != null || a.OPEN_BRACK() != null) {
      val collectionKind = if (a.OPEN_BRACK() != null) CollectionKind.List else CollectionKind.Tuple
      if (a.testlist_comp() != null) {
        mapTestList_comp(collectionKind, a.testlist_comp())
      } else if (a.yield_expr() != null) {
        mapYieldExpr(a.yield_expr())
      } else {
        CollectionCons(collectionKind, List(), new GeneralAnnotation(a))
      }
    } else if (a.OPEN_BRACE() != null) {
      // An empty set cannot be constructed with {}; this literal constructs an empty dictionary. https://docs.python.org/3/reference/expressions.html#set-displays
      if (a.dictorsetmaker() == null) {
        DictCons(List(), new GeneralAnnotation(a))
      } else if (a.dictorsetmaker().testlist_comp() != null) {
        mapTestList_comp(CollectionKind.Set, a.dictorsetmaker().testlist_comp())
      } else {
        if (a.dictorsetmaker().comp_for() != null) {
          DictComprehension(mapDictEltDoubleStar(a.dictorsetmaker().dict_elt_double_star(0)),
            mapCompFor(a.dictorsetmaker().comp_for()), new GeneralAnnotation(a))
        } else {
          DictCons(asScala(a.dictorsetmaker().dict_elt_double_star()).toList.map(mapDictEltDoubleStar), new GeneralAnnotation(a))
        }
      }
    } else if (a.NUMBER() != null) string2num(a.NUMBER().getText, a) else if (a.NAME() != null) Ident(a.NAME().getText, new GeneralAnnotation(a)) else if (a.TRUE() != null) BoolLiteral(value = true, new GeneralAnnotation(a)) else if (a.FALSE() != null) BoolLiteral(value = false, new GeneralAnnotation(a)) else if (a.NONE() != null) NoneLiteral(new GeneralAnnotation(a)) else {
      if (a.ELLIPSIS() != null) EllipsisLiteral(new GeneralAnnotation(a)) else {
        StringLiteral(asScala(a.STRING()).map(_.getText).mkString(" "), new GeneralAnnotation(a))
      }
    }
    /*    else {  todo: incorrect string literal processing?
            println(a.STRING())
            println(a.yield_expr())
            println(a.testlist_comp())
            println(a.dictorsetmaker(), a.ELLIPSIS(), a.NONE(), a.TRUE(), a.FALSE())
            ???
          }*/
  }

  def mapExprStarExpr(e: Expr_star_exprContext): ET = if (e.expr() != null) mapExpr(e.expr()) else mapStarExpr(e.star_expr())

  def mapExprList(e: ExprlistContext): ET = asScala(e.l).map(mapExprStarExpr).toList match {
    case List(x) if e.COMMA().isEmpty => x
    case l => CollectionCons(CollectionKind.Tuple, l, new GeneralAnnotation(e))
  }

  def mapVfparg(kind: ArgKind.T, c: VfpargContext): Parameter = {
    val default = Option(c.test()).map(mapTest)
    Parameter(c.vfpdef().NAME().getText, kind, None, default, new GeneralAnnotation(c))
  }

  def mapVarargslistNopos(c: Varargslist_noposContext): (List[Parameter], Option[String], Option[String]) = {
    (asScala(c.l).toList.map(x => mapVfparg(ArgKind.Keyword, x)),
      // todo: the next line may be wrong, it may break the method with which python forces all args to be keyword only (4.8.3 in tutorial)
      if (c.vfptuple().vfpdef() == null) None else Some(c.vfptuple().vfpdef().NAME().getText),
      Option(c.vfpdict()).map(_.vfpdef().NAME().getText)
    )
  }

  def mapVarargslist(c: VarargslistContext): (List[Parameter], Option[String], Option[String]) = {
    if (c == null) (List(), None, None) else {
      val tail =
        if (c.varargslist_nopos() != null) mapVarargslistNopos(c.varargslist_nopos()) else if (c.vfpdict() != null) (List(), None, Some(c.vfpdict().vfpdef().NAME().getText)) else {
          (List(), None, None)
        }
      val l = asScala(c.l).toList
      val (posOnly, others) = l.splitAt(l.indexWhere(c => c.vfpdef().DIV() != null))
      val (posAndKword, kwordOnly) = others.splitAt({
        val pos = others.indexWhere(c => c.vfpdef().STAR() != null)
        if (-1 == pos) others.length else pos
      })

      def f(kind: ArgKind.T, l: List[VfpargContext]) = l.flatMap(x =>
        if (x.vfpdef().NAME() == null) List() else List(mapVfparg(kind, x))
      )

      (
        f(ArgKind.Positional, posOnly) ++
          f(ArgKind.PosOrKeyword, posAndKword) ++
          f(ArgKind.Keyword, kwordOnly) ++
          tail._1, tail._2, tail._3
      )
    }
  }


  def mapTest(e: TestContext): T = e match {
    case e: TestOrTestContext =>
      if (e.test() == null) {
        mapOrTest(e.or_test(0))
      } else {
        Cond(mapOrTest(e.or_test(1)), mapOrTest(e.or_test(0)), mapTest(e.test()), new GeneralAnnotation(e))
      }
    case e: TestLambdefContext =>
      val (args, o1, o2) = mapVarargslist(e.lambdef().varargslist())
      AnonFun(
        args, o1, o2,
        mapTest(e.lambdef().test()),
        new GeneralAnnotation(e)
      )
  }

  def mapTestList_comp(collectionKind: CollectionKind.T, e: Testlist_compContext): T =
    if (e.test_star_expr().size() > 1) {
      CollectionCons(collectionKind, asScala(e.test_star_expr()).map(mapTestStarExpr).toList, new GeneralAnnotation(e))
    } else if (e.comp_for() == null) {
      val x = mapTestStarExpr(e.test_star_expr(0))
      if (!e.COMMA().isEmpty || collectionKind != CollectionKind.Tuple)
        CollectionCons(collectionKind, List(x), new GeneralAnnotation(e))
      else x
    }
    else {
      assert(e.test_star_expr().size() == 1)
      CollectionComprehension(collectionKind, mapTestStarExpr(e.test_star_expr(0)), mapCompFor(e.comp_for()), new GeneralAnnotation(e))
    }

  def mapCompFor(cf: Comp_forContext): List[Comprehension] = {
    ForComprehension(mapExprList(cf.exprlist()), mapOrTest(cf.or_test()), cf.ASYNC() != null) ::
      mapCompIterNullable(cf.comp_iter())
  }

  def mapCompIterNullable(context: Python3Parser.Comp_iterContext): List[Comprehension] =
    if (context == null) List() else if (context.comp_for() != null) mapCompFor(context.comp_for()) else {
      mapCompIf(context.comp_if())
    }

  def mapCompIf(context: Python3Parser.Comp_ifContext): List[Comprehension] =
    IfComprehension(mapTestNocond(context.test_nocond())) :: mapCompIterNullable(context.comp_iter())

  def mapTestNocond(context: Python3Parser.Test_nocondContext): T =
    if (context.or_test() != null) mapOrTest(context.or_test()) else {
      val (args, o1, o2) = mapVarargslist(context.lambdef_nocond().varargslist())
      AnonFun(args, o1, o2,
        mapTestNocond(context.lambdef_nocond().test_nocond()),
        new GeneralAnnotation(context)
      )
    }

  def mapTestList(e: TestlistContext): ET = asScala(e.l).map(mapTest).toList match {
    case List(x) if e.COMMA().isEmpty => x
    case l => CollectionCons(CollectionKind.Tuple, l, new GeneralAnnotation(e))
  }

  def mapStarExpr(e: Star_exprContext): Star = Star(mapExpr(e.expr()), new GeneralAnnotation(e))

  def mapTestStarExpr(e: Test_star_exprContext): T = e match {
    case e: TestNotStarContext => mapTest(e.test())
    case e: StarNotTestContext => mapStarExpr(e.star_expr())
  }

  def mapTestlistStarExpr(collectionKind: CollectionKind.T, e: Testlist_star_exprContext): ET = {
    asScala(e.l).map(mapTestStarExpr).toList match {
      case List(x) if e.COMMA().size() == 0 => x
      case l => CollectionCons(collectionKind, l, new GeneralAnnotation(e))
    }
  }

  def mapSubscript(e: Subscript_Context): T = e match {
    case i: SubIndexContext => mapTest(i.test())
    case slice: SubSliceContext => Slice(
      if (slice.start == null) None else Some(mapTest(slice.start)),
      if (slice.stop == null) None else Some(mapTest(slice.stop)),
      if (slice.step == null) None else Some(mapTest(slice.step)),
      new GeneralAnnotation(e)
    )
  }

  def mapArgument(e: ArgumentContext): (Option[String], T) = {
    if (e.STAR() != null) (None, Star(mapTest(e.test(0)), new GeneralAnnotation(e))) else if (e.POWER() != null) (None, DoubleStar(mapTest(e.test(0)), new GeneralAnnotation(e))) else if (e.ASSIGN() != null) {
      val Ident(keyword, _) = mapTest(e.test(0))
      (Some(keyword), mapTest(e.test(1)))
    } else {
      val a = mapTest(e.test(0))
      if (e.comp_for() != null) {
        val comp = mapCompFor(e.comp_for())
        (None, GeneratorComprehension(a, comp, new GeneralAnnotation(e)))
      } else {
        (None, a)
      }
    }
  }

  def mapArglistNullable(l: ArglistContext): List[(Option[String], ET)] = {
    if (l == null) List() else asScala(l.l).map(mapArgument).toList
  }

  def mapSubscriptList(c: SubscriptlistContext): ET = asScala(c.l).toList match {
    case List(x) if c.COMMA().isEmpty => mapSubscript(x)
    case l => CollectionCons(CollectionKind.Tuple, l.map(mapSubscript), new GeneralAnnotation(c))
  }

  def mapAtomExpr(e: Atom_exprContext): ET = {
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

  def mapPower(e: PowerContext): ET = {
    if (e.factor() != null) {
      Binop(Binops.Pow, mapAtomExpr(e.atom_expr()), mapFactor(e.factor()), new GeneralAnnotation(e))
    }
    else {
      mapAtomExpr(e.atom_expr())
    }
  }

  def mapFactor(e: FactorContext): T = e match {
    case p: FactorPowerContext => mapPower(p.power())
    case u: UnaryContext =>
      val x = mapFactor(u.factor())
      Unop(Unops.ofString(u.op.getText), x, new GeneralAnnotation(e))
  }

  def mapNotTest(e: Not_testContext): T = e match {
    case e: NotNotContext => Unop(Unops.LNot, mapNotTest(e.not_test()), new GeneralAnnotation(e))
    case e: NotComparisonContext =>
      val ops0 = asScala(e.comparison().ops)
      val ops = ops0.map(Compops.ofContext).toList
      val args = asScala(e.comparison().args).map(mapExpr).toList
      if (ops.isEmpty) args.head else FreakingComparison(ops, args, new GeneralAnnotation(e))
  }

  def mapBinop[TT](args: java.util.List[TT], ops: java.util.List[Token], mapa: TT => T): ET = {
    val sargs = asScala(args)
    val sops = asScala(ops)
    val head = sargs.head
    sops.zip(sargs.tail).foldLeft(mapa(head))((acc, x) => {
      Binop(Binops.ofString(x._1.getText), acc, mapa(x._2), new GeneralAnnotation(x._1))
    }
    )
  }

  def mapTerm(e: TermContext): ET = mapBinop(e.args, e.ops, mapFactor)

  def mapArithExpr(e: Arith_exprContext): ET = mapBinop(e.args, e.ops, mapTerm)

  def mapShiftExpr(e: Shift_exprContext): ET = mapBinop(e.args, e.ops, mapArithExpr)

  def mapAndExpr(e: And_exprContext): ET = mapBinop(e.args, e.ops, mapShiftExpr)

  def mapXorExpr(e: Xor_exprContext): ET = mapBinop(e.args, e.ops, mapAndExpr)

  def mapExpr(e: ExprContext): ET = mapBinop(e.args, e.ops, mapXorExpr)

  def mapOrTest(e: Or_testContext): ET = {
    def inner(l: List[And_testContext]): T = l match {
      case List(x) => mapAndTest(x)
      case h :: t => LazyLOr(mapAndTest(h), inner(t), new GeneralAnnotation(h))
    }

    inner(asScala(e.args).toList)
  }

  def mapAndTest(e: And_testContext): ET = {
    def inner(l: List[Not_testContext]): T = l match {
      case List(x) => mapNotTest(x)
      case h :: t => LazyLAnd(mapNotTest(h), inner(t), new GeneralAnnotation(h))
    }

    inner(asScala(e.args).toList)
  }

}
