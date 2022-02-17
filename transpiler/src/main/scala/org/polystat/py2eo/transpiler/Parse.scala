package org.polystat.py2eo.transpiler

import java.io.File
import scala.io.Source

import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ANTLRInputStream
import org.polystat.py2eo.parser.{PythonLexer, PythonParser}

object Parse {

  def apply(file: File, debugPrinter: (Statement, String) => Unit): Statement = {
    val stmt = Parse(file)
    debugPrinter(stmt, "afterParser")
    stmt
  }

  def apply(input: String, debugPrinter: (Statement, String) => Unit): Statement = {
    val stmt = Parse(input)
    debugPrinter(stmt, "afterParser")
    stmt
  }

  def apply(file: File): Statement = {
    assert(file.getName.endsWith(".py"))
    val input = Source.fromFile(file)
    Parse(input.mkString)
  }

  def apply(input: String): Statement = {
    val inputStream = new ANTLRInputStream(input)
    val lexer = new PythonLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new PythonParser(tokenStream)
    MapStatements.mapFile(parser.file)
  }

}
