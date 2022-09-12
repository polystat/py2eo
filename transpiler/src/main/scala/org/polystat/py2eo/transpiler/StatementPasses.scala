package org.polystat.py2eo.transpiler

import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import org.polystat.py2eo.parser.Expression.{
  AnonFun, Assignment, Await, Binop, BoolLiteral, CallIndex, CollectionComprehension, CollectionCons, CollectionKind,
  Comprehension, Cond, DictComprehension, DictCons, DictEltDoubleStar, DoubleStar, EllipsisLiteral, Field, FloatLiteral,
  ForComprehension, FreakingComparison, GeneratorComprehension, Ident, IfComprehension, ImagLiteral, IntLiteral,
  LazyLAnd, LazyLOr, NoneLiteral, Parameter, SimpleComparison, Slice, Star, StringLiteral, T, Unop, Unops,
  UnsupportedExpr, Yield, YieldFrom, Compops
}
import org.polystat.py2eo.parser.{ArgKind, Expression, GeneralAnnotation, Statement}
import org.polystat.py2eo.parser.Statement.{
  AnnAssign, Assert, Assign, AugAssign, Break, ClassDef, Continue, CreateConst, Decorators, Del, For, FuncDef, Global,
  If, IfSimple, ImportAllSymbols, ImportModule, ImportSymbol, NonLocal, Pass, Raise, Return, SimpleObject, Suite, Try,
  Unsupported, While, With
}

object StatementPasses {

  case class Names[Acc](used: HashMap[String, Int], acc : Acc) {

    // the names mentioned here are incompatible with EO (see issue #416 in github.com/cqfn/eo)
    def this(acc : Acc) = this(HashMap("x" -> 1, "i" -> 1, "msg" -> 1, "txt" -> 1), acc)

    def apply(s: String): (String, Names[Acc]) = {
      if (used.contains(s)) { (s + used(s), Names(used.+((s, used(s) + 1)), acc)) }
      else { (s + "0", Names(used.+((s, 1)), acc)) }
    }

    def apply(l: List[String]): (List[String], Names[Acc]) = {
      l.foldLeft((List[String](), this))((acc, s) => {
        val z = acc._2(s)
        (acc._1 :+ z._1, z._2)
      })
    }

  }

  type NamesU = Names[Unit]

