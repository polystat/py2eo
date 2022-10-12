package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{CollectionCons, Star, T}

object StarInCollectionConstructor {
  def apply(e : T) : T = e match {
    case CollectionCons(kind, l, ann) =>
      val l1 = l.flatMap({
        case Star(CollectionCons(kind1, l, _), _) => l
        case x => List(x)
      })
      CollectionCons(kind, l1, ann)
    case x => x
  }
}
