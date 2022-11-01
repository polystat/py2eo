package org.polystat.py2eo.transpiler

import org.junit.jupiter.api.Assertions.{assertTrue, fail}
import org.polystat.py2eo.parser.{GeneralAnnotation, PrintPython, Statement}
import org.polystat.py2eo.transpiler.Common.dfsFiles
import org.polystat.py2eo.transpiler.GenericStatementPasses.Names
import org.yaml.snakeyaml.Yaml

import java.io.{File => JFile, FileInputStream, FileWriter}
import java.nio.file.{Files, Path, StandardCopyOption}
import scala.collection.immutable.HashMap
import scala.io.Source
import scala.reflect.io.{Directory, File}
import scala.sys.process.{Process, ProcessLogger}

trait Commons {
  val testsPrefix: String = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/transpiler"
  val resultsPrefix: String = "target/results"

  val bogusAnnotation = GeneralAnnotation(None, None)
  val bogusNamesU = Names[Unit](HashMap(), ())

  def yaml2python(f: JFile): String = {
    val map = new Yaml().load[java.util.Map[String, String]](new FileInputStream(f))
    map.get("python")
  }

  case class YamlTest(python: String, isModule: Boolean, enabled: Boolean)

  def yaml2pythonModel(f: JFile): YamlTest = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))

    val sourceCode = map get "python"
    val enabled = map.containsKey("enabled") && map.getOrDefault("enabled", "false").asInstanceOf[Boolean]
    val module = map.containsKey("module") && map.getOrDefault("module", "false").asInstanceOf[Boolean]

    YamlTest(sourceCode, module, enabled)
  }

  def python: String = {
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    val notFound = Process("which python").!
    if (notFound != 0) {
      "python3"
    } else {
      assertTrue(0 == (Process("python --version") ! ProcessLogger(stdout.append(_), stderr.append(_))))
      val pattern = "Python (\\d+)".r
      val Some(match1) = pattern.findFirstMatchIn(if (stderr.toString() == "") stdout.toString() else stderr.toString())
      if (match1.group(1) == "2") "python3" else "python"
    }
  }

  def chopExtension(fileName: String): String = fileName.substring(0, fileName.lastIndexOf("."))

  def useCageHolder(test: JFile, isModule : Boolean = false): Unit = {
    val results = new JFile(resultsPrefix)
    if (!results.exists) {
      results.mkdirs()
    }

    val name = test.getName.replace(".yaml", "")
    Main.transpile(
      name,
      yaml2python(test),
      test.getParent.toString,
      results.getAbsolutePath.toString,
      Transpile.Parameters(wrapInAFunction = false, isModule = isModule),
    ) match {
      case false => fail(s"could not transpile ${test.getName}");
      case true  => ()
    }
  }

  def isEnabled(file: File): Boolean = yaml2pythonModel(file.jfile).enabled
  def isModule(file: File): Boolean = yaml2pythonModel(file.jfile).isModule

  def collect(dir: Directory, filterEnabled: Boolean = false): Array[File] = {
    val allYamlFiles = dir.deepFiles.filter(_.extension == "yaml").toArray
    if (filterEnabled) allYamlFiles.filter(isEnabled) else allYamlFiles
  }


  def debugPrinter(module: JFile)(s: Statement.T, dirSuffix: String): Unit = {
    writeFile(module, dirSuffix, ".py", PrintPython.print(s))
  }

  def readFile(f: JFile): String = {
    val s = Source.fromFile(f)
    s.mkString
  }

  def writeFile(test: JFile, dirSuffix: String, fileSuffix: String, what: String, otherLocation: Boolean = false): JFile = {
    val moduleName0 = test.getName.substring(0, test.getName.lastIndexOf("."))
    // do something with hidden files, because current EO fails them
    val moduleName = if (moduleName0.startsWith(".")) {
      "p" + moduleName0.substring(1, moduleName0.length)
    } else {
      moduleName0
    }
    val outPath = if (!otherLocation) test.getAbsoluteFile.getParentFile.getPath + "/" + dirSuffix else dirSuffix
    val d = new JFile(outPath)
    if (!d.exists()) d.mkdir()
    val outName = outPath + "/" + moduleName + fileSuffix
    val output = new FileWriter(outName)
    output.write(what)
    output.close()
    new JFile(outName)
  }

  // there is a temptation to parallelize this code with Future,
  // but there are strange problems if it is parallelized and called as a test from maven
  // with the surefire plugin. Basically, the test stops executing in the middle,
  // but it does not fail, it is just not counted at all
  def checkEOSyntaxInDirectory(path : String) : Unit = {
    val root = new JFile(path)
    val eopaths = dfsFiles(root).filter(f => f.getName.endsWith("genUnsupportedEO"))
    println(eopaths)
    eopaths.foreach(path => {
      val from = new JFile(testsPrefix + "/django-pom.xml").toPath
      val to = new JFile(path.toString + "/pom.xml").toPath
      Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
      Process("mvn clean test", path).!!
      Process(s"rm -rf \"${path.toString}/target\"").!!
    })
  }
}
