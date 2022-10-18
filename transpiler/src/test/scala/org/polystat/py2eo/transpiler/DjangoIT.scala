package org.polystat.py2eo.transpiler

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.{Order, Test, TestMethodOrder}
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.transpiler.Common.dfsFiles

import java.io.File
import java.nio.file.{Files, StandardCopyOption}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.reflect.io.Directory
import scala.sys.process.Process

@TestMethodOrder(classOf[OrderAnnotation])
class DjangoIT extends Commons {

  @Test
  @Order(1)
  def genUnsupportedDjango() : Unit = {
    val root = new File(testsPrefix)
    val django = new File(testsPrefix + "/django")
    if (!django.exists()) {
      Process("git clone -b 4.0 https://github.com/django/django", root).!!
    }
    val test = dfsFiles(django).filter(f => f.getName.endsWith(".py"))

    test.map(test =>
      {
        def db(s : Statement.T, str : String) = () // debugPrinter(test)(_, _)
        val name = test.getName
        val eoText =
          Transpile.transpile(db)(
            chopExtension(name),
            Transpile.Parameters(wrapInAFunction = false, isModule = false),
            readFile(test)
          )
        writeFile(test, "genUnsupportedEO", ".eo", eoText)
      }
    )
  }

  @Test
  @Order(2)
  def checkSyntaxForDjango() : Unit = {
    checkEOSyntaxInDirectory(testsPrefix + "/django")
  }

}

