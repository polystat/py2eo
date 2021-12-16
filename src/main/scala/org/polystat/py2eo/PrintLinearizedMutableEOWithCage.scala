import Expression.{CallIndex, CollectionCons, Cond, DictCons, Field, Ident, Parameter, StringLiteral}
import PrintEO.{EOVisibility, Text, ident, printExpr}
import PrintLinearizedImmutableEO.rmUnreachableTail
import PrintLinearizedMutableEONoCage.headers

import scala.collection.immutable.HashMap

object PrintLinearizedMutableEOWithCage {

  val bogusVisibility = new EOVisibility()

  def printFun(newName : String, f : FuncDef) : Text = {
    val (Suite(l0, _), _) = SimplePass.procStatement(SimplePass.unSuite)(Suite(List(f.body), f.body.ann.pos), SimplePass.Names(HashMap()))
    val l = rmUnreachableTail(l0)
    //    println(s"l = \n${PrintPython.printSt(Suite(l), "-->>")}")
    def isFun(f : Statement) = f match { case f : FuncDef => true case _ => false }
    val funs = l.filter(isFun)
    val funNames = funs.map{ case f : FuncDef => f.name }.toSet
    val memories = f.accessibleIdents.filter(x => x._2._1 == VarScope.Local && !funNames.contains(x._1)).
      map(x => s"cage > x${x._1}").toList
    val innerFuns = funs.flatMap{case f : FuncDef => (printFun(f.name, f))}
    def others(l : List[Statement]) : Text = l.flatMap{
      case NonLocal(l, _) => List()
      case Assign(List(Ident(name, _), DictCons(l, _)), _) =>
        "write." ::
          ident("x" + name :: "[]" :: ident(l.map{ case Left((StringLiteral(name, _), value)) =>
            printExpr(bogusVisibility)(value) + " > x" + name.substring(1, name.length - 1) }))
      case f : FuncDef => List(s"${f.name}.write ${f.name}Fun")
      case Assign(List(Ident(lhsName, _), rhs), _) =>
        List(
          s"forceData.write ${printExpr(bogusVisibility)(rhs)}",
          s"x$lhsName.write forceData"
        )
      case Assign(List(e), _) => List(printExpr(bogusVisibility)(e))
      case Return(e, _) => e.toList.map(printExpr(bogusVisibility)(_))
      case IfSimple(cond, Return(Some(yes), _), Return(Some(no), _), ann) =>
        val e = Cond(cond, yes, no, ann.pos)
        List(printExpr(bogusVisibility)(e))
      case Pass(_) => List()
      case Suite(l, _) => others(l)
    }
    val args1 = f.args.map{ case Parameter(argname, ArgKind.Positional, None, None, _) => argname }.mkString(" ")
    s"[$args1] > x${newName}" :: ident(
      "memory > forceData" :: memories ++ innerFuns ++
        ("seq > @" :: ident(
//          s"stdout \"$newName\\n\"" ::
            others(l.filterNot(isFun))))
    )
  }

  def printTest(testName : String, st : Statement) : Text = {
    val theTest@FuncDef(_, _, _, _, _, _, _, _, _, _) =
      SimpleAnalysis.computeAccessibleIdents(FuncDef(testName, List(), None, None, None, st, Decorators(List()),
        HashMap(), false, st.ann.pos))
    headers ++ printFun(theTest.name, theTest)
  }


}
