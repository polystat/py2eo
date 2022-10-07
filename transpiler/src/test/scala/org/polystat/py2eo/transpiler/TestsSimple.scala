package org.polystat.py2eo.transpiler

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import scala.reflect.io.{Directory, File}

object TestsSimple extends Commons {
  private val simpleTests = Directory(s"$testsPrefix/simple-tests")
  def test: Array[File] = collect(simpleTests, filterEnabled = true)
}

@ParameterizedTest
@MethodSource
class TestsSimple(test: File) extends Commons {
  @Test def test(): Unit = useCageHolder(test.jfile)
}