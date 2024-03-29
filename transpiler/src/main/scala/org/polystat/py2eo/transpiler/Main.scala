package org.polystat.py2eo.transpiler

import org.polystat.py2eo.transpiler.Transpile.Parameters

import scala.reflect.io.File

object Main {

  /** Current transpiler version code */
  private val versionCode = "0.0.11.3"

  /** Version information */
  private val version = s"Polystat Py2Eo version $versionCode"

  /** Usage text */
  private val usage =
    s"""USAGE: java -jar transpiler-$versionCode-jar-with-dependencies.jar file [options]
       |OPTIONS:
       |  -h,--help         Display available options
       |  -o <file>         Write output to <file>
       |  -X,--debug        Produce execution debug output
       |  -v,--version      Print version information
       |""".stripMargin

  /** Entry point */
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println(usage)
    } else if (args.contains("-v") || args.contains("--version") || args.contains("-h") || args.contains("--help")) {
      printInfo(args)
    } else if (args.count("-o".equals) > 1) {
      error("duplicated '-o' argument")
    } else {
      launch(args)
    }
  }

  /** Print version and usage if needed */
  private def printInfo(args: Array[String]): Unit = {
    if (args.contains("-v") || args.contains("--version")) println(version)
    if (args.contains("-h") || args.contains("--help")) println(usage)
  }

  /** Final checks and actual launch */
  private def launch(args: Array[String]): Unit = {
    // Retrieving output path information
    val (filteredAllFlags, outputPath) = if (args.contains("-o")) {
      val idx = args.indexOf("-o")
      if (idx == args.length) {
        error("-o flag doesn't have file parameter")
      } else {
        (args.take(idx) ++ args.drop(idx + 2), Some(args(idx + 1)))
      }
    } else {
      (args, None)
    }

    // Actual launch
    if (filteredAllFlags.nonEmpty) {
      val input = File(filteredAllFlags.head)
      if (input.exists) {
        transpile(
          input.stripExtension,
          input.slurp(),
          input.path,
          outputPath.getOrElse(input.parent.path),
          Transpile.Parameters(wrapInAFunction = true, isModule = false)
        )
      } else {
        error("no such file:", input.name)
      }
    } else {
      error("no input files")
    }
  }

  /** Transpiles the input file and (if successful) writes result to output file */
  def transpile(moduleName : String, input: String, currentDir : String, output: String, opt : Parameters): Boolean = {
    Transpile(moduleName, opt, input, currentDir, output) match {
      case None => println("\"Not Supported: input file syntax is not python 3.8\" > error"); false
      case Some(transpiled) => File(output + s"/$moduleName.eo").createFile().writeAll(transpiled); true
    }
  }

  /**
   * Prints the specified message and exits with code 1
   *
   * @return Nothing
   */
  private def error(message: String, args: String*): Nothing = {
    println(s"Error: $message ${args.mkString(" ")}")
    sys exit 1
  }
}
