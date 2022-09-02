package org.polystat.py2eo.checker.mutator

import org.cqfn.astranaut.api.TreeProcessor
import org.polystat.py2eo.checker.mutator.Mutate.Mutation.Mutation
import org.polystat.py2eo.parser.{Parse, PrintPython}

object Mutate {

  object Mutation extends Enumeration {
    type Mutation = Value
    val operatorMutation: Mutation = Value("Operator-mutation")
  }

  def apply(input: String, mutation: Mutation, occurrenceNumber: Int): String = {
    Parse(input) match {
      case None => input
      case Some(parsed) => mutation match {
        case Mutation.operatorMutation =>
          val treeProcessor = new TreeProcessor
          treeProcessor.loadRulesFromString("Binop(#1, #2)<\"+\"> -> Binop(#1, #2)<\"-\">")

          val treeProcessorInput = Convert(parsed)
          val treeProcessorOutput = treeProcessor.transform(treeProcessorInput)
          PrintPython.print(Convert(treeProcessorOutput))
      }
    }
  }
}
