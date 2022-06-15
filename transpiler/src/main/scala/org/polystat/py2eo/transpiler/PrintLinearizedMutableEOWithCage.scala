package org.polystat.py2eo.transpiler

import scala.collection.immutable.HashMap
import PrintEO.{Text, indent, printExpr}
import org.polystat.py2eo.parser.{ArgKind, Expression, Statement, VarScope}
import org.polystat.py2eo.transpiler.Common.GeneratorException
import org.polystat.py2eo.parser.Expression.{
  Await, CallIndex, CollectionComprehension, CollectionCons, DictComprehension, DictCons, DoubleStar,
  Field, GeneratorComprehension, Ident, Parameter, Slice, Star, T, isLiteral
}
import org.polystat.py2eo.parser.Statement.{
  Assign, Break, ClassDef, Decorators, FuncDef, IfSimple, NonLocal, Pass, Raise, Return, Suite, Try, While
}

object PrintLinearizedMutableEOWithCage {

  val returnLabel = "returnLabel"

  val headers = List(
    "+package org.eolang",
    "+alias goto org.eolang.gray.goto",
    "+alias stdout org.eolang.io.stdout",
    "+alias sprintf org.eolang.txt.sprintf",
    "+alias cage org.eolang.gray.cage",
    "+alias pyint preface.pyint",
    "+alias pyfloat preface.pyfloat",
    "+alias pystring preface.pystring",
    "+alias pybool preface.pybool",
    "+alias pycomplex preface.pycomplex",
    "+alias newUID preface.newUID",
    "+alias fakeclasses preface.fakeclasses",
    "+alias mkCopy preface.mkCopy",
    "+alias raiseNothing preface.raiseNothing",
    "+alias continue preface.continue",
    "+alias break preface.break",
    "+alias return preface.return",
    "+alias raiseEmpty preface.raiseEmpty",
    "+alias xmyArray preface.xmyArray",
    "+alias xlen preface.xlen",
    "+alias xStopIteration preface.xStopIteration",
    "+alias xrange preface.xrange",
    //    "+alias sprintf org.eolang.txt.sprintf",
    "+junit",
    ""
  )

  // todo: imperative style suddenly
  private object HackName {
    var count : Int = 0
    def apply(): String = {
      count = count + 1
      "tmp" + count
    }
  }

  def seqOfFields(x : Expression.T) : Option[List[String]] = x match {
    case Field(whose, name, _) => seqOfFields(whose).map(_ :+ name)
//    case CallIndex(false, whom, List((_, StringLiteral(_, _))), _) => isSeqOfFields(whom)
    case Ident(name, _) => Some(List(name))
    case _ => None
  }

  private def pe: T => String = printExpr
  private def isFun(f : Statement.T): Boolean = f match { case _: FuncDef => true case _ => false }

