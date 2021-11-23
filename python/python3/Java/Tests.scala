import Expression._
import org.junit.Assert._
import org.junit.{Before, Test}

import java.io.{File, FileWriter}
import java.nio.file.{FileAlreadyExistsException, NoSuchFileException}

// run these tests with py2eo/python/python3 as a currend directory
class Tests {

  private val testsPrefix = System.getProperty("user.dir") + "/test/"
  val intermediateDirs = List(
    "afterEmptyProcStatement", "afterExplicitStackHeap", "afterExtractAllCalls", "afterImmutabilization",
    "afterParser", "afterRemoveControlFlow", "afterSimplifyIf", "genEO", "cPythonTests"
  )

  @Before def initialize(): Unit = {
    for (dir <- intermediateDirs) {
      val f = new File(testsPrefix + dir + "/")
      if (!f.isDirectory) assertTrue(f.mkdir())
    }
  }

  @Test def removeControlFlow(): Unit = {
    for (name <- List("x", "trivial", "trivialWithBreak", "cPythonTest" )) { /// <------------
      val y = Parse.parse(testsPrefix, name)
      val z = RemoveControlFlow.removeControlFlow(y._1, y._2)
      val Suite(List(theFun@FuncDef(_, _, _, _, _, _, _), Return(_))) = z._1
      val zHacked = Suite(List(theFun, Assert(CallIndex(true, Ident(theFun.name), List()))))
      Parse.toFile(zHacked, testsPrefix + "afterRemoveControlFlow", name)


      mainAsserter(name,"afterRemoveControlFlow")

//      val stdout = new StringBuilder()
//      val stderr = new StringBuilder()
//      import scala.sys.process._
//      assertTrue(0 == (s"python3 \"$testsPrefix/afterRemoveControlFlow/$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
//      println(stdout)
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

    val z = ExplicitHeap.explicitStackHeap(theFun, x._2)
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

    val closureRuntime = java.nio.file.Paths.get(testsPrefix + "/closureRuntime.py")
    try {
      java.nio.file.Files.copy(closureRuntime, java.nio.file.Paths.get(testsPrefix + "/afterImmutabilization/closureRuntime.py"))
    }catch {
      case e: FileAlreadyExistsException => println(e.getMessage)
    }

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

  @Test def cPythonTest(): Unit = {
    val name = "cPythonTest"
    val y = Parse.parse(testsPrefix, name)

    val z = RemoveControlFlow.removeControlFlow(y._1, y._2)
    val Suite(List(theFun@FuncDef(_, _, _, _, _, _, _), Return(_))) = z._1
    val zHacked = Suite(List(theFun, Assert(CallIndex(true, Ident(theFun.name), List()))))
    Parse.toFile(zHacked, testsPrefix + "cPythonTests", name)

    mainAsserter(name,"cPythonTests")
  }

  def mainAsserter(name:String, pathPart:String): Unit ={
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    import scala.sys.process._

    val closureRuntime = java.nio.file.Paths.get(testsPrefix + "/closureRuntime.py")
    try {
      java.nio.file.Files.copy(closureRuntime, java.nio.file.Paths.get(testsPrefix + s"/$pathPart/closureRuntime.py"))
    }catch {
      case e: FileAlreadyExistsException => println(e.getMessage)
      case e: NoSuchFileException => println(e.getMessage)
    }

    assertTrue(0 == (s"python3 \"$testsPrefix/$pathPart/$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    println(stdout)
  }

}