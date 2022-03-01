package org.polystat.py2eo.checker

import org.polystat.py2eo.transpiler.Main.debugPrinter
import org.polystat.py2eo.transpiler.Transpile
import org.yaml.snakeyaml.Yaml
import org.polystat.py2eo.checker.Checker.TestResult.{TestResult, transpiles, compiles, failed, passes, timeout}
import org.polystat.py2eo.checker.Mutate.Mutation.{Mutation, nameMutation, literalMutation}

import java.io.{FileInputStream, FileWriter}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}
import scala.reflect.io.{Path, File}
import scala.sys.process.Process

object Checker {

  private val resourcesPath = System.getProperty("user.dir") + "/checker/src/test/resources/org/polystat/py2eo/checker/"
  private val runEOPath = Path(resourcesPath)/"runEO"

  def main(args: Array[String]): Unit = {

    val arr = check(resourcesPath + "simple-tests/assign", nameMutation) ++
      //check(resourcesPath + "simple-tests/while", nameMutation) ++
      check(resourcesPath + "simple-tests/if", nameMutation).buffered

    println(arr.toList)

  }

  object TestResult extends Enumeration {
    type TestResult = Value
    val failed, transpiles, compiles, passes, timeout = Value

    override def toString(): String = this match {
      case TestResult.failed => "failed"
      case TestResult.transpiles => "transpiles"
      case TestResult.compiles => "compiles"
      case TestResult.passes => "passes"
      case TestResult.timeout => "timeout"
    }
  }

  private def check(path: Path, mutation: Mutation): Iterator[TestResult] = {
    if (path.isDirectory) {
      (for {file <- path.toDirectory.list} yield check(file, mutation)).flatten
    } else if (path.name.endsWith(".yaml")) { // TODO: use of File.extension?
      Iterator(check(path.toFile, mutation))
    } else {
      Iterator.empty
    }
  }

  private def check(test: File, mutation: Mutation): TestResult = {
    val (moduleName, python) = yaml2python(test)
    val db = debugPrinter(test.jfile)(_, _)

   // val origName = writeFile(moduleName, Transpile.transpile(db)(moduleName, python))

    val mutatedPyText = Mutate(python, mutation, 1)

    // Catching exceptions from parser and mapper
    try {
      val mutatedEOText = Transpile.transpile(db)(moduleName, mutatedPyText)
      val resultFileName = writeFile("after" + mutation, mutatedEOText)
      if (!compileEO(resultFileName)) transpiles else runEO(resultFileName)
    }
    catch {
      case _: Exception => failed
    }

  }

  private def compileEO(filename: String): Boolean = {
    val originalPath = resourcesPath + "mutationTests/" + filename

    val result = Files.copy(Paths.get(originalPath), Paths.get(runEOPath + File.separator + filename), REPLACE_EXISTING)
    val ret = Process("mvn clean test", runEOPath.jfile).! == 0

    Files.delete(result)

    ret
  }

  private def runEO(filename: String): TestResult = {
    val originalPath = resourcesPath + "mutationTests/" + filename

    val result = Files.copy(Paths.get(originalPath), Paths.get(runEOPath + File.separator + "Test.eo"), REPLACE_EXISTING)
    val ret = Process("mvn clean test", runEOPath.jfile).! == 0

    Files.delete(result)

    if (ret) passes else compiles
  }

  private def writeFile(name: String, what: String): String = {
    val outPath = resourcesPath + "mutationTests"
    val d = new java.io.File(outPath)
    if (!d.exists()) d.mkdir()
    val outName = outPath + File.separator + name + ".eo"
    val output = new FileWriter(outName)
    output.write(what)
    output.close()

    name + ".eo"
  }

  private def yaml2python(f: File): (String, String) = {
    def cropExtension(s: String): String = {
      s.substring(0, s.lastIndexOf("."))
    }

    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f.jfile))
    (cropExtension(f.name), map.get("python"))
  }

}
