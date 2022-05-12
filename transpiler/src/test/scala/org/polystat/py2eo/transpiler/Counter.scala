package org.polystat.py2eo.transpiler

import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.polystat.py2eo.transpiler.Main.writeFile

import java.io.File
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Path, Paths}
import java.{lang => jl, util => ju}
import scala.language.postfixOps
import scala.sys.process.Process

object Counter extends Commons {
  @Parameters def parameters: ju.Collection[Array[jl.String]] = collect("simple-tests")
}

@RunWith(value = classOf[Parameterized])
class Counter(path: jl.String) extends Commons {
  private val runEOPath = Paths.get(".").toAbsolutePath.getParent.getParent + "/runEO"

  @Test def testDef(): Unit = {
    val test = new File(path)
    val yamlData = yaml2python(test)

    Transpile(test.getName.replace(".yaml", ""), yamlData) match {
      case None => fail(s"could not transpile ${test.getName}");
      case Some(transpiled) =>
        val path = writeFile(test, "genCageEO", ".eo", transpiled)

        if (!run(path)) {
          fail(s"could not run EO ${test.getName}")
        }
    }
  }

  private def run(file: File): Boolean = {
    val result = Files.copy(file.toPath, Path.of(s"$runEOPath/test.eo"), REPLACE_EXISTING)
    val ret = Process("mvn clean test", new File(runEOPath)).! == 0

    Files delete result

    ret
  }
}