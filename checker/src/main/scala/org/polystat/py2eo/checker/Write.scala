package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.Check.{diffName, expected}
import org.polystat.py2eo.checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation

import scala.language.postfixOps
import scala.reflect.io.{File, Path, Streamable}

object Write {

  /** Write testing results to index.html in the provided directory */
  def apply(outputPath: Path, testResults: List[TestResult]): Unit = {
    File(outputPath / "index.html") writeAll html(testResults)
  }

  /** Returns html file contents */
  private def html(tests: List[TestResult]): String = {
    lazy val stream = getClass getResourceAsStream "head.html"
    lazy val head = Streamable slurp stream
    lazy val body = s"<body>\n${table(tests)}</body>\n"

    s"<html lang=\"en-US\">\n$head$body</html>\n"
  }

  /** Returns full table */
  private def table(tests: List[TestResult]): String = {
    lazy val mutations = tests.head.results.keys toList
    lazy val head = header(mutations)
    lazy val body = tests map (test => row(test, mutations))

    s"<table id=programs>\n$head${body mkString}</table>\n"
  }

  /** Returns table header */
  private def header(mutations: List[Mutation]): String = {
    lazy val row = mutations map (mutation => s"<th class=\"sorter data\">$mutation</th>\n")
    s"<tr>\n<th class=\"sorter\">Test</th>\n${row mkString}</tr>\n"
  }

  /** Returns table row */
  private def row(test: TestResult, mutations: List[Mutation]): String = {
    lazy val name = test.name
    lazy val cells = mutations map (mutation => cell(mutation, name, test results mutation))

    s"<tr>\n<th class=\"left\">$name</th>\n${cells mkString}</tr>\n"
  }

  /** Returns table cell with test result */
  private def cell(mutation: Mutation, name: String, stage: CompilingResult): String = {
    lazy val link = diffName(name, mutation)

    lazy val kind = if (stage == CompilingResult.invalid) {
      "data"
    } else if (stage == expected(mutation)) {
      "expected data"
    } else {
      "unexpected data"
    }

    lazy val data = if (Set(CompilingResult.invalid, CompilingResult.failed) contains stage) {
      stage toString
    } else {
      s"<a href=\"$link\">$stage</a>"
    }

    s"<td class=\"$kind\">$data</td>\n"
  }
}
