package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{
  AnonFun, Assignment, Await, Binop, BoolLiteral, CallIndex, CollectionComprehension, CollectionCons, CollectionKind,
  Comprehension, Cond, DictComprehension, DictCons, DictEltDoubleStar, DoubleStar, EllipsisLiteral, Field, FloatLiteral,
  ForComprehension, FreakingComparison, GeneratorComprehension, Ident, IfComprehension, ImagLiteral, IntLiteral,
  LazyLAnd, LazyLOr, NoneLiteral, Parameter, SimpleComparison, Slice, Star, StringLiteral, T, Unop, Unops,
  UnsupportedExpr, Yield, YieldFrom, Compops
}
import org.polystat.py2eo.parser.{Expression, GeneralAnnotation, Statement}
import org.polystat.py2eo.parser.Statement.{Assign, Decorators, For, FuncDef, IfSimple, Pass, Return, Suite}
import org.polystat.py2eo.transpiler.StatementPasses.{EAfterPass, Names, NamesU, forceAllIfNecessary, forceSt, forceSt2}

import scala.collection.immutable.HashMap

object ExpressionPasses {

  def simplifyComprehensionList(inner : Statement.T, l : List[Comprehension], ann : GeneralAnnotation) : Statement.T = {
    l.foldRight(inner : Statement.T)((comprehension, accum) => {
      comprehension match {
        case IfComprehension(cond) =>
          IfSimple(cond, accum, Pass(ann.pos), ann.pos)
        case ForComprehension(what, in, isAsync) =>
          For(what, in, accum, None, isAsync, ann.pos)
      }
    })
  }

  def simplifyComprehension(lhs : Boolean, e : T, ns : NamesU) : (EAfterPass, NamesU) = {
    if (!lhs) {
      e match {
        case CollectionComprehension(kind, base, l, ann) => {
          val inner = Assign(List(CallIndex(
            true,
            Field(Ident("collectionAccum", ann.pos), "append", ann.pos),
            List((None, base)),
            ann.pos
          )),
            ann.pos
          )
          val st = Suite(
            List(
              Assign(List(Ident("collectionAccum", ann.pos), CollectionCons(kind, List(), ann.pos)), ann.pos),
              simplifyComprehensionList(inner, l, ann)
            ),
            ann.pos
          )
          (Right((st : Statement.T, Ident("collectionAccum", ann.pos))), ns)
        }
        case DictComprehension(Left((k, v)), l, ann) => {
          val inner = Assign(List(CallIndex(
            true,
            Field(Ident("collectionAccum", ann.pos), "add", ann.pos),
            List((None, k),(None, v)),
            ann.pos
          )),
            ann.pos
          )
          val st = Suite(
            List(
              Assign(List(Ident("collectionAccum", ann.pos), DictCons(List(), ann.pos)), ann.pos),
              simplifyComprehensionList(inner, l, ann)
            ),
            ann.pos
          )
          (Right((st : Statement.T, Ident("collectionAccum", ann.pos))), ns)
        }
        case x : Any => (Left(x), ns)
      }
    } else {
      (Left(e), ns)
    }
  }

  // explicitly substitute the self to each method call
  // todo: does not work if a class method is returned as a function and then called
  def simpleSyntacticMethodCall(lhs : Boolean, e : T, ns : NamesU) : (EAfterPass, NamesU) = {
    if (!lhs) {
      e match {
        case CallIndex(true, what@Field(obj@Ident(_, _), fname, fann), args, ann) =>
          (Left(CallIndex(true, what, (None, obj) :: args, ann.pos)), ns)
        case CallIndex(true, what@Field(obj, fname, fann), args, ann) =>
          val (List(objName, idName), ns1) = ns(List("obj", "id"))
          (
            Right((
              Suite(
                List(
                  Assign(List(Ident(objName, ann.pos), obj), ann.pos),
                  Assign(List(
                    Ident(idName, ann.pos),
                    CallIndex(
                      true,
                      Field(Ident(objName, ann.pos), fname, fann),
                      (None, Ident(objName, ann.pos)) :: args,
                      ann.pos
                    )
                  ), ann.pos)
                ),
                ann.pos
              ),
              Ident(idName, ann.pos)
            )),
            ns1
          )
        case CallIndex(isCall, Field(_, _, _), args, ann) => ??? // todo: must be implemented as above, but a bit more complicated
        case x : Any => (Left(x), ns)
      }
    } else {
      (Left(e), ns)
    }
  }

