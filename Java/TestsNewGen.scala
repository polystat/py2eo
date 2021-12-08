import Expression.{CallIndex, Ident}
import org.junit.Assert.assertTrue
import org.junit.{Before, Test}

import java.io.{File, FileWriter}

class TestsNewGen {
  private val testsPrefix = System.getProperty("user.dir") + "/test/"
  val intermediateDirs = List(
    "genImmutableEO", "genHeapifiedEO", "genCageEO", "genUnsupportedEO"
  )
  val separator: String = "/"
  val scalaList = List()
  val emptyArray =  Array.empty[File]

  @Before def initialize(): Unit = {
    for (dir <- intermediateDirs) {
      val f = new File(testsPrefix + dir + "/")
      if (!f.isDirectory) assertTrue(f.mkdir())
    }
  }


  @Before def getFiles(): Unit ={
    for (subfolder <- List("assignCheck","ifCheck","whileCheck")) {
      val testHolder = new File(testsPrefix + s"${File.separator}simple_tests${separator}" + subfolder)
      if (testHolder.exists && testHolder.isDirectory) {
        for (file <- testHolder.listFiles.filter(_.isFile).toList){
          emptyArray
        }
      }
    }
  }

  @Test def useCage() : Unit = {
    for (name <- List("x", "trivial")) {
      val y = Parse.parse(testsPrefix, name)

      val textractAllCalls = SimplePass.procExprInStatement(
        SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)

      val z = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
      val Suite(List(theFun, Return(_))) = z._1
      val FuncDef(mainName, _, _, _, body, _, _) = theFun

      val theFunC = ClosureWithCage.closurize(theFun)
      val hacked = Suite(List(theFunC, Assert(CallIndex(isCall = true,
        ClosureWithCage.index(Ident(mainName), "callme"),
        List((None, Ident(mainName)))))))
      Parse.toFile(hacked, testsPrefix + "afterUseCage", name)

      val stdout = new StringBuilder()
      val stderr = new StringBuilder()
      import scala.sys.process._
      assertTrue(0 == (s"python3 \"$testsPrefix${separator}afterUseCage${separator}$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
      println(stdout)

      val eoHacked = Suite(List(
        theFunC,
        Assign(List(CallIndex(isCall = true, ClosureWithCage.index(Ident(mainName), "callme"), List())))
      ))

      val output = new FileWriter(testsPrefix + "genCageEO/" + name + ".eo")
      val eoText = PrintLinearizedMutableEOWithCage.printTest(name, eoHacked)
      output.write(eoText.mkString("\n") + "\n")
      output.close()
    }
  }
}
