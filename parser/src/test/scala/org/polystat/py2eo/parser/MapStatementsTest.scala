package org.polystat.py2eo.parser

import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.polystat.py2eo.parser.Expression.Ident
import org.polystat.py2eo.parser.Statement.{Assign, Suite}

class MapStatementsTest {

  @Test def simpleAssign(): Unit = {
    Parse("a = b") match {
      case Some(Suite(List(Suite(List(
          Assign(List(Ident("a", _), Ident("b", _)), _)
        ), _)), _)) => ()
      case x : Any =>
        println(x)
        fail()
    }
  }

  @Test def longAssign(): Unit = {
    Parse("a = b = c") match {
      case Some(Suite(List(Suite(List(
          Assign(List(Ident("a", _), Ident("b", _), Ident("c", _)), _)
        ), _)), _)) => ()
      case _ : Any => fail()
    }
  }

}
