package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.CompilingResult.CompilingResult
import org.polystat.py2eo.checker.Mutate.Mutation.Mutation
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

  def diffName(test: Path, mutation: Mutation): String = s"${test.stripExtension}-$mutation-diff.txt"

  private def check(inputPath: Path, outputPath: Path, mutations: Iterable[Mutation]): Iterator[TestResult] = {
    if (inputPath isDirectory) {
      inputPath.toDirectory.list flatMap (item => check(item, outputPath, mutations))
    } else if (inputPath hasExtension "yaml") {
      Iterator(check(inputPath.toFile, outputPath, mutations))
    } else {
      Iterator empty
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
    val original = parseYaml(test)
    val mutant = Mutate(original, mutation, 1)
    println(s"checking ${test.stripExtension} with $mutation")

    if (mutant equals original) {
      CompilingResult.invalid
    } else {
      val module = test.stripExtension
      Transpile(module, mutant) match {
        case None => CompilingResult.failed
        case Some(transpiled) =>
          val mutant = File(outputPath / s"$module-$mutation").addExtension("eo")
          mutant writeAll transpiled

          val original = File(outputPath / test.changeExtension("eo").name)
          val diff = Process(s"diff $original $mutant", outputPath.jfile).lazyLines_!

          if (diff isEmpty) {
            CompilingResult.nodiff
          } else {
            val diffFile = File(outputPath / diffName(test, mutation))
            diffFile writeAll diff.mkString("\n")
            CompilingResult.passed
          }
      }
    }
  }

  private def parseYaml(file: File): String = {
    val input = file slurp
    val map = new Yaml load[java.util.Map[String, String]] input

    map get "python"
  }
}
