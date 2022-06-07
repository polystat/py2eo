package org.polystat.py2eo.transpiler

import org.junit.Assert.{assertTrue, fail}
import org.polystat.py2eo.transpiler.Main.writeFile
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException

import java.io.{File, FileInputStream}
import java.nio.file.{Files, Path}
import java.{lang => jl, util => ju}
import scala.sys.process.{Process, ProcessLogger}

trait Commons {
  val testsPrefix: String = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/transpiler"

  def yaml2python(f: File): String = {
    val map = new Yaml().load[java.util.Map[String, String]](new FileInputStream(f))
    map.get("python")
  }

  case class YamlTest(python : String, enabled : Boolean)

  def yaml2pythonModel(f : File): YamlTest = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))
    YamlTest(map.get("python"), map.containsKey("enabled") && map.getOrDefault("enabled", "false").asInstanceOf[Boolean])
  }

  def python: String = {
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    assertTrue(0 == (Process("python --version") ! ProcessLogger(stdout.append(_), stderr.append(_))))
    val pattern = "Python (\\d+)".r
    val Some(match1) = pattern.findFirstMatchIn(if (stderr.toString() == "") stdout.toString() else stderr.toString())
    if (match1.group(1) == "2") "python3" else "python"
  }


  def chopExtension(fileName: String): String = fileName.substring(0, fileName.lastIndexOf("."))

  def useCageHolder(test: File): Unit = {
    Transpile.transpileOption(Main.debugPrinter(test))(
      test.getName.replace(".yaml", ""),
      Transpile.Parameters(wrapInAFunction = false),
      yaml2python(test)
    ) match {
      case None => fail(s"could not transpile ${test.getName}");
      case Some(transpiled) => writeFile(test, "genCageEO", ".eo", transpiled)
    }
  }

  def collect(dir: String, filterEnabled: Boolean = false): ju.Collection[Array[jl.String]] = {
    val testsPrefix = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/transpiler"

    val res = collection.mutable.ArrayBuffer[String]()
    val simpleTestsFolder = new File(testsPrefix + File.separator + dir + File.separator)
    Files.walk(simpleTestsFolder.toPath).filter((p: Path) => p.toString.endsWith(".yaml")).forEach((p: Path) => {
      val testHolder = new File(p.toString)

      try {
        val map = new Yaml().load[java.util.Map[String, String]](new FileInputStream(testHolder))
        if (filterEnabled) {
          if (map.containsKey("enabled") && map.getOrDefault("enabled", "false").asInstanceOf[Boolean]) {
            res.addOne(p.toString)
          } else {
            println(s"The test ${testHolder.getName} is disabled")
          }
        } else {
          res.addOne(p.toString)
        }
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
