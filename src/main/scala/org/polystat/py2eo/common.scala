package org.polystat.py2eo

import java.io.File

object Common {

  import scala.collection.immutable.HashMap

  trait HasName {
    def name : String
  }

  class Lazy[A <: HasName](x0: => A) extends HasName {
    lazy val x: A = x0
    def name: String = x.name

    override def toString: String = x.toString
  }

  object Lazy {
    def apply[A <: HasName](x0: => A) : Lazy[A] = new Lazy(x0)
    def unapply[A <: HasName](x : Lazy[A]) : Some[A] = Some(x.x)
  }

  // a wrapper for the HashMap that no key is added twice
  class UIDMap[UID, Value](h0 : HashMap[UID, Value])  {
    private val h = h0
    def this() = this(HashMap[UID, Value]())
    def contains: UID => Boolean = h.contains
    def apply(key : UID) : Value = h(key)
    def findIfContains(key : UID, default : Value): Value = if (h.contains(key)) h(key) else default
    def +(uv : (UID, Value)): UIDMap[UID, Value] = {
      if (h.contains(uv._1)) {
        println(s"adding UID ${uv._1}, which already exists in the hash")
        assert(false)
      }
      new UIDMap(h.+(uv))
    }
  }

  // a HashMap wrapper, which remembers the order in which the keys were added and restores it when .toList is called
  case class HashMapWithOrder[Key, Value](h : HashMap[Key, Value], l : List[Key]) {
    def this() = this(HashMap[Key, Value](), List[Key]())
    def contains: Key => Boolean = h.contains
    def apply(key : Key) : Value = h(key)
    def +(kv : (Key, Value)): HashMapWithOrder[Key, Value] = HashMapWithOrder(h.+(kv), l :+ kv._1)
    def toList : List[(Key, Value)]  = l.map(key => (key, h(key)))
    def foldLeft[Acc](acc : Acc)(f : (Acc, (Key, Value)) => Acc) : Acc = l.foldLeft(acc)((acc, key) => f(acc, (key, h(key))))
  }

  class GenNames(h0: HashMap[String, Int]) {
    private val h = h0

    def this() = this(HashMap())

    def apply(pref: String): (String, GenNames) =
      if (h.contains(pref)) {
        (pref + "_" + h(pref), new GenNames(h.+((pref, 1 + h(pref)))))
      } else {
        (pref + "_0", new GenNames(h.+((pref, 1))))
      }

    override def toString: String = h.toString

    override def equals(o: Any): Boolean = o match {
      case x: GenNames => h == x.h
      case _ => false
    }
  }

  // an ocaml style HashMap to simplify variable scopes traversal
  class HashStack[Key, Value](h0 : HashMap[Key, List[Value]]) {
    private val h = h0
    def this() = this(HashMap[Key, List[Value]]())

    def push(key : Key, value : Value): HashStack[Key, Value] = {
      if (h.contains(key)) {
        val l = h(key)
        new HashStack(h.+((key, value :: l)))
      } else {
        new HashStack(h.+((key, List(value))))
      }
    }

    def contains(key : Key): Boolean = h.contains(key)

    def apply(key : Key): Value = h(key).head

    def pop(key : Key): HashStack[Key, Value] = {
      new HashStack(
        h(key) match {
          case List() => throw new AssertionError
          case List(_) => h.-(key)
          case l => h.+((key, l.tail))
        }
      )
    }

    def replace(key : Key, value : Value): HashStack[Key, Value] = this.pop(key).push(key, value)

    def keys: Iterable[Key] = h.keys

    def foldLeft[Accum](accum : Accum)(f : (Accum, (Key, Value)) => Accum) : Accum =
      h.foldLeft(accum){ case (accum, (key, l)) => f(accum, (key, l.head)) }

    def empty : HashStack[Key, Value] = new HashStack(h.empty)

    override def toString: String = h.toString
  }

  object HashStack {
    def apply[Key, Value]() : HashStack[Key, Value] = new HashStack[Key, Value](HashMap[Key, List[Value]]())
  }

  def log2Up(n: BigInt): Int = {
    var w = 1
    while ((BigInt(1) << w) <= n) w = w + 1
    w
  }

  trait HasMapFold[T] {
    def mapFold[Acc](f : (T, Acc) => (T, Acc))(acc : Acc) : (T, Acc)
  }
  trait HasMap[T] {
    def map(f : T => T) : T
  }

  type ExternalConstants = HashMap[String, BigInt]

  def dfsFiles(file : File) : List[File] = {
    if (!file.isDirectory) List(file) else {
      file.listFiles().toList.flatMap(dfsFiles)
    }
  }

  class TranspilerException(reason : String) extends Exception(reason)
  class GeneratorException(reason : String) extends TranspilerException(reason)
  class ASTAnalysisException(reason : String) extends TranspilerException(reason)
  class ASTMapperException(reason : String) extends TranspilerException(reason)

}

