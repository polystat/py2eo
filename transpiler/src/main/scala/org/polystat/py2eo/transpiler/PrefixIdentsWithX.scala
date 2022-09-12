package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{AnonFun, Assignment, CallIndex, Field, Ident, Parameter, T}

object PrefixIdentsWithX {
  // need this because EO only allows first letters of idents to be small
  def xPrefixInExpr(e : T) : T = {
    def pref(s : String) = s"x$s"
    e match {
      case Assignment(ident, rhs, ann) => Assignment(pref(ident), rhs, ann)
      case Ident(name, ann) => (Ident(pref(name), ann))
      case Field(o, fname, ann) => Field(o, pref(fname), ann)
      case CallIndex(isCall, whom, args, ann) =>
        CallIndex(isCall, whom, args.map(x => (x._1.map(pref), x._2)), ann)
      case AnonFun(args, otherPositional, otherKeyword, body, ann) =>
        AnonFun(
          args.map(p => Parameter(pref(p.name), p.kind, p.paramAnn, p.default, p.ann)),
          otherPositional.map(pref),
          otherKeyword.map(pref),
          body,
          ann
        )
      case _ => e
    }
  }
}
