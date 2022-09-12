package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.{PrintPython, Statement}

import java.io.{File, FileWriter}
import scala.io.Source

object Common {

  import scala.collection.immutable.HashMap

  type ExternalConstants = HashMap[String, BigInt]

  def dfsFiles(file : File) : List[File] = {
    if (!file.isDirectory) List(file) else {
      file :: file.listFiles().toList.flatMap(dfsFiles)
    }
  }

  class TranspilerException(reason : String) extends RuntimeException(reason)
  class GeneratorException(reason : String) extends TranspilerException(reason)
  class ASTAnalysisException(reason : String) extends TranspilerException(reason)

  val space = " "
  val crb = ")"
  val orb = "("

}

