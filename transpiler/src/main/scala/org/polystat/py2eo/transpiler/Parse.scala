package org.polystat.py2eo.transpiler

import java.io.File
import scala.io.Source

import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ANTLRInputStream
import org.polystat.py2eo.parser.{PythonLexer, PythonParser}

object Parse {

  def parse(file: File, debugPrinter: (Statement, String) => Unit): Statement = {
    assert(file.getName.endsWith(".py"))
    val s = Source.fromFile(file)
    parse(s.mkString, debugPrinter)
  }

  def parse(input : String): Statement = {
    val inputStream = new ANTLRInputStream(input)
    val lexer = new PythonLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new PythonParser(tokenStream)
    MapStatements.mapFile(parser.file)
  }

  def parse(input : String, debugPrinter: (Statement, String) => Unit): Statement = {
    val t = parse(input)
    debugPrinter(t, "afterParser")
    t
  }

}