  private def printSt(st : Statement.T) : Text = {
    st match {
      case ClassDef(name, bases, body, decorators, ann) if bases.length <= 1 && decorators.l.isEmpty =>
        val Suite(l0, _) = SimplePass.simpleProcStatement(SimplePass.unSuite)(body)
        val l = l0.filter{ case Pass(_) => false case _ => true }
        val init : Option[FuncDef] = l0
          .find{ case f : FuncDef => f.name == "x__init__" case _ => false }
          .map{ case f : FuncDef => f }
        val consArgs = init match {
          case Some(value) =>
            val argnames = value.args.tail.map(_.name).mkString(" ")
            s"$argnames"
          case None => ""
        }
        val callInit = init match {
          case None => ""
          case Some(_) => s" (goto ((result.x__init__.apply pResult $consArgs).@))"
        }
        val decorates = bases.headOption.map(_._2)
          "write." :: indent(
            name ::
            "[]" :: indent(
              "newUID.apply 0 > x__id__" ::
              s"[$consArgs] > apply" ::
              indent(
                "[stackUp] > @" ::
                indent(
                  (
                    "cage result > pResult" ::
                    "cage 0 > tmp" ::
                    "[] > result" ::
                    indent(
                      l.map{
                        case Assign(List(Ident(fieldName, _), rhs), _) => s"cage 0 > $fieldName"
                        case f : FuncDef => s"cage 0 > ${f.name}"
                      } ++
                      decorates.toList.map(e => s"goto ((${printExpr(e)}.apply).@) > base") ++
                      (
                        s"$name > x__class__" ::
                        "seq > initFields" ::
                        indent(
                          l.flatMap{
                            case Assign(List(Ident(name, _), rhs), _) => List(s"$name.write ${printExpr(rhs)}")
                            case f : FuncDef =>
                              "write." :: indent(f.name :: printFun(List(), f))
                          } ++
                          decorates.toList.map(x => "base.result.x__class__.x__id__")
                        )
                      ) ++
                      decorates.toList.map(x => "base.result > @")
                    )
                  ) :+ s"seq (result.initFields) (pResult.write result)$callInit (stackUp.forward (return pResult)) > @"
                )
              )
            )
          )
      case NonLocal(_, _) => List()
      case f: FuncDef => "write." :: indent(f.name :: printFun(List(), f))
      case Assign(List(_, CallIndex(true, Expression.Ident("xprint", _), List((None, n)), _)), _) =>
        List("stdout (sprintf \"%%s\\n\" (%s.as-string))".format(printExpr(n)))
      case Assign(List(lhs, rhs@CallIndex(true, whom, args, _)), _) if (seqOfFields(whom).isDefined &&
        seqOfFields(lhs).isDefined) =>
        whom match {
          case Ident("xcomplex", ann) if args.size == 2 =>
            List(s"${pe(lhs)}.write (pycomplex ((${pe(args(0)._2)}).as-float) ((${pe(args(1)._2)}).as-float))")
          case _ =>
            //          assert(args.forall{ case (_, Ident(_, _)) => true  case _ => false })
            List(
              s"tmp.write (goto (${pe(rhs)}.@))",
              "(tmp.x__class__.x__id__.neq (return.x__class__.x__id__)).if (stackUp.forward tmp) 0",
              s"${pe(lhs)}.write (tmp.result)"
            )
        }
      case Assign(List(lhs, rhs), _) if seqOfFields(lhs).isDefined =>
        rhs match {
          case _ : DictCons | _ : CollectionCons | _ : Await | _ : Star | _ : DoubleStar |
               _ : CollectionComprehension | _ : DictComprehension | _ : GeneratorComprehension | _ : Slice =>
            throw new GeneratorException("these expressions must be wrapped in a function call " +
              "because a copy creation is needed and dataization is impossible")
          case CallIndex(false, _, _, _) => throw new GeneratorException("this is A PROBLEM") // todo
          case CallIndex(false, _, _, _) => throw new GeneratorException("this is A PROBLEM") // todo
          case _ => ()
        }
        val seqOfFields1 = seqOfFields(rhs)
        val doNotCopy = seqOfFields1.isEmpty
        if (doNotCopy) {
          List(s"${pe(lhs)}.write (${pe(rhs)}" + ")", s"${pe(lhs)}.force")
        } else {
          val tmp = HackName()
          //            val Ident(name, _) = rhs
          val Some(l) = seqOfFields1
          List (
            s"mkCopy (${l.mkString(".")}) > $tmp",
            s"${pe(lhs)}.write ($tmp.copy)"
          )
        }

      case Assign(List(e), _) => List(pe(e))
      case Return(e, ann) => e match {
        case Some(value) =>
          List(
            s"toReturn.write (${pe(value)})",
            "stackUp.forward (return toReturn)"
          )
        case None => List("stackUp.forward (return 0)")
      }
      case IfSimple(cond, yes, no, _) =>
        val stsY = printSt(yes)
        val stsN = printSt(no)
        pe(cond) + ".if" :: indent("seq" :: indent(stsY :+ "(pybool TRUE)")) ++ indent("seq" :: indent(stsN :+ "(pybool TRUE)"))
      case While(cond, body, Some(Pass(_)), _) =>
        "write." :: indent(
          "tmp" ::
          "goto" :: indent(
            "[stackUp]" :: indent(
              "seq > @" :: indent(
                (
                  pe(cond) + ".while" :: indent(
                  "[unused]" :: indent("cage 0 > tmp" :: "seq > @" :: indent(printSt(body) :+ "(pybool TRUE)"))
                  )
                ) :+ "stackUp.forward raiseNothing"
              )
            )
          )
        ) ++
        ("if." :: indent(List("tmp.x__class__.x__id__.neq (break.x__class__.x__id__)", "stackUp.forward tmp", "0")))
      case Break(_) => List("stackUp.forward break")

      case Pass(_) => List()
      case Suite(l, _) => l.flatMap(printSt)

      case Raise(None, None, ann) => List("stackUp.forward raiseEmpty")
      case Raise(Some(e), None, _) => List("stackUp.forward %s".format(pe(e)))

      case Try(ttry, List((None, exc)), eelse, ffinally, ann) =>
        "xcaught.write (pybool TRUE)" ::
        "write." :: indent(
          "xcurrent-exception" ::
          "goto" :: indent(
            "[stackUp]" :: indent(
              "cage 0 > xcurrent-exception" ::
              "cage 0 > tmp" ::
              "seq > @" :: indent(
                printSt(ttry) :+ "stackUp.forward raiseNothing"
              )
            )
          )
        ) ++
        ("seq" :: indent(
          ("if." :: indent(
            "is-exception (xcurrent-exception.x__class__.x__id__)" ::
            "seq" :: indent(printSt(exc) :+ "0")  ++
            List("0")
          )) ++
          ("if." :: indent(
            "xcurrent-exception.x__class__.x__id__.eq (raiseNothing.x__class__.x__id__)" ::
            "seq" :: (indent(printSt(eelse.getOrElse(Pass(ann))) :+ "0")) ++
            List("0")
          )) ++
          printSt(ffinally.getOrElse(Pass(ann))) ++
          List("((is-break-continue-return (xcurrent-exception.x__class__.x__id__)).or "
               + "((is-exception (xcurrent-exception.x__class__.x__id__)).and (xcaught.not))).if "
               + "(stackUp.forward xcurrent-exception) 0")
        ))
    }
  }

