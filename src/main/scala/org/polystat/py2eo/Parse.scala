package org.polystat.py2eo

import org.antlr.v4.runtime.ANTLRInputStream

import java.io.{File, FileReader}
import scala.io.Source

object Parse {

  import org.antlr.v4.runtime.CommonTokenStream

  def parse(file: File, debugPrinter: (Statement, String) => Unit): Statement = {
    assert(file.getName.endsWith(".py"))
    val s = Source.fromFile(file)
    parse(s.mkString, debugPrinter)
  }

  def parse(input : String, debugPrinter: (Statement, String) => Unit): Statement = {
    val inputStream = new ANTLRInputStream(input)
    val lexer = new PythonLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new PythonParser(tokenStream)
    val e = parser.file()
    val t = MapStatements1.mapFile(e)
    debugPrinter(t, "afterParser")
    t
  }

}
