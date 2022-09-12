package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{CallIndex, Ident, Unop, Unops}
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.parser.Statement.{Assert, IfSimple, Pass, Raise}
import org.polystat.py2eo.transpiler.GenericStatementPasses.NamesU

object SimplifyAssert {
  def simplifyAssert(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case Assert(what, param, ann) => (
      IfSimple(
        Unop(Unops.LNot, what, ann.pos),
        Raise(
          Some(CallIndex(true, Ident("AssertionError", ann.pos), param.toList.map((None, _)), ann.pos)),
          None,
          ann.pos
        ),
        Pass(ann.pos),
        ann.pos
      ),
      ns
    )
    case _ => (s, ns)
  }
}
