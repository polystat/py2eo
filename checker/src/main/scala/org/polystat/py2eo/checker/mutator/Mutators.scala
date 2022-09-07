package org.polystat.py2eo.checker.mutator

import org.cqfn.astranaut.api.TreeProcessor
import org.polystat.py2eo.parser.Statement

/** Contains various tree mutators */
private object Mutators {

  /**
   * Tree mutator trait.
   * The extending object must specify rules for the tree processor
   *
   * @param rules DSL rules of a transformation
   */
  sealed abstract class Mutator(rules: String) {

    /** Applies this mutator to the given tree */
    def apply(tree: Statement.T): Statement.T = {
      val input = Convert(tree)
      val output = treeProcessor.transform(input)

      Convert(output)
    }

    /** Underlying [[TreeProcessor]] fot the tree transformations */
    private lazy val treeProcessor: TreeProcessor = treeProcessor(rules)

    /** Returns the [[TreeProcessor]] for the given rules */
    private def treeProcessor(rules: String): TreeProcessor = {
      val processor = new TreeProcessor
      processor.loadRulesFromString(rules)

      processor
    }
  }

  final object OperatorMutator extends Mutator("Binop(#1, #2)<\"+\"> -> Binop(#1, #2)<\"-\">")
}
