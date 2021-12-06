import Expression.{CallIndex, Ident, NoneLiteral}
import SimplePass.Names

import scala.collection.immutable.HashMap

object RemoveControlFlow {

  def inner(headLabel : String, afterLabel : String, breakTarget : String, s : Statement, ns : Names) :
        (List[(String, Statement)], Boolean, Names) = {
//    def st2Fun(headLabel : String, afterLabel : String, x : Statement) =
//      FuncDef(headLabel, List(), None, None, Suite(List(x, Return(CallIndex(true, Ident(afterLabel), List())))), new Decorators(List()))
    def goto(label : String) =
      Return(CallIndex(true, Ident(label), List()))
    def mkPhi(labels : List[String]) = {}
    s match {
      case IfSimple(cond, yes, no) =>
        val (List(yesHead, noHead), ns1) = ns(List("bb_yes", "bb_no"))
        val (yes1, by, ns2) = inner(yesHead, afterLabel, breakTarget, yes, ns1)
        val (no1, bn, ns3) =  inner(noHead, afterLabel, breakTarget, no, ns2)
        val b = by && bn
        val ans = (headLabel, IfSimple(cond, goto(yesHead), goto(noHead))) :: (yes1 ++ no1)
        (ans, b, ns3)

      case While(cond, body, eelse) =>
        val (List(bodyHead, elseHead), ns1) = ns(List("bb_body", "bb_else"))
        val (body1, bb, ns2) = inner(bodyHead, headLabel, afterLabel, body, ns1)
        val (eelse1, be, ns3) = inner(elseHead, afterLabel, breakTarget, eelse, ns2)
        val b = bb && be
        val ans = (headLabel, IfSimple(cond, goto(bodyHead), goto(elseHead))) :: (body1 ++ eelse1)
        (ans, b, ns3)

      case Suite(l) =>
        def f(h : String, a : String, l : List[Statement], ns : Names) : (List[(String, Statement)], Boolean, Names) = l match {
          case List(s) => inner(h, a, breakTarget, s, ns)
          case ::(head, next) =>
            val (intermLabel, ns1) = ns("bb_interm")
            val (head1, bh, ns2) = inner(h, intermLabel, breakTarget, head, ns1)
            val (next1, bn, ns3) = f(intermLabel, a, next, ns2)
            (head1 ++ next1, bh && bn, ns3)
        }
        f(headLabel, afterLabel, l, ns)

      case WithoutArgs(StatementsWithoutArgs.Break) => (List((headLabel, goto(breakTarget))), false, ns)
      case WithoutArgs(StatementsWithoutArgs.Continue) | Raise(_, _) | ClassDef(_, _, _, _) => ???

      case FuncDef(name, args, None, None, body, _, accessibleIdents) =>
        val vars = accessibleIdents.toList
        val nonlocals = NonLocal(vars.filter(z => z._2 == VarScope.Local || z._2 == VarScope.NonLocal).map(_._1))
        val (List(headLabelInner, afterLabelInner), ns1) = ns(List("bb_start", "bb_finish"))
        val (body1, b, ns2) = inner(headLabelInner, afterLabelInner, "", body, ns1)
        // todo: this is not at all correct, because an access to a local variable with value None may lead to
        // a dynamic type error, while an access to a variable before assignment leads to exception
        // UnboundLocalError: local variable 'x' referenced before assignment
        // I'm not sure that this behaviour can be represented with a py2py pass
        val locals = (vars.filter(z => z._2 == VarScope.Local) ++ body1 :+ (afterLabelInner, ())).
          map(z => Assign(List(Ident(z._1), NoneLiteral())))
        val body2 = body1.map(z => FuncDef(z._1, List(), None, None,
          (if (nonlocals.l.nonEmpty) Suite(List(nonlocals, z._2)) else z._2),
          Decorators(List()), HashMap())
        )
        val finish = FuncDef(afterLabelInner, List(), None, None, Return(NoneLiteral()), Decorators(List()), HashMap())
        val ans = FuncDef(name, args, None, None, Suite(
          locals ++ body2 :+ finish :+ goto(headLabelInner)
        ), Decorators(List()), HashMap())
        (List((headLabel, Suite(List(ans, goto(afterLabel))))), b, ns2)

        // todo: a hack: just throw away all the import statements
      case ImportModule(_, _) | ImportSymbol(_, _, _) | ImportAllSymbols(_) =>
        inner(headLabel, afterLabel, breakTarget, WithoutArgs(StatementsWithoutArgs.Pass), ns)

      case _ => (List((headLabel, Suite(List(s, goto(afterLabel))))), false, ns)
    }
  }

  // this pass currently only works for files with a single function, which is the test function
  def removeControlFlow(s : Statement, ns : Names) : (Statement, Names) = {
    SimpleAnalysis.checkIsSimplified(s)
    val Suite(l) = s
    val List(f@FuncDef(_, _, _, _, _, _, _)) = l.filter({
      case ImportModule(_, _) | ImportSymbol(_, _, _) | ImportAllSymbols(_) => false
      case _ => true
    })
    val (List((_, s1)), _, ns1) = inner("", "", "",
      SimpleAnalysis.computeAccessibleIdents(f), ns)
    (s1, ns1)
  }

}
