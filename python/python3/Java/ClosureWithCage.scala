import ExplicitMutableHeap.{allFunsName, index}
import Expression.{BoolLiteral, CallIndex, CollectionCons, DictCons, Field, Ident, IntLiteral, NoneLiteral, StringLiteral}
import SimplePass.{Names, procExpr}

import scala.collection.immutable.HashMap

object ClosureWithCage {

  val callme = "callme"
  def index(arr : Expression.T, ind : String) = CallIndex(false, arr, List((None, StringLiteral("\"" + ind + "\""))))

  private def closurizeInner(scope : String => VarScope.T, st : Statement) : (Statement) = {
    def pe(lhs : Boolean, e : Expression.T) = {
      val (Left(result), _) = procExpr((lhs, e, ns) => {
        e match {
          case Ident(name)  =>
            val e1 = if (scope(name) != VarScope.Arg && scope(name) != VarScope.Local &&
              scope(name) != VarScope.Global)
              index(Ident("closure"), name) else
              e
            (Left(e1), ns)
          case CallIndex(true, whom, args) => // todo: this implementation is incorrect, it evals whom twice! we must extract whom to a variable!
            (Left(CallIndex(true, index(whom, "callme"), (None, whom) :: args)), ns)
          case _ => (Left(e), ns)
        }
      })(lhs, e, Names(HashMap()))
      result
    }
    st match {
      case FuncDef(name, args, None, None, body, Decorators(List()), vars) =>
        def scope(name : String) = if (vars.contains(name)) vars(name) else VarScope.Global
        val (body1) = closurizeInner(scope, body)
        val tmpFun = s"tmpFun$name"
        val f1 = FuncDef(tmpFun,
          ("closure", ArgKind.Positional, None) :: args,
          None, None, body1, Decorators(List()), HashMap())
        val rhs = DictCons(Left((StringLiteral("\"callme\""), Ident(tmpFun))) ::
          vars.filter(x => x._2 != VarScope.Global && x._2 != VarScope.Local && x._2 != VarScope.Arg).
            map(z => Left((StringLiteral("\"" + z._1 + "\""), Ident(z._1 + "Ptr")))).toList
        )
        Suite(List(f1, Assign(List(Ident(name), rhs))))

      case Assign(List(x)) => Assign(List(pe(false, x)))
      case Assign(List(lhs, rhs)) => Assign(List(pe(true, lhs), pe(false, rhs)))

      case Return(x) => (Return(pe(false, x)))
      case IfSimple(cond, yes, no) =>
        val (yes1) = closurizeInner(scope, yes)
        val (no1) = closurizeInner(scope, no)
        (IfSimple(pe(false, cond), yes1, no1))
      case NonLocal(l) => (WithoutArgs(StatementsWithoutArgs.Pass))
      case WithoutArgs(s) => (st)

      case Suite(l) => Suite(l.map(closurizeInner(scope, _)))
    }
  }

  def closurize(st : Statement) : Statement = closurizeInner(_ => VarScope.Global, st)

}
