package org.polystat.py2eo

import Expression.{CallIndex, Parameter, CollectionCons, CollectionKind, DictCons, Ident, IntLiteral, NoneLiteral, StringLiteral}
import SimplePass.{EAfterPass, Names}

import scala.collection.immutable.HashMap


object ExplicitImmutableHeap {

  val constHeap = "constHeap"
  val ptrSuff = "Ptr"
  val immArrChangeValue = "immArrChangeValue"

  def explicitHeap(st : Statement, ns : Names): (Statement, Names) = {
    def procSt(scope : String => (VarScope.T, GeneralAnnotation))(s : Statement, ns : Names) : (Statement, Names, Boolean) = {
      def accessHeap(e : Expression.T, ns : Names) =
        CallIndex(isCall = false, Ident(ns.last(constHeap), e.ann.pos), List((None, e)), e.ann.pos)
      def callme(arr : Expression.T) =
        CallIndex(isCall = false, arr, List((None, StringLiteral(List("\"callme\""), arr.ann.pos))), arr.ann.pos)
      def index(arr : Expression.T, ind : Int) =
        CallIndex(isCall = false, arr, List((None, IntLiteral(ind, arr.ann.pos))), arr.ann.pos)
      def ptr4ident(name : Ident) : Expression.T = {
        val s = scope(name.name)
        if (s._1 == VarScope.Local || s._1 == VarScope.Arg) Ident(name.name + ptrSuff, name.ann.pos) else
        if (s._1 == VarScope.NonLocal || s._1 == VarScope.ImplicitNonLocal) {
          CallIndex(
            isCall = false, Ident("closure", name.ann.pos),
            List((None, StringLiteral(List(s"\"${name.name}\""), name.ann.pos))), name.ann.pos
          )
        } else {
          Ident(name.name, name.ann.pos)
        }
      }
      def procIdentStep(lhs : Boolean, e : Expression.T, ns : Names) : (EAfterPass, Names) = e match {
        case name@Ident(_, _) => (Left(accessHeap(ptr4ident(name), ns)), ns)
        case _ => (Left(e), ns)
      }
      def procIdentInRhs(e : Expression.T) = {
        val (Left(result), _) = SimplePass.procExpr(procIdentStep)(lhs = false, e, ns)
        result
      }
      def procCall(c : CallIndex, ns : Names) : CallIndex = {
        val whomName@Ident(_, _) = c.whom
        val whom = accessHeap(ptr4ident(whomName), ns)
        CallIndex(
          isCall = true, callme(whom),
          (None, Ident(ns.last(constHeap), whom.ann.pos)) :: (None, whom) :: c.args, c.ann.pos
        )
      }
      s match {
        case FuncDef(name, args, None, None, None, body, Decorators(List()), vars, isAsync, ann) =>
          assert(!isAsync)
          def scope(name : String) =
            if (vars.contains(name)) vars(name) else (VarScope.Global, new GeneralAnnotation())
          val locals = vars.filter(z => z._2._1 == VarScope.Local || z._2._1 == VarScope.Arg).toList
          // todo: not completely correct, because a variable with value None is not the same as a variable without a value,
          // they behave differently when accessed
          val startHeap = CreateConst(ns.last(constHeap), Ident("heap", ann.pos), ann.pos)
          val (pushLocals, ns0) = locals.foldLeft((List[Statement](startHeap), ns))(
            (acc, name) => {
              val ns = acc._2
              val pos = name._2._2
              val heap = Ident(ns.last(constHeap), pos)
              val (newHeap, ns1) = ns(constHeap)
              (
                acc._1 :+
                CreateConst(name._1 + ptrSuff, CallIndex(isCall = true, Ident("nextFreePtr", pos), List((None, heap)), pos), pos) :+
                CreateConst(
                  newHeap,
                  CallIndex(
                    isCall = true, Ident("append2heap", pos),
                    List((None, heap), (None, if (scope(name._1)._1 == VarScope.Arg) Ident(name._1, pos) else NoneLiteral(pos))),
                    pos
                  ),
                  pos
                ),
                ns1
              )
            }
          )
          val (body1, ns1) = SimplePass.procStatementGeneral(procSt(scope)(_, _))(body, ns0)
          val defaultReturn = Return(
            Some(
              CollectionCons(
                CollectionKind.Tuple,
                List(Ident(ns1.last(constHeap), ann.pos), NoneLiteral(ann.pos)), ann.pos
              )
            ),
            ann.pos
          )
          val noNeedForDefaultReturn = true
          val body2 = if (noNeedForDefaultReturn) pushLocals :+ body1 else  pushLocals :+ body1 :+ defaultReturn
          val (List(newHeap, newClosure, tmpFun), ns2) = ns1(List(constHeap, "newClosure", "tmpFun"))
          val f1 = FuncDef(
            tmpFun,
            Parameter("heap", ArgKind.Positional, None, None, ann.pos) ::
              Parameter("closure", ArgKind.Positional, None, None, ann.pos) ::
              args,
            None, None, None, Suite(body2, body1.ann.pos), Decorators(List()), HashMap(), isAsync, ann.pos
          )
          val mkNewClosure = CreateConst(
            newClosure,
            DictCons(
              Left((StringLiteral(List("\"callme\""), ann.pos), Ident(tmpFun, ann.pos))) ::
                vars.filter(x => x._2._1 != VarScope.Global && x._2._1 != VarScope.Local && x._2._1 != VarScope.Arg).
                map(z => Left((StringLiteral(List("\"" + z._1 + "\""), ann.pos), Ident(z._1 + "Ptr", ann.pos)))).toList,
              ann.pos
            ),
            ann.pos
          )
          val newFun = Ident(newClosure, ann.pos)
          val mkFun = CreateConst(
            newHeap,
            CallIndex(
              isCall = true, Ident(immArrChangeValue, ann.pos),
              List((None, Ident(ns.last(constHeap), ann.pos)), (None, ptr4ident(Ident(name, ann.pos))), (None, newFun)),
              ann.pos
            ),
            ann.pos
          )
          (Suite(List(f1, mkNewClosure, mkFun), ann.pos), ns2, false)
        case Return(Some(call@CallIndex(true, _, _, _)), ann) => (Return(Some(procCall(call, ns)), ann.pos), ns, false)
        case Return(x, ann) => (
          Return(
            x.map(
              x => CollectionCons(
                Expression.CollectionKind.Tuple, List(Ident(ns.last(constHeap), ann.pos), procIdentInRhs(x)), ann.pos
              )
            ),
            ann.pos
          ),
          ns, false
        )
        case Assign(List(Ident(_, _), CallIndex(true, pi@Ident("print", _), args, annc)), ann) => (
          Assign(List(CallIndex(isCall = true, pi, args.map(x => (x._1, procIdentInRhs(x._2))), annc.pos)), ann.pos),
          ns, false
        )
        case Assign(List(dstId@Ident(_, _), call@CallIndex(true, Ident(_, _), _, _)), ann) =>
          val CallIndex(true, _, _, _) = procIdentInRhs(call)
          val (tmpResult, ns1) = ns("constTmpResult")
          val ptr = ptr4ident(dstId)
          val (newHeap, ns2) = ns1(constHeap)
          (
            Suite(
              List(
                CreateConst(tmpResult, procCall(call, ns), call.ann.pos),
                CreateConst(
                  newHeap,
                  CallIndex(
                    isCall = true, Ident(immArrChangeValue, ann.pos),
                    List(
                      (None, index(Ident(tmpResult, call.ann.pos), 0)), (None, ptr),
                      (None, index(Ident(tmpResult, call.ann.pos), 1))
                    ),
                    ann.pos
                  ),
                  ann.pos
                )
              ),
              ann.pos
            ),
            ns2, false
          )
        case Assign(List(dstName@Ident(_, _), rhs), ann) =>
          val (newHeap, ns1) = ns(constHeap)
          val rhs1 = procIdentInRhs(rhs)
          val ptr = ptr4ident(dstName)
          (
            CreateConst(
              newHeap, CallIndex(isCall = true, Ident(immArrChangeValue, ann.pos),
              List((None, Ident(ns.last(constHeap), ann.pos)), (None, ptr), (None, rhs1)), ann.pos), ann.pos
            ),
            ns1, false
          )
        case IfSimple(cond, yes, no, ann) => (IfSimple(procIdentInRhs(cond), yes, no, ann.pos), ns, true)
        case While(cond, body, eelse, ann) => (While(procIdentInRhs(cond), body, eelse, ann.pos), ns, true)
        case NonLocal(_, ann) => (Pass(ann.pos), ns, false)
        case _ => (s, ns, true)
      }
    }
    SimplePass.procStatementGeneral(procSt(_ => (VarScope.Global, new GeneralAnnotation()))(_, _))(
      SimpleAnalysis.computeAccessibleIdents(st), ns(constHeap)._2
    )
  }

}
