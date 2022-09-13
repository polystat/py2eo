package org.polystat.py2eo.parser

import org.junit.Assert.fail
import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
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

    val testsDir = Directory(cpython / "Lib" / "test")
    val tests = testsDir.deepFiles.filter(_.extension == "py")

    val futures = for {test <- tests} yield Future {
      Parse(test.slurp).map(PrintPython.print).fold(fail())(test.writeAll(_))
    }

    for (f <- futures) Await.result(f, Duration.Inf)

    assume(Properties.isMac || Properties.isLinux)
    Process("./configure", cpython.jfile).!!

    val processorsNumber = Runtime.getRuntime.availableProcessors
    println(s"have $processorsNumber processors")
    Process(s"make -j ${processorsNumber + 2}", cpython.jfile).!!
    Process("make test", cpython.jfile).!!
  }
}
