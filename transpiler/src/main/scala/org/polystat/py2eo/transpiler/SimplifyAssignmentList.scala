package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.Ident
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.parser.Statement.{Assign, Suite}
import org.polystat.py2eo.transpiler.StatementPasses.NamesU

object SimplifyAssignmentList {
  def simplifyAssignmentList(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case Assign(l, ann) if l.size > 2 =>
      val lhs = l.init
      val rhs = l.last
      val (rhsName, ns1) = ns("rhs")
      val lhs1 = lhs.map(x => Assign(List(x, Ident(rhsName, ann.pos)), ann.pos))
      (Suite(Assign(List(Ident(rhsName, ann.pos), rhs), ann.pos) :: lhs1, ann.pos), ns1)
    case _ => (s, ns)
  }
}
