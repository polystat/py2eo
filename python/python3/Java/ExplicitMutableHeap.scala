import ExplicitImmutableHeap.constHeap
import Expression.{BoolLiteral, CallIndex, CollectionCons, DictCons, Field, Ident, IntLiteral, NoneLiteral, StringLiteral}
import SimplePass.{EAfterPass, Names, procExpr, procExprInStatement, procStatement, procStatementGeneral}

import scala.collection.immutable.HashMap

object ExplicitMutableHeap {

  val globalHeap = "theHeap"
  val allFunsName = "allFuns"

  def heapGet(e: Expression.T) = CallIndex(true, Field(Ident(globalHeap), "get"), List((None, e)))
  def index(arr: Expression.T, ind: Expression.T) = CallIndex(false, arr, List((None, ind)))
  def mkNew(e : Expression.T) = CallIndex(true, Field(Ident(globalHeap), "new"), List((None, e)))

  def explicitHeap(st: Statement, ns: Names) = {

//      def callme(arr: Expression.T) = CallIndex(false, arr, List((None, StringLiteral("\"callme\""))))

//    def unnamePointers(lhs: Boolean, e: Expression.T, ns: Names): (EAfterPass, Names) = {
//
//      procExpr((lhs, e, ns) => {
//        println(s"unnamePointers($lhs, $e)")
//        e match {
//          //          case Field(Ident(fname), _) if fname == globalHeap => (Left(e), ns)
////          case CallIndex(true, Field(Ident(fname), _), _) if fname == globalHeap => (Left(e), ns)
////          case Ident(name) if !lhs && name != globalHeap => (Left(heapGet(e)), ns)
////          case CallIndex(true, whom, args) => // todo: this implementation is incorrect, it evals whom twice! we must extract whom to a variable!
////            val args2 = args.map {
////              case x@((None, NoneLiteral()) | (None, BoolLiteral(_)) | (None, IntLiteral(_)) | (None, StringLiteral(_))) =>
////                (None, mkNew(x._2))
////              case x@(None, _) => x
////            }
////            (Left(CallIndex(true, index(Ident(allFunsName), index(whom, StringLiteral("\"callme\""))), (None, whom) :: args2)), ns)
//          case _ => (Left(e), ns)
//        }
//      })(lhs, e, ns)
//    }
//
//    val z = procExprInStatement(unnamePointers)(st, ns)

//    println("======================================================================================")

    def procSt(scope : String => VarScope.T, st : Statement, functions : List[FuncDef]) : (Statement, List[FuncDef]) = {
      def pe(lhs : Boolean, e : Expression.T) = {
        val (Left(result), _) = procExpr((lhs, e, ns) => {
          println(s" anonymous $e")
          e match {
            case CallIndex(true, Field(Ident(fname), _), _) if fname == globalHeap => (Left(e), ns)
            case Ident(name)  =>
              val e1 = if (scope(name) != VarScope.Arg && scope(name) != VarScope.Local &&
                    name != globalHeap && name != allFunsName && name != "closure")
                index(Ident("closure"), StringLiteral("\"" + name + "\"")) else
                e
              val e2 = if (!lhs && name != globalHeap && name != allFunsName && name != "closure")
                heapGet(e1) else e1
              (Left(e2), ns)
            case CallIndex(true, whom, args) => // todo: this implementation is incorrect, it evals whom twice! we must extract whom to a variable!
              val args2 = args.map {
                case x@((None, NoneLiteral()) | (None, BoolLiteral(_)) | (None, IntLiteral(_)) | (None, StringLiteral(_))) =>
                  (None, mkNew(x._2))
                case x@(None, _) => x
              }
              (Left(CallIndex(true, index(Ident(allFunsName), index(whom, StringLiteral("\"callme\""))), (None, whom) :: args2)), ns)
            case _ => (Left(e), ns)
          }
        })(lhs, e, Names(HashMap()))
        result
      }
      st match {
        case FuncDef(name, args, None, None, body, Decorators(List()), vars) =>
          def scope(name : String) = if (vars.contains(name)) vars(name) else VarScope.Global
          val (body1, fs1) = procSt(scope, body, functions)
          val newName = s"tmpFun${fs1.size}"
          val f1 = FuncDef(newName,
              ("closure", ArgKind.Positional, None) :: args,
            None, None, (body1), Decorators(List()), HashMap())
          val fs2 = fs1 :+ f1
          val rhs = DictCons(Left((StringLiteral("\"callme\""), IntLiteral(fs1.size))) ::
            vars.filter(x => x._2 != VarScope.Global && x._2 != VarScope.Local && x._2 != VarScope.Arg).
              map(z => Left((StringLiteral("\"" + z._1 + "\""), Ident(z._1)))).toList
          )
          (Assign(List(CallIndex(true, Field(Ident(globalHeap), "set"), List(Ident(name), rhs).map(x => (None, x))))), fs2)

        case Assign(List(lhs, rhs)) => (Assign(List(pe(true, lhs), mkNew(pe(false, rhs)))), functions)
        case Return(x) => (Return(pe(false, x)), functions)
        case IfSimple(cond, yes, no) =>
          val (yes1, f1) = procSt(scope, yes, functions)
          val (no1, f2) = procSt(scope, no, f1)
          (IfSimple(pe(false, cond), yes1, no1), f2)
        case NonLocal(l) => (WithoutArgs(StatementsWithoutArgs.Pass), functions)
        case WithoutArgs(s) => (st, functions)

        case Suite(l) =>
          val (l1, fs) = l.foldLeft((List[Statement](), functions))((acc, st) => {
            val (st1, fs) = procSt(scope, st, acc._2)
            (acc._1 :+ st1, fs)
          })
          (Suite(l1), fs)
      }
    }

    val (rest, allFuns) = procSt((_ => VarScope.Global), SimpleAnalysis.computeAccessibleIdents(st), List())

    val outst = Suite(
      allFuns :+
      Assign(List(Ident("allFuns"), CollectionCons(Expression.CollectionKind.List, allFuns.map(f => Ident(f.name))))) :+
      rest
    )

    (outst, ns)

  }

}
