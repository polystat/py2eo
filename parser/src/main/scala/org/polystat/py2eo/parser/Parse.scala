package org.polystat.py2eo.parser

import java.io.File
import scala.io.Source

import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

object Parse {

  def apply(file: File, debugPrinter: (Statement.T, String) => Unit): Statement.T = {
    val stmt = Parse(file)
    debugPrinter(stmt, "afterParser")
    stmt
  }

  def apply(input: String, debugPrinter: (Statement.T, String) => Unit): Statement.T = {
    val stmt = Parse(input)
    debugPrinter(stmt, "afterParser")
    stmt
  }

  def apply(file: File): Statement.T = {
    assert(file.getName.endsWith(".py"))
    val input = Source.fromFile(file)
    Parse(input.mkString)
  }

  def apply(input: String): Statement.T = {
    val inputStream = new ANTLRInputStream(input)
    val lexer = new PythonLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new PythonParser(tokenStream)
    MapStatements.mapFile(parser.file)
  }

}
