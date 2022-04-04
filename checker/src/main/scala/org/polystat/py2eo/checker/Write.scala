package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation

import scala.language.postfixOps
import scala.reflect.io.{File, Path, Streamable}

object Write {

  /** Write testing results to index.html in the provided directory */
  def apply(outputPath: Path, tests: Iterator[TestResult], mutations: Iterable[Mutation]): Unit = {
    File(outputPath / "index.html") writeAll html(tests, mutations)
  }

  /** Returns html file contents */
  private def html(tests: Iterator[TestResult], mutations: Iterable[Mutation]): String = {
    lazy val stream = getClass getResourceAsStream "head.html"
    lazy val head = Streamable slurp stream
    lazy val body = s"<body>\n${table(tests, mutations)}</body>\n"

    s"<html lang=\"en-US\">\n$head$body</html>\n"
  }

  /** Returns full table */
  private def table(tests: Iterator[TestResult], mutations: Iterable[Mutation]): String = {
    lazy val head = header(mutations)
    lazy val body = tests map (test => row(test, mutations))

    s"<table id=programs>\n$head${body mkString}</table>\n"
  }

  /** Returns table header */
  private def header(mutations: Iterable[Mutation]): String = {
    lazy val row = mutations map (mutation => s"<th class=\"sorter data\">$mutation</th>\n")
    s"<tr>\n<th class=\"sorter\">Test</th>\n${row mkString}</tr>\n"
  }

  /** Returns table row */
  private def row(test: TestResult, mutations: Iterable[Mutation]): String = {
    lazy val name = test.name
    test.results match {
      case None =>
        lazy val colspan = mutations size
        lazy val str = s"<td colspan=\"$colspan\" class=\"data\">Original test couldn't be transpiled</td>"
        s"<tr>\n<th class=\"left\">$name</th>\n$str</tr>\n"

      case Some(results) =>
        lazy val cells = mutations.toList map (mutation => cell(mutation, name, results(mutation)))
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
}
