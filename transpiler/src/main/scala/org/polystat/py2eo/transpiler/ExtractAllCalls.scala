package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{
  BoolLiteral, CollectionCons, Cond, DictCons, EllipsisLiteral, Field, FloatLiteral,
  Ident, IntLiteral, LazyLAnd, LazyLOr, NoneLiteral, StringLiteral, T
}
import org.polystat.py2eo.parser.Statement.{Assign, Suite}
import org.polystat.py2eo.transpiler.GenericStatementPasses.{EAfterPass, NamesU}

object ExtractAllCalls {
  // translate an expression to something like a three register code in order to extract each function call with
  // possible side effects to a separate statement, i.e., a set of locals assignments, where op with side effects
  // may happen only in a root node of an rhs syntax tree
  // note that, say, binops and almost anything else may also be function calls, because they may be overriden
  def apply(lhs: Boolean, e: T, ns: NamesU): (EAfterPass, NamesU) = {
    if (lhs) (Left(e), ns) else {
      e match {
        case IntLiteral(_, _) | FloatLiteral(_, _) | StringLiteral(_, _) | BoolLiteral(_, _) | DictCons(_, _)
             | CollectionCons(_, _, _) | NoneLiteral(_) | LazyLAnd(_, _, _) | LazyLOr(_, _, _) | Cond(_, _, _, _)
             | EllipsisLiteral(_) | Ident(_, _) =>
          (Left(e), ns)
        case _ =>
          val (name, ns1) = ns("e")
          val id = Ident(name, e.ann.pos)
          (Right((
            Suite(
              List(
                Assign(List(id, e), e.ann.pos),
                Assign(List(Field(id, "<", e.ann.pos)), e.ann.pos)
              ),
              e.ann.pos
            ),
            id
          )), ns1)
      }
    }
  }
}
