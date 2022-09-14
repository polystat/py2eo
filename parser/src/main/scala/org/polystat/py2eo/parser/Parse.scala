package org.polystat.py2eo.parser

import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

import java.io.{File => JFile}
import scala.reflect.io.File
import org.polystat.py2eo.parser.Statement.{T => Stmt}
import scala.util.Try

object Parse {

  /** Type for debug printer function */
  type DebugPrinter = (Stmt, String) => Unit

  /** Debug printer that does nothing */
  val nullDebugPrinter: DebugPrinter = (_, _) => ()

  /** Parse the given [[scala.reflect.io.File]] */
  def apply(file: File): Option[Stmt] = apply(file, nullDebugPrinter)

  /** Parse the given [[java.io.File]] */
  def apply(file: JFile): Option[Stmt] = apply(file, nullDebugPrinter)

  /** Parse the given input string */
  def apply(input: String): Option[Stmt] = apply(input, nullDebugPrinter)

  /**
   * Parse the given [[scala.reflect.io.File]]
   * and print debug output with the given debug printer
   */
  def apply(file: File, debugPrinter: DebugPrinter): Option[Stmt] = {
    Try(file.slurp).map(apply(_, debugPrinter)).toOption.flatten
  }

  /**
   * Parse the given [[java.io.File]]
   * and print debug output with the given debug printer
   */
  def apply(file: JFile, debugPrinter: DebugPrinter): Option[Stmt] = {
    apply(File(file), debugPrinter)
  }

  /**
   * Parse the given input string
   * and print debug output with the given debug printer
   */
  def apply(input: String, debugPrinter: DebugPrinter): Option[Stmt] = {
    val parsed = parseToOption(input)
    parsed.foreach(stmt => debugPrinter(stmt, "afterParser"))

    parsed
  }

  /** Call antlr parser and map the result to the custom AST */
  private def parseToOption(input: String): Option[Stmt] = {
    Try {
      val inputStream = new ANTLRInputStream(input)
      val lexer = new PythonLexer(inputStream)
      val tokenStream = new CommonTokenStream(lexer)
      val parser = new PythonParser(tokenStream)
      MapStatements.mapFile(parser.file)
    }.toOption
  }
}
