package org.polystat.py2eo.transpiler

import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.{AfterAll, Order, Test, TestMethodOrder}
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.polystat.py2eo.transpiler.CPythonIT.directory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path}
import scala.sys.process.Process
import scala.util.Try

@TestMethodOrder(classOf[OrderAnnotation])
final class CPythonIT extends Commons {

  private val cpythonLink = "https://github.com/python/cpython"
  private val blacklisted = Set(
    // these are excluded because of some encoding problems in the lexer
    "test_unicode_identifiers.py", "test_source_encoding.py",
    "badsyntax_3131.py", "badsyntax_pep3120.py",
    "module_koi8_r.py", "module_iso_8859_1.py",
    // most of these are excluded because they do tests by comparing stack traces as strings
    // but code before parser-printer has different line numbers than code after => traces are
    // always different =>
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

  @Test
  @Order(1)
  def parserPrinterOnCPython(): Unit = {
    val eoFiles = Directory(directory / "afterParser")
    eoFiles.createDirectory(failIfExists = false)

    val cpython = Directory(directory / "cpython")

    Process(s"git clone $cpythonLink ${cpython.name}", directory.jfile).!!
    Process("git checkout v3.8.10", cpython.jfile).!!

    val testsDir = Directory(cpython / "Lib" / "test")
    val tests = testsDir.deepFiles.filter(_.extension == "py")

    val futures = for {test <- tests if !blacklisted(test.name)} yield {
      Future {
        val module = test.stripExtension

        Try(test.slurp).toOption.flatMap(Transpile(module, Transpile.Parameters(wrapInAFunction = true, isModule = false), _)) match {
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

  @Test
  @Order(2)
  def checkEOSyntax(): Unit = {
    checkEOSyntaxInDirectory(Directory(directory / "afterParser").toString)
  }
}

object CPythonIT {
  private val directory = Directory.makeTemp(prefix = "org.polystat.py2eo.")
  @AfterAll def cleanup(): Unit = {
    directory.deleteRecursively
  }
}