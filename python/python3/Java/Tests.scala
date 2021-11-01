import java.io.FileWriter

import Expression.{CallIndex, CollectionCons, CollectionKind, DictCons, Ident}
import org.junit.Assert._
import org.junit.Test

class Tests {

//  val testsPrefix = "python/python3/test/"
  val testsPrefix = System.getProperty("user.dir") + "/test/"

//  @Test def printEO(): Unit = {
//    val name = "trivial"
//    val z = Parse.parse(testsPrefix, name)
//    val output = new FileWriter(name + ".eo")
//    output.write(PrintEO.printSt(name, z._1))
//    output.close()
//    import scala.sys.process._
//    assertTrue(0 == (s"diff \"$testsPrefix/trivial.eo.golden\" \"$testsPrefix/trivial.eo\"".!))
//  }

  @Test def removeControlFlow(): Unit = {
    val name = "trivialWithBreak"
    val y = Parse.parse(testsPrefix, name)
    val z = RemoveControlFlow.removeControlFlow(y._1, y._2)
    val Suite(List(theFun, Return(_))) = z._1
    val zHacked = Suite(List(theFun, Assign(List(CallIndex(true, Ident("outer"), List())))))
    Parse.toFile(zHacked, testsPrefix + "afterRemoveControlFlow", name)
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    import scala.sys.process._
    assertTrue(0 == (s"python3 \"$testsPrefix/afterRemoveControlFlow/$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    println(stdout)
    assertTrue(stdout.mkString("") == "34")
  }

  @Test def immutabilize() : Unit = {
    val name = "trivial"
    val y = Parse.parse(testsPrefix, name)

    val textractAllCalls = SimplePass.procExprInStatement(
      SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
//    Parse.toFile(textractAllCalls._1, "afterExtractAllCalls", name)

    val x = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
    val Suite(theFun :: _) = x._1
//    Parse.toFile(theFun, "afterRemoveControlFlow", name)

    val z = ExplicitHeap.explicitStackHeap(theFun, x._2)
    val Suite(l) = z._1
    val FuncDef(mainName, _, _, _, _, _, _) = l.head

    val hacked = Suite(List(
      ImportAllSymbols(List("closureRuntime")),
      Suite(l.init),
      Assign(List(CallIndex(true, Ident(mainName),
        List((None, CollectionCons(CollectionKind.List, List())), (None, DictCons(List()))))))
    ))

    Parse.toFile(hacked, testsPrefix + "afterImmutabilization", name)

    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    import scala.sys.process._
    assertTrue(0 == (s"cp \"$testsPrefix/closureRuntime.py\" \"$testsPrefix/afterImmutabilization/\"".!))
    assertTrue(0 == (s"python3 \"$testsPrefix/afterImmutabilization/$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    println(stdout)
    assertTrue(stdout.mkString("") == "55")
//    assertTrue(stdout.mkString("") == "1")


    val hacked4EO = Suite(List(l.head))
    val output = new FileWriter(testsPrefix + "genEO/" + name + ".eo")
    output.write(PrintLinearizedImmutableEO.printSt(name, hacked4EO))
    output.close()
  }

}