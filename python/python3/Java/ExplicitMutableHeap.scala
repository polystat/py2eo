import ExplicitImmutableHeap.constHeap
import Expression.{BoolLiteral, CallIndex, Field, Ident, IntLiteral, NoneLiteral, StringLiteral}
import SimplePass.{EAfterPass, Names, procExpr, procExprInStatement, procStatement}

object ExplicitMutableHeap {

  val globalHeap = "theHeap"

  def heapGet(e: Expression.T) = CallIndex(true, Field(Ident(globalHeap), "get"), List((None, e)))

  def explicitHeap(st: Statement, ns: Names) = {

      def mkNew(e : Expression.T) = CallIndex(true, Field(Ident(globalHeap), "new"), List((None, e)))

//      def callme(arr: Expression.T) = CallIndex(false, arr, List((None, StringLiteral("\"callme\""))))

      def index(arr: Expression.T, ind: Int) = CallIndex(false, arr, List((None, IntLiteral(ind))))

      def unnamePointersAndBoxConstantsInArgs(lhs: Boolean, e: Expression.T, ns: Names): (EAfterPass, Names) = {
        println(s"unnamePointersAndBoxConstantsInArgs($lhs, $e)")
        e match {
          //          case Field(Ident(fname), _) if fname == globalHeap => (Left(e), ns)
          //          case CallIndex(true, Field(Ident(fname), _), _) if fname == globalHeap => (Left(e), ns)
          case Ident(_) | Field(_, _) if !lhs => (Left(heapGet(e)), ns)
          case CallIndex(true, whom, args) =>
            val args2 = args.map {
              case x@((None, NoneLiteral()) | (None, BoolLiteral(_)) | (None, IntLiteral(_)) | (None, StringLiteral(_))) =>
                (None, mkNew(x._2))
              case x@(None, _) => x
            }
            (Left(CallIndex(true, whom, args2)), ns)
          case _ => (Left(e), ns)
        }
      }

      val z = procExprInStatement(procExpr(unnamePointersAndBoxConstantsInArgs))(st, ns)

      procStatement((st, ns) => st match {
        case Assign(List(lhs, rhs)) => (Assign(List(lhs, mkNew(rhs))), ns)
        case _ => (st, ns)
      })(z._1, z._2)

  }

}
