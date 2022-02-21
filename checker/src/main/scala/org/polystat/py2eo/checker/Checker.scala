package org.polystat.py2eo.checker

import java.io.{File, FileInputStream, FileWriter}
import scala.sys.process.stringSeqToProcess
import org.polystat.py2eo.transpiler.Main.debugPrinter
import org.polystat.py2eo.transpiler.Transpile
import org.yaml.snakeyaml.Yaml

object Checker extends App {
  private val testsPrefix = System.getProperty("user.dir") + "/transpiler/src/test/resources/org/polystat/py2eo/transpiler"

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

  def validate(test: File, mutation: Mutator.Mutation.Value): Unit = {

    def yaml2python(f : File): (String, String) = {
      val yaml = new Yaml()
      val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))
      (f.getName, map.get("python"))
    }

    val (moduleName, python) = yaml2python(test)
    val db = debugPrinter(test)(_, _)

    val originalEOText = Transpile.transpile(db)(moduleName, python)
    val fstName = writeFile("before", "mutations", ".eo", originalEOText)

    val mutatedEOText = Transpile.transpile(db)(moduleName, Mutator.mutate(python, mutation, 1))
    val sndName = writeFile("after", "mutations", ".eo", mutatedEOText)

    Seq("diff", fstName, sndName).!
  }

  private def validateDir(prefix: String, mutation: Mutator.Mutation.Value): Unit = {
    val test = new File(prefix)

    for (file <- test.listFiles()) if (file.getName.endsWith(".yaml")) validate(file, mutation)
  }

  private val testsDir = System.getProperty("user.dir") + "/transpiler/src/test/resources/org/polystat/py2eo/transpiler/"
  private val nameMutation = Mutator.Mutation.nameMutation
  private val literalMutation = Mutator.Mutation.literalMutation

  validateDir(testsDir + "simple-tests/assign", nameMutation)
  validateDir(testsDir + "simple-tests/while", nameMutation)
  validateDir(testsDir + "simple-tests/if", nameMutation)
}
