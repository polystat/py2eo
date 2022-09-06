package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{
  Binop, Binops, BoolLiteral, CallIndex, CollectionCons, CollectionKind, Compops, Cond,
  DictCons, Field, FloatLiteral, FreakingComparison, IntLiteral, LazyLAnd,
  LazyLOr, NoneLiteral, Parameter, SimpleComparison, StringLiteral, T, Unop, Unops,
  UnsupportedExpr
}
import org.polystat.py2eo.parser.{AugOps, Expression, GeneralAnnotation, Statement, VarScope}
import org.polystat.py2eo.parser.Statement.{
  Assign, Decorators, FuncDef, IfSimple, ImportAllSymbols, ImportModule, Pass, SimpleObject, Suite, Unsupported, While
}
import org.polystat.py2eo.transpiler.Common.{crb, orb, space}

object PrintEO {

  val Ident = "  "
  val unsupported = "unsupported"
  val decoratesSeq = "seq > @"
  type Text = List[String]

  def binop(op : Binops.T): String = op match {
    case Binops.Pow => "pow"
    case Binops.Plus => "add"
    case Binops.Minus => "sub"
    case Binops.Mul => "mul"
    case Binops.FloorDiv => "div"
    case Binops.Div => "float-div"
    case Binops.And => "and"
    case Binops.Or => "or"
    case Binops.Xor => "xor"
    case Binops.Mod => "mod"
    case Binops.Shl => "left"
    case Binops.Shr => "right"
    case Binops.And => "and"
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
    case Unops.Neg => ".bitwise-not"
    case Unops.LNot => ".not"
    case Unops.Plus => ""
  }

  def augop(t : AugOps.T) : String = t match {
    case AugOps.Plus => "aug-add"
    case AugOps.Minus => "aug-sub"
    case AugOps.Mul => "aug-mul"
    case AugOps.At => ???
    case AugOps.Div => ???
    case AugOps.Mod => "aug-mod"
    case AugOps.And => "aug-and"
    case AugOps.Or => "aug-or"
    case AugOps.Xor => "aug-xor"
    case AugOps.Shl => "aug-left"
    case AugOps.Shr => "aug-right"
    case AugOps.Pow => "aug-pow"
    case AugOps.FloorDiv => "aug-div"
  }

  def printExpr(lhs : Boolean, value : T) : String = {
    def e = printExpr _
    val toExtract = value match {
      case CollectionCons(kind, l, _)
        if kind == CollectionKind.List || kind == CollectionKind.Tuple =>
          "(wrapper (*" + l.map(x => " " + e(false, x)).mkString + crb + crb
      case CollectionCons(CollectionKind.Set, l, _) =>
        val elts = l.map(k => s" (pair ${e(false, k)} (pyint 0))").mkString("")
        (s"(wrapper (*${elts}))")
      case DictCons(l, ann) =>
        val elts = l.map{
          case Left((k, v)) => s" (pair ${e(false, k)} ${e(false, v)})"
        }.mkString("")
        (s"(wrapper (*${elts}))")
      case NoneLiteral(_) => "(pystring \"None: is there a None literal in the EO language?\")" // todo: see <<-- there
      case IntLiteral(value, _) => s"(pyint $value)"
      case FloatLiteral(value, _) => s"(pyfloat $value)"
      case StringLiteral(List(value), _) =>
        if (value == "") "(pystring \"\")" else // todo: very dubious . Value must not be an empty string
        if (value.head == '\'' && value.last == '\'') {
          "(pystring \"" + value + "\")"
        } else { s"(pystring $value)" }
      case BoolLiteral(value, _) =>
        val v = if (value) "TRUE" else "FALSE"
        s"(pybool $v)"
      //    case NoneLiteral(, _) =>
      case Binop(op, l, r, _) =>  orb + e(false, l) + "." + binop(op) + space + e(false, r) + crb
      case SimpleComparison(op, l, r, ann) if (op == Compops.Is || op == Compops.IsNot) =>
        val l1 = Field(l, "x__id__", ann.pos)
        val r1 = Field(r, "x__id__", ann.pos)
        printExpr(false, SimpleComparison(if (op == Compops.Is) Compops.Eq else Compops.Neq, l1, r1, ann.pos))
      case SimpleComparison(op, l, r, _) if op == Compops.In =>
        s"(${e(false, r)}.contains-hack ${e(false, l)})"
      case SimpleComparison(op, l, r, _) if op == Compops.NotIn =>
        s"((${e(false, r)}.contains-hack ${e(false, l)}).not)"
      case SimpleComparison(op, l, r, _) => orb + e(false, l) + "." + compop(op) + space + e(false, r) + crb
      case FreakingComparison(List(op), List(l, r), _) => orb + e(false, l) + "." + compop(op) + space + e(false, r) + crb
      case LazyLAnd(l, r, _) =>  orb + e(false, l) + ".and " + e(false, r) + crb
      case LazyLOr(l, r, _) =>  orb + e(false, l) + ".or " + e(false, r) + crb
      case Unop(op, x, _) => orb + e(false, x) + unop(op) + crb
      case Expression.Ident(name, _) => orb + (name) + crb
      case CallIndex(false, from, List((_, StringLiteral(List(fname), _))), _)
        if fname == "\"callme\"" || (from match { case Expression.Ident("closure", _) => true case _ => false}) =>
          e(false, Field(from, fname.substring(1, fname.length - 1), from.ann.pos))
      case u : UnsupportedExpr =>
        val e1 = CallIndex(true, Expression.Ident(unsupported, u.ann.pos), u.children.map(e => (None, e)), u.ann.pos)
        e(false, e1)
      case CallIndex(isCall, whom, args, _) if !isCall && args.size == 1 =>
        orb + e(false, whom) + ".get " + e(false, args(0)._2) + crb
      case Field(whose, name, _) => orb + e(false, whose) + "." + name + crb
      case Cond(cond, yes, no, _) => orb + e(false, cond) + ".as-bool.if " + e(false, yes) + space + e(false, no) + crb
      case CallIndex(true, whom, args, _)  =>
        "((" + e(false, whom) + crb + ".apply" +
          // todo: empty arg list hack
          ((args.map{case (None, ee) => " (" + e(false, ee) + crb}).mkString("")) +
        crb
    }
    if (lhs) toExtract else s"($toExtract.extract)"
  }

