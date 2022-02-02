package org.polystat.py2eo

import org.junit.Test
import org.junit.runners.Parameterized.Parameters
import org.yaml.snakeyaml.Yaml

import java.io.{File, FileInputStream}
import java.nio.file.{Files, Path, Paths}

class TestsUseCage extends Commons {
  @Parameters def parsePython():collection.mutable.ArrayBuffer[YamlItem] = {
    val res = collection.mutable.ArrayBuffer[YamlItem]()
    Files.walk(Paths.get(testsPrefix)).filter((p: Path) => p.toString.endsWith(".yaml")).forEach((p: Path) => {
      val testHolder = new File(p.toString)
      val src = new FileInputStream(testHolder)
      val yaml = new Yaml()
      val yamlObj = yaml.load(src).asInstanceOf[java.util.Map[String, Any]]
      res.addOne(new YamlItem(p,yamlObj))
    })
    res
  }

  @Test def trivialTest():Unit = {
    useCageRunner("trivial",simpleTest = false)
  }

  @Test def simplestClassTest():Unit = {
    useCageRunner("simplestClass",simpleTest = false)
  }

  @Test def myListTest():Unit = {
    useCageRunner("myList",simpleTest = false)
  }

  @Test def xTest():Unit = {
    useCageRunner("x",simpleTest = false)
  }

  @Test def whileCheckTest():Unit = {
    useCageRunner("whileCheck",simpleTest = true)
  }

  @Test def ifCheck():Unit = {
    useCageRunner("ifCheck",simpleTest = true)
  }

  @Test def assignCheck():Unit = {
    useCageRunner("assignCheck",simpleTest = true)
  }

  def useCageRunner(fName:String,simpleTest:Boolean):Unit = {
    val testData = getTestEntry(parsePython(),fName,simpleTest)
    useCageHolder(testData._1, testData._2)
  }
}
