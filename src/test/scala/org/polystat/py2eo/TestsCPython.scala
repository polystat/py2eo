package org.polystat.py2eo

import java.io.{File, FileInputStream}
import java.nio.file.{Files, Path}
import java.{lang => jl, util => ju}

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.polystat.py2eo.Main.{debugPrinter, readFile, writeFile}
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.sys.process.{Process, _}

@RunWith(value = classOf[Parameterized])
class TestsCPython(path: jl.String){
  val testsPrefix: String = getClass.getResource("").getFile

  private val python = {
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    assertTrue(0 == (s"python --version" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    val pattern = "Python (\\d+)".r
    val Some(match1) = pattern.findFirstMatchIn(if (stderr.toString() == "") stdout.toString() else stderr.toString())
    if (match1.group(1) == "2") "python3" else "python"
  }

  @Test def testDef(): Unit = {
    val dirName = testsPrefix + "testParserPrinter"
    val dir = new File(dirName)
    assert(dir.isDirectory)

    val afterParser = new File(dirName + "/afterParser")
    if (!afterParser.exists()) afterParser.mkdir()
    val cpython = new File(afterParser.getPath + "/cpython")
    if (!cpython.exists()) {
      assert(0 == Process("git clone https://github.com/python/cpython", afterParser).!)
      assert(0 == Process("git checkout v3.8.10", cpython).!)
    }

    println("Version of python is:")
    s"$python --version" !

    Future{
      val test = new File(path)
      if (!test.isDirectory && test.getName.startsWith("test_") && test.getName.endsWith(".yaml")) {
        def db = debugPrinter(test)(_, _)
        val name = test.getName
        val fName = name.substring(0, name.lastIndexOf("."))
        var eoText = ""
        try {
          val yaml = new Yaml()
          val src = new FileInputStream(test)
          val yamlObj = yaml.load(src).asInstanceOf[java.util.Map[String, Any]]
          val str = yamlObj.get("python").asInstanceOf[String]
          println(s"parsing $name")


          eoText = Transpile.transpile(db)(fName, str)

        } catch {
          case e: YAMLException =>
            val pytest = new File(test.getParent + "/" + fName)
            eoText = Transpile.transpile(db)(fName, readFile(pytest))
            println(s"Couldn't parse ${test.getName} file with error ${e.getMessage} the outPut will be at ${pytest.getName}")
          case e: NullPointerException =>
            val pytest = new File(test.getParent + "/" + fName)
            eoText = Transpile.transpile(db)(fName, readFile(pytest))
            println(s"Error with file ${test.getName} with error ${e.getMessage} the outPut will be at ${pytest.getName}")
          case e: Exception => println(s"Some unknown error ${e.getLocalizedMessage}")
        }

        writeFile(test, "genUnsupportedEOPrim", ".eo", eoText)
      }
    }

    assume(System.getProperty("os.name") == "Linux")
    assert(0 == Process("./configure", cpython).!)
    val nprocessors = Runtime.getRuntime.availableProcessors()
    println(s"have $nprocessors processors")
    assert(0 == Process(s"make -j ${nprocessors + 2}", cpython).!)
    assertTrue(0 == Process("make test", cpython).!)

    println("working with file" + path)
  }
}

object TestsCPython {
  @Parameters def parameters: ju.Collection[Array[jl.String]] = {
    val resFolder = getClass.getResource("").getFile


    val res = collection.mutable.ArrayBuffer[String]()
    var testProcessed = 0
    Files.walk(new File(resFolder).toPath).filter((p: Path) => p.toString.endsWith(".yaml") && p.toString.contains("testParserPrinter")).forEach((p: Path) => {
      val testHolder = new File(p.toString)
      try {
        println(testHolder.getPath)
        res.addOne(p.toString)
        testProcessed += 1
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
