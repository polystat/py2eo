package org.polystat.py2eo.transpiler

import org.junit.Assert.{assertTrue, fail}
import org.polystat.py2eo.parser.{PrintPython, Statement}
import org.polystat.py2eo.transpiler.Common.dfsFiles
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException

import java.io.{File, FileInputStream, FileWriter}
import java.nio.file.{Files, Path, StandardCopyOption}
import java.{lang => jl, util => ju}
import scala.io.Source
import scala.sys.process.{Process, ProcessLogger}

trait Commons {
  val testsPrefix: String = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/transpiler"
  val resultsPrefix: String = "src/test/resources/org/polystat/py2eo/transpiler/results"

  def yaml2python(f: File): String = {
    val map = new Yaml().load[java.util.Map[String, String]](new FileInputStream(f))
    map.get("python")
  }

  case class YamlTest(python: String, enabled: Boolean)

  def yaml2pythonModel(f: File): YamlTest = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))
    YamlTest(map.get("python"), map.containsKey("enabled") && map.getOrDefault("enabled", "false").asInstanceOf[Boolean])
  }

  def python: String = {
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    val notFound = Process("which python").!
    if (notFound != 0)
      "python3"
    else {
      assertTrue(0 == (Process("python --version") ! ProcessLogger(stdout.append(_), stderr.append(_))))
      val pattern = "Python (\\d+)".r
      val Some(match1) = pattern.findFirstMatchIn(if (stderr.toString() == "") stdout.toString() else stderr.toString())
      if (match1.group(1) == "2") "python3" else "python"
    }
  }

  def chopExtension(fileName: String): String = fileName.substring(0, fileName.lastIndexOf("."))

  def useCageHolder(test: File): Unit = {
    val results = new File(resultsPrefix)
    if (!results.exists) {
      results.mkdirs()
    }

    val name = test.getName.replace(".yaml", "")
    Transpile.transpileOption(debugPrinter(test))(name, Transpile.Parameters(wrapInAFunction = false), yaml2python(test)) match {
      case None => fail(s"could not transpile ${test.getName}");
      case Some(transpiled) =>
        val output = new FileWriter(results + File.separator + name + ".eo")
        output.write(transpiled)
        output.close()
    }
  }

  def collect(dir: String, filterEnabled: Boolean = false): ju.Collection[Array[jl.String]] = {
    val testsPrefix = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/transpiler"

    val res = collection.mutable.ArrayBuffer[String]()
    val simpleTestsFolder = new File(testsPrefix + File.separator + dir + File.separator)
    Files.walk(simpleTestsFolder.toPath).filter((p: Path) => p.toString.endsWith(".yaml")).forEach((p: Path) => {
      val testHolder = new File(p.toString)

      try {
        val map = new Yaml().load[java.util.Map[String, String]](new FileInputStream(testHolder))
        if (filterEnabled) {
          if (map.containsKey("enabled") && map.getOrDefault("enabled", "false").asInstanceOf[Boolean]) {
            res.addOne(p.toString)
          } else {
            println(s"The test ${testHolder.getName} is disabled")
          }
        } else {
          res.addOne(p.toString)
        }
      } catch {
        case e: YAMLException => fail(s"Couldn't parse ${testHolder.getName} file with error ${e.getMessage}")
        case e: ClassCastException => fail(s"Couldn't parse ${testHolder.getName} file with error ${e.getMessage}")
      }
    })


    val list = new ju.ArrayList[Array[jl.String]]()
    res.foreach(n => list.add(Array(n)))
    list
  }


  def debugPrinter(module: File)(s: Statement.T, dirSuffix: String): Unit = {
    writeFile(module, dirSuffix, ".py", PrintPython.print(s))
  }

  def readFile(f: File): String = {
    val s = Source.fromFile(f)
    s.mkString
  }

  def writeFile(test: File, dirSuffix: String, fileSuffix: String, what: String, otherLocation: Boolean = false): File = {
    val moduleName0 = test.getName.substring(0, test.getName.lastIndexOf("."))
    // do something with hidden files, because current EO fails them
    val moduleName = if (moduleName0.startsWith(".")) {
      "p" + moduleName0.substring(1, moduleName0.length)
    } else {
      moduleName0
    }
    val outPath = if (!otherLocation) test.getAbsoluteFile.getParentFile.getPath + "/" + dirSuffix else dirSuffix
    val d = new File(outPath)
    if (!d.exists()) d.mkdir()
    val outName = outPath + "/" + moduleName + fileSuffix
    val output = new FileWriter(outName)
    output.write(what)
    output.close()
    new File(outName)
  }

  // there is a temptation to parallelize this code with Future,
  // but there are strange problems if it is parallelized and called as a test from maven
  // with the surefire plugin. Basically, the test stops executing in the middle,
  // but it does not fail, it is just not counted at all
  def checkEOSyntaxInDirectory(path : String) : Unit = {
    val root = new File(path)
    val eopaths = dfsFiles(root).filter(f => f.getName.endsWith("genUnsupportedEO"))
    println(eopaths)
    eopaths.foreach(path => {
      val from = new File(testsPrefix + "/django-pom.xml").toPath
      val to = new File(path.toString + "/pom.xml").toPath
      Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
      assert(0 == Process("mvn clean test", path).!)
      assert(0 == Process(s"rm -rf \"${path.toString}/target\"").!)
    })
  }
}
