package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.{GeneralAnnotation, Statement}
import org.polystat.py2eo.parser.Statement.{If, IfSimple, Pass}
import org.polystat.py2eo.transpiler.GenericStatementPasses.NamesU

object SimplifyIf {
  def apply(s: Statement.T, ns: NamesU): (Statement.T, NamesU) = s match {
    case If(List((cond, yes)), Some(no), ann) => (IfSimple(cond, yes, no, ann.pos), ns)
    case If(List((cond, yes)), None, ann) => (IfSimple(cond, yes, Pass(ann), ann.pos), ns)
    case If((cond, yes) :: t, eelse, ann) =>
      val (newElse, ns1) = SimplifyIf.apply(If(
        t, eelse,
        GeneralAnnotation(
          t.head._2.ann.start,
          eelse match {
            case Some(eelse) => eelse.ann.stop
            case None => t.last._2.ann.stop
          }
        )
      ), ns)
      (IfSimple(cond, yes, newElse, ann.pos), ns1)
    case _ => (s, ns)
  }
}
