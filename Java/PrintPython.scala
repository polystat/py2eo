import Expression._

import java.io.{File, FileWriter}


object PrintPython {

  def printComprehension(e : Comprehension) : String =
    e match {
      case f : ForComprehension =>  "for " + printExpr(f.what) + " in " + printExpr(f.in)
      case x : IfComprehension => "if " + printExpr(x.cond)
    }

  def printDictElt(x : DictEltDoubleStar) = x match {
    case Left(value) =>   printExpr(value._1) + " : " + printExpr(value._2)
    case Right(value) =>  "**" + printExpr(value)
  }

  def printExpr(e : T) : String = {
    def brak(s : String, open : String = "(", close : String = ")") = open + s + close
    def rnd(s : String) = brak(s, "(", ")")
    def sqr(s : String) : String = brak(s, "[", "]")
    e match {
      case NoneLiteral(_) => "None"
      case UnsupportedExpr(t, value) => "None"
      case IntLiteral(value, _) => value.toString(10)
      case FloatLiteral(value, _) => value.toString
      case ImagLiteral(value, _) => value.toString + "j"
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
      case Unop(op, x, _) => Unops.toString(op) + printExpr(x)
      case Ident(name, _) => name
      case Star(e, _) => "*" + printExpr(e)
      case DoubleStar(e, _) => "**" + printExpr(e)
      case Slice(from, to, by, _) =>
        val from1 = from match {
          case Some(value) => printExpr(value)
          case None => "0"
        }
        val to1 = to match {
          case Some(value) => printExpr(value)
          case None => s"($from1) + 1"
        }
        val by1 = by match {
          case Some(value) => printExpr(value)
          case None => "1"
        }
        s"slice($from1, $to1, $by1)"
      case CallIndex(isCall, whom, args, _) => printExpr(whom) + (if (isCall) rnd _ else sqr _)(
        args.map{case (None, e) => printExpr(e)  case (Some(keyword), e) => keyword + "=" + printExpr(e)}.mkString(", "))
      case Field(whose, name, _) =>printExpr(whose) + "." + name
      case Cond(cond, yes, no, _) => printExpr(yes) + " if " + printExpr(cond) + " else " + printExpr(no)
      case AnonFun(args, body, _) => "lambda " + args.mkString(", ") + " : " + printExpr(body)
      case CollectionCons(kind, l, _) =>
        val braks = CollectionKind.toBraks(kind)
        brak(l.map(printExpr).mkString(", ") + (if (l.size == 1) "," else ""), braks._1, braks._2)
      case CollectionComprehension(kind, base, l, _) =>
        val braks = CollectionKind.toBraks(kind)
        brak(printExpr(base) + " " + l.map(printComprehension).mkString(" "), braks._1, braks._2)
      case DictCons(l, _) => brak(l.map(printDictElt).mkString(", "), "{", "}")
      case DictComprehension(base, l, _) => brak(printDictElt(base) + " " + l.map(printComprehension).mkString(" "), "{", "}")
    }
  }

  def option2string[T](x : Option[T]) = x match {
    case Some(value) => value.toString
    case None => ""
  }

