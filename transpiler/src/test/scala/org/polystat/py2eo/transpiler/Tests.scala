package org.polystat.py2eo.transpiler

import org.junit.{Ignore, Test}
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.transpiler.Common.dfsFiles

import java.io.File
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.sys.process.Process


class Tests extends Commons {
  @Ignore
  @Test def genUnsupportedDjango() : Unit = {
    val root = new File(testsPrefix)
    val django = new File(testsPrefix + "/django")
    if (!django.exists()) {
//      assert(0 == Process("git clone file:///home/bogus/pythonProjects/django", root).!)
      assert(0 == Process("git clone https://github.com/django/django", root).!)
    }
    val test = dfsFiles(django).filter(f => f.getName.endsWith(".py"))
    val futures = test.map(test =>
      Future {
        def db(s : Statement.T, str : String) = () // debugPrinter(test)(_, _)
        val name = test.getName
        println(s"parsing $name")
        val eoText = try {
          Transpile.transpile(db)(chopExtension(name), Transpile.Parameters(wrapInAFunction = false), readFile(test))
        } catch {
          case e : Throwable =>
            println(s"failed to transpile $name: ${e.toString}")
            throw e
        }
        writeFile(test, "genUnsupportedEO", ".eo", eoText)
      }
    )
    for (f <- futures) Await.result(f, Duration.Inf)
  }
}
