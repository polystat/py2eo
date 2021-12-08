import ExplicitImmutableHeap.constHeap
import Expression.{BoolLiteral, CallIndex, CollectionCons, DictCons, Field, Ident, IntLiteral, NoneLiteral, StringLiteral}
import SimplePass.{EAfterPass, Names, procExpr, procExprInStatement, procStatement, procStatementGeneral}

import scala.collection.immutable.HashMap

object ExplicitMutableHeap {

  val valueHeap = "valuesHeap"
  def valueHeapId(ann : GeneralAnnotation) = Ident(valueHeap, ann.pos)
  val ptrHeap = "indirHeap"
  def ptrHeapId(ann : GeneralAnnotation) = Ident(ptrHeap, ann.pos)
  val allFunsName = "allFuns"
  def allFunsId(ann : GeneralAnnotation) = Ident(allFunsName, ann.pos)
  val nopos = new GeneralAnnotation()

  def valueGet(e: Expression.T) = CallIndex(true, Field(valueHeapId(e.ann), "get", e.ann.pos), List((None, e)), e.ann.pos)
  def ptrGet(e: Expression.T) = CallIndex(true, Field(ptrHeapId(e.ann), "get", e.ann.pos), List((None, e)), e.ann.pos)
  def index(arr: Expression.T, ind: Expression.T) = CallIndex(false, arr, List((None, ind)), ind.ann.pos)
  def newValue(e : Expression.T) = CallIndex(true, Field(valueHeapId(e.ann), "new", e.ann.pos), List((None, e)), e.ann.pos)
  def newPtr(e : Expression.T) = CallIndex(true, Field(ptrHeapId(e.ann), "new", e.ann.pos), List((None, e)), e.ann.pos)

  def explicitHeap(st: Statement, ns: Names) = {

    def procSt(closure : String => Int, scope : String => (VarScope.T, GeneralAnnotation),
               st : Statement, functions : List[FuncDef]) : (Statement, List[FuncDef]) = {
      def pe(lhs : Boolean, e : Expression.T) = {
        val (Left(result), _) = procExpr((lhs, e, ns) => {
          e match {
            case CallIndex(true, Field(Ident(fname, _), _, _), _, _) if fname == valueHeap || fname == ptrHeap =>
              (Left(e), ns)
            case Ident(name, ann)  =>
              val e1 = if (scope(name)._1 != VarScope.Arg && scope(name)._1 != VarScope.Local &&
                    !List(valueHeap, ptrHeap, allFunsName, "closure").contains(name))
                index(Ident("closure", ann.pos), IntLiteral(closure(name), ann.pos)) else
                e
              val e2 = if (List(valueHeap, ptrHeap, allFunsName, "closure").contains(name)) e1 else
                if (!lhs) valueGet(ptrGet(e1)) else (e1)
              (Left(e2), ns)
            case CallIndex(true, whom, args, ann) => // todo: this implementation is incorrect, it evals whom twice! we must extract whom to a variable!
              val args2 = args.map {
                case x@((None, NoneLiteral(_)) | (None, BoolLiteral(_, _)) | (None, IntLiteral(_, _)) | (None, StringLiteral(_, _))) =>
                  (None, newValue(x._2))
                case x@(None, _) => x
              }
              (Left(CallIndex(true, index(allFunsId(ann), index(whom, IntLiteral(0, ann.pos))), (None, whom) :: args2, ann.pos)), ns)
            case _ => (Left(e), ns)
          }
        })(lhs, e, Names(HashMap()))
        result
      }
      st match {
        case FuncDef(name, args, None, None, body, Decorators(List()), vars, ann) =>
          def scope(name : String) = if (vars.contains(name)) vars(name) else (VarScope.Global, nopos)
          val add2closure =
            vars.filter(x => x._2._1 != VarScope.Global && x._2._1 != VarScope.Local && x._2._1 != VarScope.Arg).toList
          val (closure, _) = add2closure.foldLeft((HashMap[String, Int](), 1))((acc, name) =>
            (acc._1.+((name._1, acc._2)), acc._2 + 1)
          )
          val (body1, fs1) = procSt(closure.apply, scope, body, functions)
          val createLocals = vars
            .filter(x => x._2._1 == VarScope.Local)
            .map(x => Assign(List(Ident(x._1, ann.pos), newPtr(IntLiteral(0, ann.pos))), ann.pos))
          val newName = s"tmpFun${fs1.size}"
          val f1 = FuncDef(newName,
              ("closure", ArgKind.Positional, None, ann.pos) :: args,
            None, None, Suite(createLocals.toList :+ body1, body1.ann.pos), Decorators(List()), HashMap(), ann.pos)
          val fs2 = fs1 :+ f1
          val rhs = CollectionCons(Expression.CollectionKind.List,
            IntLiteral(fs1.size, ann.pos) :: add2closure.map(s => Ident(s._1, s._2._2)), ann.pos)
          (
            Suite(List(
              Assign(List(Ident("nextClosure", ann.pos), rhs), ann.pos),
              Assign(List(CallIndex(true, Field(ptrHeapId(ann), "set", ann.pos),
                List((None, Ident(name, ann.pos)), (None, newValue(Ident("nextClosure", ann.pos)))), ann.pos)), ann.pos)
            ), ann.pos)
            , fs2)

        case Assign(List(lhs, rhs), ann) => (Assign(List(
          CallIndex(true, Field(ptrHeapId(ann), "set", ann.pos),
            List((None, pe(true, lhs)), (None, newValue(pe(false, rhs)))), ann.pos)),
            ann.pos
        ), functions)
        case Return(x, ann) => (Return(pe(false, x), ann.pos), functions)
        case IfSimple(cond, yes, no, ann) =>
          val (yes1, f1) = procSt(closure, scope, yes, functions)
          val (no1, f2) = procSt(closure, scope, no, f1)
          (IfSimple(pe(false, cond), yes1, no1, ann.pos), f2)
        case NonLocal(l, ann) => (Pass(ann.pos), functions)
        case Break(_) | Pass(_) | Continue(_) => (st, functions)

        case Suite(l, ann) =>
          val (l1, fs) = l.foldLeft((List[Statement](), functions))((acc, st) => {
            val (st1, fs) = procSt(closure, scope, st, acc._2)
            (acc._1 :+ st1, fs)
          })
          (Suite(l1, ann.pos), fs)
      }
    }

    val (rest, allFuns) = procSt(_ => ???, (_ => (VarScope.Global, new GeneralAnnotation())), SimpleAnalysis.computeAccessibleIdents(st), List())

    val outst = Suite(
      allFuns :+
      CreateConst("allFuns", CollectionCons(Expression.CollectionKind.List, allFuns.map(f => Ident(f.name, f.ann.pos)), nopos), nopos) :+
      rest, st.ann.pos
    )

    (outst, ns)

  }

}
