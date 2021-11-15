import ExplicitImmutableHeap.constHeap
import Expression.{BoolLiteral, CallIndex, CollectionCons, DictCons, Field, Ident, IntLiteral, NoneLiteral, StringLiteral}
import SimplePass.{EAfterPass, Names, procExpr, procExprInStatement, procStatement, procStatementGeneral}

import scala.collection.immutable.HashMap

object ExplicitMutableHeap {

  val valueHeap = "valuesHeap"
  val ptrHeap = "indirHeap"
  val allFunsName = "allFuns"

  def valueGet(e: Expression.T) = CallIndex(true, Field(Ident(valueHeap), "get"), List((None, e)))
  def ptrGet(e: Expression.T) = CallIndex(true, Field(Ident(ptrHeap), "get"), List((None, e)))
  def index(arr: Expression.T, ind: Expression.T) = CallIndex(false, arr, List((None, ind)))
  def newValue(e : Expression.T) = CallIndex(true, Field(Ident(valueHeap), "new"), List((None, e)))
  def newPtr(e : Expression.T) = CallIndex(true, Field(Ident(ptrHeap), "new"), List((None, e)))

  def explicitHeap(st: Statement, ns: Names) = {

    def procSt(scope : String => VarScope.T, st : Statement, functions : List[FuncDef]) : (Statement, List[FuncDef]) = {
      def pe(lhs : Boolean, e : Expression.T) = {
        val (Left(result), _) = procExpr((lhs, e, ns) => {
          e match {
            case CallIndex(true, Field(Ident(fname), _), _) if fname == valueHeap || fname == ptrHeap => (Left(e), ns)
            case Ident(name)  =>
              val e1 = if (scope(name) != VarScope.Arg && scope(name) != VarScope.Local &&
                    !List(valueHeap, ptrHeap, allFunsName, "closure").contains(name))
                index(Ident("closure"), StringLiteral("\"" + name + "\"")) else
                e
              val e2 = if (List(valueHeap, ptrHeap, allFunsName, "closure").contains(name)) e1 else
                if (!lhs) valueGet(ptrGet(e1)) else ptrGet(e1)
              (Left(e2), ns)
            case CallIndex(true, whom, args) => // todo: this implementation is incorrect, it evals whom twice! we must extract whom to a variable!
              val args2 = args.map {
                case x@((None, NoneLiteral()) | (None, BoolLiteral(_)) | (None, IntLiteral(_)) | (None, StringLiteral(_))) =>
                  (None, newValue(x._2))
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
          val createLocals = vars.filter(x => x._2 == VarScope.Local).map(x => Assign(List(Ident(x._1), newPtr(IntLiteral(0)))))
          val newName = s"tmpFun${fs1.size}"
          val f1 = FuncDef(newName,
              ("closure", ArgKind.Positional, None) :: args,
            None, None, Suite(createLocals.toList :+ body1), Decorators(List()), HashMap())
          val fs2 = fs1 :+ f1
          val rhs = DictCons(Left((StringLiteral("\"callme\""), IntLiteral(fs1.size))) ::
            vars.filter(x => x._2 != VarScope.Global && x._2 != VarScope.Local && x._2 != VarScope.Arg).
              map(z => Left((StringLiteral("\"" + z._1 + "\""), Ident(z._1)))).toList
          )
          (Assign(List(ptrGet(Ident(name)), newValue(rhs))), fs2)

        case Assign(List(lhs, rhs)) => (Assign(List(pe(true, lhs), newValue(pe(false, rhs)))), functions)
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
