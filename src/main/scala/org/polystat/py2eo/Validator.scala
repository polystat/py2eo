package org.polystat.py2eo

import java.io.{File, FileWriter}
import Main.debugPrinter

import scala.sys.process.stringSeqToProcess

object Validator extends App {
  private val testsPrefix = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/"

  def writeFile(name : String, dirSuffix: String, fileSuffix: String, what: String): String = {
    val outPath = testsPrefix + "/" + dirSuffix
    val d = new File(outPath)
    if (!d.exists()) d.mkdir()
    val outName = outPath + "/" + name + fileSuffix
    val output = new FileWriter(outName)
    output.write(what)
    output.close()
    outName
  }

  def validate(mutation: (Statement, SimplePass.Names) => (Statement, SimplePass.Names)): Unit = {
    val test = new File(testsPrefix + "trivial.py")

    def db: (Statement, String) => Unit = debugPrinter(test)(_, _)

    val AST = SimplePass.allTheGeneralPasses(db, Parse.parse(test, db), new SimplePass.Names())
    val mutatedAST = mutation(AST._1, AST._2)

    val originalEOText = PrintLinearizedMutableEOWithCage.printTest(test.getName, AST._1)
    val fstName = writeFile("before", "mutations", ".eo", originalEOText.mkString("\n"))

    PrintLinearizedMutableEOWithCage.HackName.count = 0

    val mutatedEOText = PrintLinearizedMutableEOWithCage.printTest(test.getName, mutatedAST._1)
    val sndName = writeFile("after", "mutations", ".eo", mutatedEOText.mkString("\n"))

    Seq("diff", fstName, sndName) .!
  }

  validate(SimplePass.procExprInStatement(SimplePass.procExpr(SimplePass.changeIdentifierName))(_,_))
}
