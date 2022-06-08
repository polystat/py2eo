package org.polystat.py2eo.transpiler

import org.junit.Test
import org.yaml.snakeyaml.Yaml

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future, TimeoutException, blocking}
import scala.reflect.io.{Directory, File, Streamable}
import scala.sys.process.Process

class TestConstructionsCounter extends Commons {
  private val currentDirectory = Directory.Current.get
  private val testsPath = currentDirectory / "/src/test/resources/org/polystat/py2eo/transpiler/simple-tests"
  private val pom = File(currentDirectory / ".." / "runEO" / "pom.xml").slurp

  @Test
  def test(): Unit = {
    val tests = testsPath.toDirectory.deepFiles.filter(file => file.extension == "yaml").toSet

    /** Set of triplets: test name, category and future run result */
    val results = for {test <- tests} yield (test, test.parent, Future(passes(test)))

    val awaited = results.map(result => (result._1, result._2, Await.result(result._3, Duration.Inf)))
    val total = awaited.size
    val passed = awaited.count(res => res._3)
    println(s"tests passed: ${(100f * passed) / total}% ($passed of $total)")

    val constructions = awaited.map(result => result._2)
    for (construction <- constructions) {
      val relevant = awaited.filter(result => result._2 == construction)

      val total = relevant.size
      val passed = relevant.count(res => res._3)
      println(s"${construction.name} tests passed: ${(100f * passed) / total}% ($passed of $total)")
    }
  }

  /** Returns <code>true</code> if passed running stage */
  private def passes(test: File): Boolean = {
    val contents = new Yaml().load[java.util.Map[String, String]](test.slurp).get("python")
    val module = test.stripExtension

    Transpile(module, contents) match {
      case None => false
      case Some(transpiled) =>
        val dir = Directory.makeTemp()
        File(dir / "pom.xml").writeAll(pom)
        val EOFile = File(dir / "test.eo").createFile()
        EOFile.writeAll(transpiled)

        val process = Process("mvn clean test", dir.jfile).run
        val future = Future(blocking(process.exitValue))

        try Await.result(future, 2.minutes) == 0
        catch { case _: TimeoutException => process.destroy; false }
        finally dir.deleteRecursively
    }
  }
}