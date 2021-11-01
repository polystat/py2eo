import Expression.{CallIndex, CollectionCons, CollectionKind, DictCons, Field, Ident, IntLiteral, NoneLiteral, StringLiteral}
import SimplePass.{EAfterPass, Names}

import scala.collection.immutable.HashMap

object ExplicitHeap {

  val constHeap = "constHeap"
  
  def explicitStackHeap(st : Statement, ns : Names)  = {
    def procSt(scope : String => VarScope.T)(
            s : Statement, ns : Names) : (Statement, Names, Boolean) = {
//      println(s"procSt($s)")
      def accessHeap(e : Expression.T, ns : Names) = CallIndex(false, Ident(ns.last(constHeap)), List((None, e)))
      def index(arr : Expression.T, ind : Int) = CallIndex(false, arr, List((None, IntLiteral(ind))))

      def ptr4ident(name : String) : Expression.T = {
        val s = scope(name)
        if (s == VarScope.Local || s == VarScope.Arg) (Ident(name + "Ptr")) else
          if (s == VarScope.NonLocal || s == VarScope.ImplicitNonLocal)
            (CallIndex(false, Ident("closure"), List((None, StringLiteral("\"" + name + "\""))))) else
            Ident(name)
      }

      def procIdentStep(lhs : Boolean, e : Expression.T, ns : Names) : (EAfterPass, Names) = e match {
        case Ident(name) => (Left(accessHeap(ptr4ident(name), ns)), ns)
        case _ => (Left(e), ns)
      }
      def procIdentInRhs(e : Expression.T) = {
        val (Left(result), _) = SimplePass.procExpr(procIdentStep)(false, e, ns)
        result
      }
      def procCall(c : CallIndex, ns : Names) : CallIndex = {
        val Ident(whomName) = c.whom
        val whom = accessHeap(ptr4ident(whomName), ns)
        CallIndex(true, index(whom, 1),
          ((None, Ident(ns.last(constHeap))) :: (None, index(whom, 0)) :: c.args))
      }
//      def procIdentInSt = SimplePass.procExprInStatement(procIdentStep)(_, _)
      s match {
        case FuncDef(name, args, otherPositional, otherKeyword, body, decorators, vars) =>
          def scope(name : String) = if (vars.contains(name)) vars(name) else VarScope.Global
          val locals = vars.filter(z => z._2 == VarScope.Local || z._2 == VarScope.Arg).keys.toList
          // todo: not completely correct, because a variable with value None is not the same as a variable without a value,
          // they behave differently when accessed
          val startHeap = CreateConst(ns.last(constHeap), Ident("heap"))
          val (pushLocals, ns0) = locals.foldLeft((List[Statement](startHeap), ns))((acc, name) => {
            val ns = acc._2
            val heap = Ident(ns.last(constHeap))
            val (newHeap, ns1) = ns(constHeap)
            (
              acc._1 :+
              CreateConst(name + "Ptr", CallIndex(true, Ident("nextFreePtr"), List((None, heap)))) :+
              CreateConst(newHeap, CallIndex(true, Ident("append2heap"),
                List((None, heap), (None, if (scope(name) == VarScope.Arg) Ident(name) else NoneLiteral())))),
              ns1
            )
          })

//          val pushLocals = (locals.map(name => Assign(List(Ident(name + "Ptr"),
//                CallIndex(true, Ident("mkNew"), List((None, Ident("heap")), (None, if (scope(name) == VarScope.Arg) Ident(name) else NoneLiteral())))
//            ))))
          val (body1, ns1) = SimplePass.procStatementGeneral(procSt(scope)(_, _))(body, ns0)
          val defaultReturn = Return(CollectionCons(CollectionKind.Tuple, List(Ident(ns1.last(constHeap)), NoneLiteral())))
          val noNeedForDefaultReturn = true
          val body2 = if (noNeedForDefaultReturn) (pushLocals :+ body1) else  (pushLocals :+ body1 :+ defaultReturn)
          val (List(newHeap, newClosure, tmpFun), ns2) = ns1(List(constHeap, "newClosure", "tmpFun"))
          val f1 = FuncDef(tmpFun,
            ("heap", ArgKind.Positional, None) ::
              ("closure", ArgKind.Positional, None) ::
              args,
            otherPositional, otherKeyword, Suite(body2), decorators, HashMap())
          val mkNewClosure = CreateConst(newClosure,
            DictCons(vars.filter(x => x._2 != VarScope.Global && x._2 != VarScope.Local && x._2 != VarScope.Arg).
              map(z => Left((StringLiteral("\"" + z._1 + "\""), Ident(z._1 + "Ptr")))).toList)
          )
          val newFun = CollectionCons(Expression.CollectionKind.Tuple, List(Ident(newClosure), Ident(tmpFun)))
          val mkFun = (CreateConst(newHeap, CallIndex(true, Ident("immArrChangeValue"),
            List((None, Ident(ns.last(constHeap))), (None, ptr4ident(name)), (None, newFun)))))
          (Suite(List(f1, mkNewClosure, mkFun)), ns2, false)

        case NonLocal(l) => (WithoutArgs(StatementsWithoutArgs.Pass), ns, false)

        case Return(call@CallIndex(true, whom, args)) => (Return(procCall(call, ns)), ns, false)

        case Return(x) => (Return(
          CollectionCons(Expression.CollectionKind.Tuple, List(Ident(ns.last(constHeap)), procIdentInRhs(x)))
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

        case Assign(List(Ident(dstName), call@CallIndex(true, Ident("print"), args))) =>
          (Assign(List(CallIndex(true, Ident("print"), args.map(x => (x._1, procIdentInRhs(x._2)))))), ns, false)

        case Assign(List(Ident(dstName), call@CallIndex(true, Ident(_), _))) =>
          val CallIndex(true, whom, args) = procIdentInRhs(call)
//          val (Suite(List(Assign(List(dst, CallIndex(true, whom, args))))), ns1) = procIdentInSt(s, ns)
          val (tmpResult, ns1) = ns("constTmpResult")
          val ptr = ptr4ident(dstName)
          val (newHeap, ns2) = ns1(constHeap)
//          val whom1 = Ident("tmpCall")
          (Suite(List(
//            Assign(List(whom1, whom)),
            CreateConst(tmpResult, procCall(call, ns)),
            CreateConst(newHeap, CallIndex(true, Ident("immArrChangeValue"),
              List((None, index(Ident(tmpResult), 0)), (None, ptr), (None, index(Ident(tmpResult), 1)))))
          )), ns2, false)

        case Assign(List(Ident(dstName), rhs)) =>
          val (newHeap, ns1) = ns(constHeap)
          val rhs1 = procIdentInRhs(rhs)
          val ptr = ptr4ident(dstName)
          (CreateConst(newHeap, CallIndex(true, Ident("immArrChangeValue"),
            List((None, Ident(ns.last(constHeap))), (None, ptr), (None, rhs1)))), ns1, false)

        case IfSimple(cond, yes, no) => (IfSimple(procIdentInRhs(cond), yes, no), ns, true)
        case While(cond, body, eelse) => (While(procIdentInRhs(cond), body, eelse), ns, true)

        case _ => (s, ns, true)
      }
    }
    SimplePass.procStatementGeneral(procSt(_ => VarScope.Global)(_, _))(SimpleAnalysis.computeAccessibleIdents(st), ns(constHeap)._2)

  }

}
