import Expression.{CallIndex, CollectionCons, DictCons, Field, Ident, IntLiteral, NoneLiteral, StringLiteral}
import SimplePass.{EAfterPass, Names}

import scala.collection.immutable.HashMap

object ExplicitHeap {

  def explicitStackHeap(st : Statement, ns : Names)  = {
    def procSt(scope : String => VarScope.T, isArg : String => Boolean, upperVars : HashMap[String, VarScope.T])(
            s : Statement, ns : Names) : (Statement, Names, Boolean) = {
      println(s"procSt($s)")
      def accessHeap(e : Expression.T) = CallIndex(false, Ident("heap"), List((None, e)))
      def index(arr : Expression.T, ind : Int) = CallIndex(false, arr, List((None, IntLiteral(ind))))
      def procIdentStep(lhs : Boolean, e : Expression.T, ns : Names) : (EAfterPass, Names) = e match {
        case Ident(name) if !isArg(name) =>
          val v =
            if (scope(name) == VarScope.Local) accessHeap(Ident(name + "Ptr")) else
              if (scope(name) == VarScope.NonLocal) accessHeap(CallIndex(false, Ident("closure"), List((None, StringLiteral("\"" + name + "\""))))) else
                Ident(name)
          (Left(v), ns)
        case _ => (Left(e), ns)
      }
      def procIdentInRhs(e : Expression.T) = {
        val (Left(result), _) = SimplePass.procExpr(procIdentStep)(false, e, ns)
        result
      }
      def procIdentInSt = SimplePass.procExprInStatement(procIdentStep)(_, _)
      s match {
        case FuncDef(name, args, otherPositional, otherKeyword, body, decorators) =>
          val vars = SimpleAnalysis.classifyFunctionVariables(args, body, false)
          def scope(name : String) = vars(name)
          def isArg(name : String) = args.exists(x => x._1 == name)
          val locals = vars.filter(z => z._2 == VarScope.Local).keys.toList
          // todo: not completely correct, because a variable with value None is not the same as a variable without a value,
          // they behave differently when accessed
          val pushLocals = (locals.map(name => Assign(List(Ident(name + "Ptr"),
                CallIndex(true, Ident("mkNew"), List((None, if (isArg(name)) Ident(name) else NoneLiteral())))
            ))))
          val (body1, ns1) = SimplePass.procStatementGeneral(procSt(scope, isArg, vars)(_, _))(body, ns)
          val f1 = FuncDef("tmpFun", ("closure", ArgKind.Positional, None) :: args, otherPositional, otherKeyword,
            Suite((pushLocals :+ body1)), decorators)
          val mkNewClosure = Assign(List(Ident("newClosure"),
            DictCons(upperVars.filter(x => x._2 != VarScope.Global).
              map(z => Left((StringLiteral("\"" + z._1 + "\""), Ident(z._1 + "Ptr")))).toList)))
          val mkFun = Assign(List(accessHeap(Ident(name + "Ptr")), CollectionCons(Expression.CollectionKind.Tuple, List(Ident("newClosure"), Ident("tmpFun")))))
          (Suite(List(f1, mkNewClosure, mkFun)), ns1, false)

        case NonLocal(l) => (WithoutArgs(StatementsWithoutArgs.Pass), ns, false)

        case Return(x) =>
          val (Assign(List(x1)), ns1) = procIdentInSt(Assign(List(x)), ns)
          (Suite(List(Assign(List(Ident("retval"), x1)), Return(Ident("retval")))), ns1, false)

//        case Assign(List(Ident(lhs), CallIndex(true, Ident(whomName), args))) =>
//          (Assign(List(Ident(lhs), CallIndex(true, index(Ident(whomName), 1), (None, index(Ident(whomName), 0)) :: args))), ns, true)
//
//        case Assign(List(CallIndex(_, _, _))) | Assign(List(_, CallIndex(_, _, _))) => ???

        case Assign(List(Ident(_), CallIndex(true, _, _))) =>
          val (Suite(List(Assign(List(dst, CallIndex(true, whom, args))))), ns1) = procIdentInSt(s, ns)
          val whom1 = Ident("tmpCall")
          (Suite(List(
            Assign(List(whom1, whom)),
            Assign(List(dst, CallIndex(true, index(whom1, 1), (None, index(whom1, 0)) :: args)))
          )), ns1, false)

        case Assign(_) =>
          val (s1, ns1) = procIdentInSt(s, ns)
          (s1, ns1, false)

        case IfSimple(cond, yes, no) => (IfSimple(procIdentInRhs(cond), yes, no), ns, true)
        case While(cond, body, eelse) => (While(procIdentInRhs(cond), body, eelse), ns, true)

        case _ => (s, ns, true)
      }
    }
    SimplePass.procStatementGeneral(procSt(_ => VarScope.Global, _ => false, HashMap())(_, _))(st, ns)

  }

}
