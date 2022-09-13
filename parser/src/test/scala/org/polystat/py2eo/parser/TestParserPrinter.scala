package org.polystat.py2eo.parser

import org.junit.Assert.fail
import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable, Future}
import scala.reflect.io.Directory
import scala.sys.process.Process
import scala.util.Properties

/** Parse and re-print tests from cpython repo and test them out */
final class TestParserPrinter {

  private val cpythonLink = "https://github.com/python/cpython"

  @Test def apply(): Unit = {
    val directory = Directory.makeTemp(prefix = "org.polystat.py2eo")
    val cpython = Directory(directory / "cpython")

    Process(s"git clone $cpythonLink ${cpython.name}", directory.jfile).!!
    Process("git checkout v3.8.10", cpython.jfile).!!

    val testsDirectory = Directory(cpython / "Lib" / "test")
    val tests = testsDirectory.deepFiles.filter(_.extension == "py")

    val futures = for {test <- tests} yield Future {
      Parse(test.slurp).map(PrintPython.print).fold(fail())(test writeAll _)
    }

    futures foreach await

    assume(Properties.isMac || Properties.isLinux)
    Process("./configure", cpython.jfile).!!

    val availableProcessors = sys.runtime.availableProcessors
    println(s"have $availableProcessors processors")
    Process(s"make -j ${availableProcessors + 2}", cpython.jfile).!!
    Process("make test", cpython.jfile).!!
  }

  /**
   * Await and return the result (of type `T`) of an `Awaitable`.
   *
   * @param awaitable the `Awaitable` to be awaited
   */
  private def await[T](awaitable: Awaitable[T]): T = {
    Await.result(awaitable, Duration.Inf)
  }
}
