package org.polystat.py2eo

import org.junit.Assert.assertTrue
import org.polystat.py2eo.Main.{debugPrinter, writeFile}
import java.io.{File, FileInputStream}

import org.yaml.snakeyaml.Yaml

import scala.sys.process._

trait Commons {
  val testsPrefix: String = new File(getClass.getResource("").getFile).toString

  val python: String = {
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    assertTrue(0 == (s"python --version" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    val pattern = "Python (\\d+)".r
    val Some(match1) = pattern.findFirstMatchIn(if (stderr.toString() == "") stdout.toString() else stderr.toString())
    if (match1.group(1) == "2") "python3" else "python"
  }

  def getYamlStr(path:String): String ={
    val testHolder = new File(path)
    val src = new FileInputStream(testHolder)
    val yaml = new Yaml()
    val yamlObj = yaml.load(src).asInstanceOf[java.util.Map[String, Any]]
    yamlObj.get("python").asInstanceOf[String]
  }


  def useCageHolder(path: String, yamlString:String):Unit = {
    val test = new File(path)
    def db = debugPrinter(test)(_, _)
    writeFile(
      test, "genCageEO", ".eo", Transpile.transpile(db)(
        test.getName.replace(".yaml", ""),
        yamlString
      )
    )
    val runme = test.getParentFile.getPath + "/afterUseCage/" + test.getName.substring(0,test.getName.lastIndexOf(".")) + ".py"
    assertTrue(0 == s"$python \"$runme\"".!)
  }
}