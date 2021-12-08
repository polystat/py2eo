import Expression.{CallIndex, CollectionCons, CollectionKind, DictCons, Field, Ident, IntLiteral, NoneLiteral, StringLiteral}
import SimplePass.{EAfterPass, Names}

import scala.collection.immutable.HashMap

object ExplicitImmutableHeap {

  val constHeap = "constHeap"
  
  def explicitHeap(st : Statement, ns : Names)  = {
    def procSt(scope : String => (VarScope.T, GeneralAnnotation))(
            s : Statement, ns : Names) : (Statement, Names, Boolean) = {
//      println(s"procSt($s)")
      def accessHeap(e : Expression.T, ns : Names) =
        CallIndex(false, Ident(ns.last(constHeap), e.ann.pos), List((None, e)), e.ann.pos)
      def callme(arr : Expression.T) =
        CallIndex(false, arr, List((None, StringLiteral("\"callme\"", arr.ann.pos))), arr.ann.pos)
      def index(arr : Expression.T, ind : Int) =
        CallIndex(false, arr, List((None, IntLiteral(ind, arr.ann.pos))), arr.ann.pos)

      def ptr4ident(name : Ident) : Expression.T = {
        val s = scope(name.name)
        if (s == VarScope.Local || s == VarScope.Arg) (Ident(name + "Ptr", name.ann.pos)) else
          if (s == VarScope.NonLocal || s == VarScope.ImplicitNonLocal)
            (CallIndex(false, Ident("closure", name.ann.pos),
              List((None, StringLiteral("\"" + name + "\"", name.ann.pos))), name.ann.pos)) else
            Ident(name.name, name.ann.pos)
      }

      def procIdentStep(lhs : Boolean, e : Expression.T, ns : Names) : (EAfterPass, Names) = e match {
        case name@Ident(_, _) => (Left(accessHeap(ptr4ident(name), ns)), ns)
        case _ => (Left(e), ns)
      }
      def procIdentInRhs(e : Expression.T) = {
        val (Left(result), _) = SimplePass.procExpr(procIdentStep)(false, e, ns)
        result
      }
      def procCall(c : CallIndex, ns : Names) : CallIndex = {
        val whomName@Ident(_, _) = c.whom
        val whom = accessHeap(ptr4ident(whomName), ns)
        CallIndex(true, callme(whom),
          ((None, Ident(ns.last(constHeap), whom.ann.pos)) :: (None, whom) :: c.args), c.ann.pos)
      }
//      def procIdentInSt = SimplePass.procExprInStatement(procIdentStep)(_, _)
      s match {
        case FuncDef(name, args, None, None, body, Decorators(List()), vars, ann) =>
          def scope(name : String) =
            if (vars.contains(name)) vars(name) else (VarScope.Global, new GeneralAnnotation())
          val locals = vars.filter(z => z._2._1 == VarScope.Local || z._2._1 == VarScope.Arg).toList
          // todo: not completely correct, because a variable with value None is not the same as a variable without a value,
          // they behave differently when accessed
          val startHeap = CreateConst(ns.last(constHeap), Ident("heap", ann.pos), ann.pos)
          val (pushLocals, ns0) = locals.foldLeft((List[Statement](startHeap), ns))((acc, name) => {
            val ns = acc._2
            val pos = name._2._2
            val heap = Ident(ns.last(constHeap), pos)
            val (newHeap, ns1) = ns(constHeap)
            (
              acc._1 :+
              CreateConst(name + "Ptr", CallIndex(true, Ident("nextFreePtr", pos), List((None, heap)), pos), pos) :+
              CreateConst(newHeap, CallIndex(true, Ident("append2heap", pos),
                List((None, heap), (None, if (scope(name._1)._1 == VarScope.Arg) Ident(name._1, pos) else NoneLiteral(pos))), pos), pos),
              ns1
            )
          })

//          val pushLocals = (locals.map(name => Assign(List(Ident(name + "Ptr"),
//                CallIndex(true, Ident("mkNew"), List((None, Ident("heap")), (None, if (scope(name) == VarScope.Arg) Ident(name) else NoneLiteral())))
//            ))))
          val (body1, ns1) = SimplePass.procStatementGeneral(procSt(scope)(_, _))(body, ns0)
          val defaultReturn = Return(CollectionCons(CollectionKind.Tuple,
            List(Ident(ns1.last(constHeap), ann.pos), NoneLiteral(ann.pos)), ann.pos), ann.pos)
          val noNeedForDefaultReturn = true
          val body2 = if (noNeedForDefaultReturn) (pushLocals :+ body1) else  (pushLocals :+ body1 :+ defaultReturn)
          val (List(newHeap, newClosure, tmpFun), ns2) = ns1(List(constHeap, "newClosure", "tmpFun"))
          val f1 = FuncDef(tmpFun,
            ("heap", ArgKind.Positional, None, ann.pos) ::
              ("closure", ArgKind.Positional, None, ann.pos) ::
              args,
            None, None, Suite(body2, body1.ann.pos), Decorators(List()), HashMap(), ann.pos)
          val mkNewClosure = CreateConst(newClosure,
            DictCons(Left((StringLiteral("\"callme\"", ann.pos), Ident(tmpFun, ann.pos))) ::
              vars.filter(x => x._2._1 != VarScope.Global && x._2._1 != VarScope.Local && x._2._1 != VarScope.Arg).
              map(z => Left((StringLiteral("\"" + z._1 + "\"", ann.pos), Ident(z._1 + "Ptr", ann.pos)))).toList,
              ann.pos
            ),
            ann.pos
          )
          val newFun = Ident(newClosure, ann.pos)
          val mkFun = (CreateConst(newHeap, CallIndex(true, Ident("immArrChangeValue", ann.pos),
            List((None, Ident(ns.last(constHeap), ann.pos)), (None, ptr4ident(Ident(name, ann.pos))), (None, newFun)),
            ann.pos), ann.pos))
          (Suite(List(f1, mkNewClosure, mkFun), ann.pos), ns2, false)

        case NonLocal(l, ann) => (Pass(ann.pos), ns, false)

        case Return(call@CallIndex(true, whom, args, _), ann) => (Return(procCall(call, ns), ann.pos), ns, false)

        case Return(x, ann) => (Return(
          CollectionCons(Expression.CollectionKind.Tuple, List(Ident(ns.last(constHeap), ann.pos), procIdentInRhs(x)), ann.pos),
          ann.pos
        ), ns, false)
//          val (Assign(List(x1)), ns1) = procIdentInSt(Assign(List(x)), ns)
//          (Suite(List(
//            Assign(List(Ident("retval"), x1)),
//            Return(CollectionCons(Expression.CollectionKind.Tuple, List(Ident("heap"), Ident("retval"))))
//          )), ns1, false)

//        case Assign(List(Ident(lhs), CallIndex(true, Ident(whomName), args))) =>
//          (Assign(List(Ident(lhs), CallIndex(true, index(Ident(whomName), 1), (None, index(Ident(whomName), 0)) :: args))), ns, true)
//
//        case Assign(List(CallIndex(_, _, _))) | Assign(List(_, CallIndex(_, _, _))) => ???

        case Assign(List(Ident(dstName, anni), call@CallIndex(true, pi@Ident("print", _), args, annc)), ann) =>
          (Assign(List(CallIndex(true, pi, args.map(x => (x._1, procIdentInRhs(x._2))), annc.pos)), ann.pos), ns, false)

        case Assign(List(dstId@Ident(_, _), call@CallIndex(true, Ident(_, _), _, _)), ann) =>
          val CallIndex(true, whom, args, _) = procIdentInRhs(call)
//          val (Suite(List(Assign(List(dst, CallIndex(true, whom, args))))), ns1) = procIdentInSt(s, ns)
          val (tmpResult, ns1) = ns("constTmpResult")
          val ptr = ptr4ident(dstId)
          val (newHeap, ns2) = ns1(constHeap)
//          val whom1 = Ident("tmpCall")
          (Suite(List(
//            Assign(List(whom1, whom)),
            CreateConst(tmpResult, procCall(call, ns), call.ann.pos),
            CreateConst(newHeap, CallIndex(true, Ident("immArrChangeValue", ann.pos),
              List((None, index(Ident(tmpResult, call.ann.pos), 0)), (None, ptr),
                (None, index(Ident(tmpResult, call.ann.pos), 1))), ann.pos), ann.pos)
          ), ann.pos), ns2, false)

        case Assign(List(dstName@Ident(_, _), rhs), ann) =>
          val (newHeap, ns1) = ns(constHeap)
          val rhs1 = procIdentInRhs(rhs)
          val ptr = ptr4ident(dstName)
          (CreateConst(newHeap, CallIndex(true, Ident("immArrChangeValue", ann.pos),
            List((None, Ident(ns.last(constHeap), ann.pos)), (None, ptr), (None, rhs1)), ann.pos), ann.pos), ns1, false)

        case IfSimple(cond, yes, no, ann) => (IfSimple(procIdentInRhs(cond), yes, no, ann.pos), ns, true)
        case While(cond, body, eelse, ann) => (While(procIdentInRhs(cond), body, eelse, ann.pos), ns, true)

        case _ => (s, ns, true)
      }
    }
    SimplePass.procStatementGeneral(procSt(_ => (VarScope.Global, new GeneralAnnotation()))(_, _))(
      SimpleAnalysis.computeAccessibleIdents(st), ns(constHeap)._2)

  }

}
