package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation

import scala.language.postfixOps
import scala.reflect.io.{File, Path, Streamable}

object Write {

  /** Write testing results to index.html in the provided directory */
  def apply(outputPath: Path, tests: List[TestResult], mutations: Iterable[Mutation]): Unit = {
    def sorter(left: Mutation, right: Mutation): Boolean = {
      lazy val value = passed(tests, left).toDouble / applied(tests, left).size
      value < (passed(tests, right).toDouble / applied(tests, right).size)
    }

    lazy val filtered = mutations filter (mutation => applied(tests, mutation).nonEmpty)
    lazy val sorted = filtered.toList sortWith sorter
    File(outputPath / "index.html") writeAll html(tests, sorted)
  }

  /** Returns html file contents */
  private def html(tests: List[TestResult], mutations: List[Mutation]): String = {
    lazy val stream = getClass getResourceAsStream "head.html"
    lazy val head = Streamable slurp stream
    lazy val body = s"<body>\n${table(tests, mutations)}</body>\n"

    s"<html lang=\"en-US\">\n$head$body</html>\n"
  }

  /** Returns full table */
  private def table(tests: List[TestResult], mutations: List[Mutation]): String = {
    lazy val head = header(mutations)
    lazy val sum = summary(tests, mutations)
    lazy val body = tests map (test => row(test, mutations)) mkString

    s"<table id=programs>\n$head$sum$body</table>\n"
  }

  /** Returns table header */
  private def header(mutations: List[Mutation]): String = {
    lazy val row = mutations map (mutation => s"<th class=\"sorter data\">$mutation</th>\n")
    s"<tr>\n<th class=\"sorter\">Test</th>\n${row mkString}</tr>\n"
  }

  /** Returns table summary */
  private def summary(tests: List[TestResult], mutations: List[Mutation]): String = {
    def result(tests: List[TestResult], mutation: Mutation): String = {
      s"${passed(tests, mutation)} of ${applied(tests, mutation) size}"
    }

    lazy val row = mutations map (mutation => s"<th class=\"data\">${result(tests, mutation)}</th>\n")
    s"<tr>\n<th class=\"sorter\">Passed</th>\n${row mkString}</tr>\n"
  }

  /** Returns table row */
  private def row(test: TestResult, mutations: List[Mutation]): String = {
    lazy val name = test.name
    test.results match {
      case None =>
        lazy val colspan = mutations size
        lazy val str = s"<td colspan=\"$colspan\" class=\"data\">Original test couldn't be transpiled</td>"
        s"<tr>\n<th class=\"left\">$name</th>\n$str</tr>\n"

      case Some(results) =>
        lazy val cells = mutations map (mutation => cell(mutation, name, results(mutation)))
        s"<tr>\n<th class=\"left\">$name</th>\n${cells mkString}</tr>\n"
    }
  }

  /** Returns table cell with test result */
  private def cell(mutation: Mutation, name: String, stage: CompilingResult): String = {
    lazy val kind = stage match {
      case CompilingResult.invalid => "data"
      case CompilingResult.failed => "unexpected data"
      case CompilingResult.nodiff => "unexpected data"
      case CompilingResult.passed => "expected data"
    }

    lazy val data = if (stage equals CompilingResult.passed) {
      lazy val link = Check.diffName(name, mutation)
      s"<a href=\"$link\">$stage</a>"
    } else {
      stage
    }

    s"<td class=\"$kind\">$data</td>\n"
  }

  /** Returns passed tests count */
  def passed(tests: List[TestResult], mutation: Mutation): Int = {
    applied(tests, mutation) count (test => test.results match {
      case None => false
      case Some(results) => results(mutation) == CompilingResult.passed
    })
  }

  /** Returns list of tests with applied mutation */
  def applied(tests: List[TestResult], mutation: Mutation): List[TestResult] = {
    tests filter (test => test.results match {
      case None => false
      case Some(results) => results(mutation) != CompilingResult.invalid
    })
  }
}
