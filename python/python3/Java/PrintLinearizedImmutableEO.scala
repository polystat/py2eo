import Expression.{CallIndex, Cond, DictCons, Ident, StringLiteral}
import PrintEO.standardTestPreface

import scala.collection.immutable.{HashMap, HashSet}

object PrintLinearizedImmutableEO {

  import PrintEO.{EOVisibility, printExpr, Text, ident}

  def isRetIfRet(st : Statement) = st match {
    case Return(_) | IfSimple(_, Return(_), Return(_)) => true
    case _ => false
  }

  // remove all the code after return: this is needed if the previous pass generates something like return; return;
  def rmUnreachableTail(l : List[Statement]) : List[Statement] = l.foldLeft(List[Statement]())((acc, st) =>
    if (acc.nonEmpty && isRetIfRet(acc.last)) acc else acc :+ st
  )

  def printBody(visibility : EOVisibility)(st : Suite) : Text = {
    val l = rmUnreachableTail(st.l)
    l.flatMap {
      case Assign(List(CallIndex(true, Expression.Ident("print"), List((None, n))))) =>
        List(s"stdout (sprintf \"%d\\n\" ${printExpr(visibility)(n)})")
      case CreateConst(name, DictCons(l)) =>
        val visibility1 = visibility.stepInto(List())
        s"[] > $name" :: ident(l.map{ case Left((StringLiteral(name), value)) =>
          printExpr(visibility1)(value) + " > " + name.substring(1, name.length - 1) })
      case CreateConst(name, value) => List(printExpr(visibility)(value) + " > " + name)
      case one@(Return(_) | IfSimple(_, Return(_), Return(_))) =>
        val expr = one match {
          case Return(x) => x
          case IfSimple(cond, Return(yes), Return(no)) => Cond(cond, yes, no)
        }
        List(printExpr(visibility)(expr) + " > @")
      case FuncDef(name, args, None, None, body, Decorators(List()), accessibleIdents) =>
        val locals = accessibleIdents.filter(z => z._2 == VarScope.Local || z._2 == VarScope.Arg).keys
        val args1 = args.map{ case (argname, ArgKind.Positional, None) => argname }.mkString(" ")
        val st@Suite(_) = body
        val body1 = printBody(visibility.stepInto(locals.toList))(st)
        List(s"[$args1] > $name") ++ ident(body1)
      case s@Suite(l) => printBody(visibility)(s)
      case WithoutArgs(StatementsWithoutArgs.Pass) => List()
    }
  }

  def printSt(moduleName : String, body : Statement) : String = {
    val st@Suite(_) = SimpleAnalysis.computeAccessibleIdents(body)
    (
      standardTestPreface ++
        List(
          "[] > " + moduleName,
        ) ++
        ident(List(
          "[heap] > nextFreePtr",
          "  heap.length > @",
          "[heap] > append2heap",
          "  heap.append > @",
          "[heap ptr newValue] > immArrChangeValue",
          "  mapi. > @",
          "    heap",
          "    [x i]",
          "      (ptr.eq i).if (newValue) x > @",
        )) ++
        ident(printBody(new EOVisibility().stepInto(List("nextFreePtr", "append2heap", "immArrChangeValue")))(st))
      ).mkString("\n") + "\n"
  }



}
