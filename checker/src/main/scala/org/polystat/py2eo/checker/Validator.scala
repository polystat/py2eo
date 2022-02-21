package org.polystat.py2eo.checker

import java.io.{File, FileWriter}
import scala.sys.process.{Process, stringSeqToProcess}
import org.polystat.py2eo.transpiler.Main.{debugPrinter, readFile}
import org.polystat.py2eo.transpiler.{Parse, PrintLinearizedMutableEOWithCage, PrintPython, SimplePass, Statement, Transpile}

object Validator extends App {
  private val testsPrefix = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/"

  def writeFile(name: String, dirSuffix: String, fileSuffix: String, what: String): String = {
    val outPath = testsPrefix + "/" + dirSuffix
    val d = new File(outPath)
    if (!d.exists()) d.mkdir()
    val outName = outPath + "/" + name + fileSuffix
    val output = new FileWriter(outName)
    output.write(what)
    output.close()
    outName
  }

  def validate(test: File, mutation: (Statement.T, SimplePass.Names) => (Statement.T, SimplePass.Names)): Unit = {
    def db: (Statement.T, String) => Unit = debugPrinter(test)(_, _)

    SimplePass.needToChange = true

    val ns = new SimplePass.Names()
    val mutatedAST = mutation(SimplePass.simplifyIf(Parse(test, db), ns)._1, ns)
    val mutatedPy = PrintPython.print(mutatedAST._1)

    val originalEOText = Transpile.transpile(db)(test.getName, readFile(test))
    val fstName = writeFile("before", "mutations", ".eo", originalEOText)

    val mutatedEOText = Transpile.transpile(db)(test.getName, mutatedPy)
    val sndName = writeFile("after", "mutations", ".eo", mutatedEOText)

    Seq("diff", fstName, sndName).!
  }

  def validateDir(prefix: String, mutation: (Statement.T, SimplePass.Names) => (Statement.T, SimplePass.Names)): Unit = {
    val test = new File(prefix)

    for (file <- test.listFiles()) if (file.getName.endsWith(".py")) validate(file, mutation)
  }

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def validateCPython(mutation: (Statement.T, SimplePass.Names) => (Statement.T, SimplePass.Names)): Unit = {
    val dirName = testsPrefix + "mutations/validateCPython"
    val cpython = new File(dirName)
    if (!cpython.exists()) {
      Seq("git", "clone", "https://github.com/python/cpython", dirName).!
      assert(0 == Process("git checkout v3.8.10", cpython).!)
    }

    val testsDir = new File(dirName + "/Lib/test")
    for (file <- recursiveListFiles(testsDir))
      if (file.getName.startsWith("test_") && file.getName.endsWith(".py")) {
        val name = file.getPath // getName
        println(s"validating $name")
        validate(file, mutation)
      }
  }


  private val testsDir = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/"
  private val namesMutation = SimplePass.simpleProcExprInStatement(SimplePass.changeIdentifierName)(_, _)

  validateDir(testsDir + "simple_tests/assignCheck", namesMutation)
  validateDir(testsDir + "simple_tests/whileCheck", namesMutation)
  validateDir(testsDir + "simple_tests/ifCheck", namesMutation)

  validateCPython(namesMutation)
}
