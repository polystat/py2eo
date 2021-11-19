
import java.io.{File, FileWriter}
import java.nio.file.Files
import Expression._
import org.junit.Assert._
import org.junit.{Before, Test}

import java.io.{File, FileWriter}
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import scala.collection.immutable.HashMap

// run these tests with py2eo/python/python3 as a currend directory
class Tests {

  private val testsPrefix = System.getProperty("user.dir") + "/test/"
  val intermediateDirs = List(
    "afterEmptyProcStatement", "afterExplicitStackHeap", "afterExtractAllCalls", "afterImmutabilization",
    "afterParser", "afterRemoveControlFlow", "afterSimplifyIf", "afterSimplifyInheritance", "genEO", "afterHeapify"
  )

  @Before def initialize(): Unit = {
    for (dir <- intermediateDirs) {
      val f = new File(testsPrefix + dir + "/")
      if (!f.isDirectory) assertTrue(f.mkdir())
    }
  }

  @Test def removeControlFlow(): Unit = {
    for (name <- List("x", "trivial", "trivialWithBreak")) {
      val y = Parse.parse(testsPrefix, name)
      val textractAllCalls = SimplePass.procExprInStatement(
        SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
      val z = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
      val Suite(List(theFun@FuncDef(_, _, _, _, _, _, _), Return(_))) = z._1
      val zHacked = Suite(List(theFun, Assert((CallIndex(true, Ident(theFun.name), List())))))
      Parse.toFile(zHacked, testsPrefix + "afterRemoveControlFlow", name)
      val stdout = new StringBuilder()
      val stderr = new StringBuilder()
      import scala.sys.process._
      assertTrue(0 == (s"python3 \"$testsPrefix/afterRemoveControlFlow/$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
      println(stdout)
    }
  }

  @Test def immutabilize() : Unit = {
    val name = "trivial"
    val y = Parse.parse(testsPrefix, name)

    val textractAllCalls = SimplePass.procExprInStatement(
      SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
//    Parse.toFile(textractAllCalls._1, "afterExtractAllCalls", name)

    val x = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
    val Suite(List(theFun, Return(_))) = x._1
//    Parse.toFile(theFun, testsPrefix + "afterRemoveControlFlow", name)

    val z = ExplicitImmutableHeap.explicitHeap(theFun, x._2)
    val Suite(l) = z._1
    val FuncDef(mainName, _, _, _, _, _, _) = l.head

    val hacked = Suite(List(
      ImportAllSymbols(List("closureRuntime")),
      Suite(l.init),
      Assert(CallIndex(false,
          (CallIndex(true, Ident(mainName),
            List((None, CollectionCons(CollectionKind.List, List())), (None, DictCons(List()))))),
          List((None, IntLiteral(1))))
      )
    ))

    Parse.toFile(hacked, testsPrefix + "afterImmutabilization", name)

    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    import scala.sys.process._
    java.nio.file.Files.copy(java.nio.file.Paths.get(testsPrefix + "/closureRuntime.py"),
      java.nio.file.Paths.get(testsPrefix + "/afterImmutabilization/closureRuntime.py"), REPLACE_EXISTING)
//    /assertTrue(0 == (s"cp \"$testsPrefix/closureRuntime.py\" \"$testsPrefix/afterImmutabilization/\"".!))
    assertTrue(0 == (s"python3 \"$testsPrefix/afterImmutabilization/$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    println(stdout)

    val hacked4EO = Suite(List(l.head))
    val output = new FileWriter(testsPrefix + "genEO/" + name + ".eo")
    val eoText = PrintLinearizedImmutableEO.printSt(name, hacked4EO)
    output.write(eoText +
      "  * > emptyHeap\n" +
      "  [] > emptyClosure\n" +
      s"  ($mainName emptyHeap emptyClosure).get 1 > @\n"
    )
    output.close()
  }

  @Test def simplifyInheritance(): Unit = {
    val output = new FileWriter(testsPrefix + "afterSimplifyInheritance/inheritanceTest.py")
    output.write("from C3 import eo_getattr, eo_setattr\n\n\n")

    val res = Parse.parse(testsPrefix, "inheritance")
    output.write(PrintPython.printSt(res._1, ""))
    output.close()
  }

  @Test def heapify() : Unit = {
    val name = "trivial"
    val y = Parse.parse(testsPrefix, name)

    val textractAllCalls = SimplePass.procExprInStatement(
      SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)

    val x = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
    val Suite(List(theFun, Return(_))) = x._1
    val FuncDef(mainName, _, _, _, _, _, _) = theFun

    val z = ExplicitMutableHeap.explicitHeap(theFun, x._2)

    val hacked = Suite(List(
      ImportAllSymbols(List("heapifyRuntime")),
      Assign(List(Ident(mainName), ExplicitMutableHeap.newPtr(IntLiteral(0)))),
      z._1,
      Assert(CallIndex(true, ExplicitMutableHeap.index(Ident("allFuns"),
        ExplicitMutableHeap.index(ExplicitMutableHeap.valueGet(ExplicitMutableHeap.ptrGet(Ident(mainName))),
          ExplicitMutableHeap.callme)), List((None, NoneLiteral()))))
    ))

    Parse.toFile(hacked, testsPrefix + "afterHeapify", name)

    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    import scala.sys.process._
    assertTrue(0 == (s"cp \"$testsPrefix/heapifyRuntime.py\" \"$testsPrefix/afterHeapify/\"".!))
    assertTrue(0 == (s"python3 \"$testsPrefix/afterHeapify/$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    println(stdout)

    val output = new FileWriter(testsPrefix + "genEO/" + name + ".eo")
    val eoText = PrintLinearizedMutableEO.printTest(name, z._1)
    output.write(eoText.mkString("\n") + "\n")
    output.close()


  }

}
