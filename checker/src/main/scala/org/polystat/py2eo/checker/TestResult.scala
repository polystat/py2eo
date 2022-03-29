package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation

object CompilingResult extends Enumeration {
  type CompilingResult = Value
  val invalid: Value = Value("n/a")
  val failed: Value = Value("failed")
  val transpiled: Value = Value("transpiled")
  val compiled: Value = Value("compiled")
  val passed: Value = Value("passed")
  val timeout: Value = Value("timeout")
}

case class TestResult(name: String, results: Map[Mutation, CompilingResult])
