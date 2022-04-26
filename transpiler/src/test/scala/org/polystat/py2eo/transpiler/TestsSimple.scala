package org.polystat.py2eo.transpiler

import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.yaml.snakeyaml.error.YAMLException

import java.io.File
import java.nio.file.{Files, Path}
import java.{lang => jl, util => ju}


@RunWith(value = classOf[Parameterized])
class TestsSimple(path: jl.String) extends Commons {
  @Test def testDef(): Unit = {
    useCageHolder(new File(path))
  }
}

object TestsSimple {
  @Parameters def parameters: ju.Collection[Array[jl.String]] = {
    val testsPrefix = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/transpiler"

    val res = collection.mutable.ArrayBuffer[String]()
    val simpleTestsFolder = new File(testsPrefix + File.separator + "simple-tests" + File.separator)
    Files.walk(simpleTestsFolder.toPath).filter((p: Path) => p.toString.endsWith(".yaml")).forEach((p: Path) => {
      val testHolder = new File(p.toString)
      try {
        res.addOne(p.toString)
      } catch {
        case e: YAMLException => fail(s"Couldn't parse ${testHolder.getName} file with error ${e.getMessage}")
        case e: ClassCastException => fail(s"Couldn't parse ${testHolder.getName} file with error ${e.getMessage}")
      }
    })


    val list = new ju.ArrayList[Array[jl.String]]()
    res.foreach(n => list.add(Array(n)))
    list
  }
}
