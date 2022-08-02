package org.polystat.py2eo.parser

import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

import java.io.File
import scala.io.Source
import scala.util.Try

object Parse {

  /** Debug printer that does nothing */
  private val nullDebugPrinter = (_: Statement.T, _: String) => ()

  /** Parses the given .py file */
  def apply(file: File): Option[Statement.T] = apply(file, nullDebugPrinter)

  /** Parses the given input string */
  def apply(input: String): Option[Statement.T] = apply(input, nullDebugPrinter)

  /** Parses the given .py file and prints debug output with the given debug printer */
  def apply(file: File, debugPrinter: (Statement.T, String) => Unit): Option[Statement.T] = {
    assert(file.getName.endsWith(".py"))
    val input = Source.fromFile(file)
    apply(input.mkString, debugPrinter)
  }

  /** Parses the given input string and prints debug output with the given debug printer */
  def apply(input: String, debugPrinter: (Statement.T, String) => Unit): Option[Statement.T] = {
    val parsed = parseToOption(input)
    parsed.foreach(stmt => debugPrinter(stmt, "afterParser"))

    parsed
  }

  /** Calls antlr parser and maps the result to the custom AST */
  private def parseToOption(input: String): Option[Statement.T] = {
    Try {
      val inputStream = new ANTLRInputStream(input)
      val lexer = new PythonLexer(inputStream)
      val tokenStream = new CommonTokenStream(lexer)
      val parser = new PythonParser(tokenStream)
      MapStatements.mapFile(parser.file)
    }.toOption
  }
}
