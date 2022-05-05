package org.polystat.py2eo.parser

import org.antlr.v4.runtime.misc.ParseCancellationException

import java.io.File
import scala.io.Source
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

object Parse {

  def apply(file: File, debugPrinter: (Statement.T, String) => Unit): Option[Statement.T] = {
    val stmt = Parse(file)
    stmt match { case Some(stmt) => debugPrinter(stmt, "afterParser") case None => () }
    stmt
  }

  def apply(input: String, debugPrinter: (Statement.T, String) => Unit): Option[Statement.T] = {
    val stmt = Parse(input)
    stmt match { case Some(stmt) => debugPrinter(stmt, "afterParser") case None => () }
    stmt
  }

  def apply(file: File): Option[Statement.T] = {
    assert(file.getName.endsWith(".py"))
    val input = Source.fromFile(file)
    Parse(input.mkString)
  }

  def apply(input: String): Option[Statement.T] = {
    try {
      val inputStream = new ANTLRInputStream(input)
      val lexer = new PythonLexer(inputStream)
      val tokenStream = new CommonTokenStream(lexer)
      val parser = new PythonParser(tokenStream)
      Some(MapStatements.mapFile(parser.file))
    }
    catch {
      case _ : ParseCancellationException => None
    }
  }

}
