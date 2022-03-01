package org.polystat.py2eo.checker

import org.polystat.py2eo.transpiler.Main.debugPrinter
import org.polystat.py2eo.transpiler.Transpile
import org.yaml.snakeyaml.Yaml

import java.io.{File, FileInputStream, FileWriter}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}
import scala.sys.process.Process

object Checker {

  private val resourcesPrefix = System.getProperty("user.dir") + "/checker/src/test/resources/org/polystat/py2eo/checker/"
  private val runEOPath = resourcesPrefix + "runEO/"

  case class TestResult(name: String, transpiles: Boolean, compiles: Boolean, runs: Boolean)

  def main(args: Array[String]): Unit = {
    val nameMutation = Mutator.Mutation.nameMutation
    val literalMutation = Mutator.Mutation.literalMutation

    val arr = check(resourcesPrefix + "simple-tests/assign", nameMutation) ++
      //check(resourcesPrefix + "simple-tests/while", nameMutation) ++
      check(resourcesPrefix + "simple-tests/if", nameMutation)

    println("Run results for first name mutation:")
    println("+---------------+------------+----------+-------+")
    println("|   test name   | transpiles | compiles | runs  |")
    println("+---------------+------------+----------+-------+")
    for (TestResult(name, transpiles, compiles, runs) <- arr) {
      // TODO: calculate longest test name and format table for it
      print(s"| ${name + " ".repeat(13 - name.length)} |")
      print(s" ${if (transpiles) "true " else "false"}      |")
      print(s" ${if (compiles) "true " else "false"}    |")
      println(s" ${if (runs) "true " else "false"} |")
    }

    println("+---------------+------------+----------+-------+")

  }

  private def check(path: String, mutation: Mutator.Mutation.Value): Array[TestResult] = {
    val file = new File(path)
    if (file.isDirectory) {
      for {dirItem <- file.listFiles() if dirItem.getName.endsWith(".yaml")} yield check(dirItem, mutation)
    } else {
      Array(check(file, mutation))
    }
  }

  private def check(test: File, mutation: Mutator.Mutation.Value): TestResult = {
    val (moduleName, python) = yaml2python(test)
    val db = debugPrinter(test)(_, _)
    val mutatedPyText = Mutator.mutate(python, mutation, 1)

    // Catching exceptions from parser and mapper
    try {
      // No need for compiling original texts for now
      // val originalEOText = Transpile.transpile(db)(moduleName, python)
      // val fstName = writeFile("before", "mutations", ".eo", originalEOText)

      val mutatedEOText = Transpile.transpile(db)(cropExtension(moduleName), mutatedPyText)
      val resultFileName = writeFile("after", "mutations", ".eo", mutatedEOText)

      if (!compileEO(resultFileName)) {
        TestResult(moduleName, transpiles = true, compiles = false, runs = false)
      } else {
        TestResult(moduleName, transpiles = true, compiles = true, runs = runEO(resultFileName))
      }
    }
    catch {
      case _: Exception =>
        TestResult(moduleName, transpiles = false, compiles = false, runs = false)
    }

  }

  private def writeFile(name: String, dirSuffix: String, fileSuffix: String, what: String): String = {
    val outPath = resourcesPrefix + "/" + dirSuffix
    val d = new File(outPath)
    if (!d.exists()) d.mkdir()
    val outName = outPath + "/" + name + fileSuffix
    val output = new FileWriter(outName)
    output.write(what)
    output.close()

    name + fileSuffix
  }

  private def compileEO(filename: String): Boolean = {
    val originalPath = resourcesPrefix + "mutations/" + filename

    val result = Files.copy(Paths.get(originalPath), Paths.get(runEOPath + filename), REPLACE_EXISTING)
    val ret = Process("mvn clean test", new File(runEOPath)).! == 0

    Files.delete(result)

    ret
  }

  private def runEO(filename: String): Boolean = {
    val originalPath = resourcesPrefix + "mutations/" + filename

    val result = Files.copy(Paths.get(originalPath), Paths.get(runEOPath + "Test.eo"), REPLACE_EXISTING)
    val ret = Process("mvn clean test", new File(runEOPath)).! == 0

    Files.delete(result)

    ret
  }

  private def yaml2python(f: File): (String, String) = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))
    (f.getName, map.get("python"))
  }

  private def cropExtension(s: String): String = {
    s.substring(0, s.lastIndexOf("."))
  }

}
