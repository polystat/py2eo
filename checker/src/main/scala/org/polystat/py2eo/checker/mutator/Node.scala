package org.polystat.py2eo.checker.mutator

import org.cqfn.astranaut.base.{Node => AstranautNode}
import org.cqfn.astranaut.base.DraftNode

import java.util.{List => JList}
import scala.language.implicitConversions

/** Wrapper for astranaut node for using in switch-case statements */
final case class Node(name: String, data: Option[String], children: List[Node])

/** Companion object for easy node creating and conversions */
object Node {

  /** Converter from an astranaut node class */
  def apply(node: AstranautNode): Node = {
    val name = node.getTypeName
    val data = Option(node.getData).filter(_.nonEmpty)
    val children = node.getChildrenList.map(apply)

    Node(name, data, children)
  }

  /** Returns a node with the given name without data nor children */
  def apply(name: String): Node = {
    Node(name, None, Nil)
  }

  /** Returns a node with the given name and data without children */
  def apply(name: String, data: String): Node = {
    Node(name, Some(data), Nil)
  }

  /** Returns a node with the given children without data */
  def apply(name: String, children: List[Node]): Node = {
    Node(name, None, children)
  }

  /** Returns a node with the given data and children */
  def apply(name: String, data: String, children: List[Node]): Node = {
    Node(name, Some(data), children)
  }

  /** Converter to an astranaut node class */
  implicit def toAstranautNode(node: Node): AstranautNode = {
    val result = new DraftNode.Constructor
    result.setName(node.name)
    node.data.foreach(result.setData)
    result.setChildrenList(node.children.map(toAstranautNode))

    result.createNode
  }

  /** Converter to an astranaut node class */
  implicit def fromAstranautNode(node: AstranautNode): Node = Node(node)

  /** Implicit conversion from java list to scala list */
  private implicit def ListAsScala[A](list: JList[A]): List[A] = {
    import scala.jdk.CollectionConverters.CollectionHasAsScala
    list.asScala.toList
  }

  /** Implicit conversion from scala list to java list */
  private implicit def ListAsJava[A](list: List[A]): JList[A] = {
    import scala.jdk.CollectionConverters.SeqHasAsJava
    list.asJava
  }
}
