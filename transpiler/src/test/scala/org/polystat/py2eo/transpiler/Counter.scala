package org.polystat.py2eo.transpiler

import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.polystat.py2eo.transpiler.Main.writeFile
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException

import java.io.{File, FileInputStream, FileWriter}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Path, Paths}
import java.{lang => jl, util => ju}
import scala.language.postfixOps
import scala.sys.process.Process


@RunWith(value = classOf[Parameterized])
class Counter(path: jl.String) {
  val testsPrefix: String = getClass.getResource("").getFile
  private val runEOPath = Paths.get(".").toAbsolutePath.getParent.getParent + "/runEO"

  def yaml2python(f: File): String = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))

    map.get("python")
  }

  def useCageHolder(test: File): Unit = {
    val yamlData = yaml2python(test)

    val res = Transpile(test.getName.replace(".yaml", ""), yamlData)

    res match {
      case None => fail(s"could not transpile ${test.getName}");
      case Some(transpiled) =>
        val path = writeFile(
          test, "genCageEO", ".eo", transpiled
        )

        if (!run(path)) {
          fail(s"could not run EO ${test.getName}")
        }
    }
  }


  private def run(file: File): Boolean = {
    val result = Files.copy(file.toPath, Path.of(s"$runEOPath/test.eo"), REPLACE_EXISTING)
    val ret = Process("mvn clean test", new File(runEOPath)).! == 0

    Files delete result

    ret
  }

  @Test def testDef(): Unit = {
    useCageHolder(new File(path))
  }
}


object Counter {
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

