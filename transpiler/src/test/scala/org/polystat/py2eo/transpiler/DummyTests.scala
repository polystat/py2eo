package org.polystat.py2eo.transpiler

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import scala.reflect.io.{Directory, File}

object DummyTests extends Commons {
  private val dummyTests = Directory(s"$testsPrefix/dummy_tests")
  def parameters: Array[File] = collect(dummyTests)
}

class DummyTests extends Commons {

  @ParameterizedTest
  @MethodSource(Array("parameters"))
  def test(test: File): Unit = useCageHolder(test.jfile)
}

