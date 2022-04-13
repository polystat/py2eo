package org.polystat.py2eo.transpiler

import java.io.{File, FileInputStream}
import java.nio.file.{Files, Path}
import java.{lang => jl, util => ju}

import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.polystat.py2eo.transpiler.Main.writeFile
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException


@RunWith(value = classOf[Parameterized])
class TestsSimple(path: jl.String) {
  val testsPrefix: String = getClass.getResource("").getFile

  case class YamlTest(python: String, enabled: Boolean)

  def yaml2python(f: File): YamlTest = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))

    YamlTest(map.get("python"), map.containsKey("enabled") && map.getOrDefault("enabled", "false").asInstanceOf[Boolean])
  }

  def useCageHolder(test: File): Unit = {
    val yamlObj = yaml2python(test)

    if (yamlObj.enabled) {
      val res = Transpile(test.getName.replace(".yaml", ""),
        yamlObj.python)

      res match {
        case None => fail(s"could not transpile ${test.getName}");
        case Some(transpiled) =>
          writeFile(
            test, "genCageEO", ".eo", transpiled
          )
      }
    }
  }

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
        println(testHolder.getPath)
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