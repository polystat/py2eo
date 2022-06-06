package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation
import org.yaml.snakeyaml.Yaml

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path, Streamable}
import scala.sys.error
import scala.sys.process.Process

object Check {

  private val tempDir = Directory.makeTemp()

  /**
   * Apply analysis to every yaml test in the input directory
   *
   * @param inputPath  Path to tests directory
   * @param outputPath Path to output html representation
   * @param mutations  Mutations for applying in analysis
   */
  def apply(inputPath: Path, outputPath: Path, mutations: Iterable[Mutation]): Unit = {
    outputPath.createDirectory()

    val stream = getClass getResourceAsStream "compiler.py"
    val compiler = Streamable slurp stream
    (tempDir / "compiler.py").createFile().writeAll(compiler)

    val testResults = check(inputPath, outputPath, mutations)
    if (testResults isEmpty) {
      error("Provided tests directory doesn't contain .yaml files")
    } else {
      val awaitedTestResults = testResults map (testResult => testResult.await)
      Write(outputPath, awaitedTestResults, mutations)
      WriteConstructions(outputPath, awaitedTestResults)
    }
  }

  def diffName(name: String, mutation: Mutation): String = s"$name-$mutation-diff.txt"

  private def check(inputPath: Path, outputPath: Path, mutations: Iterable[Mutation]): List[TestResult] = {
    if (inputPath isDirectory) {
      inputPath.toDirectory.list.toList flatMap (item => check(item, outputPath, mutations))
    } else if (inputPath hasExtension "yaml") {
      List(check(inputPath.toFile, outputPath, mutations))
    } else {
      List empty
    }
  }

  private def check(test: File, outputPath: Path, mutations: Iterable[Mutation]): TestResult = {
    val module = test.stripExtension
    val category = test.parent.name
    println(s"checking $module")
    parseYaml(test) match {
      case None => TestResult(module, category, None)
      case Some(parsed) =>
        transpile(module, parsed) match {
          case None => TestResult(module, category, None)
          case Some(transpiled) =>
            val file = File(outputPath / test.changeExtension("eo").name)
            file writeAll transpiled

            val resultList = mutations map (mutation => (mutation, check(module, parsed, outputPath, mutation)))
            TestResult(module, category, Some(resultList.toMap))
        }
    }
  }

  private def check(module: String, originalPyText: String, outputPath: Path, mutation: Mutation): CompilingResult = {
    println(s"checking $module with $mutation")
    val mutatedPyText = Mutate(originalPyText, mutation, 1)

    if (mutatedPyText equals originalPyText) {
      CompilingResult.invalid
    } else {
      val originalPyFile = File(outputPath / s"$module.py")
      originalPyFile writeAll originalPyText

      val mutatedPyFile = File(outputPath / s"$module-$mutation.py")
      mutatedPyFile writeAll mutatedPyText

      val diffPyOutput = Process(s"diff $originalPyFile $mutatedPyFile", outputPath.jfile).lazyLines_!
      val diffFile = File(outputPath / diffName(module, mutation))
      diffFile writeAll "Diff between original (left) and mutated (right) python files\n"
      diffFile appendAll diffPyOutput.mkString("\n")

      transpile(module, mutatedPyText) match {
        case None =>
          diffFile appendAll "\n\nFailed to transpile mutated py file\n"
          CompilingResult.failed

        case Some(mutatedEoText) =>
          val mutatedEoFile = File(outputPath / s"$module-$mutation.eo")
          mutatedEoFile writeAll mutatedEoText

          val originalEoFile = File(outputPath / s"$module.eo")
          val diffEoOutput = Process(s"diff $originalEoFile $mutatedEoFile", outputPath.jfile).lazyLines_!

          if (diffEoOutput isEmpty) {
            diffFile appendAll "\n\nNo diff between original and mutated eo files\n"
            CompilingResult.nodiff
          } else {
            diffFile appendAll "\n\nDiff between original (left) and mutated (right) eo files\n"
            diffFile appendAll diffEoOutput.mkString("\n")

            CompilingResult.passed
          }
      }
    }
  }

  private def parseYaml(file: File): Option[String] = {
    val input = file slurp
    val map = new Yaml load[java.util.Map[String, String]] input

    Some(map get "python")
  }

  private def transpile(module: String, input: String): Option[String] = {
    try {
      val file = (tempDir / "test.py").createFile()
      val output = File(tempDir / "output.pyc")
      file.writeAll(input)
      val ret = Process("python3 compiler.py", tempDir.jfile).!

      val result = if (ret == 0) Some(output.bytes().mkString) else None

      file.delete
      if (ret == 0) output.delete

      result
    } catch {
      case _: Exception => None
    }
  }
}
