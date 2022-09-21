package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{CollectionCons, Ident, Parameter}
import org.polystat.py2eo.parser.{GeneralAnnotation, Statement, VarScope}
import org.polystat.py2eo.parser.Statement.{
  AnnAssign, Assign, ClassDef, CreateConst, FuncDef, Global, NonLocal, SimpleObject, Try, Unsupported
}
import org.polystat.py2eo.transpiler.Common.ASTAnalysisException
import org.polystat.py2eo.transpiler.GenericStatementPasses.NamesU
import AnalysisSupport.foldSS

import scala.collection.immutable.HashMap

object ComputeAccessibleIdents {
  private def classifyVariablesAssignedInFunctionBody(args : List[Parameter], body : Statement.T)
  : HashMap[String, (VarScope.T, GeneralAnnotation)] = {
    type H = HashMap[String, (VarScope.T, GeneralAnnotation)]
    def dontVisitOtherBlocks(s : Statement.T) : Boolean = s match {
      case FuncDef(_, _, _, _, _, _, _, _, _, _) | ClassDef(_, _, _, _, _) => false
      case _ => true
    }
    val allNonLocalAndGlobalNames = foldSS[H](
      (h, st) => st match {
        case NonLocal(l, ann) => (l.foldLeft(h)((h, name) => h.+((name, (VarScope.NonLocal, ann.pos)))), false)
        case Global(l, ann)   => (l.foldLeft(h)((h, name) => h.+((name, (VarScope.Global, ann.pos)))), false)
        case Try(ttry, excepts, eelse, ffinally, ann) => (
          excepts.foldLeft(h){
            case (h, (Some((_, Some(name))), _)) => h.+((name, (VarScope.ExceptName, ann.pos)))
            case (h, _) => h
          },
          true
        )
        case _                => (h, dontVisitOtherBlocks(st))
      }
    )(HashMap[String, (VarScope.T, GeneralAnnotation)](), body)
    val allNonLocalAndGlobalAndLocalNames = foldSS[H](
      (h, st) => {
        def add0(h : H, name : String, ann : GeneralAnnotation) : H =
          if (h.contains(name)) h else h.+((name, (VarScope.Local, ann.pos)))
        def add(name : String, ann : GeneralAnnotation) = add0(h, name, ann)
        st match {
          case ClassDef(name, _, _, _, ann) => (add(name, ann), false)
          case SimpleObject(name, _, _, ann) => (add(name, ann), false)
          case FuncDef(name, _, _, _, _, _, _, _, _, ann)  => (add(name, ann), false)
          case Assign(List(CollectionCons(_, _, _), _), _) =>
            throw new ASTAnalysisException("run this analysis after all assignment simplification passes!")
          case Assign(l, _) if l.size > 2 =>
            throw new ASTAnalysisException("run this analysis after all assignment simplification passes!")
          case Assign(List(Ident(name, _), _), ann) => (add(name, ann), true)
          case AnnAssign(Ident(name, _), _, _, ann) => (add(name, ann), true)
          case u : Unsupported => (u.declareVars.foldLeft(h)(add0(_, _, u.ann)), true)
          case CreateConst(name, _, ann) => (add(name, ann), true)
          case _ => (h, true)
        }
      }
    )(allNonLocalAndGlobalNames, body)
    args.foldLeft(allNonLocalAndGlobalAndLocalNames)((h, name) => h.+((name.name, (VarScope.Arg, name.ann))))
  }

  private def computeAccessibleIdentsF(upperVars : HashMap[String, (VarScope.T, GeneralAnnotation)], f : FuncDef) : FuncDef = {
    val v = ComputeAccessibleIdents.classifyVariablesAssignedInFunctionBody(f.args, f.body)
    val vUpper = upperVars.map(
      x => if (x._2._1 == VarScope.Local || x._2._1 == VarScope.Arg) (x._1, (VarScope.ImplicitNonLocal, x._2._2)) else x
    )
    val merged = v.foldLeft(vUpper)((acc, z) => acc.+(z))
    val (body, _) = GenericStatementPasses.procStatementGeneral[NamesU](
      (s, ns) => s match {
        case f : FuncDef => (ComputeAccessibleIdents.computeAccessibleIdentsF(merged, f), ns, false)
        case _ => (s, ns, true)
      }
    )(f.body, new GenericStatementPasses.Names())
    FuncDef(
      f.name, f.args, f.otherPositional, f.otherKeyword, f.returnAnnotation,
      body, f.decorators, merged, f.isAsync, f.ann.pos
    )
  }

  def computeAccessibleIdents(s : Statement.T) : Statement.T = {
    GenericStatementPasses.procStatementGeneral[NamesU](
      (s, ns) => s match {
        case f : FuncDef => (ComputeAccessibleIdents.computeAccessibleIdentsF(HashMap(), f), ns, false)
        case _ => (s, ns, true)
      }
    )(s, new GenericStatementPasses.Names())._1
  }
}
