package org.polystat.py2eo.checker

import scala.language.postfixOps
import scala.reflect.io.{File, Path, Streamable}

object WriteConstructions {

  val filename = "constructions.html"

  /** Write testing results to constructions.html in the provided directory */
  def apply(outputPath: Path, tests: List[AwaitedTestResult]): Unit = File(outputPath / filename) writeAll html(tests)

  /** Returns html file contents */
  private def html(tests: List[AwaitedTestResult]): String = {
    lazy val stream = getClass getResourceAsStream "head.html"
    lazy val head = Streamable slurp stream
    lazy val body = s"<body>\n${table(tests)}</body>\n"

    s"<html lang=\"en-US\">\n$head$body</html>\n"
  }

  /** Returns full table */
  private def table(tests: List[AwaitedTestResult]): String = {
    s"<table id=programs>\n$header${body(tests)}</table>\n"
  }

  /** Returns table header */
  private def header: String = {
    s"<tr>\n<th class=\"sorter\">Construction</th>\n<th class=\"sorter data\">Result</th></tr>\n"
  }

  /** Returns table body */
  private def body(tests: List[AwaitedTestResult]): String = {
    val constructions = (tests map (test => test.category)).toSet
    val constructionsMap = (for (c <- constructions) yield (c, tests filter (test => test.category == c))).toMap

    val body = for ((construction, list) <- constructionsMap) yield {
      val data = list.exists(test => {
        val sublist = test.results.getOrElse(Map.empty).values.toList
        sublist.contains(CompilingResult.failed) || sublist.contains(CompilingResult.nodiff)
      })

      val cell = if (data)
        s"<td class=\"unexpected data\">failed</td>\n"
      else
        s"<td class=\"expected data\">passed</td>\n"

      s"<tr>\n<th class=\"left\">$construction</th>\n$cell</tr>\n"
    }

    body mkString
  }
}
