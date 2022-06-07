package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object CompilingResult extends Enumeration {
  type CompilingResult = Value
  val invalid: Value = Value("n/a")
  val failed: Value = Value("failed")
  val nodiff: Value = Value("no diff")
  val passed: Value = Value("passed")
}


case class AwaitedTestResult(name: String, category: String, results: Option[Map[Mutation, CompilingResult]])

case class TestResult(name: String, category: String, results: Option[Map[Mutation, Future[CompilingResult]]]) {
  def await: AwaitedTestResult = {
    results match {
      case None => AwaitedTestResult(name, category, None)
      case Some(resultMap) =>
        val set = resultMap.toSet
        val awaited = for {(mutation, futureResult) <- set} yield (mutation, Await.result(futureResult, Duration.Inf))

        AwaitedTestResult(name, category, Some(awaited.toMap))
    }
  }
}
