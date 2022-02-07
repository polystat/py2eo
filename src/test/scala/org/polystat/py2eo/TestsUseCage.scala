package org.polystat.py2eo

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.yaml.snakeyaml.error.YAMLException

import java.io.File
import java.nio.file.{Files, Path}
import java.{lang => jl, util => ju}

@RunWith(value = classOf[Parameterized])
class TestsUseCage(path: jl.String) extends Commons {
  @Test def useCageRunner():Unit = {
    useCageHolder(path,getYamlStr(path))
  }
}

object TestsUseCage{
  @Parameters def parameters: ju.Collection[Array[jl.String]] = {
    val resFolder = getClass.getResource("").getFile
    val res = collection.mutable.ArrayBuffer[String]()

    Files.walk(new File(resFolder).toPath).filter((p: Path) => p.toString.endsWith(".yaml") &&
      !p.toString.contains("testParserPrinter") && !p.toString.contains("trivialWithBreak")
      && !p.toString.contains("inheritance")).forEach((p: Path) => {
      val testHolder = new File(p.toString)
      try {
        println(testHolder.getPath)
        res.addOne(p.toString)
      } catch {
        case e: YAMLException => println(s"Couldn't parse ${testHolder.getName} file with error ${e.getMessage}")
        case e: ClassCastException => println(s"Couldn't parse ${testHolder.getName} file with error ${e.getMessage}")
      }
    })


    val list = new ju.ArrayList[Array[jl.String]]()
    res.foreach(n => list.add(Array(n)))
    list
  }
}
