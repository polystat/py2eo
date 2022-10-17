package org.polystat.py2eo.transpiler

import org.junit.jupiter.api.Test

import scala.reflect.io.Path

class TestEnabledCounter extends Commons {
  private val testsPath: Path = "src/test/resources/org/polystat/py2eo/transpiler/simple-tests"

  case class TestResult(name: String, category: String, enabled: Boolean)

  @Test
  def apply(): Unit = {
    val tests = testsPath.toDirectory.deepFiles.filter(_.extension == "yaml").toSet
    val results = for {test <- tests if !test.name.startsWith("eo_blocked")} yield {
      TestResult(test.name, test.parent.name, isEnabled(test))
    }

    val total = results.size
    val enabled = results.count(_.enabled)
    println(s"tests enabled: ${(100f * enabled) / total}% ($enabled of $total)")

    val constructions = results.groupBy(_.category)
    val constructionsResults = for {(construction, relevant) <- constructions} yield {
      val total = relevant.size
      val passed = relevant.count(_.enabled)
      val percentage = (100f * passed) / total
      println(s"$construction tests passed: $percentage% ($passed of $total)")

      percentage
    }

    val percentage = constructionsResults.sum / constructionsResults.size
    println(s"total constructions passed: $percentage%")
  }
}