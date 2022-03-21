package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.Checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation
import org.polystat.py2eo.transpiler.Transpile
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import scala.reflect.io.{Directory, File, Path}
import scala.sys.process.Process

object Checker {

  private val usage = "the first argument should be the path to tests, the second argument should be the output path"
  private val nonValidInputError = "provided input path is not valid"
  private val emptyInputError = "provided tests directory doesn't contain .yaml files"

  // TODO: get these files as resources
  private val resourcesPath = Directory.Current.get / "checker/src/test/resources/org/polystat/py2eo/checker"
  private val runEOPath = resourcesPath / "runEO"
  private val head = File(resourcesPath / "html" / "head.html").slurp

  def main(args: Array[String]): Unit = {
    if (args.length == 2) {
      val inputPath = Path(args(0)).toAbsolute
      val outputPath = Path(args(1)).toAbsolute

      if (inputPath.exists) {
        apply(inputPath, outputPath, Mutation.values)
      } else {
        error(nonValidInputError)
      }
    } else {
      println(s"Usage: $usage")
    }
  }

  def apply(inputPath: Path, outputPath: Path, mutations: Iterable[Mutation]): Unit = {
    outputPath.createDirectory()

    val res = check(inputPath, outputPath, mutations)
    if (res.isEmpty) {
      error(emptyInputError)
    } else {
      val indexHTML = (outputPath / "index.html").createFile()
      indexHTML.writeAll(generateHTML(res))
    }
  }

  private def error(msg: String): Unit = {
    println(s"Error: $msg")
    sys.exit(1)
  }

  object CompilingResult extends Enumeration {
    type CompilingResult = Value
    val invalid: Value = Value("n/a")
    val failed: Value = Value("failed")
    val transpiled: Value = Value("transpiled")
    val compiled: Value = Value("compiled")
    val passed: Value = Value("passed")
    val timeout: Value = Value("timeout")
  }

  private case class TestResult(name: String, results: Map[Mutation, CompilingResult])

  private def check(inputPath: Path, outputPath: Path, mutations: Iterable[Mutation]): List[TestResult] = {
    if (inputPath.isDirectory) {
      inputPath.toDirectory.list.toList.flatMap(item => check(item, outputPath, mutations))
    } else if (inputPath.extension == "yaml") {
      List(check(inputPath.toFile, outputPath, mutations))
    } else {
      List.empty
    }
  }

  private def check(test: File, outputPath: Path, mutations: Iterable[Mutation]): TestResult = {
    val EOText = Transpile(test.stripExtension, parseYaml(test))
    File(outputPath / test.changeExtension("eo").name).writeAll(EOText)

    val resultList = for {mutation <- mutations} yield (mutation, check(test, outputPath, mutation))

    TestResult(test.stripExtension, resultList.toMap[Mutation, CompilingResult])
  }

  private def check(test: File, outputPath: Path, mutation: Mutation): CompilingResult = {
    val original = parseYaml(test)
    val mutant = Mutate(original, mutation, 1)

    if (mutant equals original) {
      CompilingResult.invalid
    } else try {
      val originalFile = File(outputPath / test.changeExtension("eo").name)
      val mutatedFile = File(outputPath / s"${test.stripExtension}-$mutation")
      val diffFile = File(outputPath / diffName(test, mutation))

      val mutatedEOText = Transpile(test.stripExtension, mutant)
      mutatedFile.writeAll(mutatedEOText)

      val diff = Process(s"diff $originalFile $mutatedFile", outputPath.jfile).lazyLines_!
      diffFile.writeAll(diff.mkString("\n"))

      // TODO: actually need to get rid of old eo-files in that directory
      if (!compile(mutatedFile)) CompilingResult.transpiled else run(mutatedFile)
    } catch {
      case _: Exception => CompilingResult.failed
    }
  }

  private def compile(file: File): Boolean = {
    val result = Files.copy(file.jfile.toPath, File(runEOPath / file.name).jfile.toPath, REPLACE_EXISTING)
    val ret = Process("mvn clean test", runEOPath.jfile).! == 0

    Files.delete(result)

    ret
  }

  private def run(file: File): CompilingResult = {
    val result = Files.copy(file.jfile.toPath, File(runEOPath / "Test.eo").jfile.toPath, REPLACE_EXISTING)
    val ret = Process("mvn clean test", runEOPath.jfile).! == 0

    Files.delete(result)

    if (ret) CompilingResult.passed else CompilingResult.compiled
  }

  private def parseYaml(file: File): String = {
    new Yaml().load[java.util.Map[String, String]](file.slurp).get("python")
  }

  private def diffName(test: Path, mutation: Mutation): String = s"${test.stripExtension}-$mutation-diff.txt"

  private def expected(mutation: Mutation): CompilingResult = mutation match {
    case Mutation.nameMutation => CompilingResult.transpiled
    case Mutation.literalMutation => CompilingResult.compiled
    case Mutation.operatorMutation => CompilingResult.transpiled
    case Mutation.reverseBoolMutation => CompilingResult.transpiled
    case Mutation.breakToContinue => CompilingResult.transpiled
    case Mutation.breakSyntax => CompilingResult.failed
    case Mutation.literalToIdentifier => CompilingResult.transpiled
    case Mutation.removeBrackets => CompilingResult.transpiled
  }

  private def generateHTML(testResults: List[TestResult]): String = {
    val mutations = testResults.head.results.keys.toList

    val header = (for {mutation <- mutations} yield s"<th class=\"sorter data\">$mutation</th>\n")
      .mkString("<tr>\n<th class=\"sorter\">Test</th>\n", "", "</tr>\n")

    val body = for {test <- testResults} yield {
      val name = test.name
      val row = for {mutation <- mutations} yield {
        val link = diffName(name, mutation)
        val stage = test.results(mutation)

        val kind = if (stage == CompilingResult.invalid) {
          "data"
        } else if (stage == expected(mutation)) {
          "expected data"
        } else {
          "unexpected data"
        }

        val data = if (stage == CompilingResult.invalid || stage == CompilingResult.failed) {
          stage.toString
        } else {
          s"<a href=\"$link\">$stage</a>"
        }

        s"<td class=\"${kind}\">$data</td>\n"
      }

      s"<tr>\n<th class=\"left\">$name</th>\n${row.mkString}</tr>\n"
    }

    val table = s"<table id=programs>\n$header${body.mkString}</table>\n"

    s"<html lang=\"en-US\">\n$head<body>\n$table</body>\n</html>\n"
  }

}
