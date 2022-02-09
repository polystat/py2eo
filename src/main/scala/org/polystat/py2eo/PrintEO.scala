package org.polystat.py2eo;

import org.polystat.py2eo.Expression.{
  Binop, Binops, BoolLiteral, CallIndex, CollectionCons, Compops, Cond, Field, FloatLiteral, FreakingComparison,
  IntLiteral, LazyLAnd, LazyLOr, NoneLiteral, Parameter, SimpleComparison, StringLiteral, T, Unop, Unops, UnsupportedExpr
}
import org.polystat.py2eo.Common.{crb, orb, space}

object PrintEO {

  val Ident = "  "
  val unsupported = "unsupported"
  val seq = "seq > @"
  type Text = List[String]

  def binop(op : Binops.T): String = op match {
    case Binops.Plus => "add"
    case Binops.Minus => "sub"
    case Binops.Mul => "mul"
    case Binops.Div => "div"
    case Binops.And => "and"
    case Binops.Or => "or"
    case Binops.Mod => "mod"
  }

  def compop(t: Compops.T): String = t match {
    case Compops.Eq =>  "eq"
    case Compops.Neq =>  "neq"
    case Compops.Gt =>  "greater"
    case Compops.Ge =>  "geq"
    case Compops.Lt =>  "less"
    case Compops.Le =>  "leq"
  }

  def unop(t: Unops.T): String = t match {
    case Unops.Minus => ".neg"
    case Unops.Neg => ".neg"
    case Unops.LNot => ".not"
    case Unops.Plus => ""
  }

  def printExpr(value : T) : String = {
    def e = printExpr _
    value match {
      case CollectionCons(kind, l, _) => "(* " + l.map(e).mkString(space) + crb
      case NoneLiteral(_) => "\"None: is there a None literal in the EO language?\"" // todo: see <<-- there
      case IntLiteral(value, _) => value.toString()
      case FloatLiteral(value, _) => value.toString
      case StringLiteral(List(value), _) =>
        if (value == "") "\"\"" else // todo: very dubious . Value must not be an empty string
        if (value.head == '\'' && value.last == '\'') {
          "\"" + value + "\""
        } else { value }
      case BoolLiteral(value, _) => if (value) "TRUE" else "FALSE"
      //    case NoneLiteral(, _) =>
      case Binop(op, l, r, _) =>  orb + e(l) + "." + binop(op) + space + e(r) + crb
      case SimpleComparison(op, l, r, _) => orb + e(l) + "." + compop(op) + space + e(r) + crb
      case FreakingComparison(List(op), List(l, r), _) => orb + e(l) + "." + compop(op) + space + e(r) + crb
      case LazyLAnd(l, r, _) =>  orb + e(l) + ".and " + e(r) + crb
      case LazyLOr(l, r, _) =>  orb + e(l) + ".or " + e(r) + crb
      case Unop(op, x, _) => orb + e(x) + unop(op) + crb
      case Expression.Ident(name, _) => orb + (name) + crb
      case CallIndex(false, from, List((_, StringLiteral(List(fname), _))), _)
        if fname == "\"callme\"" || (from match { case Expression.Ident("closure", _) => true case _ => false}) =>
          e(Field(from, fname.substring(1, fname.length - 1), from.ann.pos))
      case u : UnsupportedExpr =>
        val e1 = CallIndex(true, Expression.Ident(unsupported, u.ann.pos), u.children.map(e => (None, e)), u.ann.pos)
        e(e1)
      case CallIndex(isCall, whom, args, _) if !isCall && args.size == 1 =>
        orb + e(whom) + ".get " + e(args(0)._2) + crb
      case Field(whose, name, _) => orb + e(whose) + "." + name + crb
      case Cond(cond, yes, no, _) => orb + e(cond) + ".if " + e(yes) + space + e(no) + crb
      case CallIndex(true, whom, args, _)  =>
        "((" + e(whom) + crb +
          // todo: empty arg list hack
          (if (args.isEmpty) " 0" else (args.map{case (None, ee) => " (" + e(ee) + crb}.mkString(""))) +
        crb
    }
  }

