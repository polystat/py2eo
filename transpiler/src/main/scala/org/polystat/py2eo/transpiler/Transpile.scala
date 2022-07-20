package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.{Expression, Parse, PrintPython, Statement}

import scala.collection.immutable
import scala.collection.immutable.HashMap
import org.polystat.py2eo.parser.Expression.{CallIndex, Ident}
import org.polystat.py2eo.parser.Statement.{Assert, Assign, Decorators, FuncDef, Return, Suite}

object Transpile {

  case class Parameters(wrapInAFunction : Boolean)

  def apply(moduleName: String, pythonCode: String): Option[String] = {
    apply(moduleName, Transpile.Parameters(wrapInAFunction = true), pythonCode)
  }

  def apply(moduleName: String, opt : Parameters, pythonCode: String): Option[String] = {
    transpileOption((_, _) => ())(moduleName, opt, pythonCode)
  }

  def transpile(debugPrinter: (Statement.T, String) => Unit)(moduleName: String, opt : Parameters, pythonCode: String): String = {
    transpileOption(debugPrinter)(moduleName, opt, pythonCode).getOrElse("\"Not Supported: input file syntax is not python 3.8\" > error")
  }

  def applyStyle(pythonCode: String): Option[String] = {
    Parse(pythonCode).map(PrintPython.print).flatMap(Parse.apply).map(PrintPython.print)
  }

  /// [debugPrinter(statement, stageName)]
  /// is used to save the code after different stages of compilation for debug purposes,
  /// it may do nothing if debugging is not needed
  def transpileOption(debugPrinter: (Statement.T, String) => Unit)(moduleName: String, opt : Parameters, pythonCode: String): Option[String] = {
    val parsed = Parse(pythonCode, debugPrinter)
    parsed.map(
      parsed => {
        val ym1 = if (opt.wrapInAFunction) {
          val ann = parsed.ann
          Suite(List(
              FuncDef(
                "wrapper", List(), None, None, None,
                Suite(List(parsed, Return(Some(Expression.BoolLiteral(true, ann.pos)), ann.pos)), ann.pos),
                Decorators(List()), HashMap(), false, ann.pos
              )
            ),
            ann.pos
          )
        } else
          { parsed }
        val y0 = StatementPasses.procStatement(StatementPasses.simplifyIf)(ym1, new StatementPasses.Names())
        val y00 = StatementPasses.procExprInStatement((ExpressionPasses.simplifyCollectionComprehension))(y0._1, y0._2)
        debugPrinter(y00._1, "afterSimplifyCollectionComprehension")
        val y01 = StatementPasses.procStatement(StatementPasses.simplifyFor)(y00._1, y00._2)
        debugPrinter(y01._1, "afterSimplifyFor")
        val y1 = StatementPasses.procStatement(StatementPasses.xPrefixInStatement)(y01._1, y01._2)
        val y2 = StatementPasses.simpleProcExprInStatement(Expression.map(ExpressionPasses.concatStringLiteral))(y1._1, y1._2)
        val y = StatementPasses.simpleProcExprInStatement(Expression.map(
          x => ExpressionPasses.addExplicitConstructorOfCollection(ExpressionPasses.xPrefixInExpr(x))
        ))(y2._1, y2._2)

        try {
          val rmExcepts = StatementPasses.procStatement(StatementPasses.simplifyExcepts)(y._1, y._2)
          debugPrinter(rmExcepts._1, "afterRmExcepts")
          val simIf = StatementPasses.procStatement(StatementPasses.simplifyIf)(rmExcepts._1, rmExcepts._2)
          debugPrinter(simIf._1, "simplifyIf")
          val simAssList = StatementPasses.procStatement(StatementPasses.simplifyAssignmentList)(simIf._1, simIf._2)
          debugPrinter(simAssList._1, "simplifyAssList")
          val methodCall = StatementPasses.procExprInStatement((ExpressionPasses.simpleSyntacticMethodCall))(simAssList._1, simAssList._2)
          debugPrinter(methodCall._1, "methodCall")
          val textractAllCalls = StatementPasses.procExprInStatement((ExpressionPasses.extractAllCalls))(methodCall._1, methodCall._2)
          debugPrinter(textractAllCalls._1, "afterExtractAllCalls")
          val Suite(List(theFun@FuncDef(mainName, _, _, _, _, _, _, _, _, ann)), _) = textractAllCalls._1
          val hacked = Suite(List(
            theFun,
            Assign(List(Ident("assertMe", ann.pos), CallIndex(isCall = true, Ident(mainName, ann.pos), List(), ann.pos)), ann.pos),
            Assert(Ident("assertMe", ann.pos), None, ann.pos)
          ), ann.pos)
          debugPrinter(hacked, "afterUseCage")
          val eoHacked = Suite(List(
            theFun,
            Assign(List(Ident("assertMe", ann.pos), CallIndex(isCall = true, Ident(mainName, ann.pos), List(), ann.pos)), ann.pos),
            Return(Some(Ident("assertMe", ann.pos)), ann.pos)
          ), ann.pos)
          val eoText = PrintLinearizedMutableEOWithCage.printTest(moduleName, eoHacked)
          (eoText.init :+ "  (goto (apply.@)).result > @").mkString("\n")
        }
        catch {
          case e: Throwable => {
    //        println(s"Cannot generate executable EO for this python, so generating a EO with the Unsupported object: $e")
    //        throw e
            val unsupportedExpr = StatementPasses.simpleProcExprInStatement(Expression.map(ExpressionPasses.mkUnsupportedExpr))(y._1, y._2)
            val unsupportedSt = StatementPasses.procStatement(StatementPasses.mkUnsupported)(unsupportedExpr._1, unsupportedExpr._2)

            val hacked = SimpleAnalysis.computeAccessibleIdents(
              FuncDef(
                "xhack", List(), None, None, None, unsupportedSt._1, Decorators(List()),
                HashMap(), isAsync = false, unsupportedSt._1.ann.pos
              )
            )
            debugPrinter(hacked, "afterMkUnsupported")

            def findGlobals(l: Set[String], f: FuncDef): Set[String] = {
              SimpleAnalysis.foldSE[Set[String]](
                (l, e) => {
                  e match {
                    //            case Ident("ValueError") => println(f.accessibleIdents("ValueError")); l
                    case Ident(name, _) if !f.accessibleIdents.contains(name) => l.+(name)
                    case _ => l
                  }
                },
                { case _: FuncDef => false case _ => true }
              )(l, f.body)
            }

            val globals = SimpleAnalysis.foldSS[Set[String]]((l, st) => {
              (
                st match {
                  case f: FuncDef => findGlobals(l, f)
                  case _ => l
                }, true
              )
            })(immutable.HashSet(), hacked)

            PrintEO.printSt(
              ("y" + moduleName).replaceAll("[^0-9a-zA-Z]", ""), hacked,
              "+package org.eolang" ::
              "+alias pyint preface.pyint" ::
              "+alias pyfloat preface.pyfloat" ::
              "+alias pystring preface.pystring" ::
              "+alias pybool preface.pybool" ::
              "+junit" ::

              "pyint 0 > dummy-int-usage" ::
              "pystring 0 > dummy-string-usage" ::
              "pyfloat 0 > dummy-float-usage" ::
              "pybool TRUE > dummy-bool-usage" ::
              globals.map(name => s"memory 0 > $name").toList
            )
              .mkString("\n")

          }
        }
      }
    )
  }

}
