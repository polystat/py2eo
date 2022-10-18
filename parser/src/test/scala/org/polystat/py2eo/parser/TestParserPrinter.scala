package org.polystat.py2eo.parser

import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.{Test, AfterAll}

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
    // these are excluded because of some encoding problems in the lexer
    "test_unicode_identifiers.py", "test_source_encoding.py",
    "badsyntax_3131.py", "badsyntax_pep3120.py",
    "module_koi8_r.py", "module_iso_8859_1.py",
    // most of these are excluded because they do tests by comparing stack traces as strings
    // but code before parser-printer has different line numbers than code after => traces are always different =>
    // those tests cannot possibly pass
    "test_traceback.py", "test_dis.py", "test_zipfile.py",
    "test_multiprocessing_fork.py", "test_sys.py",
    "test_import.yaml", "test_strtod.py", "test_trace.py",
    "test_doctest.py", "test_concurrent_futures.py", "test_inspect.py",
    "test_tracemalloc.py", "test_multiprocessing_spawn.py",
    "test_sys_settrace.py", "test_multiprocessing_forkserver.py",
    "test_compileall.py", "test_asyncio.py", "test_unittest.py",
    "test_email.py", "test_tools.py", "test_atexit.py", "test_pdb.py",
    "test_logging.py", "test_coroutines.py", "test_tasks.py",

    "test_grammar.py", "test_headerregistry.py"
  )

  @Test def apply(): Unit = {
    val cpython = Directory(directory / "cpython")

    Process(s"git clone $cpythonLink ${cpython.name}", directory.jfile).!!
    Process("git checkout v3.8.10", cpython.jfile).!!

    val testsDirectory = Directory(cpython / "Lib" / "test")
    val tests = testsDirectory.deepFiles.filter(_.extension == "py")

    val futures = for {test <- tests if !blacklisted(test.name)} yield Future {
      Parse(test).map(PrintPython.print).fold(fail())(test writeAll _)
    }

    futures foreach await

    assume(Properties.isMac || Properties.isLinux)
    Process("./configure", cpython.jfile).!!
    Process(s"make -j ${availableProcessors + 2}", cpython.jfile).!!
    Process("make test", cpython.jfile).!!
  }

  @AfterAll def cleanup(): Unit = {
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
