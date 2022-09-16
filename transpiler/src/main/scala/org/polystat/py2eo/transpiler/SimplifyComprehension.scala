package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{
  CallIndex, CollectionComprehension, CollectionCons, Comprehension, DictComprehension,
  DictCons, Field, ForComprehension, Ident, IfComprehension, T
}
import org.polystat.py2eo.parser.{GeneralAnnotation, Statement}
import org.polystat.py2eo.parser.Statement.{Assign, For, IfSimple, Pass, Suite}
import org.polystat.py2eo.transpiler.GenericStatementPasses.{EAfterPass, NamesU}

object SimplifyComprehension {
  private def simplifyComprehensionList(inner : Statement.T, l : List[Comprehension], ann : GeneralAnnotation) : Statement.T = {
    l.foldRight(inner : Statement.T)((comprehension, accum) => {
      comprehension match {
        case IfComprehension(cond) =>
          IfSimple(cond, accum, Pass(ann.pos), ann.pos)
        case ForComprehension(what, in, isAsync) =>
          For(what, in, accum, None, isAsync, ann.pos)
      }
    })
  }

  def apply(lhs : Boolean, e : T, ns : NamesU) : (EAfterPass, NamesU) = {
    if (!lhs) {
      e match {
        case CollectionComprehension(kind, base, l, ann) => {
          println(s"simplify comprehension $e")
          val inner = Assign(List(CallIndex(
            true,
            Field(Ident("collectionAccum", ann.pos), "append", ann.pos),
            List((None, base)),
            ann.pos
          )),
            ann.pos
          )
          val st = Suite(
            List(
              Assign(List(Ident("collectionAccum", ann.pos), CollectionCons(kind, List(), ann.pos)), ann.pos),
              simplifyComprehensionList(inner, l, ann)
            ),
            ann.pos
          )
          (Right((st : Statement.T, Ident("collectionAccum", ann.pos))), ns)
        }
        case DictComprehension(Left((k, v)), l, ann) => {
          val inner = Assign(List(CallIndex(
            true,
            Field(Ident("collectionAccum", ann.pos), "add", ann.pos),
            List((None, k),(None, v)),
            ann.pos
          )),
            ann.pos
          )
          val st = Suite(
            List(
              Assign(List(Ident("collectionAccum", ann.pos), DictCons(List(), ann.pos)), ann.pos),
              simplifyComprehensionList(inner, l, ann)
            ),
            ann.pos
          )
          (Right((st : Statement.T, Ident("collectionAccum", ann.pos))), ns)
        }
        case x : Any => (Left(x), ns)
      }
    } else {
      (Left(e), ns)
    }
  }
}
