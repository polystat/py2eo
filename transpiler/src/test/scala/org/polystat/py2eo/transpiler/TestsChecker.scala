package org.polystat.py2eo.transpiler

import jdk.internal.vm.vector.VectorSupport.test
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.polystat.py2eo.transpiler.Main.{debugPrinter, writeEOFile}
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException

import java.io.{File, FileInputStream}
import java.nio.file.{Files, Path}
import java.util.concurrent.TimeUnit
import java.{lang => jl, util => ju}
import scala.reflect.io.Directory


@RunWith(value = classOf[Parameterized])
class TestsChecker (path: jl.String) {
  val testsPrefix: String = getClass.getResource("").getFile
  private val resourcesPath = Directory.Current.get / "checker/src/test/resources/org/polystat/py2eo/checker"
  private val runEOPath = resourcesPath / "runEO"

  case class YamlTest(python: String, disabled: Boolean)

  def yaml2python(f: File): YamlTest = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))

    YamlTest(map.get("python"), map.containsKey("disabled") && map.getOrDefault("disabled", "false").asInstanceOf[Boolean])
  }

  def useCageHolder(test: File): Unit = {
    def db = debugPrinter(test)(_, _)

    val z = yaml2python(test)

    if (!z.disabled) {
      val res = Transpile(test.getName.replace(".yaml", ""),
        z.python)

      res match {
        case None => fail(s"could not transpile ${test.getName}");
        case Some(transpiled) =>
          val path = writeEOFile(
            test, "genCageEO", ".eo", transpiled
          )

          if(!run(path)){
            fail(s"could not run EO ${test.getName}")
          }
      }
    }
  }


  private def run(file: File):Boolean = {
    val dir = new java.io.File(runEOPath.jfile.getPath)
    val process = new ProcessBuilder(s"mvn clean test -DpathToEo=\"$file\"").directory(dir).start
    val ret = process.waitFor(40, TimeUnit.SECONDS)

    Files delete test

    if (ret) {
      process.exitValue == 0
    } else {
      false
    }
  }

  @Test def testDef(): Unit = {
    useCageHolder(new File(path))
  }
}

object TestsChecker {
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
        case e: YAMLException => println(s"Couldn't parse ${testHolder.getName} file with error ${e.getMessage}")
        case e: ClassCastException => println(s"Couldn't parse ${testHolder.getName} file with error ${e.getMessage}")
      }
    })


    val list = new ju.ArrayList[Array[jl.String]]()
    res.foreach(n => list.add(Array(n)))
    list
  }
}