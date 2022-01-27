package org.polystat.py2eo

import Expression._

object PrintPython {

  private def printComprehension(e : Comprehension) : String =
    e match {
      case f : ForComprehension =>
        "%sfor %s in %s".format(if (f.isAsync) "async " else "", printExpr(f.what), printExpr(f.in))
      case x : IfComprehension => "if %s".format(printExpr(x.cond))
    }

  private def printDictElt(x : DictEltDoubleStar) = x match {
    case Left(value) =>  "%s : %s".format(printExpr(value._1), printExpr(value._2))
    case Right(value) =>  "**%s".format(printExpr(value))
  }

  def printExpr = printExprOrDecorator(false)(_)

  def printExprOrDecorator(noBracketsAround : Boolean)(e : T) : String = {
    def brak(s : String, open : String, close : String) = s"$open$s$close"
    def around(s : String) = if (noBracketsAround) s else brak(s, "(", ")")
    def rnd(s : String) = brak(s, "(", ")")
    def sqr(s : String) : String = brak(s, "[", "]")
    e match {
      case Assignment(ident, rhs, _) => around(s"$ident := ${printExpr(rhs)}")
      case Await(what, _) => around("await %s".format(printExpr(what)))
      case NoneLiteral(_) => "None"
      case EllipsisLiteral(_) => "..."
      case UnsupportedExpr(_, _) => "None"
      case IntLiteral(value, _) => s"$value "
      case FloatLiteral(value, _) => value
      case ImagLiteral(value, _) => s"${value}j"
      case StringLiteral(values, _) => values.mkString(" ")
      case BoolLiteral(b, _) => if (b) "True" else "False"
      case Binop(op, l, r, _) => around("%s %s %s".format(printExpr(l), Binops.toString(op), printExpr(r)))
      case LazyLOr(l, r, _) => around("%s or %s".format(printExpr(l), printExpr(r)))
      case LazyLAnd(l, r, _) => around("%s and %s".format(printExpr(l), printExpr(r)))
      case SimpleComparison(op, l, r, _) => around("%s %s %s".format(printExpr(l), Compops.toString(op), printExpr(r)))
      case FreakingComparison(ops, l, _) =>
        val sops = ops.map(Compops.toString) :+ ""
        val sopnds = l.map(printExpr)
        around(sopnds.zip(sops).flatMap(x => List(x._1, x._2)).mkString(" "))
      case Unop(op, x, _) => around(Unops.toString(op) + printExpr(x))
      case Ident(name, _) => name
      case Star(e, _) => "*%s".format(printExpr(e))
      case DoubleStar(e, _) => "**%s".format(printExpr(e))
      case Slice(from, to, by, _) =>
        def procBound(b : Option[T]) = b match {
          case None => ""
          case Some(e) => printExpr(e)
        }
        "%s:%s:%s".format(procBound(from), procBound(to), procBound(by))
      case CallIndex(false, whom, List((_, CollectionCons(CollectionKind.Tuple, l, _))), _) if l.nonEmpty =>
        "%s[%s%s]".format(printExpr(whom), l.map(printExpr).mkString(", "), if (l.size == 1) "," else "")
      case CallIndex(isCall, whom, args, _) => printExpr(whom) + (if (isCall) rnd _ else sqr _)(
        args.map{case (None, e) => printExpr(e)  case (Some(keyword), e) => s"$keyword=${printExpr(e)}"}.mkString(", ")
      )
      case Field(whose, name, _) => "%s.%s".format(printExpr(whose), name)
      case Cond(cond, yes, no, _) => "%s if %s else %s".format(printExpr(yes), printExpr(cond), printExpr(no))
      case AnonFun(args, otherPositional, otherKeyword, body, _) =>
         around("lambda %s : %s").format(
           printArgs(args, otherPositional.map(x => (x, None)), otherKeyword.map(x => (x, None))),
           printExpr(body)
         )
      case CollectionCons(kind, l, _) =>
        val braks = CollectionKind.toBraks(kind)
        brak("%s%s".format(l.map(printExpr).mkString(", "), if (l.size == 1) "," else ""), braks._1, braks._2)
      case CollectionComprehension(kind, base, l, _) =>
        val braks = CollectionKind.toBraks(kind)
        brak("%s %s".format(printExpr(base), l.map(printComprehension).mkString(" ")), braks._1, braks._2)
      case GeneratorComprehension(base, l, _) =>
        around("%s %s").format(printExpr(base), l.map(printComprehension).mkString(" "))
      case DictCons(l, _) => brak(l.map(printDictElt).mkString(", "), "{", "}")
      case DictComprehension(base, l, _) =>
        "{%s %s}".format(printDictElt(base), l.map(printComprehension).mkString(" "))
      case Yield(Some(e), _) => around("yield %s".format(printExpr(e)))
      case Yield(None, _) =>  around("yield")
      case YieldFrom(e, _) => around("yield from %s").format(printExpr(e))
    }
  }

