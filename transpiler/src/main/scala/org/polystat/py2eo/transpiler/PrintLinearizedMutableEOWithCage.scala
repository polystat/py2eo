package org.polystat.py2eo.transpiler

import scala.collection.immutable.HashMap
import PrintEO.{Text, indent, printExpr}
import org.polystat.py2eo.parser.{ArgKind, Expression, Statement, VarScope}
import org.polystat.py2eo.transpiler.Common.GeneratorException
import org.polystat.py2eo.parser.Expression.{
  Await, CallIndex, CollectionComprehension, CollectionCons, DictComprehension, DictCons, DoubleStar, Field,
  GeneratorComprehension, Ident, Parameter, Slice, Star, T, isLiteral
}
import org.polystat.py2eo.parser.Statement.{
  Assign, Break, Decorators, FuncDef, IfSimple, NonLocal, Pass, Return, SimpleObject, Suite, While, ClassDef
}

object PrintLinearizedMutableEOWithCage {

  val returnLabel = "returnLabel"

  val headers = List(
    "+package org.eolang",
    "+alias goto org.eolang.gray.goto",
    "+alias stdout org.eolang.io.stdout",
    "+alias cage org.eolang.gray.cage",
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

  private def printSt(st : Statement.T) : Text =
    st match {
      case ClassDef(name, bases, body, decorators, ann) if bases.length <= 1 && decorators.l.isEmpty =>
        val Suite(l0, _) = SimplePass.simpleProcStatement(SimplePass.unSuite)(body)
        val l = l0.filter{ case Pass(_) => false case _ => true }
        val decorates = bases.headOption.map(_._2)
          "write." :: indent(
            name ::
            "[]" :: indent(
              "[unused] > apply" ::
              indent(
                (
                  "[] > result" :: indent(
                    l.map{
                      case Assign(List(Ident(fieldName, _), rhs), _) => s"cage > $fieldName"
                      case f : FuncDef => s"cage > ${f.name}"
                    } ++
                    decorates.toList.map(e => s"${printExpr(e)}.apply 0 > base") ++
                    (
                      "seq > initFields" :: indent(
                        l.flatMap{
                          case Assign(List(Ident(name, _), rhs), _) => List(s"$name.write ${printExpr(rhs)}")
                          case f : FuncDef =>
                            "write." :: indent(f.name :: printFun(List(), f))
                        } ++
                        decorates.toList.map(x => "base")
                      )
                    ) ++
                    decorates.toList.map(x => "base.result > @")
                  )
                ) :+ "result.initFields > @"
              )
            )
          )
      case NonLocal(_, _) => List()
      case f: FuncDef => "write." :: indent(f.name :: printFun(List(), f))
      case Assign(List(lhs, rhs@CallIndex(true, whom, _, _)), _) if (seqOfFields(whom).isDefined &&
        seqOfFields(lhs).isDefined) =>
        //          assert(args.forall{ case (_, Ident(_, _)) => true  case _ => false })
        List(
          s"tmp.write ${pe(rhs)}",
          "(tmp.@)",
          s"${pe(lhs)}.write (tmp.result)"
        )
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
        if (doNotCopy)
          if (isLiteral(rhs)) {
            List(s"${pe(lhs)}.write (${pe(rhs)}" + ")")
          } else {
            val tmp = HackName()
            (s"[] > $tmp" ::
              indent("memory > dddata" :: s"dddata.write (${pe(rhs)}) > @" :: List())) :+
              s"${pe(lhs)}.write ($tmp.dddata)"
          }
        else {
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
          val sts = printSt(Assign(List(Ident("result", ann.pos), value), ann.pos))
          sts :+ s"$returnLabel.forward 0"
        case None => List(s"$returnLabel.forward 0")
      }
      case IfSimple(cond, yes, no, _) =>
        val stsY = printSt(yes)
        val stsN = printSt(no)
        pe(cond) + ".if" :: indent("seq" :: indent(stsY :+ "TRUE")) ++ indent("seq" :: indent(stsN :+ "TRUE"))
      case While(cond, body, Some(Pass(_)), _) =>
        "goto" :: indent(
          "[breakLabel]" :: indent(
            "seq > @" :: indent(
              pe(cond) + ".while" :: indent(
                "[unused]" :: indent("seq > @" :: indent(printSt(body) :+ "TRUE"))
              )
            )
          )
        )
      case Break(_) => List("breakLabel.forward 1")

      case Pass(_) => List()
      case Suite(l, _) => l.flatMap(printSt)
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
      map(x => s"cage > ${x._1}").toList ++
      funs.map { f: FuncDef => s"cage > ${f.name}" }

    val args1 = f.args.map{ case Parameter(argname, kind, None, None, _) if kind != ArgKind.Keyword =>
      argname + "NotCopied" }.mkString(" ")
    // todo: empty arg list hack
    val args2 = if (args1.isEmpty) "unused" else args1
    "[]" :: indent(
      s"[$args2] > apply" :: indent(
        preface ++ (
          "cage > result" ::
          "cage > tmp" ::
          argCopies ++ memories ++ (
            "goto > @" :: indent(
              s"[$returnLabel]" :: indent(
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
    )
  }

  def printTest(testName : String, st : Statement.T) : Text = {
    HackName.count = 0 // todo: imperative style suddenly
    println(s"doing $testName")
    val mkCopy = List(
      "[x] > mkCopy",
      "  x' > copy",
      "  copy.< > @"
    )
    val theTest@FuncDef(_, _, _, _, _, _, _, _, _, _) =
      SimpleAnalysis.computeAccessibleIdents(FuncDef(testName, List(), None, None, None, st, Decorators(List()),
        HashMap(), isAsync = false, st.ann.pos))
    val hack = printFun(mkCopy, theTest)
    headers ++ (s"[unused] > ${theTest.name}" :: hack.tail)
  }


}
