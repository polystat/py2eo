package org.polystat.py2eo.checker

import org.polystat.py2eo.parser.{Expression, Parse, PrintPython, Statement}
import org.polystat.py2eo.transpiler.SimplePass

object Mutator {

  object Mutation extends Enumeration {
    type T = Value
    val nameMutation, literalMutation = Value
  }


  private def mutateLiteral(s: Statement.T, acc: Int): Statement.T = {
    def mutateLiteralHelper(acc: Int, expr: Expression.T): (Int, Expression.T) = expr match {
      case Expression.IntLiteral(value, ann) if acc == 0 => (0, Expression.IntLiteral(value + 1, ann))
      case Expression.IntLiteral(_, _) => (acc - 1, expr)
      case _ => (acc, expr)
    }

    SimplePass.simpleProcExprInStatementAcc[Int](mutateLiteralHelper)(acc, s)._2
  }


  private def mutateNames(s: Statement.T, acc: Int): Statement.T = {
    def mutateNamesHelper(acc: Int, expr: Expression.T): (Int, Expression.T) = expr match {
      case Expression.Ident(name, ann) if acc == 0 => (-1, Expression.Ident(name + "2", ann))
      case Expression.Ident(_, _) => (acc - 1, expr)
      case _ => (acc, expr)
    }

    SimplePass.simpleProcExprInStatementAcc[Int](mutateNamesHelper)(acc, s)._2
  }


  def mutate(input: String, mutation: Mutation.T, occurrenceNumber: Int): String = {
    mutation match {
      case Mutation.nameMutation => PrintPython.print(mutateNames(Parse(input), occurrenceNumber))
      case Mutation.literalMutation => PrintPython.print(mutateLiteral(Parse(input), occurrenceNumber))
      case _ => throw new IllegalArgumentException
    }
  }

}
