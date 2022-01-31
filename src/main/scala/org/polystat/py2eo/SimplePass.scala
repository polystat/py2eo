package org.polystat.py2eo;

import Expression._

import scala.annotation.tailrec
import scala.collection.immutable.HashMap

object SimplePass {

  case class Names(used: HashMap[String, Int]) {

    // the names mentioned here are incompatible with EO (see issue #416 in github.com/cqfn/eo)
    def this() = this(HashMap("x" -> 1, "i" -> 1, "msg" -> 1, "txt" -> 1))

    def last(s: String): String = s + (used(s) - 1)

    def apply(s: String): (String, Names) = {
      if (used.contains(s)) (s + used(s), Names(used.+((s, used(s) + 1))))
      else (s + "0", Names(used.+((s, 1))))
    }

    def apply(l: List[String]): (List[String], Names) = {
      l.foldLeft((List[String](), this))((acc, s) => {
        val z = acc._2(s)
        (acc._1 :+ z._1, z._2)
      })
    }

  }

  def procStatementGeneral(f: (Statement, Names) => (Statement, Names, Boolean))(s0: Statement, ns0: Names): (Statement, Names) = {
    def pst = procStatementGeneral(f)(_, _)

    def pstl[T](extract: T => Statement, l: List[T], ns: Names) =
      l.foldLeft((List[Statement](), ns))((acc, st) => {
        val xst = pst(extract(st), acc._2)
        (acc._1 :+ xst._1, xst._2)
      })
    def procElse(s : Option[Statement]) = s match { case None => Pass(s0.ann) case Some(s) => s }
    val (s, ns, visitChildren) = f(s0, ns0)

    val nochange = (s, ns)

    if (!visitChildren) nochange else
      s match {
        case If(conditioned, eelse, ann) =>
          val xconditioned = pstl[(T, Statement)](_._2, conditioned, ns)
          val xelse = pst(procElse(eelse), xconditioned._2)
          (If(conditioned.map(_._1).zip(xconditioned._1), Some(xelse._1), ann.pos), xelse._2)
        case IfSimple(cond, yes, no, ann) =>
          val xyes = pst(yes, ns)
          val xno = pst(no, xyes._2)
          (IfSimple(cond, xyes._1, xno._1, ann.pos), xno._2)
        case While(cond, body, eelse, ann) =>
          val xbody = pst(body, ns)
          val xelse = pst(procElse(eelse), xbody._2)
          (While(cond, xbody._1, Some(xelse._1), ann.pos), xelse._2)
        case For(what, in, body, eelse, isAsync, ann) =>
          val xbody = pst(body, ns)
          val xelse = pst(procElse(eelse), xbody._2)
          (For(what, in, xbody._1, Some(xelse._1), isAsync, ann.pos), xelse._2)
        case Suite(l, ann) =>
          val xl = pstl[Statement](x => x, l, ns)
          (Suite(xl._1, ann.pos), xl._2)
        case u: Unsupported =>
          val xl = pstl[Statement](x => x, u.sts, ns)
          (new Unsupported(u.original, u.declareVars, u.es, xl._1, u.ann.pos), xl._2)

        case With(cms, body, isAsync, ann) =>
          val xbody = pst(body, ns)
          (With(cms, xbody._1, isAsync, ann.pos), xbody._2)
        case Try(ttry, excepts, eelse, ffinally, ann) =>
          val xtry = pst(ttry, ns)
          val xex = pstl[(Option[(T, Option[String])], Statement)](_._2, excepts, xtry._2)
          val xelse = pst(procElse(eelse), xex._2)
          val xfinally = pst(procElse(ffinally), xelse._2)
          (Try(xtry._1, excepts.map(_._1).zip(xex._1), Some(xelse._1), Some(xfinally._1), ann.pos), xfinally._2)

      case AugAssign(_, _, _, _) =>  nochange
      case AnnAssign(_, _, _, _) => nochange
      case Return(_, _) | Assert(_, _, _) | Raise(_, _, _) | Assign(_, _) | Pass(_) | Break(_)
           | Continue(_) | CreateConst(_, _, _) =>  nochange
      case ClassDef(name, bases, body, decorators, ann) =>
        val xbody = pst(body, ns)
        (ClassDef(name, bases, xbody._1, decorators, ann.pos), xbody._2)
      case FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation,
        body, decorators, accessibleIdents, isAsync, ann) =>
        val xbody = pst(body, ns)
        (FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation,
          xbody._1, decorators, accessibleIdents, isAsync, ann.pos), xbody._2)
      case NonLocal(_, _) | Global(_, _) | ImportModule(_, _, _) | ImportSymbol(_, _, _, _)
           | ImportAllSymbols(_, _) | Del(_, _) | _: SimpleObject => nochange
    }
  }

  def procStatement(f: (Statement, Names) => (Statement, Names))(s0: Statement, ns0: Names): (Statement, Names) =
    procStatementGeneral((st, ns) => { val z = f(st, ns); (z._1, z._2, true)})(s0, ns0)


  // all the forcing code is only needed to keep computation order if we transform an expression to a statement:
  // x = h(f(), g()) cannot be transformed to, say, z = g(); x = h(f(), z), because f() must be computed first, so
  // it must be transformed to z0 = f(); z = g(); x = h(z0, z) or not be transformed at all
  type EAfterPass = Either[T, (Statement, Ident)]

  def forceSt(e: T, ns: Names): ((Statement, Ident), Names) = {
    val (lhsName, ns1) = ns("lhs")
    val l = Ident(lhsName, e.ann.pos)
    ((Assign(List(l, e), e.ann.pos), l), ns1)
  }

  def forceSt2(e: EAfterPass, ns: Names): ((Statement, Ident), Names) = e match {
    case Left(e) => forceSt(e, ns)
    case Right(value) => (value, ns)
  }

  def forceAllIfNecessary(f: (Boolean, T, Names) => (EAfterPass, Names))
                         (l: List[(Boolean, T)], ns: Names): Either[(List[T], Names), (List[(Statement, Ident)], Names)] = {
    val (l1, ns1) = l.foldLeft((List[EAfterPass](), ns))((acc, e) => {
      val xe = procExpr(f)(e._1, e._2, acc._2)
      (acc._1 :+ xe._1, xe._2)
    })
    val hasStatement = l1.exists(_.isRight)
    if (!hasStatement) Left(l1.map { case Left(value) => value }, ns1) else
      Right(
        l1.foldLeft((List[(Statement, Ident)](), ns1))((acc, x) => x match {
          case Left(value) =>
            val (p, ns) = forceSt(value, acc._2)
            (acc._1 :+ p, ns)
          case Right(value) => (acc._1 :+ value, acc._2)
        })
      )
  }

  def procExpr(f: (Boolean, T, Names) => (EAfterPass, Names))
              (lhs: Boolean, e: T, ns: Names): (EAfterPass, Names) = {
    def pe = procExpr(f)(false, _, _)

    def reconstruct(lhs: Boolean, cons: List[T] => T, l: List[T], ns: Names): (EAfterPass, Names) = {
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
      case Binop(op, l, r, ann) if !lhs =>
        reconstruct(false, { case List(l, r) => Binop(op, l, r, ann.pos) }, List(l, r), ns)
      case Yield(None, _) => (Left(e), ns)
      case Yield(Some(e), ann) => reconstruct(false, { case List(x) => Yield(Some(x), ann.pos) }, List(e), ns)
      case YieldFrom(e, ann) => reconstruct(false, { case List(x) => YieldFrom(x, ann.pos) }, List(e), ns)
      case SimpleComparison(op, l, r, ann) if !lhs =>
        reconstruct(false, { case List(l, r) => SimpleComparison(op, l, r, ann.pos) }, List(l, r), ns)
      case FreakingComparison(ops, l, ann) if !lhs && ops.size == 1 =>
        reconstruct(false, { case List(l, r) => SimpleComparison(ops.head, l, r, ann.pos) }, List(l.head, l.last), ns)
      case Assignment(ident, rhs, ann) =>
        reconstruct(lhs, { case List(x) => Assignment(ident, x, ann) }, List(rhs), ns)
      case Unop(op, x, ann) if !lhs =>
        reconstruct(false, { case List(x) => Unop(op, x, ann.pos) }, List(x), ns)
      case Star(e, ann) if !lhs =>
        reconstruct(false, { case List(x) => Star(x, ann.pos) }, List(e), ns)
      case DoubleStar(e, ann) if !lhs =>
        reconstruct(false, { case List(x) => DoubleStar(x, ann.pos) }, List(e), ns)
      case Slice(from, to, by, ann) if !lhs =>
        val l0 = List(from, to, by)
        val l = l0.map{ case None => BoolLiteral(value = true, ann.pos)  case Some(x) => x}
        def cons(l : List[T]) = {
          val List(from, to, by) = l.zip(l0).map{ case (_, None) => None case (x, _) => Some(x) }
          Slice(from, to, by, ann.pos)
        }
        reconstruct(lhs = false, cons, l, ns)
      case CallIndex(isCall, whom, args, ann) if isCall && !lhs =>
        reconstruct(false, { case h :: t =>
          CallIndex(isCall, h, args.map(_._1).zip(t), ann.pos)
        }, whom :: args.map(_._2), ns)
      case u: UnsupportedExpr =>
        reconstruct(lhs, es => new UnsupportedExpr(u.original, es, u.ann.pos), u.children, ns)
      case LazyLAnd(l, r, ann) if !lhs => // l and r <=> r if l else false
        pe(Cond(l, r, BoolLiteral(value = false, ann.pos), ann.pos), ns)
      case LazyLOr(l, r, ann) if !lhs => // l or r <=> true if l else r
        pe(Cond(l, BoolLiteral(value = true, ann.pos), r, ann.pos), ns)

      case FreakingComparison(ops, a :: t, ann) if !lhs =>
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

      case Cond(cond, yes, no, ann) if !lhs => forceAllIfNecessary(f)(List(cond, yes, no).map(x => (false, x)), ns) match {
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

      case DictCons(l, ann) if !lhs =>
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

      case CallIndex(isCall, whom, args, ann) if !isCall =>
        val (Left(whom1), ns1) = procExpr(f)(lhs, whom, ns)
        val argsNoKw = args.map { case (None, x) => x }
        reconstruct(lhs = false, args => new CallIndex(whom1, args, isCall, ann.pos), argsNoKw, ns1)

      case CollectionComprehension(kind, base, l, ann) =>
        val l1 = base :: comprehensions2calls(l, ann)
        reconstruct(lhs, { case base :: l2 =>
          val l3 = call2comprehensions(l.zip(l2))
          CollectionComprehension(kind, base, l3, ann.pos)
        }, l1, ns)
      case GeneratorComprehension(base, l, ann) =>
        val l1 = base :: comprehensions2calls(l, ann)
        reconstruct(lhs, { case base :: l2 =>
          val l3 = call2comprehensions(l.zip(l2))
          GeneratorComprehension(base, l3, ann.pos)
        }, l1, ns)

    }
  }

  def call2comprehensions(l : List[(Comprehension, T)]) = l.map {
    case (IfComprehension(_), CallIndex(_, _, List((_, x)), _)) => IfComprehension(x)
    case (ForComprehension(_, _, isAsync), CallIndex(_, _, List((_, a), (_, b)), _)) =>
      ForComprehension(a, b, isAsync)
  }

  def comprehensions2calls(l : List[Comprehension], ann : GeneralAnnotation) = l.map{
    case IfComprehension(cond) => CallIndex(isCall = true, NoneLiteral(ann.pos), List((None, cond)), ann.pos)
    case ForComprehension(what, in, _) =>
      CallIndex(isCall = true, NoneLiteral(ann.pos), List((None, what), (None, in)), ann.pos)
  }

  def alreadyDone(s: String) = throw new Throwable(s"remove $s before!")

  def procExprInStatement(f: (Boolean, T, Names) => (EAfterPass, Names))(s: Statement, ns: Names): (Statement, Names) = {
    def pst = procExprInStatement(f)(_, _)
    def pstl(l: List[Statement], ns: Names) =
      l.foldLeft((List[Statement](), ns))((acc, st) => {
        val (st1, ns1) = pst(st, acc._2)
        (acc._1 :+ st1, ns1)
      })

    def procEA(x: EAfterPass): (List[Statement], T) = x match {
      case Left(value) => (List[Statement](), value)
      case Right(value) => (List(value._1), value._2)
    }

    s match {
      case Raise(None, None, _) | Pass(_) | Break(_) | Continue(_) | NonLocal(_, _) | Global(_, _) | ImportModule(_, _, _) |
           ImportSymbol(_, _, _, _) | ImportAllSymbols(_, _) | Return(None, _) => (s, ns)
      case Del(Ident(_, _), _) => (s, ns)
//      case With(cm, target, body, isAsync, ann) =>
//        val (body1, ns1) = pst(body, ns)
//        forceAllIfNecessary(f)((false, cm) :: target.toList.map(x => (true, x)), ns1) match {
//          case Left((l, ns)) => (With(l.head, l.tail.headOption, body1, isAsync, ann.pos), ns)
//          case Right((l, ns)) =>
//            val w = (With(l.head._2, l.map(_._2).tail.headOption, body1, isAsync, ann.pos))
//            (Suite(l.map(_._1) :+ w, ann.pos), ns)
//        }
      case IfSimple(cond, yes, no, ann) =>
        val (yes1, ns1) = pst(yes, ns)
        val (no1, ns2) = pst(no, ns1)
        f(false, cond, ns2) match {
          case (Left(cond), ns) => (IfSimple(cond, yes1, no1, ann.pos), ns)
          case (Right((scond, cond)), ns) => (Suite(List(scond, IfSimple(cond, yes1, no1, ann.pos)), ann.pos), ns)
        }

      case Try(ttry, List((None, catchBody)), Some(eelse), Some(ffinally), ann) =>
        val (try1, ns1) = pst(ttry, ns)
        val (catchBody1, ns2) = pst(catchBody, ns1)
        val (eelse1, ns3) = pst(eelse, ns2)
        val (ffinally1, ns4) = pst(ffinally, ns3)
        (Try(try1, List((None, catchBody1)), Some(eelse1), Some(ffinally1), ann.pos), ns4)

        // todo: wow, this is a lot. Maybe we should just generate GOTOs instead of such rewriting
      case While(cond, body, Some(eelse), ann) =>
        val (body1, ns1) = pst(body, ns)
        val (else1, ns2) = pst(eelse, ns1)
        f(false, cond, ns2) match {
          case (Left(cond), ns) => (While(cond, body1, Some(else1), ann.pos), ns)
          case (Right((scond, cond)), ns) =>
            val ann = cond.ann.pos
            eelse match {
              case Pass(ann1) =>
                val brk = IfSimple(Unop(Unops.LNot, cond, ann1), Break(ann1.pos), Pass(ann1.pos), ann1.pos)
                (While(BoolLiteral(value = true, ann.pos),
                  Suite(List(scond, brk, body1), body.ann.pos), Some(eelse), ann.pos), ns)
              case _ =>
                val (doElseName, ns1) = ns("doElse")
                val poselse = eelse.ann.pos
                val brk = IfSimple(Unop(Unops.LNot, cond, cond.ann.pos),
                  Suite(List(
                    Assign(List(Ident(doElseName, poselse), BoolLiteral(value = true, poselse)), poselse),
                    Break(poselse)), poselse),
                  Pass(poselse),
                  poselse)
                val pos = ann.pos
                (Suite(List(
                  Assign(List(Ident(doElseName, poselse), BoolLiteral(value = false, poselse)), poselse),
                  While(BoolLiteral(value = true, ann), Suite(List(scond, brk, body1), pos), Some(Pass(pos)), pos),
                  IfSimple(Ident(doElseName, ann), else1, Pass(pos), pos)
                ), pos), ns1)
            }
        }

      case Suite(l, ann) =>
        val (l1, ns1) = pstl(l, ns)
        (Suite(l1, ann.pos), ns1)

      case u: Unsupported =>
        val (sts, ns1) = pstl(u.sts, ns)
        val pos = u.ann.pos
        forceAllIfNecessary(f)(u.es, ns1) match {
          case Right((l, ns2)) =>
            (Suite(l.map(_._1) :+ new Unsupported(u.original, u.declareVars, u.es.zip(l).map(x => (x._1._1, x._2._2)), sts, pos), pos), ns2)
          case Left((l, ns2)) =>
            (new Unsupported(u.original, u.declareVars, u.es.zip(l).map(x => (x._1._1, x._2)), sts, pos), ns2)
        }

      case Assign(List(l, r), ann) =>
        val rp = procExpr(f)(lhs = false, r, ns)
        val lp = procExpr(f)(lhs = true, l, rp._2)
        val (str, er) = procEA(rp._1)
        val (stl, el) = procEA(lp._1)
        (Suite(str ++ stl :+ Assign(List(el, er), ann.pos), ann.pos), lp._2)

      case CreateConst(name, r, ann) =>
        val rp = procExpr(f)(lhs = false, r, ns)
        val (str, er) = procEA(rp._1)
        (Suite(str :+ CreateConst(name, er, ann.pos), ann.pos), rp._2)

      case Assign(List(e), ann) => forceAllIfNecessary(f)(List((false, e)), ns) match {
        case Left((l, ns)) => (Assign(l, ann.pos), ns)
        case Right((List((s1, _)), ns)) => (s1, ns)
      }

      case AnnAssign(lhs, rhsAnn, rhs, ann) =>
        def recon(l : List[T]) = AnnAssign(l.head, l(1), if (l.length == 3) Some(l(2)) else None, ann.pos)
        forceAllIfNecessary(f)((true, lhs) :: (List(rhsAnn) ++ rhs.toList).map((false, _)), ns) match {
          case Left((l, ns)) => (recon(l), ns)
          case Right((l, ns)) => (Suite(l.map(_._1) :+ recon(l.map(_._2)), ann.pos), ns)
        }

      case AugAssign(op, lhs, rhs, ann) =>
        val rp = procExpr(f)(lhs = false, rhs, ns)
        val lp = procExpr(f)(lhs = true, lhs, rp._2)
        val (str, er) = procEA(rp._1)
        val (stl, el) = procEA(lp._1)
        (Suite(stl ++ str :+ AugAssign(op, el, er, ann.pos), ann.pos), ns)
      case Return(Some(x), ann) => f(false, x, ns) match {
        case (Left(e), ns) => (Return(Some(e), ann.pos), ns)
        case (Right((st, e)), ns) => (Suite(List(st, Return(Some(e), e.ann.pos)), ann.pos), ns)
      }
      case Raise(Some(x), None, ann) => f(false, x, ns) match {
        case (Left(e), ns) => (Raise(Some(e), None, ann.pos), ns)
        case (Right((st, e)), ns) => (Suite(List(st, Return(Some(e), e.ann.pos)), ann.pos), ns)
      }
      case cd@ClassDef(name, bases, body, Decorators(List()), ann) =>
        val (body1, ns1) = pst(body, ns)
        forceAllIfNecessary(f)(bases.map(x => (false, x._2)), ns1) match {
          case Left((args, ns)) => (ClassDef(name, bases.zip(args).map(x => (x._1._1, x._2)), body1, cd.decorators, ann.pos), ns)
          case Right((args, ns)) =>
            (Suite(args.map(_._1) :+ ClassDef(name, bases.zip(args).map(x => (x._1._1, x._2._2)),
              body1, cd.decorators, body1.ann.pos), ann.pos), ns)
        }
      case fd@FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation,
        body, Decorators(List()), accessibleIdents, isAsync, ann) =>
        val (body1, ns1) = pst(body, ns)
        // todo: process default param values and annotations
        assert(returnAnnotation.isEmpty && args.forall(p => p.default.isEmpty && p.paramAnn.isEmpty))
        (FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation, body1, fd.decorators, accessibleIdents, isAsync, ann.pos), ns1)

      case Assert(_, _, _) => alreadyDone("assert")
      case If(_, _, _) => alreadyDone("ifelseif")
      case Assign(l, _) if l.size > 2 => alreadyDone("complex assign")
    }
  }

  def simplifyIf(s: Statement, ns: Names): (Statement, Names) = s match {
    case If(List((cond, yes)), Some(no), ann) => (IfSimple(cond, yes, no, ann.pos), ns)
    case If(List((cond, yes)), None, ann) => (IfSimple(cond, yes, Pass(ann), ann.pos), ns)
    case If((cond, yes) :: t, Some(eelse), ann) =>
      val (newElse, ns1) = simplifyIf(If(t, Some(eelse), GeneralAnnotation(t.head._2.ann.start, eelse.ann.stop)), ns)
      (IfSimple(cond, yes, newElse, ann.pos), ns1)
    case _ => (s, ns)
  }

  def mkUnsupported(s: Statement, ns: Names): (Statement, Names) = (s match {
    case Assign(List(_), _) => s
    case Assign(List(Ident(_, _), _), _) => s
    case Assign(l, ann) => new Unsupported(s, l.init.flatMap { case Ident(s, _) => List(s) case _ => List() }, ann.pos)
    case FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation, body, decorators, accessibleIdents, isAsync, ann)
      if decorators.l.nonEmpty || otherKeyword.nonEmpty || otherPositional.nonEmpty || isAsync || returnAnnotation.nonEmpty ||
        args.exists(x => x.default.nonEmpty || x.paramAnn.nonEmpty || x.kind == ArgKind.Keyword) =>
      val body1 = new Unsupported(body, List(), body.ann.pos)
      FuncDef(name, args.map(a => Parameter(a.name, ArgKind.Positional, None, None, a.ann.pos)), None, None, None, body1,
        Decorators(List()), accessibleIdents, isAsync, ann.pos)
    case For(_, _, _, _, _, _) | AugAssign(_, _, _, _) | Continue(_) | _: ClassDef | _: AnnAssign |
      Assert(_, _, _) | Raise(_, _, _) | Del(_, _) | Global(_, _) | With(_, _, _, _) | Try(_, _, _, _, _) |
      ImportAllSymbols(_, _) | Return(_, _) => new Unsupported(s, List(), s.ann.pos)
    case ImportModule(what, as, _) => new Unsupported(s, as.toList, s.ann.pos)
    case ImportSymbol(from, what, as, _) => new Unsupported(s, as.toList, s.ann.pos)
    case _ => s
  }, ns)

  def mkUnsupportedExpr(lhs: Boolean, e: Expression.T, ns: Names): (EAfterPass, Names) = {
    val e1 = e match {
      case CallIndex(isCall, _, args, _) if !isCall || args.exists(x => x._1.nonEmpty) =>
        new UnsupportedExpr(e)
      case Star(_, _) | DoubleStar(_, _) | CollectionComprehension(_, _, _, _) | DictComprehension(_, _, _) | Yield(_, _) |
        Slice(_, _, _, _) | AnonFun(_, _, _, _, _) | CollectionCons(_, _, _) | DictCons(_, _) | ImagLiteral(_, _) |
        EllipsisLiteral(_) | GeneratorComprehension(_, _, _) =>
        new UnsupportedExpr(e)
      case SimpleComparison(op, _, _, _) if (
        try {
          PrintEO.compop(op); false
        } catch {
          case _: Throwable => true
        }
      ) => new UnsupportedExpr(e)
      case Binop(op, _, _, _) if (
        try {
          PrintEO.binop(op); false
        } catch {
          case _: Throwable => true
        }
      ) => new UnsupportedExpr(e)
      case _ => e
    }
    (Left(e1), ns)
  }

  def unSuite(s : Statement, ns : Names) : (Statement, Names) = {
    @tailrec
    def inner(s : Statement) : Statement = s match {
      case Suite(l, ann) =>
        val l1 = l.flatMap{ case Suite(l, _) => l case s => List(s) }
        if (!l1.exists{ case Suite(l, _) => true case _ => false })
          Suite(l1, ann.pos)
        else
          inner(Suite(l1, ann.pos))
      case _ => s
    }
//    println(s"$s \n -> $s1")
    (inner(s), ns)
  }

  // translate an expression to something like a three register code in order to extract each function call with
  // possible side effects to a separate statement, i.e., a set of locals assignments, where op with side effects
  // may happen only in a root node of an rhs syntax tree
  // note that, say, binops and almost anything else may also be function calls, because they may be overriden
  def extractAllCalls(lhs: Boolean, e: T, ns: Names): (EAfterPass, Names) = {
    if (lhs) (Left(e), ns) else
      e match {
        case IntLiteral(_, _) | FloatLiteral(_, _) | StringLiteral(_, _) | BoolLiteral(_, _) | DictCons(_, _)
             | CollectionCons(_, _, _) | NoneLiteral(_) | LazyLAnd(_, _, _) | LazyLOr(_, _, _) | Cond(_, _, _, _)
             | EllipsisLiteral(_) | Ident(_, _) =>
          (Left(e), ns)
        case _ =>
          val (name, ns1) = ns("e")
          val id = Ident(name, e.ann.pos)
          (Right((Assign(List(id, e), e.ann.pos), id)), ns1)
      }
  }

  def simplifyInheritance(s: Statement, ns: Names): (Statement, Names) = {

    def simplifyInheritance: (Boolean, T, Names) => (EAfterPass, Names) = procExpr({
      case (false, Field(obj, name, ann), ns) =>
        (Left(CallIndex(isCall = true, Ident("eo_getattr", ann.pos), List((None, obj),
          (None, StringLiteral(List("\"" + name + "\""), ann.pos))), ann.pos)), ns)
      case (false, CallIndex(_, Ident("getattr", anni), args, annc), ns) =>
        (Left(CallIndex(isCall = true, Ident("eo_getattr", anni.pos), args, annc.pos)), ns)
      case (false, CallIndex(_, Ident("setattr", anni), args, annc), ns) =>
        (Left(CallIndex(isCall = true, Ident("eo_setattr", anni.pos), args, annc.pos)), ns)
      case (_, e, ns) => (Left(e), ns)
    })

    def explicitBases(s: Statement, ns: Names): (Statement, Names) = s match {
      case ClassDef(name, bases0, body, decorators, ann) =>
        val (newBody, ns1) = explicitBases(body, ns)
        val basesPos = if (bases0.isEmpty) ann.pos else GeneralAnnotation(bases0.head._2.ann.start, bases0.last._2.ann.stop)
        (ClassDef(name, List(), Suite(List(
          Assign(List(Ident("eo_bases", basesPos),
            CollectionCons(CollectionKind.List, bases0.map { case (None, x) => x }, basesPos)), basesPos),
          newBody),
          body.ann.pos),
          decorators, ann.pos), ns1)
      case _ => (s, ns)

    }

    val texplicitBases = SimplePass.procStatement(explicitBases)(s, ns)
    val (s1, ns1) = SimplePass.procExprInStatement(simplifyInheritance)(texplicitBases._1, texplicitBases._2)
    (Suite(List(
      ImportSymbol(List("C3"), "eo_getattr", Some("eo_getattr"), s1.ann.pos),
      ImportSymbol(List("C3"), "eo_setattr", Some("eo_setattr"), s1.ann.pos),
      s1
    ), s1.ann.pos),
    ns1)

  }

  def allTheGeneralPasses(debugPrinter: (Statement, String) => Unit, s: Statement, ns: Names): (Statement, SimplePass.Names) = {
    val t1 = SimplePass.procStatement((a, b) => (a, b))(s, ns)
    debugPrinter(t1._1, "afterEmptyProcStatement")

    val tsimplifyIf = SimplePass.procStatement(SimplePass.simplifyIf)(t1._1, t1._2)
    debugPrinter(tsimplifyIf._1, "afterSimplifyIf")

//    val tsimplifyInheritance = simplifyInheritance(tsimplifyIf._1, tsimplifyIf._2)
//    debugPrinter(tsimplifyInheritance._1, "afterSimplifyInheritance")

    tsimplifyIf
  }



}
