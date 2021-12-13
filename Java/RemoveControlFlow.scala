import Expression.{CallIndex, Ident, NoneLiteral}
import SimplePass.Names

import scala.collection.immutable.HashMap

object RemoveControlFlow {

  def inner(headLabel : Ident, afterLabel : Ident, breakTarget : Ident, s : Statement, ns : Names) :
        (List[(Ident, Statement)], Boolean, Names) = {
//    def st2Fun(headLabel : String, afterLabel : String, x : Statement) =
//      FuncDef(headLabel, List(), None, None, Suite(List(x, Return(CallIndex(true, Ident(afterLabel), List())))), new Decorators(List()))
    def goto(label : Ident, ann : GeneralAnnotation) =
      Return(Some(CallIndex(true, label, List(), ann.pos)), ann.pos)
    def mkPhi(labels : List[String]) = {}
    s match {
      case IfSimple(cond, yes, no, ann) =>
        val (List(yesHead0, noHead0), ns1) = ns(List("bb_yes", "bb_no"))
        val yesHead = Ident(yesHead0, yes.ann.pos)
        val noHead = Ident(noHead0, no.ann.pos)
        val (yes1, by, ns2) = inner(yesHead, afterLabel, breakTarget, yes, ns1)
        val (no1, bn, ns3) =  inner(noHead, afterLabel, breakTarget, no, ns2)
        val b = by && bn
        val ans = (headLabel, IfSimple(cond, goto(yesHead, cond.ann), goto(noHead, cond.ann), ann.pos)) :: (yes1 ++ no1)
        (ans, b, ns3)

      case While(cond, body, eelse, ann) =>
        val (List(bodyHead0, elseHead0), ns1) = ns(List("bb_body", "bb_else"))
        val bodyHead = Ident(bodyHead0, body.ann.pos)
        val elseHead = Ident(elseHead0, eelse.ann.pos)
        val (body1, bb, ns2) = inner(bodyHead, headLabel, afterLabel, body, ns1)
        val (eelse1, be, ns3) = inner(elseHead, afterLabel, breakTarget, eelse, ns2)
        val b = bb && be
        val ans = (headLabel, IfSimple(cond, goto(bodyHead, cond.ann), goto(elseHead, cond.ann), cond.ann.pos)) :: (body1 ++ eelse1)
        (ans, b, ns3)

      case Suite(l, ann) =>
        def f(h : Ident, a : Ident, l : List[Statement], ns : Names) : (List[(Ident, Statement)], Boolean, Names) = l match {
          case List(s) => inner(h, a, breakTarget, s, ns)
          case ::(head, next) =>
            val (intermLabel0, ns1) = ns("bb_interm")
            val intermLabel = Ident(intermLabel0, next.head.ann.pos)
            val (head1, bh, ns2) = inner(h, intermLabel, breakTarget, head, ns1)
            val (next1, bn, ns3) = f(intermLabel, a, next, ns2)
            (head1 ++ next1, bh && bn, ns3)
        }
        f(headLabel, afterLabel, l, ns)

      case Break(ann) => (List((headLabel, goto(breakTarget, ann))), false, ns)
      case Continue(_) | Raise(_, _, _) | ClassDef(_, _, _, _, _) => ???

      case FuncDef(name, args, None, None, None,  body, _, accessibleIdents, isAsync, ann) =>
        assert(!isAsync)
        val vars = accessibleIdents.toList
        val nonlocals = NonLocal(vars.filter(z => z._2._1 == VarScope.Local || z._2._1 == VarScope.NonLocal).map(_._1), ann.pos)
        val (List(headLabelInner0, afterLabelInner0), ns1) = ns(List("bb_start", "bb_finish"))
        val headLabelInner = Ident(headLabelInner0, body.ann.pos)
        val afterLabelInner = Ident(afterLabelInner0, new GeneralAnnotation())
        val (body1, b, ns2) = inner(headLabelInner, afterLabelInner, Ident("", new GeneralAnnotation()), body, ns1)
        // todo: this is not at all correct, because an access to a local variable with value None may lead to
        // a dynamic type error, while an access to a variable before assignment leads to exception
        // UnboundLocalError: local variable 'x' referenced before assignment
        // I'm not sure that this behaviour can be represented with a py2py pass
        val locals = (vars.filter(z => z._2._1 == VarScope.Local).map(z => (Ident(z._1, z._2._2), ())) ++ body1 :+ (afterLabelInner, ())).
          map(z => Assign(List(z._1, NoneLiteral(z._1.ann.pos)), z._1.ann.pos))
        val body2 = body1.map(z => FuncDef(z._1.name, List(), None, None, None,
          (if (nonlocals.l.nonEmpty) Suite(List(nonlocals, z._2), z._2.ann.pos) else z._2),
          Decorators(List()), HashMap(), isAsync, z._2.ann.pos)
        )
        val finish = FuncDef(afterLabelInner.name, List(), None, None, None,
          Return(Some(NoneLiteral(afterLabelInner.ann.pos)), afterLabelInner.ann.pos), Decorators(List()),
          HashMap(), isAsync, afterLabelInner.ann.pos)
        val ans = FuncDef(name, args, None, None, None, Suite(
          locals ++ body2 :+ finish :+ goto(headLabelInner, ann), ann.pos
        ), Decorators(List()), HashMap(), isAsync, ann.pos)
        (List((headLabel, Suite(List(ans, goto(afterLabel, new GeneralAnnotation())), body.ann.pos))), b, ns2)

        // todo: a hack: just throw away all the import statements
      case ImportModule(_, _, _) | ImportSymbol(_, _, _, _) | ImportAllSymbols(_, _) =>
        inner(headLabel, afterLabel, breakTarget, Pass(headLabel.ann.pos), ns)

      case _ => (List((headLabel, Suite(List(s, goto(afterLabel, new GeneralAnnotation())), s.ann.pos))), false, ns)
    }
  }

  // this pass currently only works for files with a single function, which is the test function
  def removeControlFlow(s : Statement, ns : Names) : (Statement, Names) = {
    SimpleAnalysis.checkIsSimplified(s)
    val (Suite(l, _), _) = SimplePass.procStatement(SimplePass.unSuite)(s, SimplePass.Names(HashMap()))
    val List(f@FuncDef(_, _, _, _, _, _, _, _, _, _)) = l.filter({
      case ImportModule(_, _, _) | ImportSymbol(_, _, _, _) | ImportAllSymbols(_, _) => false
      case _ => true
    })
    val (List((_, s1)), _, ns1) = inner(Ident("", s.ann.pos), Ident("", new GeneralAnnotation()), Ident("", new GeneralAnnotation()),
      SimpleAnalysis.computeAccessibleIdents(f), ns)
    (s1, ns1)
  }

}
