package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation
import org.polystat.py2eo.parser.{Parse, PrintPython}
import org.polystat.py2eo.transpiler.Transpile
import org.yaml.snakeyaml.Yaml

import scala.language.postfixOps
import scala.reflect.io.{File, Path}
import scala.sys.error
import scala.sys.process.Process

object Check {

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
    println(s"checking $module")
    Transpile(module, parseYaml(test)) match {
      case None => TestResult(module, None)
      case Some(transpiled) =>
        val file = File(outputPath / test.changeExtension("eo").name)
        file writeAll transpiled

        val resultList = mutations map (mutation => (mutation, check(test, outputPath, mutation)))
        TestResult(module, Some(resultList.toMap[Mutation, CompilingResult]))
    }
  }

  private def check(test: File, outputPath: Path, mutation: Mutation): CompilingResult = {
    val module = test.stripExtension
    println(s"checking $module with $mutation")

    val originalPyText = parseYaml(test)
    val mutatedPyText = Mutate(originalPyText, mutation, 1)

    if (mutatedPyText equals originalPyText) {
      CompilingResult.invalid
    } else {
      val originalPyFile = File(outputPath / s"$module.py")
      originalPyFile writeAll PrintPython.print(Parse(originalPyText))

      val mutatedPyFile = File(outputPath / s"$module-$mutation.py")
      mutatedPyFile writeAll mutatedPyText

      val diffPyOutput = Process(s"diff $originalPyFile $mutatedPyFile", outputPath.jfile).lazyLines_!
      val diffFile = File(outputPath / diffName(module, mutation))
      diffFile writeAll "Diff between original (left) and mutated (right) python files\n"
      diffFile appendAll diffPyOutput.mkString("\n")

      Transpile(module, mutatedPyText) match {
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

  private def parseYaml(file: File): String = {
    val input = file slurp
    val map = new Yaml load[java.util.Map[String, String]] input

    Transpile.applyStyle(map get "python")
  }
}
