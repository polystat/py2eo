import Expression.{CallIndex, CollectionCons, Cond, DictCons, Field, Ident, StringLiteral}
import PrintEO.{EOVisibility, Text, ident, printExpr}
import PrintLinearizedImmutableEO.rmUnreachableTail

import scala.collection.immutable.HashMap

object PrintLinearizedMutableEONoCage {

  val bogusVisibility = new EOVisibility()

  val headers = List(
    "+package org.eolang",
    "+alias cage org.eolang.gray.cage",
    "+alias stdout org.eolang.io.stdout",
    "+alias sprintf org.eolang.txt.sprintf",
//    "+junit",
//    "+junit",
    ""
  )

  val manyMemories = " memory" * 300

  val cHeap = "[] > cHeap" :: ident(List(
  s"(*$manyMemories) > a",
  "memory > last",
//  "[] > init",
//  "  memory.write 0",
  "[index] > get",
  "  a.get index > @",
  "[index value] > set",
  "  (a.get index).write value > @",
  "[value] > new",
  "  seq > @",
  "    last.write (last.is-empty.if 0 (last.add 1))",
  "    (a.get last).write value",
  "    last"
  ))

  val prelude =
    cHeap :+
    "cHeap > valuesHeap" :+
    "cHeap > indirHeap"

  def printFun(f : FuncDef) : Text = {
    val (Suite(l0), _) = SimplePass.procStatement(SimplePass.unSuite)(f.body, SimplePass.Names(HashMap()))
    val l = rmUnreachableTail(l0)
//    println(s"l = \n${PrintPython.printSt(Suite(l), "-->>")}")
    def isFun(f : Statement) = f match { case f : FuncDef => true case _ => false }
    val notMemories = "allFuns" :: l.filter(isFun).map{case f : FuncDef => f.name}
    val memories = f.accessibleIdents.filter(x => x._2 == VarScope.Local && !notMemories.contains(x._1)).
      map(x => s"memory > ${x._1}").toList
    val innerFuns = l.filter(isFun).flatMap{case f : FuncDef => (printFun(f))}
    val mkAllFuns = l.find{ case (CreateConst(name, value)) => true case _ => false} match {
      case Some(CreateConst("allFuns", CollectionCons(_, allFuns))) =>
        "* > allFuns" :: ident(allFuns.flatMap(e => List("[]", s"  ${printExpr(bogusVisibility)(e)} > callme")))
      case None => List()
    }
    def others(l : List[Statement]) : Text = l.flatMap{
      case Assign(List(Ident(name), DictCons(l))) =>
        "write." ::
        ident(name :: "[]" :: ident(l.map{ case Left((StringLiteral(name), value)) =>
          printExpr(bogusVisibility)(value) + " > " + name.substring(1, name.length - 1) }))
      case Assign(List(Ident(lhsName), rhs)) =>
        List(s"$lhsName.write ${printExpr(bogusVisibility)(rhs)}")
      case Assign(List(e)) => List(printExpr(bogusVisibility)(e))
      case Return(e) => List(printExpr(bogusVisibility)(e))
      case IfSimple(cond, Return(yes), Return(no)) =>
        val e = Cond(cond, yes, no)
        List(printExpr(bogusVisibility)(e))
      case WithoutArgs(StatementsWithoutArgs.Pass) => List()
      case CreateConst("allFuns", _) => List()
      case Suite(l) => others(l)
    }
    val args1 = f.args.map{ case (argname, ArgKind.Positional, None) => argname }.mkString(" ")
    s"[$args1] > ${f.name}" :: ident(
      memories ++ (innerFuns ++ mkAllFuns) ++ ("seq > @" :: ident(others(l.filterNot(isFun))))
    )
  }

  def printTest(testName : String, st : Statement) : Text = {
    // workaround for cqfn/eo#415
    val (st1, _) = SimplePass.procExprInStatement(SimplePass.procExpr{
      case (false, e@CallIndex(false, Ident("allFuns"), args), ns) =>
        (Left(Field(e, "callme")), ns)
      case (_, e, ns) => (Left(e), ns)
    })(st, SimplePass.Names(HashMap()))
    val theTest@FuncDef(_, _, _, _, _, _, _) =
      SimpleAnalysis.computeAccessibleIdents(FuncDef(testName, List(), None, None, st1, Decorators(List()), HashMap()))
    val head :: tail = printFun(theTest)
    headers ++
      (head :: ident(prelude) ++
        (tail.init :+
        "    (allFuns.get (nextClosure.get 0)).callme nextClosure")
      )
  }

}