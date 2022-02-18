package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.{Expression, Parse, Statement}

import scala.collection.immutable
import scala.collection.immutable.HashMap
import org.polystat.py2eo.transpiler.Common.TranspilerException
import org.polystat.py2eo.parser.Expression.{CallIndex, Ident}
import org.polystat.py2eo.parser.Statement.{Assert, Decorators, FuncDef, Return, Suite}

object Transpile {

  /// [debugPrinter(statement, stageName)]
  /// is used to save the code after different stages of compilation for debug purposes,
  /// it may do nothing if debugging is not needed
  def transpile(debugPrinter: (Statement.T, String) => Unit)(moduleName: String, pythonCode: String): String = {
    val y0 = SimplePass.procStatement(SimplePass.simplifyIf)(Parse(pythonCode, debugPrinter), new SimplePass.Names())
    val y1 = SimplePass.procStatement(SimplePass.xPrefixInStatement)(y0._1, y0._2)
    val y2 = SimplePass.simpleProcExprInStatement(Expression.map(SimplePass.concatStringLiteral))(y1._1, y1._2)
    val y = SimplePass.simpleProcExprInStatement(Expression.map(SimplePass.xPrefixInExpr))(y2._1, y2._2)

    try {
      val textractAllCalls = SimplePass.procExprInStatement(
        SimplePass.procExpr(SimplePass.extractAllCalls))(y._1, y._2)
      debugPrinter(textractAllCalls._1, "afterExtractAllCalls")
      val Suite(List(theFun@FuncDef(mainName, _, _, _, _, _, _, _, _, ann)), _) =
        ClosureWithCage.declassifyOnly(textractAllCalls._1, textractAllCalls._2)._1
      val hacked = Suite(List(
        theFun,
        Assert(CallIndex(isCall = true, Ident(mainName, ann.pos), List(), ann.pos), None, ann.pos)
      ), ann.pos)
      debugPrinter(hacked, "afterUseCage")
      val eoHacked = Suite(List(
        theFun,
        Return(Some(CallIndex(isCall = true, Ident(mainName, ann.pos), List(), ann.pos)), ann.pos)
      ), ann.pos)
      val eoText = PrintLinearizedMutableEOWithCage.printTest(moduleName, eoHacked)
      (eoText.init.init :+ "        result").mkString("\n")
    }
    catch {
      case _: MatchError | _: TranspilerException => {
        val unsupportedExpr = SimplePass.simpleProcExprInStatement(Expression.map(SimplePass.mkUnsupportedExpr))(y._1, y._2)
        val unsupportedSt = SimplePass.procStatement(SimplePass.mkUnsupported)(unsupportedExpr._1, unsupportedExpr._2)

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
          moduleName, hacked,
          globals.map(name => s"memory > $name").toList
        )
          .mkString("\n")

      }
    }
  }

}
