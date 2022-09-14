package org.polystat.py2eo.parser

import org.junit.Assert.fail
import org.junit.{After, Test}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable, Future}
import scala.reflect.io.Directory
import scala.sys.process.Process
import scala.util.Properties

/** Parse and re-print tests from cpython repo and test them out */
final class TestParserPrinter {

  private val cpythonLink = "https://github.com/python/cpython"
  private val directory = Directory.makeTemp(prefix = "org.polystat.py2eo.")
  private val availableProcessors = sys.runtime.availableProcessors
  private val blacklisted = Set(
    "test_unicode_identifiers.py", "test_source_encoding.py",
    "badsyntax_3131.py", "badsyntax_pep3120.py",
    "module_koi8_r.py", "module_iso_8859_1.py"
  )

  @Test def apply(): Unit = {
    val cpython = Directory(directory / "cpython")

    Process(s"git clone $cpythonLink ${cpython.name}", directory.jfile).!!
    Process("git checkout v3.8.10", cpython.jfile).!!

    val testsDirectory = Directory(cpython / "Lib" / "test")
    val tests = testsDirectory.deepFiles.filter(_.extension == "py")
    val whitelisted = tests.filter(test => blacklisted(test.name))

    val futures = for {test <- whitelisted} yield Future {
      Parse(test).map(PrintPython.print).fold(fail())(test writeAll _)
    }

    futures foreach await

    assume(Properties.isMac || Properties.isLinux)
    Process("./configure", cpython.jfile).!!
    Process(s"make -j ${availableProcessors + 2}", cpython.jfile).!!
    Process("make test", cpython.jfile).!!
  }

  @After def cleanup(): Unit = {
    directory.deleteRecursively
  }

  /**
   * Await and return the result (of type `T`) of an [[Awaitable]]
   *
   * @param awaitable the [[Awaitable]] to be awaited
   */
  private def await[T](awaitable: Awaitable[T]): T = {
    Await.result(awaitable, Duration.Inf)
  }
}
