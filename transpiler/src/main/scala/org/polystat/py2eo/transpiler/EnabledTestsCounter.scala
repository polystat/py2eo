package org.polystat.py2eo.transpiler

import java.io.FileInputStream
import scala.reflect.io.{File, Path}
import org.yaml.snakeyaml.Yaml

object EnabledTestsCounter {
  private val testsPath: Path = "transpiler/src/test/resources/org/polystat/py2eo/transpiler/simple-tests"

  case class TestResult(name: String, category: String, enabled: Boolean)

  def main(args: Array[String]): Unit = {
    val tests = testsPath.toDirectory.deepFiles.toSet
    val results = for {
      test <- tests
      if !test.name.startsWith("eo_blocked")
      if test.extension == "yaml"
    } yield TestResult(test.name, test.parent.name, isEnabled(test))

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

  private def isEnabled(f: File): Boolean = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f.jfile))

    map.containsKey("enabled") && map.getOrDefault("enabled", "false").asInstanceOf[Boolean]
  }

}
