import Expression.{CallIndex, Cond, DictCons, Ident, StringLiteral}
import PrintEO.standardTestPreface

import scala.collection.immutable.{HashMap, HashSet}

object PrintLinearizedImmutableEO {

  import PrintEO.{EOVisibility, printExpr, Text, indent}

  private def isRetIfRet(st : Statement) = st match {
    case Return(_, _) | IfSimple(_, Return(_, _), Return(_, _), _) => true
    case _ => false
  }

  // remove all the code after return: this is needed if the previous pass generates something like return; return;
  def rmUnreachableTail(l : List[Statement]) : List[Statement] = l.foldLeft(List[Statement]())((acc, st) =>
    if (acc.nonEmpty && isRetIfRet(acc.last)) acc else acc :+ st
  )

  def printBody(currentFunName : String, visibility : EOVisibility)(st : Suite) : Text = {
    val l = rmUnreachableTail(st.l)
    l.flatMap {
      case Assign(List(CallIndex(true, Expression.Ident("print", _), List((None, n)), _)), _) =>
        List(s"stdout (sprintf \"%d\\n\" ${printExpr(visibility)(n)})")
      case CreateConst(name, DictCons(l, _), ann) =>
        val visibility1 = visibility.stepInto(List())
        s"[] > $name!" :: indent(l.map{ case Left((StringLiteral(name, ann.pos), value)) =>
          printExpr(visibility1)(value) + " > " + name.substring(1, name.length - 1) })
      case CreateConst(name, value, _) => List(printExpr(visibility)(value) + " > " + name + "!")
      case one@(Return(_, _) | IfSimple(_, Return(_, _), Return(_, _), _)) =>
        val expr = one match {
          case Return(Some(x), _) => x
          case IfSimple(cond, Return(Some(yes), _), Return(Some(no), _), ann) => Cond(cond, yes, no, ann.pos)
        }
        /*
        val printers = List(
          s"debugMagic.printDataized \"In $currentFunName .i = \" (newClosure4.i)",
          s"debugMagic.printDataized \"In $currentFunName iPtr = \" iPtr"
        )
        val all = List(
          "debugMagic",
          "seq"
        ) ++ ident(printers) :+
          printExpr(visibility)(expr) + " > @"
        "(heap.get ((closure).bb_body0)) > newClosure4" :: "seq. > @" :: ident(all)*/
        List(
//          s"debugMagic.seq (debugMagic.printDataized \"leaving fun\" \"$currentFunName\") (" +
            "(" + printExpr(visibility)(expr) +
            ") > @!")
      case FuncDef(name, args, None, None, None, body, Decorators(List()), accessibleIdents, false, _) =>
        val locals = accessibleIdents.filter(z => z._2._1 == VarScope.Local || z._2._1 == VarScope.Arg).keys
        val args1 = args.map{ case Expression.Parameter(argname, ArgKind.Positional, None, None, _) => argname }.mkString(" ")
        val st@Suite(_, _) = body
        val body1 = printBody(name, visibility.stepInto(locals.toList))(st)
        List(s"[$args1] > $name") ++ indent(body1)
      case s@Suite(_, _) => printBody(currentFunName, visibility)(s)
      case Pass(_) => List()
    }
  }

  def printSt(moduleName : String, body : Statement) : String = {
    val st@Suite(_, _) = SimpleAnalysis.computeAccessibleIdents(body)
    (
      standardTestPreface ++
        List(
          "[] > " + moduleName,
        ) ++
        indent(List(
          "[heap] > nextFreePtr",
          "  heap.length > @",
          "[heap newValue] > append2heap",
          "  heap.append newValue > @",
          "[heap ptr newValue] > immArrChangeValue",
          "  mapi. > @!",
          "    heap",
          "    [x i]",
          "      (ptr.eq i).if (newValue) x > @!",
        )) ++
        indent(printBody("top level", new EOVisibility().stepInto(List("nextFreePtr", "append2heap", "immArrChangeValue")))(st))
      ).mkString("\n") + "\n"
  }



}
