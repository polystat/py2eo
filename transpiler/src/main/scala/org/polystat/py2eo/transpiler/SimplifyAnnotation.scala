package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.parser.Statement.{AnnAssign, Assign, Pass}
import org.polystat.py2eo.transpiler.GenericStatementPasses.NamesU

object SimplifyAnnotation {
  def simplify(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case AnnAssign(lhs, rhsAnn, None, ann) => (Pass(ann.pos), ns)
    case AnnAssign(lhs, rhsAnn, Some(rhs), ann) => (Assign(List(lhs, rhs), ann.pos), ns)
    case _ => (s, ns)
  }
}
