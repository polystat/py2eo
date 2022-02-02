package org.polystat.py2eo

import org.junit.Assert.assertTrue
import org.polystat.py2eo.Expression.{CallIndex, Ident}
import org.polystat.py2eo.Main.{debugPrinter, writeFile}

import java.io.File
import scala.sys.process._

trait Commons {
  val testsPrefix: String = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/"

  val python: String = {
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    assertTrue(0 == (s"python --version" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    val pattern = "Python (\\d+)".r
    val Some(match1) = pattern.findFirstMatchIn(if (stderr.toString() == "") stdout.toString() else stderr.toString())
    if (match1.group(1) == "2") "python3" else "python"
  }

  def getTestEntry(fArray:collection.mutable.ArrayBuffer[YamlItem],fName:String,simpleTest:Boolean):(String,String) = {
    var res = ("","")
    for (item <- fArray){
      val fileName = item.testName.getFileName.toString
      if (!simpleTest && fileName.substring(0,fileName.lastIndexOf(".")) == fName){
        res = (item.testName.toString, item.yaml.get("python").asInstanceOf[String])
      }else{
        val folderName = item.testName.getParent.getFileName.toString
        if (folderName == fName){
          res = (item.testName.toString, item.yaml.get("python").asInstanceOf[String])
        }
      }
    }
    res
  }

  def useCageHolder(path: String, yamlString:String): Unit = {
    val test = new File(path)

    def db = debugPrinter(test)(_, _)
    val y = SimplePass.allTheGeneralPasses(db, Parse.parse(yamlString, db), new SimplePass.Names())
    val textractAllCalls = SimplePass.procExprInStatement(
      SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
    val Suite(List(theFun@FuncDef(mainName, _, _, _, _, _, _, _, _, ann)), _) =
      ClosureWithCage.declassifyOnly(textractAllCalls._1, textractAllCalls._2)._1

    val hacked = Suite(List(
      theFun,
      Assert(CallIndex(isCall = true, Ident(mainName, ann.pos), List(), ann.pos), None, ann.pos)
    ), ann.pos)
    val runme = writeFile(test, "afterUseCage", ".py", PrintPython.printSt(hacked, ""))
    assertTrue(0 == s"$python \"$runme\"".!)

    val eoHacked = Suite(List(
      theFun,
      Return(Some(CallIndex(isCall = true, Ident(mainName, ann.pos), List(), ann.pos)), ann.pos)
    ), ann.pos)

    val eoText = PrintLinearizedMutableEOWithCage.printTest(test.getName.replace(".yaml", ""), eoHacked)
    writeFile(test, "genCageEO", ".eo", (eoText.init.init :+ "        result").mkString("\n"))
  }
}
