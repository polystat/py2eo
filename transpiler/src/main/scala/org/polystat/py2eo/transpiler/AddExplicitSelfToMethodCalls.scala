package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{CallIndex, Field, Ident, T}
import org.polystat.py2eo.parser.Statement.{Assign, Suite}
import org.polystat.py2eo.transpiler.GenericStatementPasses.{EAfterPass, NamesU}

object AddExplicitSelfToMethodCalls {
  // explicitly substitute the self to each method call
  // todo: does not work if a class method is returned as a function and then called
  def apply(lhs : Boolean, e : T, ns : NamesU) : (EAfterPass, NamesU) = {
    if (!lhs) {
      e match {
        case CallIndex(true, what@Field(obj@Ident(_, _), fname, fann), args, ann) =>
          (Left(CallIndex(true, what, (None, obj) :: args, ann.pos)), ns)
        case CallIndex(true, what@Field(obj, fname, fann), args, ann) =>
          val (List(objName, idName), ns1) = ns(List("obj", "id"))
          (
            Right((
              Suite(
                List(
                  Assign(List(Ident(objName, ann.pos), obj), ann.pos),
                  Assign(List(
                    Ident(idName, ann.pos),
                    CallIndex(
                      true,
                      Field(Ident(objName, ann.pos), fname, fann),
                      (None, Ident(objName, ann.pos)) :: args,
                      ann.pos
                    )
                  ), ann.pos)
                ),
                ann.pos
              ),
              Ident(idName, ann.pos)
            )),
            ns1
          )
        case CallIndex(isCall, Field(_, _, _), args, ann) => ??? // todo: must be implemented as above, but a bit more complicated
        case x : Any => (Left(x), ns)
      }
    } else {
      (Left(e), ns)
    }
  }
}
