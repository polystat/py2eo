package org.polystat.py2eo.transpiler

import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.polystat.py2eo.transpiler.Main.writeEOFile
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException

import java.io.{File, FileInputStream}
import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import java.util.concurrent.TimeUnit
import java.{lang => jl, util => ju}
import scala.language.postfixOps
import scala.reflect.io.Directory


@RunWith(value = classOf[Parameterized])
class Counter(path: jl.String) {
  val testsPrefix: String = getClass.getResource("").getFile
  private val runEOPath = Directory.Current.get.jfile + "/runEO"

  case class YamlTest(python: String)

  def yaml2python(f: File): YamlTest = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))

    YamlTest(map.get("python"))
  }

  def useCageHolder(test: File): Unit = {
    val z = yaml2python(test)

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


  private def run(file: File):Boolean = {
    //val  result = new File(runEOPath + s"/${file.getName}")
    //val path = Path.of(runEOPath + s"/${file.getName}")
    val path = Path.of(s"$runEOPath/${file.getName}")

    val pathResult = Files.move(
      Paths.get(file.getPath),
      path,
      StandardCopyOption.REPLACE_EXISTING
    )



//    val test = Files.copy(file.toPath, path, REPLACE_EXISTING).toAbsolutePath
//    println(test)
    val dir = new java.io.File(runEOPath)
    //var pb = new ProcessBuilder("mvn", "clean", "test", s"-DpathToEo=\"$test\"")
    var pb = new ProcessBuilder("mvn", "clean", "test", s"-DpathToEo=\"$pathResult\"")
    pb = pb.directory(dir)
    pb.inheritIO()
    val process = pb.start
    val ret = process.waitFor(40, TimeUnit.SECONDS)


    Files.delete(pathResult)

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


object Counter {
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

