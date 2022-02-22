package org.polystat.py2eo.checker

import java.io.{File, FileInputStream, FileWriter}
import scala.sys.process.stringSeqToProcess
import org.polystat.py2eo.transpiler.Main.debugPrinter
import org.polystat.py2eo.transpiler.Transpile
import org.yaml.snakeyaml.Yaml

object Checker extends App {
  private val testsPrefix = System.getProperty("user.dir") + "/checker/src/test/resources/org/polystat/py2eo/checker"

  private def writeFile(name: String, dirSuffix: String, fileSuffix: String, what: String): String = {
    val outPath = testsPrefix + "/" + dirSuffix
    val d = new File(outPath)
    if (!d.exists()) d.mkdir()
    val outName = outPath + "/" + name + fileSuffix
    val output = new FileWriter(outName)
    output.write(what)
    output.close()
    outName
  }

  def compileEO(filename: String): Boolean = {
    // moveTest
    // try compiling
    // remove test
  }

  def runEO(filename: String): Boolean = {
    // renameTest
    compileEO()
    // removeTest
  }

  case class TestResult(transpiles: Boolean, compiles: Boolean, runs: Boolean)

  def check(test: File, mutation: Mutator.Mutation.Value): TestResult = {

    def yaml2python(f : File): (String, String) = {
      val yaml = new Yaml()
      val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))
      (f.getName, map.get("python"))
    }

    val (moduleName, python) = yaml2python(test)
    val db = debugPrinter(test)(_, _)
    val mutatedPyText = Mutator.mutate(python, mutation, 1)

    try {
      Transpile.transpile(db)(moduleName, mutatedPyText)
    }
    catch {
      case _: Exception => TestResult(transpiles = false, compiles = false, runs = false)
    }

    val originalEOText = Transpile.transpile(db)(moduleName, python)
    val fstName = writeFile("before", "mutations", ".eo", originalEOText)

    val mutatedEOText = Transpile.transpile(db)(moduleName, mutatedPyText)
    val sndName = writeFile("after", "mutations", ".eo", mutatedEOText)

    //Seq("diff", fstName, sndName).!

    if (!compileEO(sndName)) {
      TestResult(transpiles = true, compiles = false, runs = false)
    } else {
      TestResult(transpiles = true, compiles = true, runs = runEO(sndName))
    }

  }

  private def checkDir(prefix: String, mutation: Mutator.Mutation.Value): Array[TestResult] = {
    val test = new File(prefix)
    for {file <- test.listFiles() if file.getName.endsWith(".yaml") } yield check(file, mutation)
  }

  private val testsDir = System.getProperty("user.dir") + "/checker/src/test/resources/org/polystat/py2eo/checker/"
  private val nameMutation = Mutator.Mutation.nameMutation
  private val literalMutation = Mutator.Mutation.literalMutation

  checkDir(testsDir + "simple-tests/assign", nameMutation)
  checkDir(testsDir + "simple-tests/while", nameMutation)
  checkDir(testsDir + "simple-tests/if", nameMutation)
}