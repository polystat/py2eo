package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation
import org.polystat.py2eo.transpiler.Transpile
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.concurrent.TimeUnit
import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path}
import scala.sys.error
import scala.sys.process.Process

object Check {

  // TODO: get these files as resources
  private val resourcesPath = Directory.Current.get / "checker/src/test/resources/org/polystat/py2eo/checker"
  private val runEOPath = resourcesPath / "runEO"

  /**
   * Apply analysis to every yaml test in the input directory
   *
   * @param inputPath  Path to tests directory
   * @param outputPath Path to output html representation
   * @param mutations  Mutations for applying in analysis
   */
  def apply(inputPath: Path, outputPath: Path, mutations: Iterable[Mutation]): Unit = {
    outputPath.createDirectory()

    val res = check(inputPath, outputPath, mutations)
    if (res isEmpty) {
      error("Provided tests directory doesn't contain .yaml files")
    } else {
      Write(outputPath, res, mutations)
    }
  }

  def diffName(test: Path, mutation: Mutation): String = s"${test.stripExtension}-$mutation-diff.txt"

  def expected(mutation: Mutation): CompilingResult = mutation match {
    case Mutation.nameMutation => CompilingResult.transpiled
    case Mutation.literalMutation => CompilingResult.compiled
    case Mutation.operatorMutation => CompilingResult.transpiled
    case Mutation.reverseBoolMutation => CompilingResult.transpiled
    case Mutation.breakToContinue => CompilingResult.transpiled
    case Mutation.breakSyntax => CompilingResult.failed
    case Mutation.literalToIdentifier => CompilingResult.transpiled
    case Mutation.removeBrackets => CompilingResult.transpiled
    case Mutation.addExcessParam => CompilingResult.transpiled
    case Mutation.swapParam => CompilingResult.transpiled
  }

  private def check(inputPath: Path, outputPath: Path, mutations: Iterable[Mutation]): Iterator[TestResult] = {
    if (inputPath isDirectory) {
      inputPath.toDirectory.list flatMap (item => check(item, outputPath, mutations))
    } else if (inputPath.extension == "yaml") {
      Iterator(check(inputPath toFile, outputPath, mutations))
    } else {
      Iterator empty
    }
  }

  private def check(test: File, outputPath: Path, mutations: Iterable[Mutation]): TestResult = {
    val EOText = Transpile(test.stripExtension, parseYaml(test)).get // TODO: check for value
    File(outputPath / test.changeExtension("eo").name).writeAll(EOText)

    val resultList = for {mutation <- mutations} yield (mutation, check(test, outputPath, mutation))

    TestResult(test.stripExtension, resultList.toMap[Mutation, CompilingResult])
  }

  private def check(test: File, outputPath: Path, mutation: Mutation): CompilingResult = {
    val original = parseYaml(test)
    val mutant = Mutate(original, mutation, 1)

    if (mutant equals original) {
      CompilingResult.invalid
    } else {
      Transpile(test.stripExtension, mutant) match {
        case Some(transpiled) =>
          val originalFile = File(outputPath / test.changeExtension("eo").name)
          val mutatedFile = File(outputPath / s"${test.stripExtension}-$mutation")
          val diffFile = File(outputPath / diffName(test, mutation))
          mutatedFile.writeAll(transpiled)

          val diff = Process(s"diff $originalFile $mutatedFile", outputPath.jfile).lazyLines_!
          diffFile.writeAll(diff.mkString("\n"))

          // TODO: actually need to get rid of old eo-files in that directory
          if (!compile(mutatedFile)) {
            CompilingResult.transpiled
          } else {
            run(mutatedFile)
          }

        case None => CompilingResult.failed
      }
    }
  }

  private def compile(file: File): Boolean = {
    val result = Files.copy(file.jfile.toPath, File(runEOPath / file.name).jfile.toPath, REPLACE_EXISTING)
    val ret = Process("mvn clean test", runEOPath.jfile).! == 0

    Files delete result

    ret
  }

  private def run(file: File): CompilingResult = {
    val test = Files.copy(file.jfile.toPath, File(runEOPath / "Test.eo").jfile.toPath, REPLACE_EXISTING)

    // Dunno why but it doesn't work without this line
    val dir = new java.io.File(runEOPath.jfile.getPath)

    val process = new ProcessBuilder("mvn clean test").directory(dir).start
    val ret = process.waitFor(40, TimeUnit.SECONDS)

    Files delete test

    if (ret) {
      if (process.exitValue == 0) {
        CompilingResult.passed
      } else {
        CompilingResult.compiled
      }
    } else {
      CompilingResult.timeout
    }
  }

  private def parseYaml(file: File): String = {
    val input = file slurp
    val map = new Yaml load[java.util.Map[String, String]] input

    map get "python"
  }
}
