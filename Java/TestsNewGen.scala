import java.io.{File, FileWriter}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

import Expression.{CallIndex, CollectionCons, CollectionKind, DictCons, Ident, IntLiteral, NoneLiteral}
import org.junit.Assert.assertTrue
import org.junit.{Before, Test}

class TestsNewGen {
  private val testsPrefix = System.getProperty("user.dir") + "/test/"
  val intermediateDirs = List(
    "genImmutableEO", "genHeapifiedEO", "genCageEO", "genUnsupportedEO"
  )
  val separator: String = "/"
  val scalaList = List()
  var files = Array.empty[File]


  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles.filter(f => !f.getParent.contains("after") && f.getName.contains(".py"))
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }


  @Before def initialize(): Unit = {
    for (dir <- intermediateDirs) {
      val f = new File(testsPrefix + dir + separator)
      if (!f.isDirectory) assertTrue(f.mkdir())
    }
    files = recursiveListFiles(new File(System.getProperty("user.dir") + "/test/"))
  }

  @Test def immutabilize() : Unit = {
    for (file <- files) {
      val name = file.getName.replace(".py", "")
      val y = Parse.parse(file.getParent, name)

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
        Assert(CallIndex(isCall = false,
          (CallIndex(true, Ident(mainName),
            List((None, CollectionCons(CollectionKind.List, List())), (None, DictCons(List()))))),
          List((None, IntLiteral(1))))
        )
      ))

      Parse.toFile(hacked, testsPrefix + "afterImmutabilization", name)

      val stdout = new StringBuilder()
      val stderr = new StringBuilder()
      import scala.sys.process._
      java.nio.file.Files.copy(java.nio.file.Paths.get(testsPrefix + s"${separator}closureRuntime.py"),
        java.nio.file.Paths.get(testsPrefix + s"${separator}afterImmutabilization${separator}closureRuntime.py"), REPLACE_EXISTING)
      assertTrue(0 == (s"python3 \"$testsPrefix${separator}afterImmutabilization${separator}$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
      println(stdout)

      val hacked4EO = Suite(List(l.head))
      val output = new FileWriter(testsPrefix + s"genImmutableEO${separator}" + name + ".eo")
      val eoText = PrintLinearizedImmutableEO.printSt(name, hacked4EO)
      output.write(eoText +
        "  * > emptyHeap\n" +
        "  [] > emptyClosure\n" +
        s"  ($mainName emptyHeap emptyClosure).get 1 > @\n"
      )
      output.close()
    }
  }

  @Test def simplifyInheritance(): Unit = {
    val output = new FileWriter(testsPrefix + s"afterSimplifyInheritance${separator}inheritanceTest.py")
    output.write("from C3 import eo_getattr, eo_setattr\n\n\n")

    val res = Parse.parse(testsPrefix, "inheritance")
    output.write(PrintPython.printSt(res._1, ""))
    output.close()
  }

  @Test def removeControlFlow(): Unit = {
    for (file <- files) {
      val name = file.getName.replace(".py", "")
      val y = Parse.parse(file.getParent, name)

      val textractAllCalls = SimplePass.procExprInStatement(
        SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
      val z = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
      val Suite(List(theFun@FuncDef(_, _, _, _, _, _, _), Return(_))) = z._1
      val zHacked = Suite(List(theFun, Assert((CallIndex(true, Ident(theFun.name), List())))))
      Parse.toFile(zHacked, testsPrefix + "afterRemoveControlFlow", name)
      val stdout = new StringBuilder()
      val stderr = new StringBuilder()
      import scala.sys.process._
      assertTrue(0 == (s"python3 \"$testsPrefix${separator}afterRemoveControlFlow${separator}$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
      println(stdout)
    }
  }


  @Test def heapify(): Unit = {
    for (file <- files) {
      val name = file.getName.replace(".py", "")
      val y = Parse.parse(file.getParent, name)

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
        Assert(CallIndex(isCall = true, ExplicitMutableHeap.index(Ident("allFuns"),
          ExplicitMutableHeap.index(ExplicitMutableHeap.valueGet(ExplicitMutableHeap.ptrGet(Ident(mainName))),
            ExplicitMutableHeap.callme)), List((None, NoneLiteral()))))
      ))

      Parse.toFile(hacked, testsPrefix + "afterHeapify", name)

      println(name)

      val stdout = new StringBuilder()
      val stderr = new StringBuilder()
      import scala.sys.process._
      java.nio.file.Files.copy(java.nio.file.Paths.get(testsPrefix + s"${separator}heapifyRuntime.py"),
        java.nio.file.Paths.get(testsPrefix + s"${separator}afterHeapify${separator}heapifyRuntime.py"), REPLACE_EXISTING)
      assertTrue(0 == (s"python3 \"$testsPrefix${separator}afterHeapify${separator}$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
      println(stdout)

      val output = new FileWriter(testsPrefix + s"genHeapifiedEO${separator}" + name + ".eo")
      val eoText = PrintLinearizedMutableEONoCage.printTest(name, z._1)
      output.write(eoText.mkString("\n") + "\n")
      output.close()
    }
  }

  @Test def simpleConstructionTest(): Unit = {
    for (file <- files) {
      val fileName = file.getName.replace(".py", "")
      Parse.parse(file.getParent + separator, fileName)
      val stdout = new StringBuilder()
      val stderr = new StringBuilder()
      import scala.sys.process._
      assertTrue(0 == (s"python3 \"${file.getParent}${separator}afterParser${separator}$fileName.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
      println(stdout)
    }
  }
}
