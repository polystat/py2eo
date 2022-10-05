package org.polystat.py2eo.transpiler

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.polystat.py2eo.parser.Expression.Ident
import org.polystat.py2eo.parser.Statement.{If, IfSimple, Pass}

class SimplifyIfTest extends Commons {
  @Test def ififelse(): Unit = {
    def mkId(name : String) = Ident(name, bogusAnnotation)
    val pass = Pass(bogusAnnotation)
    val input = If(
      List((mkId("a"), pass), (mkId("b"), pass)),
      Some(pass),
      bogusAnnotation
    )
    val output = SimplifyIf(input, bogusNamesU)
    println(output)
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
