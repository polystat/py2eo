import Expression._
import PrintEO.{EOVisibility, Text, indent, printExpr}
import PrintLinearizedImmutableEO.rmUnreachableTail
import PrintLinearizedMutableEONoCage.headers

import scala.annotation.tailrec
import scala.collection.immutable.HashMap

object PrintLinearizedMutableEOWithCage {

  val bogusVisibility = new EOVisibility()
  val returnLabel = "returnLabel"

  // todo: imperative style suddenly
  object HackName {
    var count : Int = 0
    def apply() = {
      count = count + 1
      "tmp" + count
    }
  }

  @tailrec
  def isSeqOfFields(x : Expression.T) : Boolean = x match {
    case Field(whose, name, ann) => isSeqOfFields(whose)
    case CallIndex(false, whom, List((_, StringLiteral(_, _))), ann) => isSeqOfFields(whom)
    case Ident(_, _) => true
    case _ => false
  }

  def printFun(newName : String, f : FuncDef) : Text = {
    //    println(s"l = \n${PrintPython.printSt(Suite(l), "-->>")}")
    def pe = printExpr(bogusVisibility)(_)
    def isFun(f : Statement) = f match { case f : FuncDef => true case _ => false }
    val funs = SimpleAnalysis.foldSS[List[FuncDef]]((l, st) => st match {
      case f : FuncDef => (l :+ f, false)
      case _ => (l, true)
    })(List(), f.body)
    val funNames = funs.map{ case f : FuncDef => f.name }.toSet
    val argCopies = f.args.map(parm => s"${parm.name}NotCopied' > x${parm.name}")
    val memories = f.accessibleIdents.filter(x => x._2._1 == VarScope.Local && !funNames.contains(x._1)).
      map(x => s"cage > x${x._1}").toList
    val innerFuns = funs.flatMap{case f : FuncDef => (printFun(f.name, f))}

    def inner(st : Statement) : (Text) =
      st match {
        case NonLocal(l, _) => List()
        case SimpleObject(name, l, ann) =>
          (("write." ::
            indent("x" + name :: "[]" :: indent(
              l.map{ case (name, value) => "cage > x" + name } ++ (
                "seq > @" :: indent(l.map{case (name, value) => s"x$name.write " + pe(value)})
              ))
            )) :+ s"(x$name.@)")
        case f : FuncDef => List()
        case Assign(List(lhs, rhs@CallIndex(true, whom, args, ann)), _) if isSeqOfFields(whom) && isSeqOfFields(lhs) =>
//          assert(args.forall{ case (_, Ident(_, _)) => true  case _ => false })
          (List(
            s"tmp.write ${pe(rhs)}",
            "(tmp.@)",
            s"${pe(lhs)}.write (tmp.xresult)"
          ))
        case Assign(List(lhs, rhs), _) if isSeqOfFields(lhs) =>
          rhs match {
            case _ : DictCons | _ : CollectionCons | _ : Await | _ : Star | _ : DoubleStar |
              _ : CollectionComprehension | _ : DictComprehension | _ : GeneratorComprehension | _ : Slice =>
              throw new Throwable("these expressions must be wrapped in a function call " +
                "because a copy creation is needed and dataization is impossible")
            case CallIndex(false, whom, args, ann) => throw new Throwable("this is A PROBLEM") // todo
            case CallIndex(false, whom, args, ann) => throw new Throwable("this is A PROBLEM") // todo
            case _ => ()
          }
          val doNotCopy = rhs match {
//            case z if isSeqOfFields(z) => false
            case Ident(name, ann) => false
            case _ => true
          }
          if (doNotCopy)
            List(s"${pe(lhs)}.write (${pe(rhs)}" + (if (isLiteral(rhs)) "" else ".@") + ")")
          else {
            val tmp = HackName()
            val Ident(name, _) = rhs
            (s"[] > $tmp" :: indent(List(s"x$name' > copy", "copy.< > @"))) :+
              s"${pe(lhs)}.write ($tmp.copy)"
          }

        case Assign(List(e), _) => List(pe(e))
        case Return(e, ann) => e match {
          case Some(value) =>
            val (sts) = inner((Assign(List(Ident("result", ann.pos), value), ann.pos)))
            (sts :+ s"$returnLabel.forward 0")
          case None => List(s"$returnLabel.forward 0")
        }
        case IfSimple(cond, yes, no, ann) =>
          val (stsY) = inner(yes)
          val (stsN) = inner(no)
          pe(cond) + ".if" :: indent("seq" :: indent(stsY :+ "TRUE")) ++ indent("seq" :: indent(stsN :+ "TRUE"))
        case While(cond, body, Pass(_), ann) => {
          "goto" :: indent(
            s"[breakLabel]" :: indent(
              "seq > @" :: indent(
                pe(cond) + ".while" :: indent(
                  s"[unused]" :: indent("seq > @" :: indent(inner(body) :+ "TRUE"))
                )
              )
            )
          )
        }
        case Break(ann) => List(s"breakLabel.forward 1")

        case Pass(_) => List()
        case Suite(l, _) => l.flatMap(inner)
      }

    val args1 = f.args.map{ case Parameter(argname, kind, None, None, _) if kind != ArgKind.Keyword =>
      argname + "NotCopied" }.mkString(" ")
    // todo: empty arg list hack
    val args2 = if (args1.isEmpty) "unused" else args1
    s"[$args2] > x${newName}" :: indent(
      "cage > xresult" ::
      "cage > tmp" ::
      argCopies ++ memories ++ innerFuns ++
        ("goto > @" :: indent(
          (s"[$returnLabel]" :: indent(
            ("seq > @" :: indent(
              s"stdout \"$newName\\n\"" ::
              f.args.map(parm => s"x${parm.name}.<") ++
              (inner(f.body) :+
                "123"
                )
              )
            )
          ))
        ))
    )
  }

  def printTest(testName : String, st : Statement) : Text = {
    println(s"doing $testName")
    val theTest@FuncDef(_, _, _, _, _, _, _, _, _, _) =
      SimpleAnalysis.computeAccessibleIdents(FuncDef(testName, List(), None, None, None, st, Decorators(List()),
        HashMap(), false, st.ann.pos))
    headers ++ printFun(theTest.name, theTest)
  }


}
