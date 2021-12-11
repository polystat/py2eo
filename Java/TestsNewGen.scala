import Expression.{CallIndex, Ident, IntLiteral, NoneLiteral}
import org.junit.Assert.assertTrue
import org.junit.{Before, Test}

import java.io.{File, FileWriter}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

class TestsNewGen {
  private val testsPrefix = System.getProperty("user.dir") + "/test/"
  val intermediateDirs = List(
    "genImmutableEO", "genHeapifiedEO", "genCageEO", "genUnsupportedEO"
  )
  val separator: String = "/"
  val scalaList = List()
  var files = Array.empty[File]

  @Before def initialize(): Unit = {
    for (dir <- intermediateDirs) {
      val f = new File(testsPrefix + dir + "/")
      if (!f.isDirectory) assertTrue(f.mkdir())
    }

    for (subfolder <- List("assignCheck", "ifCheck", "whileCheck")) {
      val testHolder = new File(testsPrefix + s"${File.separator}simple_tests${separator}" + subfolder)
      if (testHolder.exists && testHolder.isDirectory) {
        for (file <- testHolder.listFiles.filter(_.isFile).toList) {
          files :+= file
        }
      }
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
