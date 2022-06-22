package org.polystat.py2eo.transpiler

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

import java.io.File
import java.nio.file.{Files, StandardCopyOption}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.reflect.io.Directory
import scala.sys.process.Process

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DjangoTest extends Commons {

  @Test def firstlyGenUnsupportedDjango(): Unit = {
    val root = Directory(testsPrefix)
    val djangoDir = Directory(root / "django")
    if (!djangoDir.exists) {
      assert(0 == Process("git clone https://github.com/django/django", root.jfile).!)
    }

    val tests = djangoDir.deepFiles.filter(_.extension == "py")
    val futures = for {test <- tests} yield {
      Future {
        Transpile(test.stripExtension, test.slurp) match {
          case None => println(s"failed to transpile ${test.name}")
          case Some(transpiled) => writeFile(test.jfile, "genUnsupportedEO", ".eo", transpiled)
        }
      }
    }

    for (f <- futures) Await.result(f, Duration.Inf)
  }

  @Test def secondlyCheckSyntaxForDjango() : Unit = {
    val django = new File("/tmp/django")
    val eopaths = Files.walk(django.toPath).filter(f => f.endsWith("genUnsupportedEO"))
    val futures = eopaths.map(path =>
      Future {
        val from = new File(testsPrefix + "/django-pom.xml").toPath
        val to = new File(path.toString + "/pom.xml").toPath
        println(s"$from -> $to")
        Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
        assert(0 == Process(
          s"cp -a '$testsPrefix/../../../../../../main/eo/preface/' ${path.toString}"
        ).!
        )
        assert(0 == Process("mvn clean test", path.toFile).!)
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
    futures.forEach(f => Await.result(f, Duration.Inf))
  }
}
