package org.polystat.py2eo

import org.junit.Assert._
import org.junit.{Ignore, Test}
import org.polystat.py2eo.Common.dfsFiles
import org.polystat.py2eo.Expression._

import java.io.File
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.sys.process._

class Tests extends Commons {
  var files = Array.empty[File]

  import Main.{debugPrinter, readFile, writeFile}


  @Test def simplifyInheritance(): Unit = {
    val name = "inheritance"
    val test = new File(testsPrefix + "/" + name + ".yaml")
    def db = debugPrinter(test)(_, _)

    SimplePass.allTheGeneralPasses(db, Parse.parse(getYamlStr(test.getPath), db), new SimplePass.Names())
  }

  @Ignore
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
        val eoText = try {
          Transpile.transpile(db)(name.substring(0, name.length - 3), readFile(test))
        } catch {
          case e : Throwable =>
            println(s"failed to transpile $name: ${e.toString}")
            throw e
        }
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

}

