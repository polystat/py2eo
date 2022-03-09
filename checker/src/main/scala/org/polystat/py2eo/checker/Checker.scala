package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.Checker.CompilingResult.{CompilingResult, compiled, failed, invalid, passed, transpiled}
import org.polystat.py2eo.checker.Mutate.Mutation
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation
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

  private val html = File(mutationsPath / "index.html")
  private val head = File(resourcesPath / "html" / "head.html").slurp

  def main(args: Array[String]): Unit = {

    mutationsPath.createDirectory()

    val path = if (args.isEmpty) resourcesPath / "simple-tests" else Path(args(0))
    val res = check(path, Mutation.values)

    html.writeAll(generateHTML(res))
  }

  object CompilingResult extends Enumeration {
    type CompilingResult = Value
    val invalid, failed, transpiled, compiled, passed, timeout = Value

    override def toString(): String = this match {
      case `invalid` => "n/a"
      case `failed` => "failed"
      case `transpiled` => "transpiles"
      case `compiled` => "compiles"
      case `passed` => "passes"
      case `timeout` => "timeout"
    }
  }

  case class TestResult(name: String, results: Map[Mutation, CompilingResult])

  private def check(path: Path, mutations: Iterable[Mutation]): List[TestResult] = {
    if (path.isDirectory) {
      path.toDirectory.list.toList.flatMap(item => check(item, mutations))
    } else if (path.extension == "yaml") {
      List(check(path.toFile, mutations))
    } else {
      List.empty
    }
  }

  private def check(test: File, mutations: Iterable[Mutation]): TestResult = {
    val db = debugPrinter(test.jfile)(_, _)
    val EOText = Transpile.transpile(db)(test.stripExtension, parseYaml(test))
    writeFile(test.stripExtension, EOText)

    val resultList = for {mutation <- mutations} yield (mutation, check(test, mutation))

    TestResult(test.stripExtension, resultList.toMap[Mutation, CompilingResult])
  }

  private def check(test: File, mutation: Mutation): CompilingResult = {
    val originalPyText = parseYaml(test)
    val mutatedPyText = Mutate(originalPyText, mutation, 1)

    if (mutatedPyText equals originalPyText) {
      invalid
    } else try {
      val db = debugPrinter(test.jfile)(_, _)
      val mutatedEOText = Transpile.transpile(db)(test.stripExtension, mutatedPyText)
      val resultFileName = writeFile(s"${test.stripExtension}-$mutation", mutatedEOText)

      val diffPath = mutationsPath / diffName(test, mutation)
      // Process(s"diff ${test.changeExtension("eo").name} $resultFileName", mutationsPath.jfile).!

      // Java zone: had troubles with redirecting scala.sys.Process output to file
      // TODO: migrate to scala.sys.Process
      val processBuilder = new ProcessBuilder("diff", test.changeExtension("eo").name, resultFileName)
      processBuilder.directory(mutationsPath.jfile)
      processBuilder.redirectOutput(diffPath.jfile)
      processBuilder.start()

      // TODO: actually need to get rid of old eo-files in that directory
      if (!compile(resultFileName)) transpiled else run(resultFileName)
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

    if (ret) passed else compiled
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

  private def diffName(test: Path, mutation: Mutation): String = s"${test.stripExtension}-$mutation-diff.txt"

  private def generateHTML(testResults: List[TestResult]): String = {
    val mutations = testResults.head.results.keys.toList

    val header = (for {mutation <- mutations} yield s"<th class=\"sorter data\">$mutation</th>\n")
      .mkString("<tr>\n<th class=\"sorter\">Test</th>\n", "", "</tr>\n")

    val body = for {test <- testResults} yield {
      val name = test.name
      val row = for {mutation <- mutations} yield {
        val link = diffName(name, mutation)
        val stage = test.results.getOrElse(mutation, failed)

        if (stage == invalid || stage == failed) {
          s"<td class=\"data\">$stage</td>\n"
        } else {
          s"<td class=\"data\"><a href=\"$link\">$stage</a></td>\n"
        }
      }

      s"<tr>\n<th class=\"left\">$name</th>\n${row.mkString}</tr>\n"
    }

    val table = s"<table id=programs>\n$header${body.mkString}</table>\n"

    s"<html lang=\"en-US\">\n$head<body>\n$table</body>\n</html>\n"
  }

}