  // need this because EO only allows first letters of idents to be small
  def xPrefixInExpr(e : T) : T = {
    def pref(s : String) = s"x$s"
    e match {
      case Assignment(ident, rhs, ann) => Assignment(pref(ident), rhs, ann)
      case Ident(name, ann) => (Ident(pref(name), ann))
      case Field(o, fname, ann) => Field(o, pref(fname), ann)
      case CallIndex(isCall, whom, args, ann) =>
        CallIndex(isCall, whom, args.map(x => (x._1.map(pref), x._2)), ann)
      case AnonFun(args, otherPositional, otherKeyword, body, ann) =>
        AnonFun(
          args.map(p => Parameter(pref(p.name), p.kind, p.paramAnn, p.default, p.ann)),
          otherPositional.map(pref),
          otherKeyword.map(pref),
          body,
          ann
        )
      case _ => e
    }
  }

  def concatStringLiteral(e : T) : T = {
    def reescape(s : String) : String = {
      val v = s.foldLeft(("", false))(
        (acc, char) =>
          if ('\\' == char && !acc._2) (acc._1, true) else
            if ('\\' == char && acc._2) (acc._1 + "\\\\", false) else {
              if (!acc._2 && '"' == char) (acc._1 + "\\\"", false) else
              if (!acc._2) (acc._1 :+ char, false) else
                if ('\"' == char) (acc._1 + "\\\"", false) else
                  { (acc._1 :+ char, false) }
            }
      )
      assert(!v._2)
      v._1
    }
    e match {
      case StringLiteral(values, ann) =>
        val s = values.map(
          s => {
            val s0 = if (!s.startsWith("'") && !s.startsWith("\"")) s.substring(1, s.length) else s
            val s1 = if (!s0.startsWith("'") && !s0.startsWith("\"")) s0.substring(1, s0.length) else s0
            val s2 = if (s1.startsWith("\"\"\"") || s1.startsWith("'''")) s1.substring(2, s1.length - 2) else s1
            reescape(s2.substring(1, s2.length - 1))
          }
        )
          .mkString
        val s1 = s.replace("\n", "\\n")
        StringLiteral(List("\"" + s1 + "\""), ann.pos)
      case e : Any => e
    }
  }

  def addExplicitConstructorOfCollection(e : T) : T = {
    e match {
      case CollectionCons(kind, l, ann)
        if kind == CollectionKind.List || kind == CollectionKind.Tuple =>
          CallIndex(true, Ident("xmyArray", e.ann.pos), List((None, e)), e.ann.pos)
      case DictCons(_, _) | CollectionCons(CollectionKind.Set, _, _) =>
        CallIndex(true, Ident("xmyMap", e.ann.pos), List((None, e)), e.ann.pos)
      case _ => e
    }
  }

  // translate an expression to something like a three register code in order to extract each function call with
  // possible side effects to a separate statement, i.e., a set of locals assignments, where op with side effects
  // may happen only in a root node of an rhs syntax tree
  // note that, say, binops and almost anything else may also be function calls, because they may be overriden
  def extractAllCalls(lhs: Boolean, e: T, ns: NamesU): (EAfterPass, NamesU) = {
    if (lhs) (Left(e), ns) else {
      e match {
        case IntLiteral(_, _) | FloatLiteral(_, _) | StringLiteral(_, _) | BoolLiteral(_, _) | DictCons(_, _)
             | CollectionCons(_, _, _) | NoneLiteral(_) | LazyLAnd(_, _, _) | LazyLOr(_, _, _) | Cond(_, _, _, _)
             | EllipsisLiteral(_) | Ident(_, _) =>
          (Left(e), ns)
        case _ =>
          val (name, ns1) = ns("e")
          val id = Ident(name, e.ann.pos)
          (Right((
            Suite(
              List(
                Assign(List(id, e), e.ann.pos),
                Assign(List(Field(id, "<", e.ann.pos)), e.ann.pos)
              ),
              e.ann.pos
            ),
            id
          )), ns1)
      }
    }
  }

