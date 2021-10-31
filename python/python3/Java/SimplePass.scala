import Expression._

import scala.collection.immutable.{HashMap, HashSet}

object SimplePass {

  case class Names(used : HashMap[String, Int]) {

    def this() = this(HashMap())

    def apply(s : String) : (String, Names) = {
      if (used.contains(s)) (s + used(s), Names(used.+((s, used(s) + 1))))
      else (s + "0", Names(used.+((s, 1))))
    }

    def apply(l : List[String]) : (List[String], Names) = {
      l.foldLeft((List[String](), this))((acc, s) => {
        val z = acc._2(s)
        (acc._1 :+ z._1, z._2)
      })
    }

  }

  def procStatementGeneral(f : (Statement, Names) => (Statement, Names, Boolean))(s0 : Statement, ns0 : Names) : (Statement, Names) = {
    def pst = procStatementGeneral(f)(_, _)
    def pstl[T](extract : T => Statement, l : List[T], ns : Names) = l.foldLeft((List[Statement](), ns))((acc, st) => {
      val xst = pst(extract(st), acc._2)
      (acc._1 :+ xst._1, xst._2)
    })

    val (s, ns, visitChildren) = f(s0, ns0)

    val nochange = (s, ns)

    if (!visitChildren) nochange else
    s match {
      case If(conditioned, eelse) => {
        val xconditioned = pstl[(T, Statement)](_._2, conditioned, ns)
        val xelse = pst(eelse, xconditioned._2)
        (If(conditioned.map(_._1).zip(xconditioned._1), xelse._1), xelse._2)
      }
      case IfSimple(cond, yes, no) =>
        val xyes = pst(yes, ns)
        val xno  = pst(no, xyes._2)
        (IfSimple(cond, xyes._1, xno._1), xno._2)
      case While(cond, body, eelse) =>
        val xbody = pst(body, ns)
        val xelse = pst(eelse, xbody._2)
        (While(cond, xbody._1, xelse._1), xelse._2)
      case For(what, in, body, eelse) =>
        val xbody = pst(body, ns)
        val xelse = pst(eelse, xbody._2)
        (For(what, in, xbody._1, xelse._1), xelse._2)
      case Suite(l) => {
        val xl = l.foldLeft((List[Statement](), ns))((acc, st) => {
          val xst = pst(st, acc._2)
          (acc._1 :+ xst._1, acc._2)
        })
        (Suite(xl._1), xl._2)
      }
      case With(cm, target, body) =>
        val xbody = pst(body, ns)
        (With(cm, target, xbody._1), xbody._2)
      case Try(ttry, excepts, eelse, ffinally) =>
        val xtry = pst(ttry, ns)
        val xex = pstl[(Option[(T, Option[String])], Statement)](_._2, excepts, xtry._2)
        val xelse = pst(eelse, xex._2)
        val xfinally = pst(ffinally, xelse._2)
        (Try(xtry._1, excepts.map(_._1).zip(xex._1), xelse._1, xfinally._1), xfinally._2)

      case AugAssign(op, lhs, rhs) => nochange
      case Return(_) | Assert(_) | Raise(_, _) | Assign(_) | WithoutArgs(_) => nochange
      case ClassDef(name, bases, body, decorators) =>
        val xbody = pst(body, ns)
        (new ClassDef(name, bases, xbody._1, decorators), xbody._2)
      case FuncDef(name, args, otherPositional, otherKeyword, body, decorators, accessibleIdents) =>
        val xbody = pst(body, ns)
        (FuncDef(name, args, otherPositional, otherKeyword, xbody._1, decorators, accessibleIdents), xbody._2)
      case NonLocal(_) | Global(_) | ImportModule(_, _) | ImportSymbol(_, _, _) | ImportAllSymbols(_) | Del(_) => nochange
    }
  }

  def procStatement(f : (Statement, Names) => (Statement, Names))(s0 : Statement, ns0 : Names) : (Statement, Names) =
    procStatementGeneral((st, ns) => { val z = f(st, ns); (z._1, z._2, true)})(s0, ns0)


  // all the forcing code is only needed to keep computation order if we transform an expression to a statement:
  // x = h(f(), g()) cannot be transformed to, say, z = g(); x = h(f(), z), because f() must be computed first, so
  // it must be transformed to z0 = f(); z = g(); x = h(z0, z) or not be transformed at all
  type EAfterPass = Either[T, (Statement, Ident)]

  def forceSt(e : T, ns : Names) : ((Statement, Ident), Names) = {
    val (lhsName, ns1) = ns("lhs")
    val l = Ident(lhsName)
    ((Assign(List(l, e)), l), ns1)
  }

