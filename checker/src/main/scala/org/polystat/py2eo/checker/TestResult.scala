package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation

object CompilingResult extends Enumeration {
  type CompilingResult = Value
  val invalid: Value = Value("n/a")
  val failed: Value = Value("failed")
  val nodiff: Value = Value("no diff")
  val passed: Value = Value("passed")
}

case class TestResult(name: String, results: Option[Map[Mutation, CompilingResult]])
