package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.Checker.CompilingResult.{CompilingResult, compiles, failed, passes, transpiles}
import org.polystat.py2eo.checker.Mutate.Mutation.{Mutation, literalMutation, nameMutation}
import org.polystat.py2eo.transpiler.Main.debugPrinter
import org.polystat.py2eo.transpiler.Transpile
import org.yaml.snakeyaml.Yaml

import java.io.{FileInputStream, FileWriter}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}
import scala.reflect.io.{File, Path}
import scala.sys.process.Process

object Checker {

  private val resourcesPath = Path(System.getProperty("user.dir") + "/checker/src/test/resources/org/polystat/py2eo/checker")
  private val mutationsPath = resourcesPath / "mutationTests"
  private val runEOPath = resourcesPath / "runEO"
  private val htmlPath = mutationsPath / "index.html"

  def main(args: Array[String]): Unit = {

    // Creating temp directory for mutation results
    mutationsPath.createDirectory()

    val mutationList = List(nameMutation, literalMutation)

    val arr = check(resourcesPath / "simple-tests", mutationList)
    generateHTML(htmlPath, mutationList, arr)

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

  case class TestResult(name: String, results: List[CompilingResult])

  private def check(path: Path, mutations: List[Mutation]): List[TestResult] = {
    if (path.isDirectory) {
      (for {file <- path.toDirectory.list.toList} yield check(file, mutations)).flatten
    } else if (path.extension == "yaml") {
      List(check(path.toFile, mutations))
    } else {
      List.empty
    }
  }

  private def check(test: File, mutations: List[Mutation]): TestResult = {
    TestResult(test.stripExtension, for {mutation <- mutations} yield check(test, mutation))
  }

  private def check(test: File, mutation: Mutation): CompilingResult = {
    val python = parseYaml(test)
    val db = debugPrinter(test.jfile)(_, _)
    val mutatedPyText = Mutate(python, mutation, 1)

    // Catching exceptions from parser and mapper
    try {
      val mutatedEOText = Transpile.transpile(db)(test.stripExtension, mutatedPyText)
      val resultFileName = writeFile("after" + mutation, mutatedEOText)
      if (!compile(resultFileName)) transpiles else run(resultFileName)
    } catch {
      case _: Exception => failed
    }
  }

  private def compile(filename: String): Boolean = {
    val originalPath = mutationsPath + File.separator + filename

    val result = Files.copy(Paths.get(originalPath), Paths.get(runEOPath + File.separator + filename), REPLACE_EXISTING)
    val ret = Process("mvn clean test", runEOPath.jfile).! == 0

    Files.delete(result)

    ret
  }

  private def run(filename: String): CompilingResult = {
    val originalPath = mutationsPath + File.separator + filename

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

  private def parseYaml(path: Path): String = {
    val yaml = new Yaml()
    yaml.load[java.util.Map[String, String]](new FileInputStream(path.jfile)).get("python")
  }

  private def generateHTML(path: Path, mutations: List[Mutation], table: List[TestResult]): Unit = {
    path.createFile()

    val output = new FileWriter(path.jfile)

    output.write("<table>\n  <tr>\n    <th>\n      Test name\n    </th>\n")
    output.write(mutations.mkString("    <th>\n      ", "\n    </th>\n    <th>\n      ", "\n    </th>\n  </tr>\n"))

    for (tableRow <- table) {
      output.write(s"  <tr>\n    <th>\n      ${tableRow.name}\n    </th>\n")
      output.write(tableRow.results.mkString("    <th>\n      ", "\n    </th>\n    <th>\n      ", "\n    </th>\n"))
      output.write("  </tr>\n")
    }

    output.write("</table>\n")

    output.close()
  }

}
