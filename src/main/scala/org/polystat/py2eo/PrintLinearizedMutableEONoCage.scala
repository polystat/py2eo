package org.polystat.py2eo;

import Expression.{CallIndex, CollectionCons, Cond, DictCons, Field, Ident, Parameter, StringLiteral}
import PrintEO.{Text, indent, printExpr}
import PrintLinearizedImmutableEO.rmUnreachableTail

import scala.collection.immutable.HashMap

object PrintLinearizedMutableEONoCage {

  val headers = List(
    "+package org.eolang",
    "+alias goto org.eolang.gray.goto",
    "+alias stdout org.eolang.io.stdout",
    "+alias cage org.eolang.gray.cage",
//    "+alias sprintf org.eolang.txt.sprintf",
    "+junit",
    ""
  )

  private val manyMemories = " memory" * 300

  private val cHeap = "[] > cHeap" :: indent(
    List(
      s"(*$manyMemories) > a",
      "memory > last",
      "[index] > get",
      "  a.get index > @",
      "[index value] > set",
      "  (a.get index).write value > @",
      "[value] > new",
      "  seq > @",
      "    last.write (last.is-empty.if 0 (last.add 1))",
      "    (a.get last).write value",
      "    last"
    )
  )

  private val prelude =
    cHeap :+
    "cHeap > valuesHeap" :+
    "cHeap > indirHeap"

  def printFun(f : FuncDef) : Text = {
    val (Suite(l0, _), _) = SimplePass.procStatement(SimplePass.unSuite)(f.body, SimplePass.Names(HashMap()))
    val l = rmUnreachableTail(l0)
    def isFun(f : Statement) = f match { case _: FuncDef => true case _ => false }
    val notMemories = "allFuns" :: l.filter(isFun).map{case f : FuncDef => f.name}
    val memories = f.accessibleIdents
      .filter(x => x._2._1 == VarScope.Local && !notMemories.contains(x._1))
      .map(x => s"memory > ${x._1}").toList
    val innerFuns = l.filter(isFun).flatMap{case f : FuncDef => printFun(f) }
    val mkAllFuns = l.find{ case CreateConst(_, _, _) => true case _ => false} match {
      case Some(CreateConst("allFuns", CollectionCons(_, allFuns, _), _)) =>
        "* > allFuns" :: indent(allFuns.flatMap(e => List("[]", s"  ${printExpr(e)} > callme")))
      case None => List()
    }
    def others(l : List[Statement]) : Text = l.flatMap{
      case Assign(List(Ident(name, _), DictCons(l, _)), _) =>
        "write." ::
        indent(
          name :: "[]" :: indent(
            l.map{
              case Left((StringLiteral(List(name), _), value)) =>
                "%s > %s".format(printExpr(value), name.substring(1, name.length - 1))
            }
          )
        )
      case Assign(List(Ident(lhsName, _), rhs), _) =>
        List(s"$lhsName.write ${printExpr(rhs)}")
      case Assign(List(e), _) => List(printExpr(e))
      case Return(e, _) => e.toList.map(printExpr(_))
      case IfSimple(cond, Return(Some(yes), _), Return(Some(no), _), ann) =>
        val e = Cond(cond, yes, no, ann.pos)
        List(printExpr(e))
      case Pass(_) => List()
      case CreateConst("allFuns", _, _) => List()
      case Suite(l, _) => others(l)
    }
    val args1 = f.args.map{ case Parameter(argname, ArgKind.Positional, None, None, _) => argname }.mkString(" ")
    s"[$args1] > ${f.name}" :: indent(
      memories ++ (innerFuns ++ mkAllFuns) ++ ("seq > @" :: indent(others(l.filterNot(isFun))))
    )
  }

  def printTest(testName : String, st : Statement) : Text = {
    // workaround for cqfn/eo#415
    val (st1, _) = SimplePass.procExprInStatement(
      SimplePass.procExpr{
        case (false, e@CallIndex(false, Ident("allFuns", _), _, _), ns) =>
          (Left(Field(e, "callme", e.ann.pos)), ns)
        case (_, e, ns) => (Left(e), ns)
      }
    )(st, SimplePass.Names(HashMap()))
    val theTest@FuncDef(_, _, _, _, _, _, _, _, _, _) =
      SimpleAnalysis.computeAccessibleIdents(
        FuncDef(
          testName, List(), None, None, None,
          st1, Decorators(List()), HashMap(), isAsync = false, st.ann.pos
        )
      )
    val head :: tail = printFun(theTest)
    headers ++
      (
        head :: indent(prelude) ++
        (tail.init :+ "    (allFuns.get (nextClosure.get 0)).callme nextClosure")
      )
  }

}