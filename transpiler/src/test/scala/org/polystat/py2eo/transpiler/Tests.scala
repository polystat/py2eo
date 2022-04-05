package org.polystat.py2eo.transpiler

import org.junit.Assert.assertTrue
import org.junit.{Ignore, Test}
import org.polystat.py2eo.parser.{Parse, Statement}
import org.polystat.py2eo.transpiler.Common.dfsFiles
import org.yaml.snakeyaml.Yaml

import java.io.{File, FileInputStream}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.sys.process.{Process, ProcessLogger}


class Tests {
  // the script to convert python files to .yaml containers. Use it to add new big files
  // for i in *.py; do perl -e 'print "python: |\n"; while (<>) { print "  $_" } ' < $i > ${i%.py}.yaml; done

  val separator: String = "/"
  var files = Array.empty[File]
  private val testsPrefix = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/transpiler"

  import Main.{debugPrinter, readFile, writeFile}

  private val python = {
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    assertTrue(0 == (Process("python --version") ! ProcessLogger(stdout.append(_), stderr.append(_))))
    val pattern = "Python (\\d+)".r
    val Some(match1) = pattern.findFirstMatchIn(if (stderr.toString() == "") stdout.toString() else stderr.toString())
    if (match1.group(1) == "2") "python3" else "python"
  }

  case class YamlTest(python : String, disabled : Boolean)

  def yaml2python(f : File): YamlTest = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))
    YamlTest(map.get("python"), map.containsKey("disabled"))
  }

  def chopExtension(fileName : String): String = fileName.substring(0, fileName.lastIndexOf("."))

  @Ignore
  @Test def simplifyInheritance(): Unit = {
    val name = "inheritance"
    val test = new File(testsPrefix + "/" + name + ".py")
    def db = debugPrinter(test)(_, _)

    SimplePass.allTheGeneralPasses(db, Parse(test, db), new SimplePass.Names())
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
    Process(s"$python --version") !

    // todo: test_named_expressions.py uses assignment expressions which are not supported.
    // test_os leads to a strange error with inode numbers on the rultor server only
    // supporting them may take several days, so this feature is currently skipped

    // "test_strtod.py", todo: what's the problem here???
    // "test_zipimport_support.py", todo: what's the problem here???
    // "test_zipfile64.py" works for more 2 minutes, too slowm
    // test_sys.py just hangs the testing with no progress (with no CPU load)
    // test_dis.py, test*trace*.py are not supported, because they seem to compare line numbers, which change after printing
    // many test for certain libraries are not present here, because these libraries are not installed by default in the CI

//    val test = List("test_threading.yaml", "test_sndhdr.yaml").map(name => new File(dirName + "/" + name))
    val test = dir.listFiles().toList
    val futures = test.map(test =>
      Future {
        if (!test.isDirectory && test.getName.startsWith("test_") && test.getName.endsWith(".yaml")) {
          def db = debugPrinter(test)(_, _)
          val name = chopExtension(test.getName)
          println(s"parsing $name")
          val py = try {
            yaml2python(test)
          } catch {
            case e : Throwable =>
              println(s"OBLOM for $name")
              throw e
          }
          val eoText = Transpile.transpile(db)(name, py.python)
          writeFile(test, "genUnsupportedEOPrim", ".eo", eoText)
          Files.copy(
            Paths.get(s"$dirName/afterParser/$name.py"),
            Paths.get(s"$dirName/afterParser/cpython/Lib/test/$name.py"),
            REPLACE_EXISTING
          )
        }
      }
    )
    for (f <- futures) Await.result(f, Duration.Inf)

    assume(System.getProperty("os.name") == "Linux" || System.getProperty("os.name") == "Mac OS X")
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
        def db(s : Statement.T, str : String) = () // debugPrinter(test)(_, _)
        val name = test.getName
        println(s"parsing $name")
        val eoText = try {
          Transpile.transpile(db)(chopExtension(name), readFile(test))
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
}