  def option2string[T](x : Option[T]): String = x match {
    case Some(value) => value.toString
    case None => ""
  }

  def printSt(s : Statement, indentAmount : String) : String = {
    def async(isAsync : Boolean) = if (isAsync) "async " else ""
    val indentIncrAmount = indentAmount + "    "
    def indentPos(str : String) : String = "%s%s # %s".format(indentAmount, str, s.ann)
    def printDecorators(decorators: Decorators) =
      decorators.l.map(z => "%s@%s\n".format(indentAmount, printExprOrDecorator(true)(z))).mkString("")
    s match {
      case _: Unsupported => indentPos("assert(false)")
      case Del(e, _) => indentPos("del %s".format(printExpr(e)))
      case With(cms, body, isAsync, _) =>
        val cmsString = cms.map(
          cm => {
            cm._2 match {
              case Some(value) => "%s as %s".format(printExpr(cm._1), printExpr(value))
              case None => printExpr(cm._1)
            }
          }
        ).mkString(", ")
        "%s%swith %s: #%s\n%s".format(
          indentAmount, async(isAsync), cmsString, s.ann, printSt(body, indentIncrAmount)
        )
      case If(conditioned, eelse, _) =>
        def oneCase(keyword : String, p : (T, Statement)) = {
          "%s%s (%s): # %s \n%s".format(
            indentAmount, keyword, printExpr(p._1), p._2.ann.toString,
            printSt(p._2, indentIncrAmount)
          )
        }
        val iif :: elifs = conditioned
        val elseString = eelse match {
          case Some(eelse) => "%selse: # %s\n%s".format(indentAmount, eelse.ann.toString, printSt(eelse, indentIncrAmount))
          case None => ""
        }
        (oneCase("if", iif) :: elifs.map(oneCase("elif", _))).mkString("\n") + "\n" + elseString
      case IfSimple(cond, yes, no, ann) => printSt(If(List((cond, yes)), Some(no), ann.pos), indentAmount)
      case While(cond, body, eelse, _) =>
        "%swhile (%s): # %s\n%s\n%s".format(
          indentAmount, printExpr(cond), s.ann, printSt(body, indentIncrAmount),
          eelse match {
            case Some(eelse) => "%selse:\n%s".format(indentAmount, printSt(eelse, indentIncrAmount))
            case None => ""
          }
        )
      case For(what, in, body, eelse, isAsync, _) =>
        "%s%sfor %s in %s: # %s\n%s\n%s".format(
          indentAmount, async(isAsync), printExpr(what), printExpr(in), s.ann,
          printSt(body, indentIncrAmount),
          eelse match {
            case Some(eelse) => "%selse:\n%s".format(indentAmount, printSt(eelse, indentIncrAmount))
            case None => ""
          }
        )
      case Try(ttry, excepts, eelse, ffinally, _) =>
        val elseString = if (excepts.isEmpty) "" else {
          eelse match {
            case Some(eelse) => "%selse:\n%s\n".format(indentAmount, printSt(eelse, indentIncrAmount))
            case None => ""
          }
        }
        "%stry: # %s\n%s\n%s\n%s%s".format(
          indentAmount, s.ann,
          printSt(ttry, indentIncrAmount),
          excepts.map(x =>
            "%sexcept %s:\n%s".format(
              indentAmount,
              option2string(x._1.map(y => printExpr(y._1) + option2string(y._2.map(z => " as " + z)))),
              printSt(x._2, indentIncrAmount)
            )
          ).mkString("\n"),
          elseString,
          ffinally match {
            case Some(ffinally) => "%sfinally:\n%s".format(indentAmount, printSt(ffinally, indentIncrAmount))
            case None => ""
          }
        )
      case Suite(l, _) => l.map(printSt(_, indentAmount)).mkString("\n")
      case AugAssign(op, lhs, rhs, _) => indentPos("%s%s%s".format(printExpr(lhs), AugOps.toString(op), printExpr(rhs)))
      case Assign(l, _) => indentPos(l.map(printExpr).mkString(" = "))
      case AnnAssign(lhs, rhsAnn, rhs, _) =>
        "%s%s : %s%s".format(
          indentAmount, printExpr(lhs), printExpr(rhsAnn),
          rhs match { case None => "" case Some(e) => " = %s".format(printExpr(e))}
        )
      case CreateConst(name, value, ann) =>
        printSt(Assign(List(Ident(name, value.ann.pos), value), ann.pos.pos), indentAmount)
      case Break(_) => indentPos("break")
      case Continue(_) => indentPos("continue")
      case Pass(_) => indentPos("pass")
      case Return(Some(x), _) => indentPos("return %s".format(printExpr(x)))
      case Return(None, _) => indentPos("return ")
      case Assert(what, param, _) => indentPos("assert %s".format((what :: param.toList).map(printExpr).mkString(", ")))
      case Raise(Some(x), Some(from), _) => indentPos("raise %s from %s".format(printExpr(x), printExpr(from)))
      case Raise(Some(x), None, _) => indentPos("raise %s".format(printExpr(x)))
      case Raise(None, None, _) => indentPos("raise")
      case NonLocal(l, _) => indentPos("nonlocal %s".format(l.mkString(", ")))
      case Global(l, _) => indentPos("global %s".format(l.mkString(", ")))
      case SimpleObject(name, fields, ann) =>
        "%sclass %s: # %s\n%s".format(
          indentAmount, name, s.ann,
            printSt(
              Suite(fields.map(z => Assign(List(Ident(z._1, z._2.ann.pos), z._2), z._2.ann.pos)), ann.pos),
              indentIncrAmount
            )
        )
      case ClassDef(name, bases, body, decorators, _) =>
        "%s%sclass %s(%s): # %s\n%s".format(
          printDecorators(decorators),
          indentAmount, name,
          bases.map(
            x => {
              val r = printExprOrDecorator(true)(x._2)
              x._1 match {
                case None => r
                case Some(name) => s"$name=$r"
              }
            }
          ).mkString(", "), s.ann,
          printSt(body, indentIncrAmount)
        )
      case FuncDef(name, args, otherPositional, otherKeyword, returnAnnotation, body, decorators, _, isAsync, _) =>
        val retAnn = returnAnnotation match { case None => "" case Some(e) => " -> " + printExpr(e) }
        "%s%s%sdef %s(%s)%s: # %s\n%s".format(
          printDecorators(decorators),
          indentAmount, async(isAsync), name,
          printArgs(args, otherPositional, otherKeyword), retAnn, s.ann,
          printSt(body, indentIncrAmount)
        )
      case ImportModule(what, as, _) =>
        indentPos("import %s%s".format(what.mkString("."), as match { case None => "" case Some(x) => s" as $x"}))
      case ImportSymbol(from, what, as0, _) =>
        val as = as0 match { case None => "" case Some(s) => s" as $s" }
        indentPos(s"from ${from.mkString(".")} import $what$as")
      case ImportAllSymbols(from, _) => indentPos(s"from ${from.mkString(".")} import *")
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
