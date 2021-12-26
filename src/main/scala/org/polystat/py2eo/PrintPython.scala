package org.polystat.py2eo;

import Expression._

import java.io.{File, FileWriter}


object PrintPython {

  private def printComprehension(e : Comprehension) : String =
    e match {
      case f : ForComprehension => (if (f.isAsync) "async " else "") + "for " + printExpr(f.what) + " in " + printExpr(f.in)
      case x : IfComprehension => "if " + printExpr(x.cond)
    }

  private def printDictElt(x : DictEltDoubleStar) = x match {
    case Left(value) =>   printExpr(value._1) + " : " + printExpr(value._2)
    case Right(value) =>  "**" + printExpr(value)
  }

  def printExpr(e : T) : String = {
    def brak(s : String, open : String = "(", close : String = ")") = open + s + close
    def rnd(s : String) = brak(s, "(", ")")
    def sqr(s : String) : String = brak(s, "[", "]")
    e match {
      case Await(what, _) => brak("await " + printExpr(what))
      case NoneLiteral(_) => "None"
      case EllipsisLiteral(_) => "..."
      case UnsupportedExpr(_, _) => "None"
      case IntLiteral(value, _) => value.toString(10) + " "
      case FloatLiteral(value, _) => value
      case ImagLiteral(value, _) => value + "j"
      case StringLiteral(value, _) => value
      case BoolLiteral(b, _) => if (b) "True" else "False"
      case Binop(op, l, r, _) => brak(printExpr(l) + " " + Binops.toString(op) + " " + printExpr(r))
      case LazyLOr(l, r, _) => rnd(printExpr(l) + " or " + printExpr(r))
      case LazyLAnd(l, r, _) => rnd(printExpr(l) + " and " + printExpr(r))
      case SimpleComparison(op, l, r, _) => brak(printExpr(l) + " " + Compops.toString(op) + " " + printExpr(r))
      case FreakingComparison(ops, l, _) =>
        val sops = ops.map(Compops.toString) :+ ""
        val sopnds = l.map(printExpr)
        brak(sopnds.zip(sops).flatMap(x => List(x._1, x._2)).mkString(" "))
      case Unop(op, x, _) => brak(Unops.toString(op) + printExpr(x))
      case Ident(name, _) => name
      case Star(e, _) => "*" + printExpr(e)
      case DoubleStar(e, _) => "**" + printExpr(e)
      case Slice(from, to, by, _) =>
        def procBound(b : Option[T]) = b match {
          case None => ""
          case Some(e) => printExpr(e)
        }
        s"${procBound(from)}:${procBound(to)}:${procBound(by)}"
      case CallIndex(false, whom, List((_, CollectionCons(CollectionKind.Tuple, l, _))), _) if l.nonEmpty =>
        printExpr(whom) + sqr(l.map(printExpr).mkString(", ") + (if (l.size == 1) "," else ""))
      case CallIndex(isCall, whom, args, _) => printExpr(whom) + (if (isCall) rnd _ else sqr _)(
        args.map{case (None, e) => printExpr(e)  case (Some(keyword), e) => keyword + "=" + printExpr(e)}.mkString(", "))
      case Field(whose, name, _) =>printExpr(whose) + "." + name
      case Cond(cond, yes, no, _) => printExpr(yes) + " if " + printExpr(cond) + " else " + printExpr(no)
      case AnonFun(args, otherPositional, otherKeyword, body, _) =>
        brak("lambda " + printArgs(args, otherPositional.map(x => (x, None)), otherKeyword.map(x => (x, None))) + " : " + printExpr(body))
      case CollectionCons(kind, l, _) =>
        val braks = CollectionKind.toBraks(kind)
        brak(l.map(printExpr).mkString(", ") + (if (l.size == 1) "," else ""), braks._1, braks._2)
      case CollectionComprehension(kind, base, l, _) =>
        val braks = CollectionKind.toBraks(kind)
        brak(printExpr(base) + " " + l.map(printComprehension).mkString(" "), braks._1, braks._2)
      case GeneratorComprehension(base, l, _) => printExpr(base) + " " + l.map(printComprehension).mkString(" ")
      case DictCons(l, _) => brak(l.map(printDictElt).mkString(", "), "{", "}")
      case DictComprehension(base, l, _) => brak(printDictElt(base) + " " + l.map(printComprehension).mkString(" "), "{", "}")
      case Yield(Some(e), _) => brak("yield " + printExpr(e))
      case Yield(None, _) =>  brak("yield")
      case YieldFrom(e, _) => brak("yield from " + printExpr(e))
    }
  }

