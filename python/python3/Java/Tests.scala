import java.io.FileWriter

import Expression.{CallIndex, Ident}
import org.junit.Assert._
import org.junit.Test

class Tests {

  val testsPrefix = "python/python3/test/"
//  val testsPrefix = "test/"

  @Test def printEO(): Unit = {
    val name = "trivial"
    val z = Parse.parse(testsPrefix, name)
    val output = new FileWriter(name + ".eo")
    output.write(PrintEO.printSt(name, z._1))
    output.close()
    import scala.sys.process._
    assertTrue(0 == (s"diff \"$testsPrefix/trivial.eo.golden\" \"$testsPrefix/trivial.eo\"".!))
  }

  @Test def removeControlFlow(): Unit = {
    val name = "trivialWithBreak"
    val y = Parse.parse(testsPrefix, name)
    val z = RemoveControlFlow.removeControlFlow(y._1, y._2)
    val Suite(List(theFun, Return(_))) = z._1
    val zHacked = Suite(List(theFun, Assign(List(CallIndex(true, Ident("outer"), List())))))
    Parse.toFile(zHacked, testsPrefix + "afterRemoveControlFlow", name)
    val stdout = new StringBuilder()
    val stderr = new StringBuilder()
    import scala.sys.process._
    assertTrue(0 == (s"python3 \"$testsPrefix/afterRemoveControlFlow/$name.py\"" ! ProcessLogger(stdout.append(_), stderr.append(_))))
    println(stdout)
    assertTrue(stdout.mkString("") == "34")
  }

}