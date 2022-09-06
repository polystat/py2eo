package org.polystat.py2eo.transpiler

import org.junit.Test
import org.polystat.py2eo.transpiler.Counter.testsPrefix
import org.yaml.snakeyaml.Yaml

import scala.reflect.io.{File, Path}

import scala.beans.BeanProperty

// This object works with the https://github.com/polystat/awesome-bugs repository
// The idea is to try to transiple all the .py sources from it
class AwesomeBugs {

  def extractPy(f : File) : Option[String] = {
    val v = new Yaml()
      .load(f.inputStream())
      .asInstanceOf[java.util.Map[String, String]]
    Option(v.get("bad").asInstanceOf[java.util.Map[String, String]].get("test.py"))
  }

  val awBugs = "awesome-bugs/tests/inheritance/"

//  @Test def printPython() : Unit = {
//    println(extractPy(new java.io.File(testsPrefix + "/" + tryMe)))
//  }

  @Test def printAllPython() : Unit = {
    Path(testsPrefix + "/" + awBugs).toDirectory.deepFiles.filter(_.extension == "yml").foreach(f => {
      println(s"filename = ${f.name}")
      extractPy(f).map(Transpile(f.name, Transpile.Parameters(wrapInAFunction = false), _)) match {
        case Some(value) => println(value)
        case None => ()
      }
    })
  }

}
