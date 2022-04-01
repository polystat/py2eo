package org.polystat.py2eo.transpiler

import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.polystat.py2eo.transpiler.Main.{debugPrinter, writeFile}
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException

import java.io.{File, FileInputStream}
import java.nio.file.{Files, Path}
import java.{lang => jl, util => ju}


@RunWith(value = classOf[Parameterized])
class TestsSimple(path: jl.String) {
  val testsPrefix: String = getClass.getResource("").getFile

  case class YamlTest(python : String, disabled : Boolean)

  def yaml2python(f : File): YamlTest = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))
    YamlTest(map.get("python"), map.containsKey("disabled"))
  }

  def useCageHolder(test : File): Unit = {
    def db = debugPrinter(test)(_, _)
    val z = yaml2python(test)

    println("use cage " + test.getName)

    if (!z.disabled) {
      try {
        writeFile(
          test, "genCageEO", ".eo", Transpile.transpile(db)(
            test.getName.replace(".yaml", ""),
            z.python
          )
        )
      }catch {
        case e : Throwable =>
          println(s"failed to transpile ${test.getName}: ${e.toString}")
          fail(e.getLocalizedMessage)
      }

      //    val runme = test.getParentFile.getPath + "/afterUseCage/" + chopExtension(test.getName) + ".py"
      //    assertTrue(0 == Process(python + " \"" + runme + "\"").!)
    }
  }

  @Test def testDef(): Unit = {
    useCageHolder(new File(path))
  }
}

object TestsSimple {
  @Parameters def parameters: ju.Collection[Array[jl.String]] = {
    val resFolder = getClass.getResource("").getFile

    val res = collection.mutable.ArrayBuffer[String]()
    val simpleTestsFolder = new File(resFolder + File.separator + "simple-tests")
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