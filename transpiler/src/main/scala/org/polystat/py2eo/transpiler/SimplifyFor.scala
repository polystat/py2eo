package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{BoolLiteral, CallIndex, Field, Ident}
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.parser.Statement.{Assign, For, Pass, Suite, Try, While}
import org.polystat.py2eo.transpiler.GenericStatementPasses.NamesU

object SimplifyFor {
  def apply(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case For(what, in, body, eelse, false, ann) =>
      val (List(it, inn), ns1) = ns(List("it", "inn"))
      (Suite(List(
        Assign(List(Ident(inn, in.ann.pos), in), in.ann.pos),
        Assign(List(
          Ident(it, in.ann.pos),
          CallIndex(true, Field(Ident(inn, in.ann.pos), "__iter__", in.ann.pos), List(), in.ann.pos)
        ), in.ann.pos),
        Try(
          While(
            BoolLiteral(true, ann.pos),
            Suite(
              List(
                Assign(
                  List(
                    what,
                    CallIndex(true, Field(Ident(it, in.ann.pos), "__next__", in.ann.pos), List(), in.ann.pos)
                  ),
                  what.ann.pos
                ),
                body
              ),
              body.ann.pos
            ),
            None,
            ann.pos
          ),
          List(
            (Some(Ident("StopIteration", ann.pos), None), eelse.getOrElse(Pass(ann.pos)))
          ),
          None,
          None,
          ann.pos
        )
      ), ann.pos), ns1)
    case _ => (s, ns)
  }
}