  def forceSt2(e : EAfterPass, ns : Names) : ((Statement, Ident), Names) = e match {
    case Left(e) => forceSt(e, ns)
    case Right(value) => (value, ns)
  }

  def forceAllIfNecessary(f : (Boolean, T, Names) => (EAfterPass, Names))
                         (l : List[(Boolean, T)], ns : Names) : Either[(List[T], Names), (List[(Statement, Ident)], Names)] = {
    val (l1, ns1) = l.foldLeft((List[EAfterPass](), ns))((acc, e) => {
      val xe = procExpr(f)(e._1, e._2, acc._2)
      (acc._1 :+ xe._1, xe._2)
    })
    val hasStatement = l1.exists(_.isRight)
    if (!hasStatement) Left(l1.map{case Left(value) => value }, ns1) else
      Right (
        l1.foldLeft((List[(Statement, Ident)](), ns1))((acc, x) => x match {
          case Left(value) =>
            val (p, ns) = forceSt(value, acc._2)
            (acc._1 :+ p, ns)
          case Right(value) => (acc._1 :+ value, acc._2)
        })
      )
  }

  def procExpr(f : (Boolean, T, Names) => (EAfterPass, Names))
              (lhs : Boolean, e : T, ns : Names) : (EAfterPass, Names) = {
    def pe = procExpr(f)(false, _, _)

    def reconstruct(lhs : Boolean, cons : List[T] => T, l : List[T], ns : Names) : (EAfterPass, Names) = {
      forceAllIfNecessary(f)(l.map(x => (lhs, x)), ns) match {
        case Left((es, ns)) => f(lhs, cons(es), ns)
        case Right((sts, ns)) =>
          val (lhsName, ns1) = ns("lhs")
          val sts1 = sts.map(_._1)
          f(lhs, cons(sts.map(_._2)), ns1) match {
            case (Left(e), ns) =>
              val last = Assign(List(Ident(lhsName), e))
              (Right(Suite(sts1 :+ last), Ident(lhsName)), ns)
            case (Right((st, lastName)), ns) =>
              (Right(Suite(sts1 :+ st), lastName), ns)
          }
      }
    }

    e match {
      case Binop(op, l, r) if !lhs => reconstruct(false, { case List(l, r) => Binop(op, l, r)}, List(l, r), ns)
      case SimpleComparison(op, l, r) if !lhs => reconstruct(false, { case List(l, r) => SimpleComparison(op, l, r)}, List(l, r), ns)
      case FreakingComparison(ops, l) if !lhs && ops.size == 1 =>
        reconstruct(false, { case List(l, r) => SimpleComparison(ops.head, l, r)}, List(l.head, l.last), ns)
      case Unop(op, x) if !lhs => reconstruct(false, { case List(x) => Unop(op, x) }, List(x), ns)
      case Star(e) if !lhs => reconstruct(false, { case List(x) => Star(x) }, List(e), ns)
      case DoubleStar(e) if !lhs => reconstruct(false, { case List(x) => DoubleStar(x) }, List(e), ns)
      case Slice(from, to, by) if !lhs =>
        val l0 = List(from, to, by)
        val l = l0.map{ case None => BoolLiteral(true)  case Some(x) => x}
        def cons(l : List[T]) = {
          val List(from, to, by) = l.zip(l0).map{ case (_, None) => None case (x, _) => Some(x) }
          Slice(from, to, by)
        }
        reconstruct(false, cons, l, ns)
      case CallIndex(isCall, whom, args) if isCall && !lhs =>
        reconstruct(false, { case (h :: t) => CallIndex(isCall, h, args.map(_._1).zip(t)) }, whom :: args.map(_._2), ns)
      case LazyLAnd(l, r) if !lhs => // l and r <=> r if l else false
        pe(Cond(l, r, BoolLiteral(false)), ns)
      case LazyLOr(l, r) if !lhs => // l or r <=> true if l else r
        pe(Cond(l, BoolLiteral(true), r), ns)

      case FreakingComparison(ops, a :: t) if !lhs =>
        val (a0, ns0) = pe(a, ns)
        val (a1, ns1) = forceSt2(a0, ns0)
        val (a2, ns2) = forceSt(BoolLiteral(true), ns1)
        val z = ops.zip(t).foldLeft((List(a1._1, a2._1), a1._2, ns2))((acc, x) => {
          val (r0, ns0) = pe(x._2, acc._3)
          val (r, ns1) = forceSt2(r0, ns0)
          val updCond = Assign(List(a2._2, LazyLAnd(a2._2, SimpleComparison(x._1, acc._2, r._2))))
          (acc._1 :+ IfSimple(a2._2, Suite(List(r._1, updCond)), WithoutArgs.pass), r._2, ns1)
        })
        (Right((Suite(z._1), a2._2)), z._3)

      case AnonFun(args, body) =>
        val (funname, ns1) = ns("anonFun")
        val (xbody, ns2) = pe(body, ns1)
        val finalBody = xbody match {
          case Left(value) =>   Return(value)
          case Right(value) =>  Suite(List(value._1, Return(value._2)))
        }
        // todo: all the keyword args must be supported in the "lambda" as well
        val f = FuncDef(funname, args.map(x => (x, ArgKind.Positional, None)), None, None, finalBody, Decorators(List()), HashMap())
        (Right((f, Ident(funname))), ns2)

      case Cond(cond, yes, no) if !lhs => forceAllIfNecessary(f)(List(cond, yes, no).map(x => (false, x)), ns) match {
        case Left((List(a, b, c), ns)) => (Left(Cond(a, b, c)), ns)
        case Right((List((cond, condName), (yes, yesName), (no, noName)), ns)) =>
          (Right((Suite(List(cond, If(List((condName, yes)), Suite(List(no, Assign(List(yesName, noName))))))), yesName)), ns)
      }

      case CollectionCons(kind, l) if !lhs =>
        reconstruct(false, l => CollectionCons(kind, l), l, ns)

      case DictCons(l) if !lhs =>
        val simple = l.flatMap({ case Right(x) => List(x) case Left((x, y)) => List(x, y) })
        def cons(original : List[DictEltDoubleStar], simple : List[T]) : List[DictEltDoubleStar] = (original, simple) match {
          case (Nil, Nil) => List()
          case (Right(_) :: otl, z :: stl) => Right(z) :: cons(otl, stl)
          case (Left(_) :: otl, a :: b :: stl) => Left((a, b)) :: cons(otl, stl)
        }
        reconstruct(false, simple => DictCons(cons(l, simple)), simple, ns)

      case IntLiteral(_) | Ident(_) | StringLiteral(_) | BoolLiteral(_) | NoneLiteral() | FloatLiteral(_) => f(lhs, e, ns)
      case Field(whose, name) => reconstruct(lhs, { case List(x) => Field(x, name) }, List(whose), ns)

      case CallIndex(isCall, whom, args) if !isCall =>
        val (Left(whom1), ns1) = procExpr(f)(lhs, whom, ns)
        val argsNoKw = args.map{case (None, x) => x}
        reconstruct(false, { case args => new CallIndex(whom1, args, isCall) }, argsNoKw, ns1)
    }

  }

