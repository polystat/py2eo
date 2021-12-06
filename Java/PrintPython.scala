import Expression._


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
      case NoneLiteral() => "None"
      case UnsupportedExpr(t, value) => "None"
      case IntLiteral(value) => value.toString(10)
      case FloatLiteral(value) => value.toString
      case ImagLiteral(value) => value.toString + "j"
      case StringLiteral(value) => value
      case BoolLiteral(b) => if (b) "True" else "False"
      case Binop(op, l, r) => brak(printExpr(l) + " " + Binops.toString(op) + " " + printExpr(r))
      case LazyLOr(l, r) => rnd(printExpr(l) + " or " + printExpr(r))
      case LazyLAnd(l, r) => rnd(printExpr(l) + " and " + printExpr(r))
      case SimpleComparison(op, l, r) => brak(printExpr(l) + " " + Compops.toString(op) + " " + printExpr(r))
      case FreakingComparison(ops, l) =>
        val sops = ops.map(Compops.toString) :+ ""
        val sopnds = l.map(printExpr)
        brak(sopnds.zip(sops).flatMap(x => List(x._1, x._2)).mkString(" "))
      case Unop(op, x) => Unops.toString(op) + printExpr(x)
      case Ident(name) => name
      case Star(e) => "*" + printExpr(e)
      case DoubleStar(e) => "**" + printExpr(e)
      case Slice(from, to, by) =>
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
      case CallIndex(isCall, whom, args) => printExpr(whom) + (if (isCall) rnd _ else sqr _)(
        args.map{case (None, e) => printExpr(e)  case (Some(keyword), e) => keyword + "=" + printExpr(e)}.mkString(", "))
      case Field(whose, name) =>printExpr(whose) + "." + name
      case Cond(cond, yes, no) => printExpr(yes) + " if " + printExpr(cond) + " else " + printExpr(no)
      case AnonFun(args, body) => "lambda " + args.mkString(", ") + " : " + printExpr(body)
      case CollectionCons(kind, l) =>
        val braks = CollectionKind.toBraks(kind)
        brak(l.map(printExpr).mkString(", ") + (if (l.size == 1) "," else ""), braks._1, braks._2)
      case CollectionComprehension(kind, base, l) =>
        val braks = CollectionKind.toBraks(kind)
        brak(printExpr(base) + " " + l.map(printComprehension).mkString(" "), braks._1, braks._2)
      case DictCons(l) => brak(l.map(printDictElt).mkString(", "), "{", "}")
      case DictComprehension(base, l) => brak(printDictElt(base) + " " + l.map(printComprehension).mkString(" "), "{", "}")
    }
  }

  def option2string[T](x : Option[T]) = x match {
    case Some(value) => value.toString
    case None => ""
  }

  def printSt(s : Statement, shift : String) : String = {
//    def mksh(shift : Int) = " " * shift
    val shiftIncr = shift + "    "
    def printDecorators(decorators: Decorators) =
      decorators.l.map(z => shift + "@" + printExpr(z) + "\n").mkString("")
    s match {
      case u : Unsupported => shift + "assert(false)"

      case Del(e) => shift + "del " + printExpr(e)
      case Yield(Some(e)) => shift + "yield " + printExpr(e)
      case Yield(None) => shift + "yield"

      case With(cm, target, body) =>
        shift + "with " + printExpr(cm) + (target match {
          case Some(value) => " as " + printExpr(value)
          case None => ""
        }) + ":\n" +
        printSt(body, shiftIncr)

      case If(conditioned, eelse) =>
        def oneCase(keyword : String, p : (T, Statement)) =
          shift + keyword + " (" + printExpr(p._1) + "):\n"+
          printSt(p._2, shiftIncr)

        val (iif :: elifs) = conditioned
        (oneCase("if", iif) :: elifs.map(oneCase("elif", _))).mkString("\n") + "\n" + (
            shift + "else:\n" +
            printSt(eelse, shiftIncr)
        )

      case IfSimple(cond, yes, no) => printSt(If(List((cond, yes)), no), shift)

      case While(cond, body, eelse) =>
        shift + "while (" + printExpr(cond) + "):\n" +
          printSt(body, shiftIncr) + "\n" +
        shift + "else:\n" +
          printSt(eelse, shiftIncr)

      case For(what, in, body, eelse) =>
        shift + "for " + printExpr(what) + " in " + printExpr(in) + ":\n" +
          printSt(body, shiftIncr) + "\n" +
        shift + "else:\n" +
          printSt(eelse, shiftIncr)

      case Try(ttry, excepts, eelse, ffinally) =>
        shift + "try:\n" +
          printSt(ttry, shiftIncr) + "\n" +
        excepts.map(x =>
          shift + "except " + option2string(x._1.map(y => printExpr(y._1) + option2string(y._2.map(z => " as " + z)))) + ":\n" +
            printSt(x._2, shiftIncr)
        ).mkString("\n") + "\n" +
        shift + "else:\n" +
          printSt(eelse, shiftIncr) + "\n" +
        shift + "finally:\n" +
          printSt(ffinally, shiftIncr)

      case Suite(l) => l.map(printSt(_, shift)).mkString("\n")

      case AugAssign(op, lhs, rhs) => shift + printExpr(lhs) + " " + AugOps.toString(op) + " " + printExpr(rhs)
      case Assign(l) => shift + l.map(printExpr).mkString(" = ")
      case CreateConst(name, value) => printSt(Assign(List(Ident(name), value)), shift)
      case WithoutArgs(s) => shift + StatementsWithoutArgs.toString(s)
      case Return(x) =>shift + "return " + printExpr(x)
      case Assert(x) => shift + "assert " + printExpr(x)
      case Raise(Some(x), Some(from)) => shift + "raise " + printExpr(x) + " from " + printExpr(from)
      case Raise(Some(x), None) => shift + "raise " + printExpr(x)
      case Raise(None, None) => shift + "raise"
      case NonLocal(l) => shift + "nonlocal " + l.mkString(", ")
      case Global(l) => shift + "global " + l.mkString(", ")
      case ClassDef(name, bases, body, decorators) =>
        printDecorators(decorators) +
        shift + "class " + name + "(" + bases.map(printExpr).mkString(", ") + "):\n" +
          printSt(body, shiftIncr)
      case FuncDef(name, args, otherPositional, otherKeyword, body, decorators, _) =>
        val positionalOnly = args.filter(_._2 == ArgKind.Positional)
        val posOrKeyword = args.filter(_._2 == ArgKind.PosOrKeyword)
        val keywordOnly = args.filter(_._2 == ArgKind.Keyword)
        assert(positionalOnly ++ posOrKeyword ++ keywordOnly == args)
        def printArg(x : (String, ArgKind.T, Option[T])) = x._1 + (x._3 match { case None => "" case Some(default) => " = " + printExpr(default)})
        val argstring = positionalOnly.map(printArg) ++ (if (positionalOnly.isEmpty) List() else List("/")) ++ posOrKeyword.map(printArg) ++
          (otherPositional match {
            case Some(name) => List("*" + name)
            case None => if (keywordOnly.isEmpty) List() else List("*")
          }) ++
          keywordOnly.map(_._1) ++ otherKeyword.toList.map("**" + _)
        printDecorators(decorators) +
        shift + "def " + name + "(" + argstring.mkString(", ") + "):\n" +
        printSt(body, shiftIncr)
      case ImportModule(what, as) => shift + s"import ${what.mkString(".")} as $as"
      case ImportSymbol(from, what, as) => shift + s"from ${from.mkString(".")} import $what as $as"
      case ImportAllSymbols(from) => shift + s"from ${from.mkString(".")} import *"
    }
  }

}
