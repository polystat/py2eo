package org.polystat.py2eo.transpiler

import org.junit.Assert.fail
import org.junit.runners.MethodSorters
import org.junit.{FixMethodOrder, Test}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path}
import scala.sys.process.Process
import scala.util.Try

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
final class CPythonTests extends Commons {

  private val dirPath: Path = s"$testsPrefix/testParserPrinter"
  private val cpythonLink = "https://github.com/python/cpython"

  @Test def aParserPrinterOnCPython(): Unit = {
    val dir = Directory(dirPath)
    dir.createDirectory(failIfExists = false)

    val eoFiles = Directory(dirPath / "afterParser")
    eoFiles.createDirectory(failIfExists = false)

    val cpython = Directory(dirPath / "cpython")
    if (!cpython.exists) {
      Process(s"git clone $cpythonLink ${cpython.name}", dirPath.jfile).!!
      Process("git checkout v3.8.10", cpython.jfile).!!
    }

    println("Version of python is:")
    Process(s"$python --version").!!

    val testsDir = Directory(cpython / "Lib" / "test")
    val tests = testsDir.deepFiles.filter(_.extension == "py")

    val futures = for {test <- tests} yield {
      Future {
        val module = test.stripExtension
        println(s"transpiling $module")

        Try(test.slurp).map(Transpile(module, _)).get match {
          case None => fail()
          case Some(transpiled) =>
            val result = File(eoFiles / s"$module.eo")
            result.createFile(failIfExists = false)
            result.writeAll(transpiled)
        }
      }
    }

    for (f <- futures) Await.result(f, Duration.Inf)
  }

  @Test def bCheckEOSyntax(): Unit = {
    checkEOSyntaxInDirectory(dirPath.toString)
  }
}