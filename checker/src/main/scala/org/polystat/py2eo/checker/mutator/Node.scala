package org.polystat.py2eo.checker.mutator

import org.cqfn.astranaut.base
import org.cqfn.astranaut.base.DraftNode

import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.jdk.CollectionConverters.SeqHasAsJava
import scala.language.implicitConversions

/** Wrapper for astranaut node for using in switch-case statements */
case class Node(name: String, data: Option[String], children: List[Node] = Nil)

/** Companion object for easy node creating */
object Node {

  /** Converter from an astranaut node class */
  def apply(node: base.Node): Node = {
    val name = node.getTypeName
    val data = Option(node.getData).filter(_.nonEmpty)
    val children = node.getChildrenList.asScala.toList.map(apply)

    Node(name, data, children)
  }

  /** Returns a node with the given name without data nor children */
  def apply(name: String): Node = Node(name, None)

  /** Returns a node with the given name and data without children */
  def apply(name: String, data: String): Node = Node(name, Some(data))

  /** Returns a node with the given children without data */
  def apply(name: String, children: List[Node]): Node = Node(name, None, children)

  /** Returns a node with the given data and children */
  def apply(name: String, data: String, children: List[Node]): Node = Node(name, Some(data), children)

  /** Converter to an astranaut node class */
  implicit def toAstranautNode(node: Node): base.Node = {
    val result = new DraftNode.Constructor
    result.setName(node.name)
    node.data.foreach(result.setData)
    result.setChildrenList(node.children.map(toAstranautNode).asJava)

    result.createNode
  }

  /** Converter to an astranaut node class */
  implicit def fromAstranautNode(node: base.Node): Node = Node(node)
}
