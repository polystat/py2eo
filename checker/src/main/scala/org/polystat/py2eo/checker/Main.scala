package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.Mutate.Mutation

import scala.language.postfixOps
import scala.reflect.io.Path

object Main {

  private val usage = "the first argument should be the path to tests, the second argument should be the output path"
  private val nonValidInputError = "provided input path is not valid"

  def main(args: Array[String]): Unit = {
    if (args.length == 2) {
      val inputPath = Path(args(0)) toAbsolute
      val outputPath = Path(args(1)) toAbsolute
      val mutations = Mutation values

      if (inputPath exists) {
        Check(inputPath, outputPath, mutations)
      } else {
        error(nonValidInputError)
      }
    } else {
      println(s"Usage: $usage")
    }
  }

  /**
   * Print the specified message and exit with code 1
   *
   * @return Nothing
   */
  private def error(message: String): Nothing = {
    println(s"Error: $message")
    sys exit 1
  }
}