  def alreadyDone(s : String) = throw new Throwable(s"remove $s before!")

  def procExprInStatement(f : (Boolean, T, Names) => (EAfterPass, Names))(s : Statement, ns : Names) : (Statement, Names) = {
    def pst = procExprInStatement(f)(_, _)

    def procEA(x : EAfterPass) : (List[Statement], T) = x match {
      case Left(value) => (List[Statement](), value)
      case Right(value) => (List(value._1), value._2)
    }

    s match {
      case Raise(None, None) | WithoutArgs(_) | NonLocal(_) | Global(_) | ImportModule(_, _) | ImportSymbol(_, _, _) | ImportAllSymbols(_) => (s, ns)
      case Del(Ident(_)) => (s, ns)
      case IfSimple(cond, yes, no) =>
        val (yes1, ns1) = pst(yes, ns)
        val (no1, ns2) = pst(no, ns1)
        f(false, cond, ns2) match {
          case (Left(cond), ns) => (IfSimple(cond, yes1, no1), ns)
          case (Right((scond, cond)), ns) => (Suite(List(scond, IfSimple(cond, yes1, no1))), ns)
        }

      case Try(ttry, List((None, catchBody)), eelse, ffinally) =>
        val (try1, ns1) = pst(ttry, ns)
        val (catchBody1, ns2) = pst(catchBody, ns1)
        val (eelse1, ns3) = pst(eelse, ns2)
        val (ffinally1, ns4) = pst(ffinally, ns3)
        (Try(try1, List((None, catchBody1)), eelse1, ffinally1), ns4)

        // todo: wow, this is a lot. Maybe we should just generate GOTOs instead of such rewriting
      case While(cond, body, eelse) =>
        val (body1, ns1) = pst(body, ns)
        val (else1, ns2) = pst(eelse, ns1)
        f(false, cond, ns2) match {
          case (Left(cond), ns) => (While(cond, body1, else1), ns)
          case (Right((scond, cond)), ns) =>
            eelse match {
              case WithoutArgs(StatementsWithoutArgs.Pass) =>
                val brk = IfSimple(Unop(Unops.LNot, cond), WithoutArgs.brk, WithoutArgs.pass)
                (While(BoolLiteral(true), Suite(List(scond, brk, body1)), eelse), ns)
              case _ =>
                val (doElseName, ns1) = ns("doElse")
                val brk = IfSimple(Unop(Unops.LNot, cond),
                  Suite(List(Assign(List(Ident(doElseName), BoolLiteral(true))), WithoutArgs.brk)), WithoutArgs.pass)
                (Suite(List(
                  Assign(List(Ident(doElseName), BoolLiteral(false))),
                  While(BoolLiteral(true), Suite(List(scond, brk, body1)), WithoutArgs.pass),
                  IfSimple(Ident(doElseName), else1, WithoutArgs.pass)
                )), ns1)
            }
        }

      case Suite(l) =>
        val (l1, ns1) = l.foldLeft((List[Statement](), ns))((acc, st) => {
          val (st1, ns1) = pst(st, acc._2)
          (acc._1 :+ st1, ns1)
        })
        (Suite(l1), ns1)
      case Assign(List(l, r)) =>
        val rp = procExpr(f)(false, r, ns)
        val lp = procExpr(f)(true, l, rp._2)
        val (str, er) = procEA(rp._1)
        val (stl, el) = procEA(lp._1)
        (Suite(str ++ stl :+ Assign(List(el, er))), lp._2)

      case Assign(List(e)) => forceAllIfNecessary(f)(List((false, e)), ns) match {
        case Left((l, ns)) => (Assign(l), ns)
        case Right((List((s1, _)), ns)) => (s1, ns)
      }
      case AugAssign(op, lhs, rhs) =>
        val rp = procExpr(f)(false, rhs, ns)
        val lp = procExpr(f)(true, lhs, rp._2)
        val (str, er) = procEA(rp._1)
        val (stl, el) = procEA(lp._1)
        (Suite((stl ++ str :+ AugAssign(op, el, er))), ns)
      case Return(x) => f(false, x, ns) match {
        case (Left(e), ns) => (Return(e), ns)
        case (Right((st, e)), ns) => (Suite(List(st, Return(e))), ns)
      }
      case Raise(Some(x), None) => f(false, x, ns) match {
        case (Left(e), ns) => (Raise(Some(e), None), ns)
        case (Right((st, e)), ns) => (Suite(List(st, Return(e))), ns)
      }
      case cd@ClassDef(name, bases, body, Decorators(List())) =>
        val (body1, ns1) = pst(body, ns)
        forceAllIfNecessary(f)(bases.map(x => (false, x)), ns1) match {
          case Left((args, ns)) => (new ClassDef(name, args, body1, cd.decorators), ns)
          case Right((args, ns)) =>(Suite(args.map(_._1) :+ new ClassDef(name, args.map(_._2), body1, cd.decorators)), ns)
        }
      case fd@FuncDef(name, args, otherPositional, otherKeyword, body, Decorators(List()), accessibleIdents) =>
        val (body1, ns1) = pst(body, ns)
        (FuncDef(name, args, otherPositional, otherKeyword, body1, fd.decorators, accessibleIdents), ns1)

      case Assert(_) => alreadyDone("assert")
      case If(_, _) => alreadyDone("ifelseif")
      case Assign(l) if l.size > 2 => alreadyDone("complex assign")
    }
  }

