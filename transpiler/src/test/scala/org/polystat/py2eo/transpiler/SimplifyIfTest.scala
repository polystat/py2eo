package org.polystat.py2eo.transpiler

import org.junit.Assert.assertTrue
import org.junit.Test
import org.polystat.py2eo.parser.Expression.Ident
import org.polystat.py2eo.parser.Statement.{If, IfSimple, Pass}
import org.polystat.py2eo.transpiler.Counter.{bogusAnnotation, bogusNamesU}

class SimplifyIfTest {
  @Test def ififelse() = {
    def mkId(name : String) = Ident(name, bogusAnnotation)
    val pass = Pass(bogusAnnotation)
    val input = If(
      List((mkId("a"), pass), (mkId("b"), pass)),
      Some(pass),
      bogusAnnotation
    )
    val output = SimplifyIf(input, bogusNamesU)
    assertTrue(
      output._1 ==
        (
          IfSimple(
            mkId("a"),
            pass,
            IfSimple(mkId("b"), pass, pass, bogusAnnotation),
            bogusAnnotation
          )
        )
    )
  }
}
