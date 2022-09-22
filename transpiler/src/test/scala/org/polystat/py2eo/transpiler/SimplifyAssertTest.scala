package org.polystat.py2eo.transpiler

import org.junit.Assert.assertTrue
import org.junit.Test
import org.polystat.py2eo.parser.Expression.{CallIndex, Ident, Unop, Unops}
import org.polystat.py2eo.parser.Statement.{Assert, IfSimple, Pass, Raise}
import org.polystat.py2eo.transpiler.Counter.{bogusAnnotation, bogusNamesU}

class SimplifyAssertTest {

  @Test def noParam() = {
    val input = Assert(
      Ident("some name here", bogusAnnotation),
      None,
      bogusAnnotation
    )
    val output = SimplifyAssert.apply(input, bogusNamesU)
    assertTrue(
      output._1 ==
        IfSimple(
          Unop(
            Unops.LNot,
            Ident("some name here", bogusAnnotation),
            bogusAnnotation
          ),
          Raise(
            Some(
              CallIndex(
                true,
                Ident("AssertionError", bogusAnnotation),
                List(),
                bogusAnnotation
              )
            ),
            None,
            bogusAnnotation
          ),
          Pass(bogusAnnotation),
          bogusAnnotation
        )
    )
  }

  @Test def someParam() = {
    val input = Assert(
      Ident("some name here", bogusAnnotation),
      Some(Ident("other name here", bogusAnnotation)),
      bogusAnnotation
    )
    val output = SimplifyAssert.apply(input, bogusNamesU)
    assertTrue(
      output._1 ==
        IfSimple(
          Unop(
            Unops.LNot,
            Ident("some name here", bogusAnnotation),
            bogusAnnotation
          ),
          Raise(
            Some(
              CallIndex(
                true,
                Ident("AssertionError", bogusAnnotation),
                List(
                  (None, Ident("other name here", bogusAnnotation))
                ),
                bogusAnnotation
              )
            ),
            None,
            bogusAnnotation
          ),
          Pass(bogusAnnotation),
          bogusAnnotation
        )
    )
  }

}
