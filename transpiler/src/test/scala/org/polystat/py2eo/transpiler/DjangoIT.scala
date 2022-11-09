package org.polystat.py2eo.transpiler

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.{AfterAll, Order, Test, TestMethodOrder}
import org.polystat.py2eo.parser.Statement

import scala.reflect.io.{Directory, File}
import scala.sys.process.Process

@TestMethodOrder(classOf[OrderAnnotation])
class DjangoIT extends Commons {

  private val djangoLink = "https://github.com/django/django"

  @Test
  @Order(1)
  def genUnsupportedDjango(): Unit = {
    val directory = Directory.makeTemp(prefix = "org.polystat.py2eo.")
    val django = Directory(directory + "/django")

    Process(s"git clone -b 4.0 $djangoLink ${django.name}", directory.jfile).!!

    val tests = django.deepFiles.filter(_.extension == "py").toList
    for (test <- tests) {
      def db(s: Statement.T, str: String) = () // debugPrinter(test)(_, _)

      val name = chopExtension(test.name)
      val eoText =
        Transpile.transpile(db)(
          name,
          Transpile.Parameters(wrapInAFunction = false, isModule = false),
          readFile(test.jfile)
        )
      println(s"transpiled $name")
      val dir = test.parent / test.name.stripSuffix(".py") / "genUnsupportedEO"
      dir.createDirectory(failIfExists = false)
      val result = File(dir / s"$name.eo")
      result.createFile(failIfExists = false)
      result.writeAll(eoText)
    }
    println(s"Total of ${tests.length} files transpiled")
    checkEOSyntaxInDirectory(Directory(directory + "/django").toString)
//    directory.deleteRecursively()
  }

}

