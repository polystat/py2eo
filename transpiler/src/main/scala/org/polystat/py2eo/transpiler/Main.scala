package org.polystat.py2eo.transpiler

import java.io.{File, FileWriter}
import java.nio.file.{Files, Paths}
import scala.io.Source

object Main {
  var debug: Boolean = false

  def main(args: Array[String]): Unit = {
    if (!args.isEmpty) {
      var pyFilePath = ""
      var outputFolderPath = ""
      debug = args.contains("--debug") || args.contains("-X")

      for (arg <- args) {
        if (Files.exists(Paths.get(arg)) && new File(arg).isFile && new File(arg).getName.contains(".py")) {
          pyFilePath = arg
        }

        if (Files.exists(Paths.get(arg)) && new File(arg).isDirectory) {
          outputFolderPath = arg
        }
      }

      if (pyFilePath.nonEmpty) {
        val pyFile = new File(args(0))

        if (pyFile.isFile && pyFile.getName.endsWith(".py")) {
          println(s"Working with file ${pyFile.getAbsolutePath}")

          def db = debugPrinter(pyFile)(_, _)

          val moduleName = pyFile.getName.substring(0, pyFile.getName.lastIndexOf("."))
          val eoText = Transpile.transpile(db)(moduleName, readFile(pyFile))
          writeFile(pyFile, if (outputFolderPath.nonEmpty) outputFolderPath + File.separator + "genCageEO" else "genCageEO",
            ".eo", eoText, outputFolderPath.nonEmpty)
        } else {
          println("Provided path is not a file")
        }
      } else if (args.contains("--version") || args.contains("-V")) {
        println("The current EOLang transpiler version is 0.0.5")
      } else if (args.contains("--help") || args.contains("-h")) {
        println("usage: java -jar .\\py2eo-${version_code}-SNAPSHOT-jar-with-dependencies.jar .\\sample_test.py [options] \n\nOptions: \n" +
          "-h,--help \t\t\t Display help information\n" +
          "-o     \t\t\t\t path to output .eo file\n" +
          "-X,--debug     \t\t Produce execution debug output\n" +
          "-v,--version \t\t Display version information")
      } else {
        println("Please add the path to .py file")
      }

    } else {
      println("Please add the path to .py file")
    }
  }

  def debugPrinter(module: File)(s: Statement, dirSuffix: String): Unit = {
    writeFile(module, dirSuffix, ".py", PrintPython.print(s))
  }

  def readFile(f: File): String = {
    val s = Source.fromFile(f)
    s.mkString
  }

  def writeFile(test: File, dirSuffix: String, fileSuffix: String, what: String, otherLocation: Boolean = false): String = {
    val moduleName = test.getName.substring(0, test.getName.lastIndexOf("."))
    val outPath = if (!otherLocation) test.getParentFile.getPath + "/" + dirSuffix else dirSuffix
    val d = new File(outPath)
    if (!d.exists()) d.mkdir()
    val outName = outPath + "/" + moduleName + fileSuffix
    val output = new FileWriter(outName)
    output.write(what)
    output.close()
    outName
  }
}
