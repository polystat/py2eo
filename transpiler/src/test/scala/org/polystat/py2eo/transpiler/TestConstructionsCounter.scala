package org.polystat.py2eo.transpiler

import org.junit.Test
import org.yaml.snakeyaml.Yaml

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future, TimeoutException, blocking}
import scala.reflect.io.{File, Path}
import scala.sys.process.Process

class TestConstructionsCounter extends Commons {
  private val testsPath: Path = "src/test/resources/org/polystat/py2eo/transpiler/simple-tests"
  private val runEOPath: Path = "../runEO/"

  @Test
  def test(): Unit = {
    val tests = testsPath.toDirectory.deepFiles.filter(file => file.extension == "yaml").toSet

    /** Set of triplets: test name, category and run result */
    val results = for {test <- tests} yield (test, test.parent, passes(test))

    val total = results.size
    val passed = results.count(res => res._3)
    println(s"tests passed: ${(100f * passed) / total}% ($passed of $total)")

    val constructions = results.map(result => result._2)
    val constructionsResults = for {construction <- constructions} yield {
      val relevant = results.filter(result => result._2 == construction)

      val total = relevant.size
      val passed = relevant.count(res => res._3)
      val percentage = (100f * passed) / total
      println(s"${construction.name} tests passed: $percentage% ($passed of $total)")

      percentage
    }

    println(s"total constructions passed: ${constructionsResults.sum / constructionsResults.size}%")
  }

  /** Returns <code>true</code> if passed running stage */
  private def passes(test: File): Boolean = {
    val contents = new Yaml().load[java.util.Map[String, String]](test.slurp).get("python")
    val module = test.stripExtension

    val parameters = Transpile.Parameters(wrapInAFunction = false)
    Transpile(module, parameters, contents) match {
      case None => false
      case Some(transpiled) =>
        val EOFile = (runEOPath / "test.eo").createFile()
        EOFile.writeAll(transpiled)

        val process = Process("mvn clean test", runEOPath.jfile).run
        val future = Future(blocking(process.exitValue))

        try
          Await.result(future, 2.minutes) == 0
        catch {
          case _: TimeoutException => process.destroy; false
        } finally
          EOFile.delete
    }
  }
}