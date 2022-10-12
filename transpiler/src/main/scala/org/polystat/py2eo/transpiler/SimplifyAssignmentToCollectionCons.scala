package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{CallIndex, CollectionCons, Field, Ident}
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.parser.Statement.{Assign, Suite}
import org.polystat.py2eo.transpiler.GenericStatementPasses.NamesU

object SimplifyAssignmentToCollectionCons {
  def apply(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case Assign(List(CollectionCons(_, lhs, _), rhs), ann) =>
      val (iter, ns1) = ns("it")
      val iterID = Ident(iter, ann.pos)
      val extractIter = Assign(List(
        iterID,
        CallIndex(true, Field(rhs, "__iter__", ann.pos), List(), ann.pos)
      ), ann.pos)
      val assignments = lhs.map(lhs =>
        Assign(List(lhs, CallIndex(true, Field(iterID, "__next__", ann.pos), List(), ann.pos)), ann.pos)
      )
      (
        Suite((extractIter :: assignments), ann.pos),
        ns1
      )
    case _ => (s, ns)
  }
}
