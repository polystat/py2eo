package org.polystat.py2eo.checker

import org.polystat.py2eo.transpiler.Main.debugPrinter
import org.polystat.py2eo.transpiler.Transpile
import org.yaml.snakeyaml.Yaml
import org.polystat.py2eo.checker.Checker.CompilingResult.{CompilingResult, transpiles, compiles, failed, passes, timeout}
import org.polystat.py2eo.checker.Mutate.Mutation.{Mutation, nameMutation, literalMutation}

import java.io.{FileInputStream, FileWriter}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}
import scala.reflect.io.{Path, File}
import scala.sys.process.Process

object Checker {

  private val resourcesPath = Path(System.getProperty("user.dir") + "/checker/src/test/resources/org/polystat/py2eo/checker/")
  private val mutationsPath = resourcesPath/"mutationTests"
  private val runEOPath = resourcesPath/"runEO"

  def main(args: Array[String]): Unit = {

    // Creating temp directory for mutation results
    mutationsPath.createDirectory()

    val arr = check(resourcesPath/"simple-tests"/"assign", Iterator(nameMutation, literalMutation)) ++
      //check(resourcesPath + "simple-tests/while", Iterator(nameMutation)) ++
      check(resourcesPath/"simple-tests"/"if", Iterator(nameMutation, literalMutation)).buffered

    println(arr.toList)

  }

  object CompilingResult extends Enumeration {
    type CompilingResult = Value
    val failed, transpiles, compiles, passes, timeout = Value

    override def toString(): String = this match {
      case CompilingResult.failed => "failed"
      case CompilingResult.transpiles => "transpiles"
      case CompilingResult.compiles => "compiles"
      case CompilingResult.passes => "passes"
      case CompilingResult.timeout => "timeout"
    }
  }

  case class TestResult(name: String, results: Iterator[CompilingResult])

  private def check(path: Path, mutations: Iterator[Mutation]): Iterator[TestResult] = {
    if (path.isDirectory) {
      (for {file <- path.toDirectory.list} yield check(file, mutations)).flatten
    } else if (path.name.endsWith(".yaml")) { // TODO: use of File.extension?
      Iterator(check(path.toFile, mutations))
    } else {
      Iterator.empty
    }
  }

  private def check(test: File, mutations: Iterator[Mutation]): TestResult = {
    val (moduleName, python) = yaml2python(test)
    val db = debugPrinter(test.jfile)(_, _)

    // Need to transpile original file to get diffs
    val origName = writeFile(moduleName, Transpile.transpile(db)(moduleName, python))
    TestResult(moduleName, for {mutation <- mutations} yield check(test, mutation))
  }

  private def check(test: File, mutation: Mutation): CompilingResult = {
    val (moduleName, python) = yaml2python(test)
    val db = debugPrinter(test.jfile)(_, _)
    val mutatedPyText = Mutate(python, mutation, 1)

    // Catching exceptions from parser and mapper
    try {
      val mutatedEOText = Transpile.transpile(db)(moduleName, mutatedPyText)
      val resultFileName = writeFile("after" + mutation, mutatedEOText)
      if (!compile(resultFileName)) transpiles else run(resultFileName)
    }
    catch {
      case _: Exception => failed
    }
  }

  private def compile(filename: String): Boolean = {
    val originalPath = mutationsPath + filename

    val result = Files.copy(Paths.get(originalPath), Paths.get(runEOPath + File.separator + filename), REPLACE_EXISTING)
    val ret = Process("mvn clean test", runEOPath.jfile).! == 0

    Files.delete(result)

    ret
  }

  private def run(filename: String): CompilingResult = {
    val originalPath = mutationsPath + filename

    val result = Files.copy(Paths.get(originalPath), Paths.get(runEOPath + File.separator + "Test.eo"), REPLACE_EXISTING)
    val ret = Process("mvn clean test", runEOPath.jfile).! == 0

    Files.delete(result)

    if (ret) passes else compiles
  }

  private def writeFile(name: String, what: String): String = {
    val filename = name + ".eo"
    val output = new FileWriter(mutationsPath + File.separator + filename)
    output.write(what)
    output.close()

    filename
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
