package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.CompilingResult.failed
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation

import scala.language.postfixOps
import scala.reflect.io.{File, Path, Streamable}

object WriteGrouped {
  /** Write testing results to index.html in the provided directory */
  def apply(outputPath: Path, tests: List[TestResult], mutations: Iterable[Mutation]): Unit = {
    val awaited = tests map (test => test.await)

    lazy val filtered = mutations filter (mutation => applied(awaited, mutation).nonEmpty)
    lazy val sorted = filtered.toList sortWith sorter(awaited)
    File(outputPath / "index_new.html") writeAll html(awaited, sorted)
  }

  /** Returns html file contents */
  private def html(tests: List[AwaitedTestResult], mutations: List[Mutation]): String = {
    lazy val stream = getClass getResourceAsStream "head.html"
    lazy val head = Streamable slurp stream
    lazy val body = s"<body>\n${table(tests, mutations)}</body>\n"

    s"<html lang=\"en-US\">\n$head$body</html>\n"
  }

  /** Returns full table */
  private def table(tests: List[AwaitedTestResult], mutations: List[Mutation]): String = {
    lazy val head = header(mutations)
    lazy val res = tests.exists(res => res.results.toList.contains(failed))

    lazy val body =  s"<td class=data>${tests.head.folder}</td>\n<td class=data>$res</td>"

    s"<table id=programs>\n$head$body</table>\n"
  }

  /** Returns table header */
  private def header(mutations: List[Mutation]): String = {
    lazy val row = s"<th class=\"sorter data\">passed/failed</th>\n"
    s"<tr>\n<th class=\"sorter\">Test</th>\n${row mkString}</tr>\n"
  }



  /** Sorter for mutations */
  def sorter(tests: List[AwaitedTestResult])(left: Mutation, right: Mutation): Boolean = {
    lazy val value = passed(tests, left).toDouble / applied(tests, left).size
    value < (passed(tests, right).toDouble / applied(tests, right).size)
  }

  /** Returns passed tests count */
  def passed(tests: List[AwaitedTestResult], mutation: Mutation): Int = {
    applied(tests, mutation) count (test => test.results match {
      case None => false
      case Some(results) => results(mutation) == CompilingResult.passed
    })
  }

  /** Returns list of tests with applied mutation */
  def applied(tests: List[AwaitedTestResult], mutation: Mutation): List[AwaitedTestResult] = {
    tests filter (test => test.results match {
      case None => false
      case Some(results) => results(mutation) != CompilingResult.invalid
    })
  }
}
