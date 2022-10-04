package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{BoolLiteral, CallIndex, CollectionCons, CollectionKind, DictCons, Ident, T}

object AddExplicitConstructionOfCollection {
  def apply(e : T) : T = {
    e match {
      case CollectionCons(kind, l, ann)
        if kind == CollectionKind.List || kind == CollectionKind.Tuple =>
          CallIndex(
            true,
            Ident("xmyArray", e.ann.pos),
            List((None, BoolLiteral(kind == CollectionKind.List, e.ann.pos)), (None, e)),
            e.ann.pos
          )
      case DictCons(_, _) | CollectionCons(CollectionKind.Set, _, _) =>
        CallIndex(true, Ident("xmyMap", e.ann.pos), List((None, e)), e.ann.pos)
      case _ => e
    }
  }
}
