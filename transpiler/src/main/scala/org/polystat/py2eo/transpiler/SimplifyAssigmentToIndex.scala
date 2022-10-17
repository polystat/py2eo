package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{CallIndex, Field}
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.parser.Statement.Assign
import org.polystat.py2eo.transpiler.GenericStatementPasses.NamesU

object SimplifyAssigmentToIndex {
  def simplify(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
      case Assign(List(CallIndex(false, whom, List(index), _), rhs), ann) =>
        (Assign(List(CallIndex(true, Field(whom, "setAtIndex", ann.pos), List(index, (None, rhs)), ann.pos)), ann.pos), ns)
      case _ => (s, ns)
  }
}