  def indent(l : Text): List[String] = l.map(Ident + _)

  def printSt(st : Statement) : Text = {
    def s(x : Statement) = printSt(x)

    st match {
      case SimpleObject(name, l, _) =>
        ("write." ::
          indent(name :: "[]" :: indent(
            l.map{ case (name, _) => "cage > " + name } ++ (
              seq :: indent(l.map{case (name, value) => s"$name.write " + printExpr(value)})
              ))
          )) :+ s"($name.@)"
      case ImportModule(_, _, _) | ImportAllSymbols(_, _) => List() // todo: a quick hack
      case Pass(_) => List()
      case IfSimple(cond, yes, no, _) =>
        List(printExpr(cond) + ".if") ++ indent(s(yes)) ++ indent(s(no))
      // todo: a hackish printer for single integers only!
      case Assign(List(CallIndex(true, Expression.Ident("print", _), List((None, n)), _)), _) =>
        List("stdout (sprintf \"%d\\n\" %s)".format(printExpr(n)))
      case Assign(List(e@UnsupportedExpr(t, value)), _) => List(printExpr(e))
      case Assign(List(c@CallIndex(true, whom, args, _)), ann) =>
        s(Assign(List(Expression.Ident("bogusForceDataize", new GeneralAnnotation()), c), ann.pos))
      case Assign(List(Expression.Ident(lname, _), erhs), _) =>
        List((lname) + ".write " + printExpr(erhs))
      case Assign(List(_), _) => List(unsupported)
      case Suite(List(st), _) => s(st)
      case Suite(l, _) => List("seq") ++ indent(l.flatMap(s))
      case u : Unsupported =>
        val e1 = CallIndex(true, Expression.Ident(unsupported, new GeneralAnnotation()), u.es.map(e => (None, e._2)), u.ann.pos)
        val head = printExpr(e1)
        List(head) ++ indent(u.sts.flatMap(s))
      case While(cond, body, Some(Pass(_)), _) =>
        List("while.",
          Ident + printExpr(cond),
        ) ++ indent("[unused]" :: indent(seq :: indent(printSt(body))))
      case FuncDef(name, args, None, None, None, body, Decorators(List()), h, false, _) =>
        val locals = h.filter(z => z._2._1 == VarScope.Local).keys
        val args1 = args.map{ case Parameter(argname, _, None, None, _) => argname }.mkString(space)
        val body1 = printSt(body)
        List(s"$name.write") ++
          indent(s"[$args1]" ::
            indent(locals.map(name => s"memory > $name").toList ++ List(seq) ++ indent(body1)))
      case u : Unsupported =>
        val e1 = CallIndex(true, Expression.Ident(unsupported, new GeneralAnnotation()), u.es.map(e => (None, e._2)), u.ann.pos)
        val head = printExpr(e1)
        List(head) ++ indent(u.sts.flatMap(s))

    }
  }

  val standardTestPreface = List(
    "+package org.eolang",
    "+alias org.eolang.txt.sprintf",
    "+alias org.eolang.io.stdout",
    "+junit",
    ""
  )

  def printSt(moduleName : String, st : Statement, hackPreface : Text) : Text = {
    hackPreface ++
    List(
      "[] > " + moduleName,
      Ident + "[args...] > unsupported",
      Ident + "[args...] > xunsupported",
      Ident + "memory > bogusForceDataize",
      Ident + "memory > xbogusForceDataize",
      Ident + "memory > xhack",
      Ident + seq
    ) ++
    indent(indent(printSt(st)))
  }

  def printTest(moduleName : String, st : Statement, hackPreface : Text) : Text =
    standardTestPreface ++ printSt(moduleName, st, hackPreface)
}