  def simplifyIf(s : Statement, ns : Names) : (Statement, Names) = s match {
    case If(List((cond, yes)), no) => (IfSimple(cond, yes, no), ns)
    case If((cond, yes) :: t, eelse) =>
      val (newElse, ns1) = simplifyIf(If(t, eelse), ns)
      (IfSimple(cond, yes, newElse), ns1)
    case _ => (s, ns)
  }

  // translate an expression to something like a three register code in order to extract each function call with
  // possible side effects to a separate statement, i.e., a set of locals assignments, where op with side effects
  // may happen only in a root node of an rhs syntax tree
  // note that, say, binops and almost anything else may also be function calls, because they may be overriden
  def extractAllCalls(lhs : Boolean, e : T, ns : Names) : (EAfterPass, Names) = {
    if (lhs) (Left(e), ns) else
    e match {
      case IntLiteral(_) | FloatLiteral(_) | StringLiteral(_) | BoolLiteral(_) | DictCons(_) | CollectionCons(_, _)
           | NoneLiteral() | LazyLAnd(_, _) | LazyLOr(_, _) | Cond(_, _, _) | Ident(_) => (Left(e), ns)
      case _ =>
        val (name, ns1) = ns("e")
        val id = Ident(name)
        (Right((Assign(List(id, e)), id)), ns1)
    }
  }

}
