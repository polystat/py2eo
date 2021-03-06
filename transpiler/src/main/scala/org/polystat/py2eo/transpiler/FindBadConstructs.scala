package org.polystat.py2eo.transpiler

import java.io.File
import org.polystat.py2eo.parser.Expression.{CallIndex, Ident, StringLiteral}
import org.polystat.py2eo.parser.Parse

object FindBadConstructs extends App {
  val dir = new File(".")

  for (file <- dir.listFiles()) {
    if (!file.isDirectory && file.getName.endsWith(".py")) {
      inFile(file)
    }
  }

  def inFile(file: File) : Unit = {
    println(s"parsing ${file.getPath}")
    val Some(y) = Parse(file)
    SimpleAnalysis.foldSE[Unit](
      (_, e) => e match {
        case CallIndex(true, Ident("eval", _), (_, StringLiteral(_, _)) :: _, _) => ()
        case CallIndex(true, Ident("eval", ann), _, _) =>
          println(s"bad eval ${file.getName}:$ann")
        case _ => ()
      },
      _ => true
    )((), y)
  }

}
