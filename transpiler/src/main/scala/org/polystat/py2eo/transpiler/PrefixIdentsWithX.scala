package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{AnonFun, Assignment, CallIndex, Field, Ident, Parameter, T}
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.parser.Statement.{
  ClassDef, CreateConst, FuncDef, Global, ImportAllSymbols, ImportModule, ImportSymbol, NonLocal, SimpleObject, Try, Unsupported
}
import org.polystat.py2eo.transpiler.GenericStatementPasses.NamesU

object PrefixIdentsWithX {
  // need this because EO only allows first letters of idents to be small
  def apply(e : T) : T = {
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

  def xPrefixInStatement(x : Statement.T, ns : NamesU) : (Statement.T, NamesU) = {
    def pref(s : String) = s"x$s"
    (
      x match {
        case CreateConst(name, value, ann) => CreateConst(pref(name), value, ann)
        case FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation, body, decorators, accessibleIdents, isAsync, ann) =>
          FuncDef(
            pref(name),
            args.map(p => Parameter(pref(p.name), p.kind, p.paramAnn, p.default, p.ann)),
            otherPositional.map(x => (pref(x._1), x._2)),
            otherKeyword.map(x => (pref(x._1), x._2)),
            returnAnnotation, body, decorators, accessibleIdents, isAsync, ann
          )
        case ClassDef(name, bases, body, decorators, ann) =>
          ClassDef(pref(name), bases.map(x => (x._1.map(pref), x._2)), body, decorators, ann)
        case SimpleObject(name, decorates, fields, ann) =>
          SimpleObject(pref(name), decorates, fields.map(x => (pref(x._1), x._2)), ann)
        case NonLocal(l, ann) => NonLocal(l.map(pref), ann)
        case Global(l, ann) => Global(l.map(pref), ann)
        case ImportModule(what, as, ann) => ImportModule(what.map(pref), as.map(pref), ann)
        case ImportAllSymbols(from, ann) => ImportAllSymbols(from.map(pref), ann)
        case ImportSymbol(from, what, as, ann) => ImportSymbol(from.map(pref), pref(what), as.map(pref), ann)
        case Try(ttry, excepts, eelse, ffinally, ann) =>
          Try(ttry, excepts.map(x => (x._1.map(x => (x._1, x._2.map(pref))), x._2)), eelse, ffinally, ann)
        case u : Unsupported =>
          new Unsupported(u.original, u.declareVars.map(pref), u.es, u.sts, u.ann)
        case _ => x
      }
      , ns
    )
  }
}
