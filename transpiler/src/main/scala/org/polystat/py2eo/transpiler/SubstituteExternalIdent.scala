package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{Field, Ident}
import org.polystat.py2eo.parser.Statement
import org.polystat.py2eo.parser.Statement.{ImportModule, Pass}
import org.polystat.py2eo.transpiler.GenericExpressionPasses.procExpr
import org.polystat.py2eo.transpiler.GenericStatementPasses.{NamesU, simpleProcExprInStatementAcc, simpleProcStatement}

import scala.collection.immutable.HashMap

object SubstituteExternalIdent {
  def apply(s : Statement.T, ns : NamesU) : (Statement.T, NamesU) = {
    val externalIdents = AnalysisSupport.foldSS[HashMap[String, String]]({
      case (acc, ImportModule(what, as, ann)) =>
        val alias = as.getOrElse(what.last)
        (acc.+((alias, (what).mkString(".x") + ".ap")), true)
      case (acc, _) => (acc, true)
    })(HashMap[String, String](), s)
    println(s"externalIdents = $externalIdents")
    val s1 = simpleProcExprInStatementAcc[NamesU]((acc, e) => {
      val (Left(e1), acc1) = procExpr[Unit]({
        case (false, Field(Ident(moduleName, ann), name, acci), acc)
          if (externalIdents.contains(moduleName)) =>
            (Left(Ident(externalIdents(moduleName) + ".x" + name, ann)), acc)
        case (_, e, acc) => (Left(e), acc)
      }
      )(false, e, acc)
      (acc1, e1)
    })(ns, s)
    (s1._2, s1._1)
  }
}