  def indent(l : Text): List[String] = l.map(Ident + _)

  def printSt(st : Statement.T) : Text = {
    def s(x : Statement.T) = printSt(x)

    st match {
      case SimpleObject(name, decorates, l, _) =>
        (
          "write." ::
            indent(
              name :: "[]" :: indent(
                l.map{ case (name, _) => "cage 0 > " + name } ++ (
                  "seq > initFields" :: indent(l.map{case (name, value) => s"$name.write " + printExpr(false, value)})
                  ) ++
                  decorates.toList.map(e => s"${printExpr(false, e)} > @")
              )
            )
          ) :+ s"($name.initFields)"
      case ImportModule(_, _, _) | ImportAllSymbols(_, _) => List() // todo: a quick hack
      case Pass(_) => List()
      case IfSimple(cond, yes, no, _) =>
        List(printExpr(false, cond) + ".if") ++ indent(s(yes)) ++ indent(s(no))
      // todo: a hackish printer for single integers only!
      case Assign(List(e@UnsupportedExpr(t, value)), _) => List(printExpr(false, e))
      case Assign(List(c@CallIndex(true, whom, args, _)), ann) =>
        s(Assign(List(Expression.Ident("bogusForceDataize", new GeneralAnnotation()), c), ann.pos))
      case Assign(List(Expression.Ident(lname, _), erhs), _) =>
        List((lname) + ".write " + printExpr(false, erhs))
      case Assign(List(_), _) => List(unsupported)
      case Suite(List(st), _) => s(st)
      case Suite(l, _) => List("seq") ++ indent(l.flatMap(s))
      case u : Unsupported =>
        val e1 = CallIndex(true, Expression.Ident(unsupported, new GeneralAnnotation()), u.es.map(e => (None, e._2)), u.ann.pos)
        val head = printExpr(false, e1)
        List(head) ++ indent(u.sts.flatMap(s))
      case While(cond, body, Some(Pass(_)), _) =>
        List("while.",
          Ident + printExpr(false, cond),
        ) ++ indent("[unused]" :: indent(decoratesSeq :: indent(printSt(body))))
      case FuncDef(name, args, None, None, None, body, Decorators(List()), h, false, _) =>
        val locals = h.filter(z => z._2._1 == VarScope.Local).keys
        val args1 = args.map{ case Parameter(argname, _, None, None, _) => argname }.mkString(space)
        val body1 = printSt(body)
        List(s"$name.write") ++
          indent(s"[$args1]" ::
            indent(locals.map(name => s"memory 0 > $name").toList ++ List(decoratesSeq) ++ indent(body1)))
      case u : Unsupported =>
        val e1 = CallIndex(true, Expression.Ident(unsupported, new GeneralAnnotation()), u.es.map(e => (None, e._2)), u.ann.pos)
        val head = printExpr(false, e1)
        List(head) ++ indent(u.sts.flatMap(s))

    }
  }

  val standardTestPreface = List(
    "+package org.eolang",
    "+alias org.eolang.txt.sprintf",
    "+alias org.eolang.io.stdout",
    "+alias pyint preface.pyint",
    "+junit",
    ""
  )

  def printSt(moduleName : String, st : Statement.T, hackPreface : Text) : Text = {
    hackPreface ++
    List(
      "[] > " + moduleName,
      Ident + "[args...] > unsupported",
      Ident + "[args...] > xunsupported",
      Ident + "memory 0 > bogusForceDataize",
      Ident + "memory 0 > xbogusForceDataize",
      Ident + "memory 0 > xhack",
      Ident + decoratesSeq
    ) ++
    indent(indent(printSt(st)))
  }

  def printTest(moduleName : String, st : Statement.T, hackPreface : Text) : Text =
    standardTestPreface ++ printSt(moduleName, st, hackPreface)
}
