package org.polystat.py2eo;

import Expression.{CallIndex, Ident, StringLiteral}

import java.io.File

object FindBadConstructs extends App {
  val dir = new File(".")

  for (file <- dir.listFiles()) {
    if (!file.isDirectory && file.getName.endsWith(".py")) {
      inFile(file)
    }
  }

  def inFile(file: File) : Unit = {
    println(s"parsing ${file.getPath}")
    val y = Parse.parse(file, (_, _) => ())
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
