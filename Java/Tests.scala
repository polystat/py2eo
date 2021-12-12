
import java.io.{File, FileWriter}
import java.nio.file.{Files, Paths}
import Expression._
import org.junit.Assert._
import org.junit.{Before, Ignore, Test}

import java.io.{File, FileWriter}
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import scala.collection.immutable.HashMap
import scala.collection.{immutable, mutable}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.sys.process._

class Tests {

  private val testsPrefix = System.getProperty("user.dir") + "/test/"

  def writeFile(test : File, dirSuffix : String, fileSuffix : String, what : String) : String = {
    assert(test.getName.endsWith(".py"))
    val moduleName = test.getName.substring(0, test.getName.length - 3)
    val outPath = test.getParentFile.getPath + "/" + dirSuffix
    val d = new File(outPath)
    if (!d.exists()) d.mkdir()
    val outName = outPath + "/" + moduleName + fileSuffix
    val output = new FileWriter(outName)
    output.write(what)
    output.close()
    outName
  }

  def debugPrinter(module : File)(s : Statement, dirSuffix : String) : Unit = {
    val what = PrintPython.printSt(s, "")
    writeFile(module, dirSuffix, ".py", what)
  }

  val python = {
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    assertTrue(0 == (s"python --version" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    val pattern = "Python (\\d+)".r
    val Some(match1) = pattern.findFirstMatchIn(if (stderr.toString() == "") stdout.toString() else stderr.toString())
    if (match1.group(1) == "2") "python3" else "python"
  }

  @Test def removeControlFlow(): Unit = {
    for (name <- List("x", "trivial", "trivialWithBreak")) {
      val test = new File(testsPrefix + "/" + name + ".py")
      val y = SimplePass.allTheGeneralPasses(debugPrinter(test), Parse.parse(test, debugPrinter(test)), new SimplePass.Names())
      val textractAllCalls = SimplePass.procExprInStatement(
        SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
      val z = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
      val Suite(List(theFun@FuncDef(_, _, _, _, _, _, _, _, _, _), Return(_, _)), _) = z._1
      val thePos = theFun.ann.pos
      val zHacked = Suite(List(theFun, new Assert((CallIndex(true, Ident(theFun.name, thePos), List(), thePos)), thePos)), thePos)
      val runme = writeFile(test, "afterRemoveControlFlow", ".py", PrintPython.printSt(zHacked, ""))
      val stdout = new StringBuilder()
      val stderr = new StringBuilder()
      assertTrue(0 == (s"$python \"$runme\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
      println(stdout)
    }
  }
	
  @Test def immutabilize() : Unit = {
    val name = "trivial"
    val test = new File(testsPrefix + "/" + name + ".py")

    val y = SimplePass.allTheGeneralPasses(debugPrinter(test), Parse.parse(test, debugPrinter((test))), new SimplePass.Names())

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
      new Assert(CallIndex(false,
          (CallIndex(true, Ident(mainName, pos),
            List((None, CollectionCons(CollectionKind.List, List(), pos)), (None, DictCons(List(), pos))), pos)),
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

    val res = SimplePass.allTheGeneralPasses(db, Parse.parse(test, db), new SimplePass.Names())
  }

  @Test def heapify() : Unit = {
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
      new Assert(CallIndex(true, ExplicitMutableHeap.index(Ident("allFuns", pos),
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

  @Test def useCage() : Unit = {
    for (name <- List("x", "trivial")) {
      val test = new File(testsPrefix + "/" + name + ".py")
      def db = debugPrinter(test)(_, _)

      val y = SimplePass.allTheGeneralPasses(db, Parse.parse(test, db), new SimplePass.Names())

      val textractAllCalls = SimplePass.procExprInStatement(
        SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)

      val z = RemoveControlFlow.removeControlFlow(textractAllCalls._1, textractAllCalls._2)
      val Suite(List(theFun, Return(_, _)), _) = z._1
      val FuncDef(mainName, _, _, _, _, body, _, _, _, ann) = theFun

      val theFunC = ClosureWithCage.closurize(theFun)
      val hacked = Suite(List(theFunC, new Assert((CallIndex(true,
        ClosureWithCage.index(Ident(mainName, ann.pos), "callme"),
        List((None, Ident(mainName, ann.pos))), ann.pos)), ann.pos)), ann.pos)
      val runme = writeFile(test, "afterUseCage", ".py", PrintPython.printSt(hacked, ""))

      val stdout = new StringBuilder()
      val stderr = new StringBuilder()
      assertTrue(0 == (s"$python \"$runme\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
      println(stdout)

      val eoHacked = Suite(List(
        theFunC,
        Assign(List(CallIndex(true, ClosureWithCage.index(Ident(mainName, ann.pos), "callme"), List(), ann.pos)), ann.pos)
      ), ann.pos)

      val eoText = PrintLinearizedMutableEOWithCage.printTest(name, eoHacked)
      writeFile(test, "genCageEO", ".eo", eoText.mkString("\n"))

    }
  }

  @Test def useUnsupported() : Unit = {
    for (name <- List("x", "trivial", "twoFuns", "test_typing", "test_typing_part1")) {
      val test = new File(testsPrefix + "/" + name + ".py")
      def db = debugPrinter(test)(_, _)

      val y = SimplePass.procStatement(SimplePass.simplifyIf)(Parse.parse(test, db), new SimplePass.Names())
      val unsupportedSt = SimplePass.procStatement(SimplePass.mkUnsupported)(y._1, y._2)
      val unsupportedExpr = SimplePass.procExprInStatement(SimplePass.procExpr(SimplePass.mkUnsupportedExpr))(
        unsupportedSt._1, unsupportedSt._2)
      PrintPython.toFile(unsupportedExpr._1, testsPrefix + "afterMkUnsupported", name)

      val hacked = SimpleAnalysis.computeAccessibleIdents(
        FuncDef("hack", List(), None, None, None, unsupportedExpr._1, new Decorators(List()), HashMap(), false,  unsupportedExpr._1.ann.pos))

      def findGlobals(l : Set[String], f : FuncDef) : Set[String] = {
        SimpleAnalysis.foldSE[Set[String]](
          (l, e) => {e match {
//            case Ident("ValueError") => println(f.accessibleIdents("ValueError")); l
            case Ident(name, _) if !f.accessibleIdents.contains(name) => l.+(name)
            case _ => l
          }},
          { case _ : FuncDef => false case _ => true }
        )(l, f.body)
      }

      val globals = SimpleAnalysis.foldSS[Set[String]]((l, st) => {
        (st match { case f : FuncDef => findGlobals(l, f)  case _ => l }, true)
      })(immutable.HashSet(), hacked)

      println(s"globals = $globals")

      val eoText = PrintEO.printSt(name, hacked, globals.map(name => s"[args...] > x$name").toList)
      writeFile(test, "genUnsupportedEO", ".eo", eoText.mkString("\n"))
    }
  }

  @Ignore
  @Test def parserPrinterOnCPython() : Unit = {
    val dirName = testsPrefix + "/testParserPrinter"
    val dir = new File(dirName)
    assert(dir.isDirectory)
    // todo: the following files fail to parse (the grammar is wrong)
    //    val test = List(
    //
    //    "test_named_expressions.py","test_positional_only_arg.py", "test_functools.py", "test_buffer.py",
    //    "test_array.py", "test_positional_only_arg.py", "test_types.py", "test_dis.py", "test_inspect.py",
    //    "test_statistics.py",
    //    )
    //      .map(name => new File(dirName + "/" + name))

    // "test_zipimport_support.py", todo: what's the problem here???
    // "test_zipfile64.py" works for more 2 minutes, too slowm
    // test_sys.py just hangs the testing with no progress (with no CPU load)

    val test = List("test_named_expressions.py")
          .map(name => new File(dirName + "/" + name))
//    val test = dir.listFiles().toList
    val futures = test.map(test =>
      Future
      {
        if (!test.isDirectory && test.getName.startsWith("test_") && test.getName.endsWith(".py")
           && test.getName != "test_strtod.py"  //todo: don't know, what's with this test!
          ) {
          def db = debugPrinter(test)(_, _)

          println(s"parsing ${test.getName}")
          val y = Parse.parse(test, db)
          Files.copy(
            Paths.get(s"$dirName/afterParser/${test.getName}"),
            Paths.get(s"$dirName/afterParser/cpython/Lib/test/${test.getName}"),
            REPLACE_EXISTING
          )
          val stdout = new StringBuilder()
          val stderr = new StringBuilder()
          val exitCode =
            Process(s"$python ${test.getName}", new File(s"$dirName/afterParser/cpython/Lib/test/"),
              "PYTHONPATH" -> "..") !  ProcessLogger(stdout.append(_), stderr.append(_))
          writeFile(test, "stdout", ".stdout", stdout.toString())
          writeFile(test, "stderr", ".stderr", stderr.toString())
          if (0 != exitCode) println(s"non-zero exit code for test ${test.getName}!")
          assertTrue(exitCode == 0)
        }
      }
    )
    for (f <- futures) Await.result(f, Duration.Inf)
  }

}

