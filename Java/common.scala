import scala.collection.immutable.HashMap
package Common {

  import scala.collection.immutable.HashMap

  trait HasName {
    def name : String
  }

  class Lazy[A <: HasName](x0: => A) extends HasName {
    lazy val x = x0
    def name = x.name

    override def toString: String = x.toString
  }

  object Lazy {
    def apply[A <: HasName](x0: => A) = new Lazy(x0)
    def unapply[A <: HasName](x : Lazy[A]) : Some[A] = Some(x.x)
  }

  // a wrapper for the HashMap that no key is added twice
  class UIDMap[UID, Value](h0 : HashMap[UID, Value])  {
    val h = h0
    def this() = this(HashMap[UID, Value]())
    def contains = h.contains _
    def apply(key : UID) : Value = h(key)
    def findIfContains(key : UID, default : Value) = if (h.contains(key)) h(key) else default
    def +(uv : (UID, Value)) = {
      if (h.contains(uv._1)) {
        println(s"adding UID ${uv._1}, which already exists in the hash")
        assert(false)
      }
      new UIDMap(h.+(uv))
    }
  }
/*  object UIDMap {
    def apply[A, B](elems: (A, B)*): UIDMap[A, B] = new UIDMap[A, B](HashMap[A, B](elems))
  }*/

  // a HashMap wrapper, which remembers the order in which the keys were added and restores it when .toList is called
  case class HashMapWithOrder[Key, Value](h : HashMap[Key, Value], l : List[Key]) {
    def this() = this(HashMap[Key, Value](), List[Key]())
    def contains = h.contains _
    def apply(key : Key) : Value = h(key)
    def +(kv : (Key, Value)) = HashMapWithOrder(h.+(kv), l :+ kv._1)
    def toList  = l.map(key => (key, h(key)))
    def foldLeft[Acc](acc : Acc)(f : (Acc, (Key, Value)) => Acc) : Acc = l.foldLeft(acc)((acc, key) => f(acc, (key, h(key))))
  }

  class GenNames(h0: HashMap[String, Int]) {
    val h = h0

    def this() = this(HashMap())

    def apply(pref: String) =
      if (h.contains(pref))
        (pref + "_" + h(pref), new GenNames(h.+((pref, 1 + h(pref)))))
      else
        (pref + "_0", new GenNames(h.+((pref, 1))))

    override def toString: String = h.toString

    override def equals(o: Any): Boolean = o match {
      case x: GenNames => h == x.h
      case _ => false
    }
  }

  class Collector[T] {
    private var l =  List[T]()
    def add(x : T) = l = x :: l
    def get = l
  }

  // an ocaml style HashMap to simplify variable scopes traversal
  class HashStack[Key, Value](h0 : HashMap[Key, List[Value]]) {
    val h = h0
    def this() = this(HashMap[Key, List[Value]]())

    def push(key : Key, value : Value) = {
      if (h.contains(key)) {
        val l = h(key)
        new HashStack(h.+((key, value :: l)))
      }; else
        new HashStack(h.+((key, List(value))))
    }

    def contains(key : Key) = h.contains(key)

    def apply(key : Key) = h(key).head

    def pop(key : Key) = {
      new HashStack(
        h(key) match {
          case List() => throw new AssertionError
          case List(_) => h.-(key)
          case l => h.+((key, l.tail))
        }
      )
    }

    def replace(key : Key, value : Value) = this.pop(key).push(key, value)

    def keys = h.keys

    def foldLeft[Accum](accum : Accum)(f : (Accum, (Key, Value)) => Accum) : Accum =
      h.foldLeft(accum){ case (accum, (key, l)) => f(accum, (key, l.head)) }

    def empty = new HashStack(h.empty)

    override def toString: String = h.toString
  }

  object HashStack {
    def apply[Key, Value]() = new HashStack[Key, Value](HashMap[Key, List[Value]]())
  }

  object log2Up {
    def apply(n: BigInt): Int = {
      var w = 1
      while ((BigInt(1) << w) <= n) w = w + 1
      w
    }
  }

  trait HasMapFold[T] {
    def mapFold[Acc](f : (T, Acc) => (T, Acc))(acc : Acc) : (T, Acc)
  }
  trait HasMap[T] {
    def map(f : T => T) : T
  }


}

package object Common {

  type ExternalConstants = HashMap[String, BigInt]

  val successfullyFinishedString = "SUCCESSFULLY FINISHED"
}
