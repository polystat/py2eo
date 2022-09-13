package org.polystat.py2eo.transpiler

import org.polystat.py2eo.parser.Expression.{StringLiteral, T}

object ConcatStringLiteral {
  def apply(e : T) : T = {
    def reescape(s : String) : String = {
      val v = s.foldLeft(("", false))(
        (acc, char) =>
          if ('\\' == char && !acc._2) (acc._1, true) else
            if ('\\' == char && acc._2) (acc._1 + "\\\\", false) else {
              if (!acc._2 && '"' == char) (acc._1 + "\\\"", false) else
              if (!acc._2) (acc._1 :+ char, false) else
                if ('\"' == char) (acc._1 + "\\\"", false) else
                  { (acc._1 :+ char, false) }
            }
      )
      assert(!v._2)
      v._1
    }
    e match {
      case StringLiteral(values, ann) =>
        val s = values.map(
          s => {
            val s0 = if (!s.startsWith("'") && !s.startsWith("\"")) s.substring(1, s.length) else s
            val s1 = if (!s0.startsWith("'") && !s0.startsWith("\"")) s0.substring(1, s0.length) else s0
            val s2 = if (s1.startsWith("\"\"\"") || s1.startsWith("'''")) s1.substring(2, s1.length - 2) else s1
            reescape(s2.substring(1, s2.length - 1))
          }
        )
          .mkString
        val s1 = s.replace("\n", "\\n")
        StringLiteral(List("\"" + s1 + "\""), ann.pos)
      case e : Any => e
    }
  }
}