  def option2string[T](x : Option[T]): String = x match {
    case Some(value) => value.toString
    case None => ""
  }

  def printSt(s : Statement, shift : String) : String = {
    def async(isAsync : Boolean) = if (isAsync) "async " else ""
    val shiftIncr = shift + "    "
    val posComment = " # " + s.ann
    def printDecorators(decorators: Decorators) =
      decorators.l.map(z => shift + "@" + printExpr(z) + "\n").mkString("")
    s match {
      case _: Unsupported => shift + "assert(false)" + posComment

      case Del(e, _) => shift + "del " + printExpr(e) + posComment

      case With(cm, target, body, isAsync, _) =>
        shift + async(isAsync) + "with " + printExpr(cm) + (target match {
          case Some(value) => " as " + printExpr(value)
          case None => ""
        }) + ":" + posComment + "\n" +
        printSt(body, shiftIncr)

      case If(conditioned, eelse, _) =>
        def oneCase(keyword : String, p : (T, Statement)) =
          shift + keyword + " (" + printExpr(p._1) + "):" + " # " + p._2.ann.toString + "\n" +
          printSt(p._2, shiftIncr)

        val iif :: elifs = conditioned
        (oneCase("if", iif) :: elifs.map(oneCase("elif", _))).mkString("\n") + "\n" + (
            shift + "else:" + " # " + eelse.ann.toString + "\n" +
            printSt(eelse, shiftIncr)
        )

      case IfSimple(cond, yes, no, ann) => printSt(If(List((cond, yes)), no, ann.pos), shift)

      case While(cond, body, eelse, _) =>
        shift + "while (" + printExpr(cond) + "):" + posComment + "\n" +
          printSt(body, shiftIncr) + "\n" +
        shift + "else:\n" +
          printSt(eelse, shiftIncr)

      case For(what, in, body, eelse, isAsync, _) =>
        shift + async(isAsync) + "for " + printExpr(what) + " in " + printExpr(in) + ":" + posComment + "\n" +
          printSt(body, shiftIncr) + "\n" +
        shift + "else:\n" +
          printSt(eelse, shiftIncr)

      case Try(ttry, excepts, eelse, ffinally, _) =>
        shift + "try:" + posComment + "\n" +
          printSt(ttry, shiftIncr) + "\n" +
        excepts.map(x =>
          shift + "except " + option2string(x._1.map(y => printExpr(y._1) + option2string(y._2.map(z => " as " + z)))) + ":\n" +
            printSt(x._2, shiftIncr)
        ).mkString("\n") + "\n" +
        (if (excepts.isEmpty) "" else
          shift + "else:\n" +
            printSt(eelse, shiftIncr) + "\n"
        ) +
        shift + "finally:\n" +
          printSt(ffinally, shiftIncr)

      case Suite(l, _) => l.map(printSt(_, shift)).mkString("\n")

      case AugAssign(op, lhs, rhs, _) => shift + printExpr(lhs) + " " + AugOps.toString(op) + " " + printExpr(rhs) + posComment
      case Assign(l, _) => shift + l.map(printExpr).mkString(" = ") + posComment
      case AnnAssign(lhs, rhsAnn, rhs, _) =>
        shift + printExpr(lhs) + " : " + printExpr(rhsAnn) + (rhs match { case None => "" case Some(e) => " = " + printExpr(e)})
      case CreateConst(name, value, ann) => printSt(Assign(List(Ident(name, value.ann.pos), value), ann.pos.pos), shift) + posComment
      case Break(_) => shift + "break" + posComment
      case Continue(_) => shift + "continue" + posComment
      case Pass(_) => shift + "pass" + posComment
      case Return(Some(x), _) =>shift + "return " + printExpr(x) + posComment
      case Return(None, _) => shift + "return " + posComment
      case Assert(l, _) => shift + "assert " + l.map(printExpr).mkString(", ") + posComment
      case Raise(Some(x), Some(from), _) => shift + "raise " + printExpr(x) + " from " + printExpr(from) + posComment
      case Raise(Some(x), None, _) => shift + "raise " + printExpr(x) + posComment
      case Raise(None, None, _) => shift + "raise" + posComment
      case NonLocal(l, _) => shift + "nonlocal " + l.mkString(", ") + posComment
      case Global(l, _) => shift + "global " + l.mkString(", ") + posComment

      case SimpleObject(name, fields, ann) =>
        shift + s"class $name:" + posComment + "\n" +
          printSt(Suite(fields.map(z => Assign(List(Ident(z._1, z._2.ann.pos), z._2), z._2.ann.pos)), ann.pos), shiftIncr)

      case ClassDef(name, bases, body, decorators, _) =>
        printDecorators(decorators) +
        shift + "class " + name + "(" +
          bases.map(x =>
            (x._1 match { case None => "" case Some(name) => name + "="}) + printExpr(x._2)
          ).mkString(", ") + "):" + posComment + "\n" +
          printSt(body, shiftIncr)
      case FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation, body, decorators, _, isAsync, _) =>
        val retAnn = returnAnnotation match { case None => "" case Some(e) => " -> " + printExpr(e) }
        printDecorators(decorators) +
        shift + async(isAsync) + "def " + name +
          "(" + printArgs(args, otherPositional, otherKeyword) + ")" + retAnn + ":" + posComment + "\n" +
        printSt(body, shiftIncr)
      case ImportModule(what, as, _) =>
        shift + s"import ${what.mkString(".")}" + (as match { case None => "" case Some(x) => s" as $x"}) + posComment
      case ImportSymbol(from, what, as, _) => shift + s"from ${from.mkString(".")} import $what as $as" + posComment
      case ImportAllSymbols(from, _) => shift + s"from ${from.mkString(".")} import *" + posComment
    }
  }

  def printArgs(args : List[Parameter], otherPositional : Option[(String, Option[T])],
                otherKeyword : Option[(String, Option[T])]) : String = {
    val positionalOnly = args.filter(_.kind == ArgKind.Positional)
    val posOrKeyword = args.filter(_.kind == ArgKind.PosOrKeyword)
    val keywordOnly = args.filter(_.kind == ArgKind.Keyword)
    assert(positionalOnly ++ posOrKeyword ++ keywordOnly == args)
    def f(pref : String, z : Option[(String, Option[T])]) : List[String] =
      z match {
        case Some((name, None)) => List(pref + name)
        case Some((name, Some(typAnn))) => List(pref + name + " : " + printExpr(typAnn))
        case None => if (pref != "*" || keywordOnly.isEmpty) List() else List(pref)
      }
    def printArg(x : Parameter) =
      x.name +
        (x.paramAnn match { case None => "" case Some(value) => " : " + printExpr(value)}) +
        (x.default match { case None => "" case Some(default) => " = " + printExpr(default)})
    val argstring = positionalOnly.map(printArg) ++
      (if (positionalOnly.isEmpty) List() else List("/")) ++
      posOrKeyword.map(printArg) ++ f("*", otherPositional) ++
      keywordOnly.map(printArg) ++ f("**", otherKeyword)
    argstring.mkString(", ")
  }

}
