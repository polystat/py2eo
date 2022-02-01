package org.polystat.py2eo;

import Expression.{CallIndex, Ident}
import java.io.{File, FileWriter}
import java.nio.file.{Files, Paths}

object Main {

  def main(args: Array[String]): Unit = {
    if (!args.isEmpty) {
      if (Files.exists(Paths.get(args(0)))) {
        val pyFile = new File(args(0))
        if (pyFile.isFile && pyFile.getName.substring(pyFile.getName.lastIndexOf('.') + 1) == "py") {
          println(s"Working with file ${pyFile.getAbsolutePath}")

          def db = debugPrinter(pyFile)(_, _)

          val y = SimplePass.allTheGeneralPasses(db, Parse.parse(pyFile, db), new SimplePass.Names())

          val textractAllCalls = SimplePass.procExprInStatement(
            SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
          val Suite(List(theFun@FuncDef(mainName, _, _, _, _, body, _, _, _, ann)), _) =
            ClosureWithCage.declassifyOnly(textractAllCalls._1)


          val eoHacked = Suite(List(
            theFun,
            Return(Some(CallIndex(true, Ident(mainName, ann.pos), List(), ann.pos)), ann.pos)
          ), ann.pos)


          val eoText = PrintLinearizedMutableEOWithCage.printTest(pyFile.getName.replace(".py", ""), eoHacked)
          writeFile(pyFile, "genCageEO", ".eo", (eoText.init.init :+ "        xresult").mkString("\n"))
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

  def writeFile(test: File, dirSuffix: String, fileSuffix: String, what: String): String = {
    val moduleName = test.getName.substring(0, test.getName.length - 3)
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