package org.polystat.py2eo;

import java.io.{File, FileWriter}
import java.nio.file.{Files, Paths}
import scala.io.Source

object Main {

  def main(args: Array[String]): Unit = {
    if (!args.isEmpty) {
      if (Files.exists(Paths.get(args(0)))) {
        val pyFile = new File(args(0))
        if (pyFile.isFile && pyFile.getName.endsWith("py")) {
          println(s"Working with file ${pyFile.getAbsolutePath}")
          def db = debugPrinter(pyFile)(_, _)
          val moduleName = pyFile.getName.substring(0, pyFile.getName.length - 3)
          val eoText = Transpile.transpile(db)(moduleName, readFile(pyFile))
          writeFile(pyFile, "genCageEO", ".eo", eoText)
        } else {
          println("Provided path is not a file")
        }
      } else {
        println("Provided path is unaccessible")
      }
    } else {
      println("Please add the path to .py file")
    }
  }

  def debugPrinter(module: File)(s: Statement, dirSuffix: String): Unit = {
    val what = PrintPython.printSt(s, "")
    writeFile(module, dirSuffix, ".py", what)
  }

  def readFile(f : File) : String = {
    val s = Source.fromFile(f)
    s.mkString
  }

  def writeFile(test: File, dirSuffix: String, fileSuffix: String, what: String): String = {
    val moduleName = test.getName.substring(0, test.getName.lastIndexOf("."))
    val outPath = test.getParentFile.getPath + "/" + dirSuffix
    val d = new File(outPath)
    if (!d.exists()) d.mkdir()
    val outName = outPath + "/" + moduleName + fileSuffix
    val output = new FileWriter(outName)
    output.write(what)
    output.close()
    outName
  }
}