  def mkUnsupportedExpr(e: Expression.T): T = {
    def mkUnsupportedExprInner(original : Expression.T): UnsupportedExpr = {
      new UnsupportedExpr(original, SimpleAnalysis.childrenE(original), original.ann.pos)
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
      case StringLiteral(value, ann) if value.length > 1 || value.exists(
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

  def procExpr[Acc](f: (Boolean, T, Names[Acc]) => (EAfterPass, Names[Acc]))
              (lhs: Boolean, e: T, ns: Names[Acc]): (EAfterPass, Names[Acc]) = {
    def pe = ExpressionPasses.procExpr[Acc](f)(false, _, _)

    def reconstruct(lhs: Boolean, cons: List[T] => T, l: List[T], ns: Names[Acc]): (EAfterPass, Names[Acc]) = {
      forceAllIfNecessary(f)(l.map(x => (lhs, x)), ns) match {
        case Left((es, ns)) => f(lhs, cons(es), ns)
        case Right((sts, ns)) =>
          val (lhsName, ns1) = ns("lhs")
          val sts1 = sts.map(_._1)
          f(lhs, cons(sts.map(_._2)), ns1) match {
            case (Left(e), ns) =>
              val last = Assign(List(Ident(lhsName, e.ann.pos), e), e.ann.pos)
              (Right(Suite(sts1 :+ last, GeneralAnnotation(sts1.head.ann.start, e.ann.stop)),
                Ident(lhsName, e.ann.pos)), ns)
            case (Right((st, lastName)), ns) =>
              (Right(Suite(sts1 :+ st, GeneralAnnotation(sts1.head.ann.start, st.ann.stop)), lastName), ns)
          }
      }
    }

    e match {
      case Binop(op, l, r, ann) =>
        reconstruct(false, { case List(l, r) => Binop(op, l, r, ann.pos) }, List(l, r), ns)
      case Yield(None, _) => (Left(e), ns)
      case Yield(Some(e), ann) => reconstruct(false, { case List(x) => Yield(Some(x), ann.pos) }, List(e), ns)
      case YieldFrom(e, ann) => reconstruct(false, { case List(x) => YieldFrom(x, ann.pos) }, List(e), ns)
      case SimpleComparison(op, l, r, ann) =>
        reconstruct(false, { case List(l, r) => SimpleComparison(op, l, r, ann.pos) }, List(l, r), ns)
      case FreakingComparison(ops, l, ann) if ops.size == 1 =>
        reconstruct(false, { case List(l, r) => SimpleComparison(ops.head, l, r, ann.pos) }, List(l.head, l.last), ns)
      case Assignment(ident, rhs, ann) =>
        reconstruct(lhs, { case List(x) => Assignment(ident, x, ann) }, List(rhs), ns)
      case Unop(op, x, ann) =>
        reconstruct(false, { case List(x) => Unop(op, x, ann.pos) }, List(x), ns)
      case Star(e, ann) =>
        reconstruct(false, { case List(x) => Star(x, ann.pos) }, List(e), ns)
      case DoubleStar(e, ann) =>
        reconstruct(false, { case List(x) => DoubleStar(x, ann.pos) }, List(e), ns)
      case Slice(from, to, by, ann) =>
        val l0 = List(from, to, by)
        val l = l0.map{ case None => BoolLiteral(value = true, ann.pos)  case Some(x) => x}
        def cons(l : List[T]) = {
          val List(from, to, by) = l.zip(l0).map{ case (_, None) => None case (x, _) => Some(x) }
          Slice(from, to, by, ann.pos)
        }
        reconstruct(lhs = false, cons, l, ns)
      // todo: this is a hack. We do not process the function to be called to make the
      // output simpler for polystat
      case CallIndex(isCall, whom, args, ann) if isCall =>
        reconstruct(
          false,
          t => CallIndex(isCall, whom, args.map(_._1).zip(t), ann.pos),
          args.map(_._2),
          ns
        )
      case CallIndex(isCall, whom, args, ann) if !isCall =>
        val (Left(whom1), ns1) = ExpressionPasses.procExpr(f)(lhs, whom, ns)
        val argsNoKw = args.map { case (None, x) => x }
        reconstruct(lhs = false, args => new CallIndex(whom1, args, isCall, ann.pos), argsNoKw, ns1)
      case u: UnsupportedExpr =>
        reconstruct(lhs, es => new UnsupportedExpr(u.original, es, u.ann.pos), u.children, ns)
      case LazyLAnd(l, r, ann) => // l and r <=> r if l else false
        pe(Cond(l, r, BoolLiteral(value = false, ann.pos), ann.pos), ns)
      case LazyLOr(l, r, ann) => // l or r <=> true if l else r
        pe(Cond(l, BoolLiteral(value = true, ann.pos), r, ann.pos), ns)
      case FreakingComparison(ops, a :: t, ann) =>
        val (a0, ns0) = pe(a, ns)
        val (a1, ns1) = forceSt2(a0, ns0)
        val (a2, ns2) = forceSt(BoolLiteral(value = true, ann.pos), ns1)
        val z = ops.zip(t).foldLeft((List(a1._1, a2._1), a1._2, ns2))((acc, x) => {
          val (r0, ns0) = pe(x._2, acc._3)
          val (r, ns1) = forceSt2(r0, ns0)
          val pos = new GeneralAnnotation(acc._2.ann.start, r._2.ann.stop)
          val updCond = Assign(List(a2._2, LazyLAnd(a2._2,
            SimpleComparison(x._1, acc._2, r._2, pos), pos)), pos)
          (acc._1 :+ IfSimple(a2._2, Suite(List(r._1, updCond), pos), Pass(pos), pos), r._2, ns1)
        })
        (Right((Suite(z._1, ann.pos), a2._2)), z._3)
      case AnonFun(args, otherPositional, otherKeyword, body, ann) =>
        val (funname, ns1) = ns("anonFun")
        val (xbody, ns2) = pe(body, ns1)
        val finalBody = xbody match {
          case Left(value) => Return(Some(value), value.ann.pos)
          case Right(value) =>
            Suite(List(value._1, Return(Some(value._2), value._2.ann.pos)), GeneralAnnotation(value._1.ann.start, value._2.ann.stop))
        }
        // todo: all the keyword args must be supported in the "lambda" as well
        val f = FuncDef(funname, args, otherPositional.map(x => (x, None)), otherKeyword.map(x => (x, None)), None,
          finalBody, Decorators(List()), HashMap(), isAsync = false, ann.pos)
        (Right((f, Ident(funname, ann.pos))), ns2)
      case Cond(cond, yes, no, ann) => forceAllIfNecessary(f)(List(cond, yes, no).map(x => (false, x)), ns) match {
        case Left((List(a, b, c), ns)) => (Left(Cond(a, b, c, ann.pos)), ns)
        case Right((List((cond, condName), (yes, yesName), (no, noName)), ns)) =>
          (Right((Suite(List(cond,
            IfSimple(
              condName, yes,
              Suite(List(no, Assign(List(yesName, noName), noName.ann.pos)), no.ann.pos),
              ann.pos
            )), ann.pos), yesName)), ns)
      }
      case CollectionCons(kind, l, ann) =>
        reconstruct(lhs, l => CollectionCons(kind, l, ann.pos), l, ns)
      case DictCons(l, ann) =>
        val simple = l.flatMap({ case Right(x) => List(x) case Left((x, y)) => List(x, y) })
        def cons(original: List[DictEltDoubleStar], simple: List[T]): List[DictEltDoubleStar] = (original, simple) match {
          case (Nil, Nil) => List()
          case (Right(_) :: otl, z :: stl) => Right(z) :: cons(otl, stl)
          case (Left(_) :: otl, a :: b :: stl) => Left((a, b)) :: cons(otl, stl)
        }
        reconstruct(lhs = false, simple => DictCons(cons(l, simple), ann.pos), simple, ns)
      case IntLiteral(_, _) | Ident(_, _) | StringLiteral(_, _) | BoolLiteral(_, _) | NoneLiteral(_) | FloatLiteral(_, _) |
        EllipsisLiteral(_) | ImagLiteral(_, _) => f(lhs, e, ns)
      case Field(whose, name, ann) => reconstruct(lhs, { case List(x) => Field(x, name, ann.pos) }, List(whose), ns)
      case CollectionComprehension(kind, base, l, ann) =>
        val l1 = base :: comprehensions2calls(l, ann)
        reconstruct(lhs, { case base :: l2 =>
          val l3 = call2comprehensions(l.zip(l2))
          CollectionComprehension(kind, base, l3, ann.pos)
        }, l1, ns)
      case DictComprehension(Left((k, v)), l, ann) =>
        val l1 = k :: v :: comprehensions2calls(l, ann)
        reconstruct(lhs, { case k :: v :: l2 =>
          val l3 = call2comprehensions(l.zip(l2))
          DictComprehension(Left((k, v)), l3, ann.pos)
        }, l1, ns)
      case GeneratorComprehension(base, l, ann) =>
        val l1 = base :: comprehensions2calls(l, ann)
        reconstruct(lhs, { case base :: l2 =>
          val l3 = call2comprehensions(l.zip(l2))
          GeneratorComprehension(base, l3, ann.pos)
        }, l1, ns)

    }
  }

  def call2comprehensions(l : List[(Comprehension, T)]): List[Comprehension] = l.map {
    case (IfComprehension(_), CallIndex(_, _, List((_, x)), _)) => IfComprehension(x)
    case (ForComprehension(_, _, isAsync), CallIndex(_, _, List((_, a), (_, b)), _)) =>
      ForComprehension(a, b, isAsync)
  }

  def comprehensions2calls(l : List[Comprehension], ann : GeneralAnnotation): List[CallIndex] = l.map{
    case IfComprehension(cond) => CallIndex(isCall = true, NoneLiteral(ann.pos), List((None, cond)), ann.pos)
    case ForComprehension(what, in, _) =>
      CallIndex(isCall = true, NoneLiteral(ann.pos), List((None, what), (None, in)), ann.pos)
  }
}
