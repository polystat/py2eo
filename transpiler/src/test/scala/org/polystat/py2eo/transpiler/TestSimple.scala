package org.polystat.py2eo.transpiler

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import java.util.stream.StreamSupport
import scala.jdk.CollectionConverters.IterableHasAsJava
import scala.reflect.io.{Directory, File}

object TestSimple extends Commons {
  private val simpleTests = Directory(s"$testsPrefix/simple-tests")
  def parameters(): java.util.stream.Stream[File] = {
    val a = collect(simpleTests, filterEnabled = true).toList
    StreamSupport.stream(a.asJava.spliterator(), false)
  }
}

class TestSimple extends Commons {

  @ParameterizedTest
  @MethodSource(Array("parameters"))
  def test(test: File): Unit = useCageHolder(test.jfile)
}