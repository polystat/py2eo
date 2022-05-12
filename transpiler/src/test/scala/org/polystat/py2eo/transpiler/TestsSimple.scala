package org.polystat.py2eo.transpiler

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

import java.io.File
import java.{lang => jl, util => ju}

object TestsSimple extends Commons {
  @Parameters def parameters: ju.Collection[Array[jl.String]] = collect("simple-tests", needsFiltering = true)
}

@RunWith(value = classOf[Parameterized])
class TestsSimple(path: jl.String) extends Commons {
  @Test def testDef(): Unit = useCageHolder(new File(path))
}