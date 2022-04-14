package org.polystat.py2eo.transpiler

import org.junit.Assert.fail
import org.polystat.py2eo.transpiler.Main.writeFile
import org.yaml.snakeyaml.Yaml

import java.io.{File, FileInputStream}

trait Commons {
  val testsPrefix: String = System.getProperty("user.dir") + "/src/test/resources/org/polystat/py2eo/transpiler"
  case class YamlTest(python: String, enabled: Boolean)

  def yaml2python(f: File): YamlTest = {
    val yaml = new Yaml()
    val map = yaml.load[java.util.Map[String, String]](new FileInputStream(f))

    YamlTest(map.get("python"), map.containsKey("enabled") && map.getOrDefault("enabled", "false").asInstanceOf[Boolean])
  }

  def useCageHolder(test: File): Unit = {
    val yamlObj = yaml2python(test)

    if (yamlObj.enabled) {
      val res = Transpile(test.getName.replace(".yaml", ""),
        yamlObj.python)

      res match {
        case None => fail(s"could not transpile ${test.getName}");
        case Some(transpiled) =>
          writeFile(
            test, "genCageEO", ".eo", transpiled
          )
      }
    }
  }
}
