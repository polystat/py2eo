package org.polystat.py2eo

import org.junit.Assert._
import org.junit.{Ignore, Test}
import org.junit.runners.Parameterized.Parameters
import org.polystat.py2eo.Common.dfsFiles
import org.polystat.py2eo.Expression._
import org.scalatest.Tag
import org.yaml.snakeyaml.Yaml

import java.io.{File, FileInputStream, FileWriter}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Path, Paths}
import scala.collection.immutable
import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.sys.process._

object SlowTest extends Tag("SlowTest")
class YamlItem(var testName: Path, var yaml: java.util.Map[String, Any])

class Tests {
  val separator: String = "/"
  var files = Array.empty[File]
  private val testsPrefix = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/"
  private val yamlPrefix = System.getProperty("user.dir") + "/src/test/resources/yaml/"

  import Main.{writeFile, readFile, debugPrinter}

  private val python = {
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    assertTrue(0 == (s"python --version" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    val pattern = "Python (\\d+)".r
    val Some(match1) = pattern.findFirstMatchIn(if (stderr.toString() == "") stdout.toString() else stderr.toString())
    if (match1.group(1) == "2") "python3" else "python"
  }

  @Test def removeControlFlow(): Unit = {
    for (name <- List("x", "trivial", "trivialWithBreak", "myList", "simplestClass")) {
      val test = new File(testsPrefix + "/" + name + ".py")
      val y = SimplePass.allTheGeneralPasses(debugPrinter(test), Parse.parse(test, debugPrinter(test)), new SimplePass.Names())
      val textractAllCalls = SimplePass.procExprInStatement(
        SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
      val z = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
      val Suite(List(theFun@FuncDef(_, _, _, _, _, _, _, _, _, _), Return(_, _)), _) = z._1
      val thePos = theFun.ann.pos
      val zHacked = Suite(List(theFun, new Assert(CallIndex(isCall = true, Ident(theFun.name, thePos), List(), thePos), thePos)), thePos)
      val runme = writeFile(test, "afterRemoveControlFlow", ".py", PrintPython.printSt(zHacked, ""))
      val stdout = new StringBuilder()
      val stderr = new StringBuilder()
      assertTrue(0 == (s"$python \"$runme\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
      println(stdout)
    }
  }
	
  @Test def immutabilize(): Unit = {
    val name = "trivial"
    val test = new File(testsPrefix + "/" + name + ".py")

    val y = SimplePass.allTheGeneralPasses(debugPrinter(test), Parse.parse(test, debugPrinter(test)), new SimplePass.Names())

    val textractAllCalls = SimplePass.procExprInStatement(
      SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
//    Parse.toFile(textractAllCalls._1, "afterExtractAllCalls", name)

    val x = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
    val Suite(List(theFun, Return(_, _)), _) = x._1
//    Parse.toFile(theFun, testsPrefix + "afterRemoveControlFlow", name)

    val z = ExplicitImmutableHeap.explicitHeap(theFun, x._2)
    val Suite(l, _) = z._1
    val FuncDef(mainName, _, _, _, _, _, _, _, _, ann) = l.head

    val pos = ann.pos
    val hacked = Suite(List(
      ImportAllSymbols(List("closureRuntime"), pos),
      Suite(l.init, pos),
      new Assert(CallIndex(isCall = false,
          CallIndex(isCall = true, Ident(mainName, pos),
            List((None, CollectionCons(CollectionKind.List, List(), pos)), (None, DictCons(List(), pos))), pos),
          List((None, IntLiteral(1, pos))), pos),
        pos
      )
    ), pos)

    val runme = writeFile(test, "afterImmutabilization", ".py", PrintPython.printSt(hacked, ""))

    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    Files.copy(Paths.get(testsPrefix + "/closureRuntime.py"),
      Paths.get(testsPrefix + "/afterImmutabilization/closureRuntime.py"), REPLACE_EXISTING)
    assertTrue(0 == (s"$python \"$runme\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    println(stdout)

    val hacked4EO = Suite(List(l.head), pos)
    val eoText =
      PrintLinearizedImmutableEO.printSt(name, hacked4EO) +
        "  * > emptyHeap\n" +
        "  [] > emptyClosure\n" +
        s"  ($mainName emptyHeap emptyClosure).get 1 > @\n"
    writeFile(test, "genImmutableEO", ".eo", eoText)
  }

  @Test def simplifyInheritance(): Unit = {
    val name = "inheritance"
    val test = new File(testsPrefix + "/" + name + ".py")
    def db = debugPrinter(test)(_, _)

    SimplePass.allTheGeneralPasses(db, Parse.parse(test, db), new SimplePass.Names())
  }

  @Test def heapify(): Unit = {
    val name = "trivial"
    val test = new File(testsPrefix + "/" + name + ".py")
    def db = debugPrinter(test)(_, _)

    val y = SimplePass.allTheGeneralPasses(db, Parse.parse(test, db), new SimplePass.Names())

    val textractAllCalls = SimplePass.procExprInStatement(
      SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)

    val x = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
    val Suite(List(theFun, Return(_, _)), _) = x._1
    val FuncDef(mainName, _, _, _, _, _, _, _, _, ann) = theFun

    val z = ExplicitMutableHeap.explicitHeap(theFun, x._2)

    val pos = ann.pos
    val hacked = Suite(List(
      ImportAllSymbols(List("heapifyRuntime"), pos),
      Assign(List(Ident(mainName, pos), ExplicitMutableHeap.newPtr(IntLiteral(0, pos))), pos),
      z._1,
      new Assert(CallIndex(isCall = true, ExplicitMutableHeap.index(Ident("allFuns", pos),
        ExplicitMutableHeap.index(ExplicitMutableHeap.valueGet(ExplicitMutableHeap.ptrGet(Ident(mainName, pos))),
          IntLiteral(0, pos))), List((None, NoneLiteral(pos))), pos), pos)
    ), pos)

    val runme = writeFile(test, "afterHeapify", ".py", PrintPython.printSt(hacked, ""))

    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    java.nio.file.Files.copy(java.nio.file.Paths.get(testsPrefix + "/heapifyRuntime.py"),
      java.nio.file.Paths.get(test.getParentFile.getPath + "/afterHeapify/heapifyRuntime.py"), REPLACE_EXISTING)
    assertTrue(0 == (s"$python \"$runme\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    println(stdout)

    val eoText = PrintLinearizedMutableEONoCage.printTest(name, z._1)
    writeFile(test, "genHeapifiedEO", ".eo", eoText.mkString("\n"))
  }

  @Test def trivialTest():Unit = {
    useCageHolder(testsPrefix + "/trivial.py")
  }

  @Test def simplestClassTest():Unit = {
    useCageHolder(testsPrefix + "/simplestClass.py")
  }

  @Test def myListTest():Unit = {
    useCageHolder(testsPrefix + "/myList.py")
  }

  @Test def xTest():Unit = {
    useCageHolder(testsPrefix + "/x.py")
  }

  @Test def parserPrinterOnCPython(): Unit = {
    val dirName = testsPrefix + "/testParserPrinter"
    val dir = new File(dirName)
    assert(dir.isDirectory)

    val afterParser = new File(dirName + "/afterParser")
    if (!afterParser.exists()) afterParser.mkdir()
    val cpython = new File(afterParser.getPath + "/cpython")
    if (!cpython.exists()) {
//      assert(0 == Process("git clone file:///home/bogus/cpython/", afterParser).!)
      assert(0 == Process("git clone https://github.com/python/cpython", afterParser).!)
      assert(0 == Process("git checkout v3.8.10", cpython).!)
    }

    println("Version of python is:")
    s"$python --version" !

    // todo: test_named_expressions.py uses assignment expressions which are not supported.
    // test_os leads to a strange error with inode numbers on the rultor server only
    // supporting them may take several days, so this feature is currently skipped

    // "test_strtod.py", todo: what's the problem here???
    // "test_zipimport_support.py", todo: what's the problem here???
    // "test_zipfile64.py" works for more 2 minutes, too slowm
    // test_sys.py just hangs the testing with no progress (with no CPU load)
    // test_dis.py, test*trace*.py are not supported, because they seem to compare line numbers, which change after printing
    // many test for certain libraries are not present here, because these libraries are not installed by default in the CI

//    val test = List("test_named_expressions.py").map(name => new File(dirName + "/" + name))
    val test = dir.listFiles().toList
    val futures = test.map(test =>
      Future {
        if (!test.isDirectory && test.getName.startsWith("test_") && test.getName.endsWith(".py")) {
          def db = debugPrinter(test)(_, _)

          val name = test.getName
          println(s"parsing $name")
          val eoText = Transpile.transpile(db)(name.substring(0, name.length - 3), readFile(test))
          writeFile(test, "genUnsupportedEOPrim", ".eo", eoText)
          Files.copy(
            Paths.get(s"$dirName/afterParser/${test.getName}"),
            Paths.get(s"$dirName/afterParser/cpython/Lib/test/${test.getName}"),
            REPLACE_EXISTING
          )
        }
      }
    )
    for (f <- futures) Await.result(f, Duration.Inf)

    assume(System.getProperty("os.name") == "Linux")
    assert(0 == Process("./configure", cpython).!)
    val nprocessors = Runtime.getRuntime.availableProcessors()
    println(s"have $nprocessors processors")
    assert(0 == Process(s"make -j ${nprocessors + 2}", cpython).!)
    assertTrue(0 == Process("make test", cpython).!)
  }

  @Ignore
  @Test def genUnsupportedDjango() : Unit = {
    val root = new File(testsPrefix)
    val django = new File(testsPrefix + "/django")
    if (!django.exists()) {
//      assert(0 == Process("git clone file:///home/bogus/pythonProjects/django", root).!)
      assert(0 == Process("git clone https://github.com/django/django", root).!)
    }
    val test = dfsFiles(django).filter(f => f.getName.endsWith(".py"))
    val futures = test.map(test =>
      Future {
        def db(s : Statement, str : String) = () // debugPrinter(test)(_, _)
        val name = test.getName
        println(s"parsing $name")
        val eoText = Transpile.transpile(db)(name.substring(0, name.length - 3), readFile(test))
        writeFile(test, "genUnsupportedEO", ".eo", eoText)
      }
    )
    for (f <- futures) Await.result(f, Duration.Inf)
  }

  def useCageHolder(path: String): Unit = {
    val test = new File(path)
    def db = debugPrinter(test)(_, _)
    writeFile(
      test, "genCageEO", ".eo", Transpile.transpile(db)(
        test.getName.replace(".py", ""),
        readFile(test)
      )
    )
    val runme = test.getParentFile.getPath + "/afterUseCage/" + test.getName
    assertTrue(0 == s"$python \"$runme\"".!)
  }


  def useCageHolder(path: String, yamlString:String): Unit = {
    val test = new File(path)

    def db = debugPrinter(test)(_, _)
    val y = SimplePass.allTheGeneralPasses(db, Parse.parse(yamlString, db), new SimplePass.Names())
    val textractAllCalls = SimplePass.procExprInStatement(
      SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
    val Suite(List(theFun@FuncDef(mainName, _, _, _, _, _, _, _, _, ann)), _) =
      ClosureWithCage.declassifyOnly(textractAllCalls._1, textractAllCalls._2)._1

    val hacked = Suite(List(
      theFun,
      Assert(CallIndex(isCall = true, Ident(mainName, ann.pos), List(), ann.pos), None, ann.pos)
    ), ann.pos)
    val runme = writeFile(test, "afterUseCage", ".py", PrintPython.printSt(hacked, ""))
    assertTrue(0 == s"$python \"$runme\"".!)

    val eoHacked = Suite(List(
      theFun,
      Return(Some(CallIndex(isCall = true, Ident(mainName, ann.pos), List(), ann.pos)), ann.pos)
    ), ann.pos)

    val eoText = PrintLinearizedMutableEOWithCage.printTest(test.getName.replace(".yaml", ""), eoHacked)
    writeFile(test, "genCageEO", ".eo", (eoText.init.init :+ "        result").mkString("\n"))
  }


  @Test def whileCheckTest():Unit = {
    simpleConstructionCheck(yamlTest = "whileCheck")
  }

  @Test def ifCheck():Unit = {
    simpleConstructionCheck(yamlTest = "ifCheck")
  }

  @Test def assignCheck():Unit = {
    simpleConstructionCheck(yamlTest = "assignCheck")
  }

  def simpleConstructionCheck(path:String = null, yamlTest:String = null):Unit = {
    if (path != null){
      val testHolder = new File(path)
      if (testHolder.exists && testHolder.isDirectory) {
        for (file <- testHolder.listFiles.filter(_.isFile).toList) {
          if (!file.getName.contains(".disabled") && !file.getName.contains(".yaml")) {
            useCageHolder(file.getPath)
          }
        }
      }
    }else{
      if (yamlTest != null){
        for (item <- parsePython()){
          val file = new File(item.testName.toString)
          if (item.testName.getParent.getFileName.toString == yamlTest){
            useCageHolder(file.getPath, item.yaml.get("python").asInstanceOf[String])
          }
        }
      }
    }

  }

  @Parameters def parsePython():collection.mutable.ArrayBuffer[YamlItem] = {
    val res = collection.mutable.ArrayBuffer[YamlItem]()
    Files.walk(Paths.get(testsPrefix)).filter((p: Path) => p.toString.endsWith(".yaml")).forEach((p: Path) => {
      val testHolder = new File(p.toString)
      val src = new FileInputStream(testHolder)
      val yaml = new Yaml()
      val yamlObj = yaml.load(src).asInstanceOf[java.util.Map[String, Any]]
      res.addOne(new YamlItem(p,yamlObj))
    })
    res
  }
}

