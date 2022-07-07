package org.polystat.py2eo.transpiler

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.transpiler.Common.dfsFiles

import java.io.File
import java.nio.file.{Files, StandardCopyOption}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.reflect.io.Directory
import scala.sys.process.Process

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DjangoTest extends Commons {

  @Test def aGenUnsupportedDjango() : Unit = {
    val root = new File(testsPrefix)
    val django = new File(testsPrefix + "/django")
    if (!django.exists()) {
      //      assert(0 == Process("git clone file:///home/bogus/pythonProjects/django", root).!)
      assert(0 == Process("git clone -b 4.0 https://github.com/django/django", root).!)
    }
    val test = dfsFiles(django).filter(f => f.getName.endsWith(".py"))
    val futures = test.map(test =>
//      Future
      {
        def db(s : Statement.T, str : String) = () // debugPrinter(test)(_, _)
        val name = test.getName
        println(s"parsing $name")
        val eoText = try {
          Transpile.transpile(db)(
            chopExtension(name),
            Transpile.Parameters(wrapInAFunction = false),
            readFile(test)
          )
        } catch {
          case e : Throwable =>
            println(s"failed to transpile $name: ${e.toString}")
            throw e
        }
        writeFile(test, "genUnsupportedEO", ".eo", eoText)
      }
    )
//    for (f <- futures) Await.result(f, Duration.Inf)
  }

  @Test def bCheckSyntaxForDjango() : Unit = {
    val django = new File(testsPrefix + "/django")
    val eopaths = dfsFiles(django).filter(f => f.getName.endsWith("genUnsupportedEO"))
    println(eopaths)
    val futures = eopaths.map(path =>
//      Future
      {
        val from = new File(testsPrefix + "/django-pom.xml").toPath
        val to = new File(path.toString + "/pom.xml").toPath
        println(s"$from -> $to")
        Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
//        assert(0 == Process(
//          s"cp -a '$testsPrefix/../../../../../../main/eo/preface/' ${path.toString}"
//        ).!
//        )
        assert(0 == Process("mvn clean test", path).!)
        assert(0 == Process(s"rm -rf ${path.toString}").!)
        //        val stdout = new StringBuilder
        //        val stderr = new StringBuilder
        //        val exitCode = Process("mvn clean test", path.toFile) ! ProcessLogger(stdout append _, stderr append _)
        //        if (0 != exitCode) {
        //          println(s"for path $to stdout is \n $stdout\n stderr is \n $stderr\n")
        //        } else {
        //          assert(0 == Process(s"rm -rf ${path.toString}").!)
        //        }
      }
    )
//    for (f <- futures) Await.result(f, Duration.Inf)
  }
}

