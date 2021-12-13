import Expression.{CallIndex, CollectionCons, Cond, DictCons, Field, Ident, StringLiteral}
import PrintEO.{EOVisibility, Text, ident, printExpr}
import PrintLinearizedImmutableEO.rmUnreachableTail
import PrintLinearizedMutableEONoCage.headers

import scala.collection.immutable.HashMap

object PrintLinearizedMutableEOWithCage {

  val bogusVisibility = new EOVisibility()

  def printFun(newName : String, f : FuncDef) : Text = {
    val (Suite(l0), _) = SimplePass.procStatement(SimplePass.unSuite)(Suite(List(f.body)), SimplePass.Names(HashMap()))
    val l = rmUnreachableTail(l0)
    //    println(s"l = \n${PrintPython.printSt(Suite(l), "-->>")}")
    def isFun(f : Statement) = f match { case f : FuncDef => true case _ => false }
    val funs = l.filter(isFun)
    val funNames = funs.map{ case f : FuncDef => f.name }.toSet
    val memories = f.accessibleIdents.filter(x => x._2 == VarScope.Local && !funNames.contains(x._1)).
      map(x => s"cage > x${x._1}").toList
    val innerFuns = funs.flatMap{case f : FuncDef => (printFun(f.name, f))}
    def others(l : List[Statement]) : Text = l.flatMap{
      case NonLocal(l) => List()
      case Assign(List(Ident(name), DictCons(l))) =>
        "write." ::
          ident("x" + name :: "[]" :: ident(l.map{ case Left((StringLiteral(name), value)) =>
            printExpr(bogusVisibility)(value) + " > x" + name.substring(1, name.length - 1) }))
      case f : FuncDef => List(s"${f.name}.write ${f.name}Fun")
      case Assign(List(Ident(lhsName), rhs)) =>
        List(
          s"forceData.write ${printExpr(bogusVisibility)(rhs)}",
          s"x$lhsName.write forceData"
        )
      case Assign(List(e)) => List(printExpr(bogusVisibility)(e))
      case Return(e) => List(printExpr(bogusVisibility)(e))
      case IfSimple(cond, Return(yes), Return(no)) =>
        val e = Cond(cond, yes, no)
        List(printExpr(bogusVisibility)(e))
      case WithoutArgs(StatementsWithoutArgs.Pass) => List()
      case Suite(l) => others(l)
    }
    val args1 = f.args.map{ case (argname, ArgKind.Positional, None) => argname }.mkString(" ")
    s"[$args1] > x${newName}" :: ident(
      "memory > forceData" :: memories ++ innerFuns ++
        ("seq > @" :: ident(
//          s"stdout \"$newName\\n\"" ::
            others(l.filterNot(isFun))))
    )
  }

  def printTest(testName : String, st : Statement) : Text = {
    val theTest@FuncDef(_, _, _, _, _, _, _) =
      SimpleAnalysis.computeAccessibleIdents(FuncDef(testName, List(), None, None, st, Decorators(List()), HashMap()))
    headers ++ printFun(theTest.name, theTest)
  }


}