  def printSt(s : Statement, shift : String) : String = {
//    def mksh(shift : Int) = " " * shift
    val shiftIncr = shift + "    "
    val posComment = " # " + s.ann
    def printDecorators(decorators: Decorators) =
      decorators.l.map(z => shift + "@" + printExpr(z) + "\n").mkString("")
    s match {
      case u : Unsupported => shift + "assert(false)" + posComment

      case Del(e, ann) => shift + "del " + printExpr(e) + posComment
      case Yield(Some(e), ann) => shift + "yield " + printExpr(e) + posComment
      case Yield(None, ann) => shift + "yield" + posComment
      case YieldFrom(e, ann) => shift + "yield " + printExpr(e) + posComment

      case With(cm, target, body, ann) =>
        shift + "with " + printExpr(cm) + (target match {
          case Some(value) => " as " + printExpr(value)
          case None => ""
        }) + ":" + posComment + "\n" +
        printSt(body, shiftIncr)

      case If(conditioned, eelse, ann) =>
        def oneCase(keyword : String, p : (T, Statement)) =
          shift + keyword + " (" + printExpr(p._1) + "):" + " # " + p._2.ann.toString + "\n" +
          printSt(p._2, shiftIncr)

        val (iif :: elifs) = conditioned
        (oneCase("if", iif) :: elifs.map(oneCase("elif", _))).mkString("\n") + "\n" + (
            shift + "else:" + " # " + eelse.ann.toString + "\n" +
            printSt(eelse, shiftIncr)
        )

      case IfSimple(cond, yes, no, ann) => printSt(If(List((cond, yes)), no, ann.pos), shift)

      case While(cond, body, eelse, ann) =>
        shift + "while (" + printExpr(cond) + "):" + posComment + "\n" +
          printSt(body, shiftIncr) + "\n" +
        shift + "else:\n" +
          printSt(eelse, shiftIncr)

      case For(what, in, body, eelse, ann) =>
        shift + "for " + printExpr(what) + " in " + printExpr(in) + ":" + posComment + "\n" +
          printSt(body, shiftIncr) + "\n" +
        shift + "else:\n" +
          printSt(eelse, shiftIncr)

      case Try(ttry, excepts, eelse, ffinally, ann) =>
        shift + "try:" + posComment + "\n" +
          printSt(ttry, shiftIncr) + "\n" +
        excepts.map(x =>
          shift + "except " + option2string(x._1.map(y => printExpr(y._1) + option2string(y._2.map(z => " as " + z)))) + ":\n" +
            printSt(x._2, shiftIncr)
        ).mkString("\n") + "\n" +
        shift + "else:\n" +
          printSt(eelse, shiftIncr) + "\n" +
        shift + "finally:\n" +
          printSt(ffinally, shiftIncr)

      case Suite(l, ann) => l.map(printSt(_, shift)).mkString("\n")

      case AugAssign(op, lhs, rhs, ann) => shift + printExpr(lhs) + " " + AugOps.toString(op) + " " + printExpr(rhs) + posComment
      case Assign(l, ann) => shift + l.map(printExpr).mkString(" = ") + posComment
      case CreateConst(name, value, ann) => printSt(Assign(List(Ident(name, value.ann.pos), value), ann.pos.pos), shift) + posComment
      case Break(ann) => shift + "break" + posComment
      case Continue(ann) => shift + "cantinue" + posComment
      case Pass(ann) => shift + "pass" + posComment
      case Return(x, ann) =>shift + "return " + printExpr(x) + posComment
      case Assert(x, ann) => shift + "assert " + printExpr(x) + posComment
      case Raise(Some(x), Some(from), ann) => shift + "raise " + printExpr(x) + " from " + printExpr(from) + posComment
      case Raise(Some(x), None, ann) => shift + "raise " + printExpr(x) + posComment
      case Raise(None, None, ann) => shift + "raise" + posComment
      case NonLocal(l, ann) => shift + "nonlocal " + l.mkString(", ") + posComment
      case Global(l, ann) => shift + "global " + l.mkString(", ") + posComment
      case ClassDef(name, bases, body, decorators, ann) =>
        printDecorators(decorators) +
        shift + "class " + name + "(" + bases.map(printExpr).mkString(", ") + "):" + posComment + "\n" +
          printSt(body, shiftIncr)
      case FuncDef(name, args, otherPositional, otherKeyword, body, decorators, _, ann) =>
        val positionalOnly = args.filter(_._2 == ArgKind.Positional)
        val posOrKeyword = args.filter(_._2 == ArgKind.PosOrKeyword)
        val keywordOnly = args.filter(_._2 == ArgKind.Keyword)
        assert(positionalOnly ++ posOrKeyword ++ keywordOnly == args)
        def printArg(x : (String, ArgKind.T, Option[T], _)) = x._1 + (x._3 match { case None => "" case Some(default) => " = " + printExpr(default)})
        val argstring = positionalOnly.map(printArg) ++ (if (positionalOnly.isEmpty) List() else List("/")) ++ posOrKeyword.map(printArg) ++
          (otherPositional match {
            case Some(name) => List("*" + name)
            case None => if (keywordOnly.isEmpty) List() else List("*")
          }) ++
          keywordOnly.map(_._1) ++ otherKeyword.toList.map("**" + _)
        printDecorators(decorators) +
        shift + "def " + name + "(" + argstring.mkString(", ") + "):" + posComment + "\n" +
        printSt(body, shiftIncr)
      case ImportModule(what, as, ann) => shift + s"import ${what.mkString(".")} as $as" + posComment
      case ImportSymbol(from, what, as, ann) => shift + s"from ${from.mkString(".")} import $what as $as" + posComment
      case ImportAllSymbols(from, ann) => shift + s"from ${from.mkString(".")} import *" + posComment
    }
  }

  def toFile(t : Statement, dirName : String, moduleName : String) = {
    val dir = new File(dirName)
    if (!dir.exists()) dir.mkdir()
    val output = new FileWriter(dirName + "/" + moduleName + ".py")
    output.write(PrintPython.printSt(t, ""))
    output.close()
  }
}
