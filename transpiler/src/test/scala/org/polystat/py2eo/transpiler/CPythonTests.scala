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
  private val blacklisted = Set(
    "test_unicode_identifiers.py", "test_source_encoding.py",
    "badsyntax_3131.py", "badsyntax_pep3120.py",
    "module_koi8_r.py", "module_iso_8859_1.py"
  )

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

    val futures = for {test <- tests if !blacklisted(test.name)} yield {
      Future {
        val module = test.stripExtension
        println(s"transpiling $module")

        Try(test.slurp).toOption.flatMap(Transpile(module, _)) match {
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
    checkEOSyntaxInDirectory(Directory(dirPath / "afterParser").toString)
  }
}