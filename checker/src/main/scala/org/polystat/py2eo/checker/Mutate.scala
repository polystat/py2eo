package org.polystat.py2eo.checker

import org.polystat.py2eo.checker.Mutate.Mutation.Mutation
import org.polystat.py2eo.parser.{Expression, Parse, PrintPython, Statement}
import org.polystat.py2eo.transpiler.SimplePass

object Mutate {

  object Mutation extends Enumeration {
    type Mutation = Value
    val nameMutation: Mutation = Value("Name-mutation")
    val literalMutation: Mutation = Value("Literal-mutation")
    val operatorMutation: Mutation = Value("Operator-mutation")
    val reverseBoolMutation: Mutation = Value("Reverse-bool-literal")
    val breakToContinue: Mutation = Value("Break-to-Continue")
    val literalToIdentifier: Mutation = Value("False-to-false")
    val swapParam: Mutation = Value("Swap-param")
  }

  def apply(input: String, mutation: Mutation, occurrenceNumber: Int): String = {
    Parse(input) match {
      case None => input
      case Some(parsed) =>
        mutation match {
          case Mutation.nameMutation => PrintPython.print(mutateNames(parsed, occurrenceNumber))
          case Mutation.literalMutation => PrintPython.print(mutateLiteral(parsed, occurrenceNumber))
          case Mutation.operatorMutation => input.replace('+', '-')
          case Mutation.reverseBoolMutation => input.replace("true", "false")
          case Mutation.breakToContinue => input.replace("break", "continue")
          case Mutation.literalToIdentifier => input.replace("False", "false")
          case Mutation.swapParam => PrintPython.print(swapParam(parsed, occurrenceNumber))
          case _ => throw new IllegalArgumentException
        }
    }
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

  private def swapParam(s: Statement.T, acc: Int): Statement.T = {
    def swapParamHelper(acc: Int, expr: Expression.T): (Int, Expression.T) = expr match {
      case Expression.CallIndex(flag, callee, args, ann) if acc == 0 && args.length > 1 => (
        -1, Expression.CallIndex(flag, callee, args.reverse, ann)
      )
      case Expression.CallIndex(_, _, _, _) => (acc - 1, expr)
      case _ => (acc, expr)
    }

    SimplePass.simpleProcExprInStatementAcc[Int](swapParamHelper)(acc, s)._2
  }

}
