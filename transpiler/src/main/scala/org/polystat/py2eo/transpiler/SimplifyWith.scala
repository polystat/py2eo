package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{BoolLiteral, CallIndex, Field, Ident, NoneLiteral, Unop, Unops}
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.parser.Statement.{Assign, IfSimple, Pass, Raise, Suite, Try, With}
import org.polystat.py2eo.transpiler.StatementPasses.NamesU

object SimplifyWith {
  def simplifyWith(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = s match {
    case With(List((cms, target)), body, isAsync, ann) =>
      assert(!isAsync)
      val (List(manager, value, hit_except), ns1) =
        ns(List("manager", "value", "hit_except"))
      val managerId = Ident(manager, ann.pos)
      val enter = Field(managerId, "__enter__", ann.pos)
      val exit = Field(managerId, "__exit__", ann.pos)
      val code = Suite(
        List(
          Assign(List(Ident(manager, ann.pos), cms), ann.pos),
          Assign(List(
              Ident(value, ann.pos),
              CallIndex(
                true,
                enter,
                List(),
                ann.pos
              )
            ),
            ann.pos
          ),
          Assign(List(
              Ident(hit_except, ann.pos),
              BoolLiteral(false, ann.pos)
            ),
            ann.pos
          ),
          Try(
            target match {
              case Some(v) =>
                Suite(List(
                    Assign(List(v, Ident(value, ann.pos)), ann.pos),
                    body
                  ),
                  ann.pos
                )
              case None => body
            },
            List(
              (
                None,
                Suite(
                  List(
                    Assign(List(
                        Ident(hit_except, ann.pos),
                        BoolLiteral(true, ann.pos)
                      ),
                      ann.pos
                    ),
                    IfSimple(
                      Unop(
                        Unops.LNot,
                        CallIndex(
                          true,
                          exit,
                          List(
                            (None, Field(Ident("current-exception", ann.pos), "__class__", ann.pos)),
                            (None, Ident("current-exception", ann.pos)),
                            (None, NoneLiteral(ann.pos))
                          ),
                          ann.pos
                        ),
                        ann.pos
                      ),
                      Raise(None, None, ann.pos),
                      Pass(ann.pos),
                      ann.pos
                    )
                  ),
                  ann.pos
                )
              )
            ),
            None,
            Some(
              IfSimple(
                Unop(Unops.LNot, Ident(hit_except, ann.pos), ann.pos),
                Assign(
                  List(
                    CallIndex(
                      true,
                      exit,
                      List(
                        (None, NoneLiteral(ann.pos)),
                        (None, NoneLiteral(ann.pos)),
                        (None, NoneLiteral(ann.pos))
                      ),
                      ann.pos
                    )
                  ),
                  ann.pos
                ),
                Pass(ann.pos),
                ann.pos
              )
            ),
            ann.pos
          )
        ),
        ann.pos
      )
      (code, ns1)
    case _ => (s, ns)
  }
}
