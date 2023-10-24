package org.polystat.py2eo.parser

import org.junit.jupiter.api.{AfterAll, Assertions, Test}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.reflect.io.{Directory, File}
import scala.sys.process.Process
import scala.util.Properties

object PyParserIT {

  /** Delete the directory by hand since Scala has some problems with it */
  @AfterAll def cleanup(): Unit = this.directory.deleteRecursively

  /** Temporary directory where to conduct tests */
  private val directory = Directory.makeTemp(prefix = "org.polystat.py2eo.").toAbsolute

  /** Repository with python tests */
  private val repo = "https://github.com/python/cpython"

  /** Blacklisted test names; do not update them */
  private val blacklisted = Set(
    // these are excluded because of some encoding problems in the lexer
    "test_unicode_identifiers.py", "test_source_encoding.py",
    "badsyntax_3131.py", "badsyntax_pep3120.py",
    "module_koi8_r.py", "module_iso_8859_1.py",
    // most of these are excluded because they do tests by comparing
    // stack traces as strings but code before parser-printer has different
    // line numbers than code after => traces are always different =>
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
}

/** Parse and re-print tests from cpython repo and test them out */
final class PyParserIT {
  @Test def apply(): Unit = {
    val cpython = Directory(PyParserIT.directory / "cpython")
    Process(s"git clone --depth=1 --branch v3.8.10 ${PyParserIT.repo} $cpython").!!

    val tests = Directory(cpython / "Lib" / "test")
      .deepFiles
      .toList
      .filter(_.extension == "py")
      .filter(_.name.startsWith("test"))
      .filterNot(file => PyParserIT.blacklisted(file.name))
      .map(reprint)

    tests.foreach { Await.result(_, Duration.Inf) }
    println(s"Total of ${tests.length} files transpiled")

    assume(Properties.isMac || Properties.isLinux)
    Process("./configure", cpython.jfile).!!
    Process(s"make -j ${sys.runtime.availableProcessors}", cpython.jfile).!!
    Process("make test", cpython.jfile).!!
  }

  /** Parses and reprints the given test and calls [[Assertions.fail]] if failed to parse it */
  private def reprint(test: File): Future[Unit] = Future {
    Parse(test).map(PrintPython.print).fold(this.fail(test))(test writeAll _)
  }

  /** Prints the failed test name and calls [[Assertions.fail]] */
  private def fail(test: File): Unit = {
    println(s"failed on ${test.name}")
    Assertions.fail()
  }
}
