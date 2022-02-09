package org.polystat.py2eo;

import PrintEO.{Text, indent, printExpr}
import org.polystat.py2eo.Common.GeneratorException

import scala.collection.immutable.HashMap

object PrintLinearizedMutableEOWithCage {

  import org.polystat.py2eo.Expression.{
    Await, CallIndex, CollectionComprehension, CollectionCons, DictComprehension, DictCons, DoubleStar,
    Field, GeneratorComprehension, Ident, Parameter, Slice, Star, T, isLiteral
  }

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
  object HackName {
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

  def pe: T => String = printExpr
  def isFun(f : Statement): Boolean = f match { case _: FuncDef => true case _ => false }

  def printSt(st : Statement) : Text =
    st match {
      case SimpleObject(name, l, _) =>
        ("write." ::
          indent(name :: "[]" :: indent(
            l.map{ case (name, _) => "cage > " + name } ++ (
              "seq > @" :: indent(l.map{case (name, value) => s"$name.write " + printExpr(value)})
              ))
          )) :+ s"($name.@)"
      case NonLocal(_, _) => List()
      case f: FuncDef => List()
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

  def printFun(newName : String, preface : List[String], f : FuncDef) : Text = {
    //    println(s"l = \n${PrintPython.printSt(Suite(l), "-->>")}")
    val funs = SimpleAnalysis.foldSS[List[FuncDef]]((l, st) => st match {
      case f : FuncDef => (l :+ f, false)
      case _ => (l, true)
    })(List(), f.body)
    val funNames = funs.map { f: FuncDef => f.name }.toSet
    val argCopies = f.args.map(parm => s"${parm.name}NotCopied' > ${parm.name}")
    val memories = f.accessibleIdents.filter(x => x._2._1 == VarScope.Local && !funNames.contains(x._1)).
      map(x => s"cage > ${x._1}").toList
    val innerFuns = funs.flatMap { f: FuncDef => printFun(f.name, List(), f) }

    val args1 = f.args.map{ case Parameter(argname, kind, None, None, _) if kind != ArgKind.Keyword =>
      argname + "NotCopied" }.mkString(" ")
    // todo: empty arg list hack
    val args2 = if (args1.isEmpty) "unused" else args1
    s"[$args2] > $newName" :: indent(
      preface ++ (
        "cage > result" ::
        "cage > tmp" ::
        argCopies ++ memories ++ innerFuns ++ (
          "goto > @" :: indent(
            s"[$returnLabel]" :: indent(
              "seq > @" :: indent(
                ("stdout \"" + newName + "\\n\"") ::
                f.args.map(parm => s"${parm.name}.<") ++
                (printSt(f.body) :+ "123")
              )
            )
          )
        )
      )
    )
  }

  def printTest(testName : String, st : Statement) : Text = {
    println(s"doing $testName")
    val mkCopy = List(
      "[x] > mkCopy",
      "  x' > copy",
      "  copy.< > @"
    )
    val theTest@FuncDef(_, _, _, _, _, _, _, _, _, _) =
      SimpleAnalysis.computeAccessibleIdents(FuncDef(testName, List(), None, None, None, st, Decorators(List()),
        HashMap(), isAsync = false, st.ann.pos))
    headers ++ printFun(theTest.name, mkCopy, theTest)
  }


}