  def procStatementGeneral[Acc](f: (Statement.T, Acc) => (Statement.T, Acc, Boolean))(s0: Statement.T, ns0: Acc): (Statement.T, Acc) = {
    def pst = procStatementGeneral(f)(_, _)

    def pstl[T](extract: T => Statement.T, l: List[T], ns: Acc) =
      l.foldLeft((List[Statement.T](), ns))((acc, st) => {
        val xst = pst(extract(st), acc._2)
        (acc._1 :+ xst._1, xst._2)
      })
    def procElse(s : Option[Statement.T]) = s match { case None => Pass(s0.ann) case Some(s) => s }
    val (s, ns, visitChildren) = f(s0, ns0)

    val nochange = (s, ns)

    if (!visitChildren) nochange else {
      s match {
        case If(conditioned, eelse, ann) =>
          val xconditioned = pstl[(T, Statement.T)](_._2, conditioned, ns)
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
          val xl = pstl[Statement.T](x => x, l, ns)
          (Suite(xl._1, ann.pos), xl._2)
        case u: Unsupported =>
          val xl = pstl[Statement.T](x => x, u.sts, ns)
          (new Unsupported(u.original, u.declareVars, u.es, xl._1, u.ann.pos), xl._2)

        case With(cms, body, isAsync, ann) =>
          val xbody = pst(body, ns)
          (With(cms, xbody._1, isAsync, ann.pos), xbody._2)
        case Try(ttry, excepts, eelse, ffinally, ann) =>
          val xtry = pst(ttry, ns)
          val xex = pstl[(Option[(T, Option[String])], Statement.T)](_._2, excepts, xtry._2)
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
  }

  def procStatement(f: (Statement.T, NamesU) => (Statement.T, NamesU))(s0: Statement.T, ns0: NamesU): (Statement.T, NamesU) =
    procStatementGeneral[NamesU]((st, ns) => { val z = f(st, ns); (z._1, z._2, true)})(s0, ns0)

  def simpleProcStatement(f : Statement.T => Statement.T)(s : Statement.T) : Statement.T = {
    procStatement((st, ns) => (f(st), ns))(s, Names(HashMap(), ()))._1
  }

  // all the forcing code is only needed to keep computation order if we transform an expression to a statement:
  // x = h(f(), g()) cannot be transformed to, say, z = g(); x = h(f(), z), because f() must be computed first, so
  // it must be transformed to z0 = f(); z = g(); x = h(z0, z) or not be transformed at all
  type EAfterPass = Either[T, (Statement.T, Ident)]

  def forceSt[Acc](e: T, ns: Names[Acc]): ((Statement.T, Ident), Names[Acc]) = {
    val (lhsName, ns1) = ns("lhs")
    val l = Ident(lhsName, e.ann.pos)
    ((Assign(List(l, e), e.ann.pos), l), ns1)
  }

  def forceSt2[Acc](e: EAfterPass, ns: Names[Acc]): ((Statement.T, Ident), Names[Acc]) = e match {
    case Left(e) => forceSt(e, ns)
    case Right(value) => (value, ns)
  }

  def forceAllIfNecessary[Acc](f: (Boolean, T, Names[Acc]) => (EAfterPass, Names[Acc]))
                         (l: List[(Boolean, T)], ns: Names[Acc]): Either[(List[T], Names[Acc]), (List[(Statement.T, Ident)], Names[Acc])] = {
    val (l1, ns1) = l.foldLeft((List[EAfterPass](), ns))((acc, e) => {
      val xe = GenericExpressionPasses.procExpr(f)(e._1, e._2, acc._2)
      (acc._1 :+ xe._1, xe._2)
    })
    val hasStatement = l1.exists(_.isRight)
    if (!hasStatement) Left(l1.map { case Left(value) => value }, ns1) else {
      Right(
        l1.foldLeft((List[(Statement.T, Ident)](), ns1))((acc, x) => x match {
          case Left(value) =>
            val (p, ns) = forceSt(value, acc._2)
            (acc._1 :+ p, ns)
          case Right(value) => (acc._1 :+ value, acc._2)
        })
      )
    }
  }

  def simplifyAssignmentList(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case Assign(l, ann) if l.size > 2 =>
      val lhs = l.init
      val rhs = l.last
      val (rhsName, ns1) = ns("rhs")
      val lhs1 = lhs.map(x => Assign(List(x, Ident(rhsName, ann.pos)), ann.pos))
      (Suite(Assign(List(Ident(rhsName, ann.pos), rhs), ann.pos) :: lhs1, ann.pos), ns1)
    case _ => (s, ns)
  }

  // it DOES call procExpr itself!
  def procExprInStatement(f: (Boolean, T, NamesU) => (EAfterPass, NamesU))(s: Statement.T, ns: NamesU): (Statement.T, NamesU) = {
    def pst = procExprInStatement(f)(_, _)
    def pstl(l: List[Statement.T], ns: NamesU) =
      l.foldLeft((List[Statement.T](), ns))((acc, st) => {
        val (st1, ns1) = pst(st, acc._2)
        (acc._1 :+ st1, ns1)
      })

    def procEA(x: EAfterPass): (List[Statement.T], T) = x match {
      case Left(value) => (List[Statement.T](), value)
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
        GenericExpressionPasses.procExpr(f)(false, cond, ns2) match {
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
        GenericExpressionPasses.procExpr(f)(false, cond, ns2) match {
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
        val rp = GenericExpressionPasses.procExpr(f)(lhs = false, r, ns)
        val lp = GenericExpressionPasses.procExpr(f)(lhs = true, l, rp._2)
        val (str, er) = procEA(rp._1)
        val (stl, el) = procEA(lp._1)
        (Suite(str ++ stl :+ Assign(List(el, er), ann.pos), ann.pos), lp._2)

      case CreateConst(name, r, ann) =>
        val rp = GenericExpressionPasses.procExpr(f)(lhs = false, r, ns)
        val (str, er) = procEA(rp._1)
        (Suite(str :+ CreateConst(name, er, ann.pos), ann.pos), rp._2)

      case SimpleObject(name, decorates, fields, ann) =>
        forceAllIfNecessary(f)((decorates.toList.map(x => ("", x)) ++ fields).map(x => (false, x._2)), ns) match {
          case Right((l, ns)) =>
            val (l1, dec) = decorates match { case None => (l, None) case Some(_) => (l.tail, Some(l.head._2))}
            val obj = SimpleObject(name, dec, l1.zip(fields).map(x => (x._2._1, x._1._2)), ann.pos)
            (Suite(l.map(_._1) :+ obj, ann.pos), ns)
          case Left((l, ns)) =>
            val (l1, dec) = decorates match { case None => (l, None) case Some(_) => (l.tail, Some(l.head))}
            (SimpleObject(name, dec, l1.zip(fields).map(x => (x._2._1, x._1)), ann.pos), ns)
        }

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
        val rp = GenericExpressionPasses.procExpr(f)(lhs = false, rhs, ns)
        val lp = GenericExpressionPasses.procExpr(f)(lhs = true, lhs, rp._2)
        val (str, er) = procEA(rp._1)
        val (stl, el) = procEA(lp._1)
        (Suite(stl ++ str :+ AugAssign(op, el, er, ann.pos), ann.pos), ns)
      case Return(Some(x), ann) => GenericExpressionPasses.procExpr(f)(false, x, ns) match {
        case (Left(e), ns) => (Return(Some(e), ann.pos), ns)
        case (Right((st, e)), ns) => (Suite(List(st, Return(Some(e), e.ann.pos)), ann.pos), ns)
      }
      case Raise(Some(x), None, ann) => GenericExpressionPasses.procExpr(f)(false, x, ns) match {
        case (Left(e), ns) => (Raise(Some(e), None, ann.pos), ns)
        case (Right((st, e)), ns) => (Suite(List(st, Raise(Some(e), None, e.ann.pos)), ann.pos), ns)
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
        val packed = List(returnAnnotation) ++ args.flatMap(p => List(p.default, p.paramAnn))
        val (sts, packed1, ns2) =
          forceAllIfNecessary(f)(packed.map{ case Some(e) => (false, e) case None => (false, NoneLiteral(fd.ann))}, ns1) match {
            case Left((packed1, ns)) => (List(), packed1, ns)
            case Right((l, ns)) => (l.map(_._1), l.map(_._2), ns)
          }
        val packed2 = packed.zip(packed1).map(x => x._1.map(_ => x._2))
        val args1 = packed2.tail.grouped(2).zip(args).map{ case (List(default, annot), p) => Parameter(p.name, p.kind, annot, default, p.ann) }
        val resFun = FuncDef(name, args1.toList, otherPositional, otherKeyword, packed2.head, body1, fd.decorators, accessibleIdents, isAsync, ann.pos)
        if (sts.isEmpty) (resFun, ns2) else (Suite(sts :+ resFun, ann), ns2)
    }
  }

  def list2option[T](l : List[T]) : Option[T] = l match {
    case List() => None
    case List(x) => Some(x)
  }

  def simpleProcExprInStatementAcc[Acc](f: (Acc, T) => (Acc, T))(acc : Acc, s: Statement.T): (Acc, Statement.T) = {
    def fl(acc : Acc, l : List[T]) = l.foldLeft((acc, List[T]()))(
      (acc, e) => {
        val v = f(acc._1, e)
        (v._1, acc._2 :+ v._2)
      }
    )
    def fo(acc : Acc, o : Option[T]) : (Acc, Option[T]) = {
      val v = fl(acc, o.toList)
      (v._1, list2option(v._2))
    }
    def pst(acc : Acc, s : Statement.T) : (Acc, Statement.T) = simpleProcExprInStatementAcc(f)(acc, s)
    def pstl(acc : Acc, l : List[Statement.T]) : (Acc, List[Statement.T]) = l.foldLeft((acc, List[Statement.T]()))(
      (acc, st) => {
        val v = simpleProcExprInStatementAcc(f)(acc._1, st)
        (v._1, acc._2 :+ v._2)
      }
    )
    def psto(acc : Acc, o : Option[Statement.T]) : (Acc, Option[Statement.T]) = {
      val v = pstl(acc, o.toList)
      (v._1, list2option(v._2))
    }
    s match {
      case If(conditioned, eelse, ann) =>
        val (acc1, conds) = fl(acc, conditioned.map(_._1))
        val (acc2, sts) = pstl(acc1, conditioned.map(_._2))
        val (acc3, eelse1) = psto(acc2, eelse)
        (acc3, If(conds.zip(sts), eelse1, ann))
      case IfSimple(cond, yes, no, ann) =>
        val (acc1, cond1) = f(acc, cond)
        val (acc2, yes1) = pst(acc1, yes)
        val (acc3, no1) = pst(acc2, no)
        (acc3, IfSimple(cond1, yes1, no1, ann))
      case While(cond, body, eelse, ann) =>
        val (acc1, cond1) = f(acc, cond)
        val (acc2, body1) = pst(acc1, body)
        val (acc3, eelse1) = psto(acc2, eelse)
        (acc3, While(cond1, body1, eelse1, ann))
      case For(what, in, body, eelse, isAsync, ann) =>
        val (acc1, List(what1, in1)) = fl(acc, List(what, in))
        val (acc2, body1) = pst(acc1, body)
        val (acc3, eelse1) = psto(acc2, eelse)
        (acc3, For(what1, in1, body1, eelse1, isAsync, ann))
      case Suite(l, ann) =>
        val (acc1, l1) = pstl(acc, l)
        (acc1, Suite(l1, ann))
      case AugAssign(op, lhs, rhs, ann) =>
        val (acc1, List(lhs1, rhs1)) = fl(acc, List(lhs, rhs))
        (acc1, AugAssign(op, lhs1, rhs1, ann))
      case AnnAssign(lhs, rhsAnn, rhs, ann) =>
        val (acc1, List(lhs1, rhsAnn1)) = fl(acc, List(lhs, rhsAnn))
        val (acc2, rhs1) = fo(acc1, rhs)
        (acc2, AnnAssign((lhs1), (rhsAnn1), rhs1, ann))
      case Assign(l, ann) =>
        val (acc1, l1) = fl(acc, l)
        (acc1, Assign(l1, ann))
      case CreateConst(name, value, ann) =>
        val (acc1, value1) = f(acc, value)
        (acc1, CreateConst(name, value1, ann))
      case Pass(ann) => (acc, s)
      case Break(ann) => (acc, s)
      case Continue(ann) => (acc, s)
      case Return(x, ann) =>
        val (acc1, x1) = fo(acc, x)
        (acc1, Return(x1, ann))
      case Assert(what, param, ann) =>
        val (acc1, what1) = f(acc, what)
        val (acc2, param1) = fo(acc1, param)
        (acc2, Assert(what1, param1, ann))
      case Raise(e, from, ann) =>
        val (acc1, e1) = fo(acc, e)
        val (acc2, from1) = fo(acc1, from)
        (acc2, Raise(e1, from1, ann))
      case Del(l, ann) =>
        val (acc1, l1) = f(acc, l)
        (acc1, Del((l1), ann))
      case FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation, body, decorators, accessibleIdents, isAsync, ann) =>
        val (acc1, args1) = args.foldLeft((acc, List[Parameter]()))(
          (acc, p) => {
            val (acc1, ann1) = fo(acc._1, p.paramAnn)
            val (acc2, default1) = fo(acc1, p.default)
            (acc2, acc._2 :+ p.withAnnDefault(ann1, default1))
          }
        )

        def inner(acc: Acc, x: Option[(String, Option[T])]) = x match {
          case Some((s, value)) =>
            val (acc1, value1) = fo(acc, value)
            (acc1, Some(s, value1))
          case None => (acc, None)
        }

        val (acc2, otherPositional1) = inner(acc1, otherPositional)
        val (acc3, otherKeyword1) = inner(acc2, otherKeyword)
        val (acc4, returnAnnotation1) = fo(acc3, returnAnnotation)
        val (acc5, body1) = pst(acc4, body)
        val (acc6, dec1) = fl(acc5, decorators.l)
        (acc6,
          FuncDef(
            name, args1, otherPositional1, otherKeyword1, returnAnnotation1,
            body1, Decorators(dec1), HashMap(), isAsync, ann
          )
        )
      case ClassDef(name, bases, body, decorators, ann) =>
        val (acc1, bases1) = fl(acc, bases.map(_._2))
        val (acc2, body1) = pst(acc1, body)
        val (acc6, dec1) = fl(acc2, decorators.l)
        (acc6, ClassDef(name, bases.map(_._1).zip(bases1), body1, Decorators(dec1), ann))
      case SimpleObject(name, decorates, fields, ann) =>
        val (acc1, fields1) = fl(acc, fields.map(_._2))
        val (acc2, dec1) = fo(acc1, decorates)
        (acc2, SimpleObject(name, dec1, fields.map(_._1).zip(fields1), ann))
      case NonLocal(l, ann) => (acc, s)
      case Global(l, ann) => (acc, s)
      case ImportModule(what, as, ann) => (acc, s)
      case ImportAllSymbols(from, ann) => (acc, s)
      case ImportSymbol(from, what, as, ann) => (acc, s)
      case With(cms, body, isAsync, ann) =>
        val (acc1, cms1) = cms.foldLeft((acc, List[(T, Option[T])]()))(
          (acc, x) => {
            val (acc1, e1) = f(acc._1, x._1)
            val (acc2, e2) = fo(acc1, x._2)
            (acc2, acc._2 :+ (e1, e2))
          }
        )
        val (acc2, body1) = pst(acc1, body)
        (acc2, With(cms1, body1, isAsync, ann))
      case NonLocal(l, ann) => (acc, s)
      case Global(l, ann) => (acc, s)
      case ImportModule(what, as, ann) => (acc, s)
      case ImportAllSymbols(from, ann) => (acc, s)
      case ImportSymbol(from, what, as, ann) => (acc, s)
      case With(cms, body, isAsync, ann) =>
        val (acc1, cms1) = cms.foldLeft((acc, List[(T, Option[T])]()))(
          (acc, x) => {
            val (acc1, e1) = f(acc._1, x._1)
            val (acc2, e2) = fo(acc1, x._2)
            (acc2, acc._2 :+ (e1, e2))
          }
        )
        val (acc2, body1) = pst(acc1, body)
        (acc2, With(cms1, body1, isAsync, ann))
      case Try(ttry, excepts, eelse, ffinally, ann) =>
        val (acc1, ttry1) = pst(acc, ttry)
        val (acc2, excepts1) = excepts.foldLeft((acc1, List[(Option[(T, Option[String])], Statement.T)]()))(
          (acc, x) => {
            val (acc1, a) = fo(acc._1, x._1.map(_._1))
            val (acc2, b) = pst(acc1, x._2)
            (acc2, acc._2 :+ (a.zip(x._1.map(_._2)), b))
          }
        )
        val (acc3, eelse1) = psto(acc2, eelse)
        val (acc4, ffinally1) = psto(acc3, ffinally)
        (acc4, Try(ttry1, excepts1, eelse1, ffinally1, ann))
      case uns: Unsupported =>
        val (acc1, orig1) = pst(acc, uns.original)
        val (acc2, es1) = fl(acc1, uns.es.map(_._2))
        val (acc3, sts1) = pstl(acc2, uns.sts)
        (
          acc3,
          new Unsupported(orig1, uns.declareVars, uns.es.map(_._1).zip(es1), sts1, uns.ann)
        )
    }
  }

  def simpleProcExprInStatement(f : T => T)(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = {
    val (ns1, st1) = simpleProcExprInStatementAcc[NamesU]((ns, e) => (ns, f(e)))(ns, s)
    (st1, ns1)
  }

  def simplifyIf(s: Statement.T, ns: NamesU): (Statement.T, NamesU) = s match {
    case If(List((cond, yes)), Some(no), ann) => (IfSimple(cond, yes, no, ann.pos), ns)
    case If(List((cond, yes)), None, ann) => (IfSimple(cond, yes, Pass(ann), ann.pos), ns)
    case If((cond, yes) :: t, eelse, ann) =>
      val (newElse, ns1) = simplifyIf(If(
        t, eelse,
        GeneralAnnotation(
          t.head._2.ann.start,
          eelse match {
            case Some(eelse) => eelse.ann.stop
            case None => t.last._2.ann.stop
          }
        )
      ), ns)
      (IfSimple(cond, yes, newElse, ann.pos), ns1)
    case _ => (s, ns)
  }

  def mkUnsupported(s: Statement.T, ns: NamesU): (Statement.T, NamesU) = {
    def mkUnsupportedInner(original: Statement.T, declareVars: List[String], ann: GeneralAnnotation): Unsupported = {
      new Unsupported(original, declareVars, AnalysisSupport.childrenS(original)._2, AnalysisSupport.childrenS(original)._1, ann)
    }
    (s match {
    case Assign(List(_), _) => s
    case Assign(List(Ident(_, _), _), _) => s
    case Assign(l, ann) => mkUnsupportedInner(s, l.init.flatMap { case Ident(s, _) => List(s) case _ => List() }, ann.pos)
    case FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation, body, decorators, accessibleIdents, isAsync, ann)
      if decorators.l.nonEmpty || otherKeyword.nonEmpty || otherPositional.nonEmpty || isAsync || returnAnnotation.nonEmpty ||
        args.exists(x => x.default.nonEmpty || x.paramAnn.nonEmpty || x.kind == ArgKind.Keyword) =>
      val body1 = mkUnsupportedInner(body, List(), body.ann.pos)
      FuncDef(name, args.map(a => Parameter(a.name, ArgKind.Positional, None, None, a.ann.pos)), None, None, None, body1,
        Decorators(List()), accessibleIdents, false, ann.pos)
    case While(_, _, None, _) => s
    case While(_, _, Some(Pass(_)), _) => s
    case For(_, _, _, _, _, _) | AugAssign(_, _, _, _) | Continue(_) | Break(_) | _: ClassDef | _: AnnAssign |
      Assert(_, _, _) | Raise(_, _, _) | Del(_, _) | Global(_, _) | NonLocal(_, _) | With(_, _, _, _) | Try(_, _, _, _, _) |
      ImportAllSymbols(_, _) | Return(_, _) | While(_, _, _, _) => mkUnsupportedInner(s, List(), s.ann.pos)
    case ImportModule(what, as, _) => mkUnsupportedInner(s, as.toList, s.ann.pos)
    case ImportSymbol(from, what, as, _) => mkUnsupportedInner(s, as.toList, s.ann.pos)
    case _ => s
  }, ns)
  }

  def unSuite(s : Statement.T) : (Statement.T) = {
    @tailrec
    def inner(s : Statement.T) : Statement.T = s match {
      case Suite(l, ann) =>
        val l1 = l.flatMap{ case Suite(l, _) => l case s : Any => List(s) }
        if (!l1.exists{ case Suite(l, _) => true case _ => false }) {
          Suite(l1, ann.pos)
        } else {
          inner(Suite(l1, ann.pos))
        }
      case _ => s
    }
//    println(s"$s \n -> $s1")
    (inner(s))
  }

  def xPrefixInStatement(x : Statement.T, ns : NamesU) : (Statement.T, NamesU) = {
    def pref(s : String) = s"x$s"
    (
      x match {
        case CreateConst(name, value, ann) => CreateConst(pref(name), value, ann)
        case FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation, body, decorators, accessibleIdents, isAsync, ann) =>
          FuncDef(
            pref(name),
            args.map(p => Parameter(pref(p.name), p.kind, p.paramAnn, p.default, p.ann)),
            otherPositional.map(x => (pref(x._1), x._2)),
            otherKeyword.map(x => (pref(x._1), x._2)),
            returnAnnotation, body, decorators, accessibleIdents, isAsync, ann
          )
        case ClassDef(name, bases, body, decorators, ann) =>
          ClassDef(pref(name), bases.map(x => (x._1.map(pref), x._2)), body, decorators, ann)
        case SimpleObject(name, decorates, fields, ann) =>
          SimpleObject(pref(name), decorates, fields.map(x => (pref(x._1), x._2)), ann)
        case NonLocal(l, ann) => NonLocal(l.map(pref), ann)
        case Global(l, ann) => Global(l.map(pref), ann)
        case ImportModule(what, as, ann) => ImportModule(what.map(pref), as.map(pref), ann)
        case ImportAllSymbols(from, ann) => ImportAllSymbols(from.map(pref), ann)
        case ImportSymbol(from, what, as, ann) => ImportSymbol(from.map(pref), pref(what), as.map(pref), ann)
        case Try(ttry, excepts, eelse, ffinally, ann) =>
          Try(ttry, excepts.map(x => (x._1.map(x => (x._1, x._2.map(pref))), x._2)), eelse, ffinally, ann)
        case u : Unsupported =>
          new Unsupported(u.original, u.declareVars.map(pref), u.es, u.sts, u.ann)
        case _ => x
      }
      , ns
    )
  }

  // change a list of different except clauses to just one parameterless except with clauses implemented as ifelseif
  // todo: exception object compatibility is not fully implemented ("or a tuple containing an item that is the class
  //  or a base class of the exception object"), see  https://docs.python.org/3/reference/compound_stmts.html#the-try-statement
  // todo: also must implement named exceptions and del of those a the end of an except clause
  // todo: also must rethrow an exception if it is not catched
  def preSimplifyExcepts(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case Try(ttry, List((None, x)), eelse, ffinally, ann) =>
      print("empty except\n")
      (
        Try(ttry, List((None, Suite(List(
          Assign(List(Ident("caught", ann.pos), BoolLiteral(true, ann.pos)), ann.pos),
          x
        ), ann.pos))), eelse, ffinally, ann),
        ns
      )
    case _ => (s, ns)
  }

  def simplifyExcepts(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case Try(ttry, List((None, x)), eelse, ffinally, ann) => (s, ns)
    case Try(ttry, excepts, eelse, ffinally, ann) =>
      val ex1 = excepts.map(
        x => {
          val body = x._2
          (
            LazyLOr(
              SimpleComparison(Compops.Eq,
                Field(Field(Ident("current-exception", ann.pos), "__class__", ann.pos), "__id__", ann.pos),
                x._1 match {
                  case Some((e, _)) => Field(e, "__id__", ann.pos)
                  case None => IntLiteral(1, ann.pos)
                },
                ann.pos
              ),
              LazyLAnd(
                SimpleComparison(Compops.Eq,
                  Field(Field(Ident("current-exception", ann.pos), "__class__", ann.pos), "__id__", ann.pos),
                  Field(Field(Ident("fakeclasses", ann.pos), "pyTypeClass", ann.pos), "__id__", ann.pos),
                  ann.pos
                ),
                SimpleComparison(Compops.Eq,
                  Field(Ident("current-exception", ann.pos), "__id__", ann.pos),
                  x._1 match {
                    case Some((e, _)) => Field(e, "__id__", ann.pos)
                    case None => IntLiteral(1, ann.pos)
                  },
                  ann.pos
                ),
                ann.pos
              ),
              ann.pos
            ),
            Suite(
              (x._1.toList.flatMap(x => x._2.toList.map (
                name => (Assign(List(Ident(name, ann.pos), Ident("current-exception", ann.pos)), ann.pos))
              ))) ++
              List(Assign(List(Ident("caught", ann.pos), BoolLiteral(true, ann.pos)), ann.pos), body),
              ann.pos
            )
          )
        }
      )
      val asIf = if (ex1.isEmpty) Pass(ann.pos) else If(ex1, Some(Pass(ann.pos)), ann.pos)
      (Try(ttry, List((None, asIf)), eelse, ffinally, ann.pos), ns)
    case _ => (s, ns)
  }

  def simplifyWith(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case With(List((cms, target)), body, isAsync, ann) =>
      assert(!isAsync)
      val (List(manager, value, hit_except), ns1) =
        ns(List("manager", "value", "hit_except"))
      val managerId = Ident(manager, ann.pos)
      val enter = Field(managerId, "__enter__", ann.pos)
      val exit = Field(managerId, "__exit__", ann.pos)
      val code = Suite(
        List(
          Assign(List(Ident(manager, ann.pos), cms), ann.pos),
          Assign(List(
              Ident(value, ann.pos),
              CallIndex(
                true,
                enter,
                List(),
                ann.pos
              )
            ),
            ann.pos
          ),
          Assign(List(
              Ident(hit_except, ann.pos),
              BoolLiteral(false, ann.pos)
            ),
            ann.pos
          ),
          Try(
            target match {
              case Some(v) =>
                Suite(List(
                    Assign(List(v, Ident(value, ann.pos)), ann.pos),
                    body
                  ),
                  ann.pos
                )
              case None => body
            },
            List(
              (
                None,
                Suite(
                  List(
                    Assign(List(
                        Ident(hit_except, ann.pos),
                        BoolLiteral(true, ann.pos)
                      ),
                      ann.pos
                    ),
                    IfSimple(
                      Unop(
                        Unops.LNot,
                        CallIndex(
                          true,
                          exit,
                          List(
                            (None, Field(Ident("current-exception", ann.pos), "__class__", ann.pos)),
                            (None, Ident("current-exception", ann.pos)),
                            (None, NoneLiteral(ann.pos))
                          ),
                          ann.pos
                        ),
                        ann.pos
                      ),
                      Raise(None, None, ann.pos),
                      Pass(ann.pos),
                      ann.pos
                    )
                  ),
                  ann.pos
                )
              )
            ),
            None,
            Some(
              IfSimple(
                Unop(Unops.LNot, Ident(hit_except, ann.pos), ann.pos),
                Assign(
                  List(
                    CallIndex(
                      true,
                      exit,
                      List(
                        (None, NoneLiteral(ann.pos)),
                        (None, NoneLiteral(ann.pos)),
                        (None, NoneLiteral(ann.pos))
                      ),
                      ann.pos
                    )
                  ),
                  ann.pos
                ),
                Pass(ann.pos),
                ann.pos
              )
            ),
            ann.pos
          )
        ),
        ann.pos
      )
      (code, ns1)
    case _ => (s, ns)
  }

  def simplifyAssert(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case Assert(what, param, ann) => (
      IfSimple(
        Unop(Unops.LNot, what, ann.pos),
        Raise(
          Some(CallIndex(true, Ident("AssertionError", ann.pos), param.toList.map((None, _)), ann.pos)),
          None,
          ann.pos
        ),
        Pass(ann.pos),
        ann.pos
      ),
      ns
    )
    case _ => (s, ns)
  }

  def simplifyFor(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case For(what, in, body, eelse, false, ann) =>
      val (List(it, inn), ns1) = ns(List("it", "inn"))
      (Suite(List(
        Assign(List(Ident(inn, in.ann.pos), in), in.ann.pos),
        Assign(List(
          Ident(it, in.ann.pos),
          CallIndex(true, Field(Ident(inn, in.ann.pos), "__iter__", in.ann.pos), List(), in.ann.pos)
        ), in.ann.pos),
        Try(
          While(
            BoolLiteral(true, ann.pos),
            Suite(
              List(
                Assign(
                  List(
                    what,
                    CallIndex(true, Field(Ident(it, in.ann.pos), "__next__", in.ann.pos), List(), in.ann.pos)
                  ),
                  what.ann.pos
                ),
                body
              ),
              body.ann.pos
            ),
            None,
            ann.pos
          ),
          List(
            (Some(Ident("StopIteration", ann.pos), None), eelse.getOrElse(Pass(ann.pos)))
          ),
          None,
          None,
          ann.pos
        )
      ), ann.pos), ns1)
    case _ => (s, ns)
  }


  def simplifyInheritance(s: Statement.T, ns: NamesU): (Statement.T, NamesU) = {

    def simplifyInheritance: (Boolean, T, NamesU) => (EAfterPass, NamesU) = GenericExpressionPasses.procExpr({
      case (false, Field(obj, name, ann), ns) =>
        (Left(CallIndex(isCall = true, Ident("eo_getattr", ann.pos), List((None, obj),
          (None, StringLiteral(List("\"" + name + "\""), ann.pos))), ann.pos)), ns)
      case (false, CallIndex(_, Ident("getattr", anni), args, annc), ns) =>
        (Left(CallIndex(isCall = true, Ident("eo_getattr", anni.pos), args, annc.pos)), ns)
      case (false, CallIndex(_, Ident("setattr", anni), args, annc), ns) =>
        (Left(CallIndex(isCall = true, Ident("eo_setattr", anni.pos), args, annc.pos)), ns)
      case (_, e, ns) => (Left(e), ns)
    })

    def explicitBases(s: Statement.T, ns: NamesU): (Statement.T, NamesU) = s match {
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

    val texplicitBases = StatementPasses.procStatement(explicitBases)(s, ns)
    val (s1, ns1) = StatementPasses.procExprInStatement(simplifyInheritance)(texplicitBases._1, texplicitBases._2)
    (Suite(List(
      ImportSymbol(List("C3"), "eo_getattr", Some("eo_getattr"), s1.ann.pos),
      ImportSymbol(List("C3"), "eo_setattr", Some("eo_setattr"), s1.ann.pos),
      s1
    ), s1.ann.pos),
    ns1)

  }

  def allTheGeneralPasses(debugPrinter: (Statement.T, String) => Unit, s: Statement.T, ns: NamesU): (Statement.T, NamesU) = {
    val t1 = StatementPasses.procStatement((a, b) => (a, b))(s, ns)
    debugPrinter(t1._1, "afterEmptyProcStatement")

    val tsimplifyIf = StatementPasses.procStatement(StatementPasses.simplifyIf)(t1._1, t1._2)
    debugPrinter(tsimplifyIf._1, "afterSimplifyIf")

    //    val tsimplifyInheritance = simplifyInheritance(tsimplifyIf._1, tsimplifyIf._2)
    //    debugPrinter(tsimplifyInheritance._1, "afterSimplifyInheritance")

    tsimplifyIf
  }



}
