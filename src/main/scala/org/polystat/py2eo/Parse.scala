package org.polystat.py2eo

import org.antlr.v4.runtime.ANTLRInputStream

import java.io.{File, FileReader}

object Parse {

  import org.antlr.v4.runtime.CommonTokenStream

  def parse(file: File, debugPrinter: (Statement, String) => Unit): Statement = {
    assert(file.getName.endsWith(".py"))

    val input = new FileReader(file)

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
