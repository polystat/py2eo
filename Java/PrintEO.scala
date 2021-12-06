import Expression._

import scala.collection.immutable.{HashMap, HashSet}

object PrintEO {

  val Ident = "  "
  type Text = List[String]

  def binop(op : Binops.T) = op match {
    case Binops.Plus => "add"
    case Binops.Minus => "sub"
    case Binops.Mul => "mul"
    case Binops.Div => "div"
    case Binops.And => "and"
    case Binops.Or => "or"
    case Binops.Mod => "mod"
  }

  def compop(t: Compops.T) = t match {
    case Compops.Eq =>  "eq"
    case Compops.Neq =>  "neq"
    case Compops.Gt =>  "greater"
    case Compops.Ge =>  "geq"
    case Compops.Lt =>  "less"
    case Compops.Le =>  "leq"
  }

  def unop(t: Unops.T) = t match {
    case Unops.Minus => "neg"
    case Unops.Neg => "neg"
    case Unops.LNot => "not"
  }

  // supports explicit access to parent fields with ^. prefixes
  class EOVisibility(builtinNames : HashSet[String], h0 : HashMap[String, String]) {
    def this() = this(HashSet(), HashMap())
    val h = h0
    // todo: this code is basically disabled, remove EOVisibility later
//    def apply(name : String) = if (builtinNames.contains(name)) name else h(name)
    def apply(name : String) = name
    def stepInto(locals : List[String]) = new EOVisibility(
      builtinNames,
      locals.foldLeft(h.map(z => (z._1, /*"^." +*/ z._2)))((acc, name) => acc.+((name, name))))
  }

  def printExpr(visibility : EOVisibility)(value : T) : String = {
    def e = printExpr(visibility)(_)
    value match {
      case CollectionCons(kind, l) => "(* " + l.map(e).mkString(" ") + ")"
      case NoneLiteral() => "\"None: is there a None literal in the EO language?\"" // todo: see <<-- there
      case IntLiteral(value) => value.toString()
      case FloatLiteral(value) => value.toString
      case StringLiteral(value0) =>
        val value = value0.replace("\"\"", "")
        if (value == "") "\"\"" else // todo: very dubious . Value must not be an empty string
        if (value.head == '\'' && value.last == '\'')
          "\"" + value + "\""
        else value
      case BoolLiteral(value) => if (value) "TRUE" else "FALSE"
      //    case NoneLiteral() =>
      case Binop(op, l, r) =>  "(" + e(l) + "." + binop(op) + " " + e(r) + ")"
      case SimpleComparison(op, l, r) => "(" + e(l) + "." + compop(op) + " " + e(r) + ")"
      case FreakingComparison(List(op), List(l, r)) => "(" + e(l) + "." + compop(op) + " " + e(r) + ")"
      case LazyLAnd(l, r) =>  "(" + e(l) + ".and " + e(r) + ")"
      case LazyLOr(l, r) =>  "(" + e(l) + ".or " + e(r) + ")"
      case Unop(op, x) => "(" + e(x) + "." + unop(op) + ")"
      case Expression.Ident(name) => "(x" + visibility(name) + ")"
      case CallIndex(false, from, List((_, StringLiteral(fname))))
        if fname == "\"callme\"" || (from match { case Expression.Ident("closure") => true case _ => false}) =>
          e(Field(from, fname.substring(1, fname.length - 1)))
      case u : UnsupportedExpr =>
        val e1 = CallIndex(true, Expression.Ident("unsupported"), u.children.map(e => (None, e)))
        e(e1)
      case CallIndex(isCall, whom, args) if !isCall && args.size == 1 =>
        "(" + e(whom) + ".get " + e(args(0)._2) + ")"
      case Field(whose, name) => "(" + e(whose) + ".x" + name + ")"
      case Cond(cond, yes, no) => "(" + e(cond) + ".if " + e(yes) + " " + e(no) + ")"
      case CallIndex(true, whom, args)  =>
        "((" + e(whom) + ")" + args.map{case (None, ee) => " (" + e(ee) + ")"}.mkString("") + ")"
    }
  }

  def ident(l : Text) = l.map(Ident + _)

  def printSt(visibility : EOVisibility)(st : Statement) : Text = {
    def s = printSt(visibility)(_)

    st match {
      case ImportModule(_, _) | ImportAllSymbols(_) => List() // todo: a quick hack
      case WithoutArgs(StatementsWithoutArgs.Pass) => List()
      case IfSimple(cond, yes, no) =>
        List(printExpr(visibility)(cond) + ".if") ++ ident(s(yes)) ++ ident(s(no))
      // todo: a hackish printer for single integers only!
      case Assign(List(CallIndex(true, Expression.Ident("print"), List((None, n))))) =>
        List(s"stdout (sprintf \"%d\\n\" ${printExpr(visibility)(n)})")
      case Assign(List(e@UnsupportedExpr(t, value))) => List(printExpr(visibility)(e))
      case Assign(List(c@CallIndex(true, whom, args))) =>
        s(Assign(List(Expression.Ident("bogusForceDataize"), c)))
      case Assign(List(Expression.Ident(lname), erhs)) =>
        List("x" + visibility(lname) + ".write " + printExpr(visibility)(erhs))
      case Assign(List(_)) => List("unsupported")
      case Suite(List(st)) => s(st)
      case Suite(l) => List("seq") ++ ident(l.flatMap(s))
      case u : Unsupported =>
        val e1 = CallIndex(true, Expression.Ident("unsupported"), u.es.map(e => (None, e._2)))
        val head = printExpr(visibility)(e1)
        List(head) ++ ident(u.sts.flatMap(s))
      case While(cond, body, WithoutArgs(StatementsWithoutArgs.Pass)) =>
        List("while.",
          Ident + printExpr(visibility)(cond),
        ) ++ ident("[unused]" :: ident("seq > @" :: ident(printSt(visibility.stepInto(List()))(body))))
      case FuncDef(name, args, None, None, body, Decorators(List()), h) =>
        val locals = h.filter(z => z._2 == VarScope.Local).keys
        val args1 = args.map{ case (argname, _, None) => "x" + argname }.mkString(" ")
        val body1 = printSt(visibility.stepInto(locals.toList))(body)
        List(s"x$name.write") ++
          ident(s"[$args1]" ::
            ident(locals.map(name => s"memory > x$name").toList ++ List("seq > @") ++ ident(body1)))
    }
  }

  val standardTestPreface = List(
    "+package org.eolang",
    "+alias org.eolang.txt.sprintf",
    "+alias org.eolang.io.stdout",
    "",
  )

  def printSt(moduleName : String, st : Statement, hackPreface : Text) : Text = {
//    val h = SimpleAnalysis.classifyFunctionVariables(List(), st, false)
//    val locals = h.filter(z => z._2 == VarScope.Local).keys
    (
      standardTestPreface ++ hackPreface ++
      List(
        "[] > " + moduleName,
        Ident + "[args...] > unsupported",
        Ident + "[args...] > xunsupported",
        Ident + "memory > bogusForceDataize",
        Ident + "memory > xbogusForceDataize",
        Ident + "memory > xhack",
        Ident + "seq > @"
      ) ++
      ident(ident(printSt(new EOVisibility().stepInto(List("bogusForceDataize")/* :: locals.toList*/))(st)))
    )
  }

}
