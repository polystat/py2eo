import ExplicitMutableHeap.{allFunsName, index}
import Expression.{BoolLiteral, CallIndex, CollectionCons, DictCons, Field, Ident, IntLiteral, NoneLiteral, StringLiteral}
import SimplePass.{Names, procExpr}

import scala.collection.immutable.HashMap

object ClosureWithCage {

  val callme = "callme"
  def index(arr : Expression.T, ind : String) =
    CallIndex(false, arr, List((None, StringLiteral("\"" + ind + "\"", arr.ann.pos))), arr.ann.pos)

  private def closurizeInner(scope : String => (VarScope.T, GeneralAnnotation), st : Statement) : (Statement) = {
    def pe(lhs : Boolean, e : Expression.T) = {
      val (Left(result), _) = procExpr((lhs, e, ns) => {
        e match {
          case Ident(name, ann)  =>
            val e1 = if (scope(name)._1 != VarScope.Arg && scope(name)._1 != VarScope.Local &&
              scope(name)._1 != VarScope.Global)
              index(Ident("closure", ann.pos), name) else
              e
            (Left(e1), ns)
          case CallIndex(true, whom, args, ann) => // todo: this implementation is incorrect, it evals whom twice! we must extract whom to a variable!
            (Left(CallIndex(true, index(whom, "callme"), (None, whom) :: args, ann.pos)), ns)
          case _ => (Left(e), ns)
        }
      })(lhs, e, Names(HashMap()))
      result
    }
    st match {
      case FuncDef(name, args, None, None, body, Decorators(List()), vars, isAsync, ann) =>
        def scope(name : String) = if (vars.contains(name)) vars(name) else (VarScope.Global, new GeneralAnnotation())
        val (body1) = closurizeInner(scope, body)
        val tmpFun = s"tmpFun$name"
        val f1 = FuncDef(tmpFun,
          ("closure", ArgKind.Positional, None, ann.pos) :: args,
          None, None, body1, Decorators(List()), HashMap(), isAsync, ann.pos)
        val rhs = DictCons(Left((StringLiteral("\"callme\"", ann.pos), Ident(tmpFun, ann.pos))) ::
          vars.filter(x => x._2._1 != VarScope.Global && x._2._1 != VarScope.Local && x._2._1 != VarScope.Arg).
            map(z => Left((StringLiteral("\"" + z._1 + "\"", ann.pos), Ident(z._1 + "Ptr", ann.pos)))).toList,
          ann.pos
        )
        Suite(List(f1, Assign(List(Ident(name, ann.pos), rhs), ann.pos)), ann.pos)

      case Assign(List(x), ann) => Assign(List(pe(false, x)), ann.pos)
      case Assign(List(lhs, rhs), ann) =>
        Assign(List(pe(true, lhs), pe(false, rhs)), ann.pos)

      case Return(x, ann) => (Return(pe(false, x), ann.pos))
      case IfSimple(cond, yes, no, ann) =>
        val (yes1) = closurizeInner(scope, yes)
        val (no1) = closurizeInner(scope, no)
        (IfSimple(pe(false, cond), yes1, no1, ann.pos))
      case Pass(_) | NonLocal(_, _) => (st)

      case Suite(l, ann) => Suite(l.map(closurizeInner(scope, _)), ann.pos)
    }
  }

  def closurize(st : Statement) : Statement = closurizeInner(_ => (VarScope.Global, new GeneralAnnotation()), st)

}
