package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{BoolLiteral, Compops, Field, Ident, IntLiteral, LazyLAnd, LazyLOr, SimpleComparison}
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.parser.Statement.{Assign, If, Pass, Suite, Try}
import org.polystat.py2eo.transpiler.GenericStatementPasses.NamesU

object SimplifyExceptions {
  def simplifyExcepts(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case Try(ttry, List((None, x)), eelse, ffinally, ann) => (s, ns)
    case Try(ttry, excepts, eelse, ffinally, ann) =>
      val ex1 = excepts.map(
        x => {
          val body = x._2
          (
            LazyLOr(
              SimpleComparison(Compops.Eq,
                Field(Field(Ident("current-exception", ann.pos), "__class__", ann.pos), "__id__", ann.pos),
                x._1 match {
                  case Some((e, _)) => Field(e, "__id__", ann.pos)
                  case None => IntLiteral(1, ann.pos)
                },
                ann.pos
              ),
              LazyLAnd(
                SimpleComparison(Compops.Eq,
                  Field(Field(Ident("current-exception", ann.pos), "__class__", ann.pos), "__id__", ann.pos),
                  Field(Field(Ident("fakeclasses", ann.pos), "pyTypeClass", ann.pos), "__id__", ann.pos),
                  ann.pos
                ),
                SimpleComparison(Compops.Eq,
                  Field(Ident("current-exception", ann.pos), "__id__", ann.pos),
                  x._1 match {
                    case Some((e, _)) => Field(e, "__id__", ann.pos)
                    case None => IntLiteral(1, ann.pos)
                  },
                  ann.pos
                ),
                ann.pos
              ),
              ann.pos
            ),
            Suite(
              (x._1.toList.flatMap(x => x._2.toList.map (
                name => (Assign(List(Ident(name, ann.pos), Ident("current-exception", ann.pos)), ann.pos))
              ))) ++
              List(Assign(List(Ident("caught", ann.pos), BoolLiteral(true, ann.pos)), ann.pos), body),
              ann.pos
            )
          )
        }
      )
      val asIf = if (ex1.isEmpty) Pass(ann.pos) else If(ex1, Some(Pass(ann.pos)), ann.pos)
      (Try(ttry, List((None, asIf)), eelse, ffinally, ann.pos), ns)
    case _ => (s, ns)
  }

  // change a list of different except clauses to just one parameterless except with clauses implemented as ifelseif
  // @todo #331: exception object compatibility is not fully implemented ("or a tuple containing an item that is the class
  //  or a base class of the exception object"), see  https://docs.python.org/3/reference/compound_stmts.html#the-try-statement
  // @todo #331: also must implement named exceptions and del of those a the end of an except clause
  // @todo #331: also must rethrow an exception if it is not catched
  def preSimplifyExcepts(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case Try(ttry, List((None, x)), eelse, ffinally, ann) =>
      (
        Try(ttry, List((None, Suite(List(
          Assign(List(Ident("caught", ann.pos), BoolLiteral(true, ann.pos)), ann.pos),
          x
        ), ann.pos))), eelse, ffinally, ann),
        ns
      )
    case _ => (s, ns)
  }
}