  private def printFun(preface : List[String], f : FuncDef) : Text = {
    //    println(s"l = \n${PrintPython.printSt(Suite(l), "-->>")}")
    val funs = SimpleAnalysis.foldSS[List[FuncDef]]((l, st) => st match {
      case f : FuncDef => (l :+ f, false)
      case _ : ClassDef => (l, false)
      case _ => (l, true)
    })(List(), f.body)
    val funNames = funs.map { f: FuncDef => f.name }.toSet
    val argCopies = f.args.map(parm => s"${parm.name}NotCopied' > ${parm.name}")
    val memories =
      f.accessibleIdents.filter(x => x._2._1 == VarScope.Local && !funNames.contains(x._1)).
      map(x => s"cage 0 > ${x._1}").toList ++
      funs.map { f: FuncDef => s"cage 0 > ${f.name}" }

    val args2 = (f.args.map{ case Parameter(argname, kind, None, None, _) if kind != ArgKind.Keyword =>
      argname + "NotCopied" }).mkString(" ")
    "[]" :: indent(
      s"[$args2] > apply" :: indent(
        "[stackUp] > @" :: indent(
          preface ++ (
            "cage 0 > tmp" ::
            "cage 0 > toReturn" ::
            argCopies ++ memories ++ (
              "seq > @" :: indent(
                ("stdout \"" + f.name + "\\n\"") ::
                f.args.map(parm => s"${parm.name}.<") ++
                (printSt(f.body) :+ "123")
              )
            )
          )
        )
      )
    )
  }

  def printTest(testName : String, st : Statement.T) : Text = {
    HackName.count = 0 // todo: imperative style suddenly
    println(s"doing $testName")
    val mkCopy = {
    List(
      "[id] > is-exception",
      "  id.greater (pyint 3) > @",
      "[id] > is-break-continue-return",
      "  (id.greater (pyint 0)).and (id.less (pyint 4)) > @",
      "[] > xbool",
      "  [x] > apply",
      "    [stackUp] > @",
      "      seq > @",
      "        stackUp.forward (return x)",
      "        123",
      "cage 0 > xcurrent-exception",
      "cage FALSE > xcaught",
      "pyint 0 > dummy-int-usage",
      "pyfloat 0 > dummy-float-usage",
      "pybool TRUE > dummy-bool-usage",
      "pycomplex 0 0 > dummy-pycomplex",
      "pystring (sprintf \"\") > dummy-bool-string",
      "newUID > dummy-newUID",
      "fakeclasses.pyFloatClass > xfloat",
      "fakeclasses.pyComplexClass > xcomplex",
      "raiseNothing > dummy-rn",
      "continue > dummy-continue",
      "break > dummy-break",
      "return > dummy-return",
      "raiseEmpty > dummy-raiseEmpty",
      "xmyArray > dummy-xmyArray",
      "mkCopy > dummy-mkCopy",
      "xlen > dummy-xlen",
      "xStopIteration > dummy-stop-iteration",
      "xrange > dummy-xrange",
    )
    }
    val theTest@FuncDef(_, _, _, _, _, _, _, _, _, _) =
      SimpleAnalysis.computeAccessibleIdents(FuncDef(testName, List(), None, None, None, st, Decorators(List()),
        HashMap(), isAsync = false, st.ann.pos))
    val hack = printFun(mkCopy, theTest)
    headers ++ ((s"[unused] > ${theTest.name}" :: hack.tail))
  }


}
