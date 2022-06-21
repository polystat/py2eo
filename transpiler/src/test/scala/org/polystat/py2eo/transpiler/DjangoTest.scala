package org.polystat.py2eo.transpiler

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.reflect.io.Directory
import scala.sys.process.Process


class DjangoTest extends Commons {

  @Test
  def genUnsupportedDjango(): Unit = {
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
}
