package com.github.davidhoyt.playground.shapeless

case class Foo(field1: String, field2: Option[Int])
case class Simple(field1: String)

object MergeApp extends App {
  import _root_.shapeless._

//  val mergeSimple3 = Merge.deriveMerge[Simple, String :: HNil]
//  val mergeSimple4 = Merge.summon[Simple]
  val mergeSimple5 = Merge.summon[Foo]

//  println(mergeSimple4.merge(Simple("test"), Simple(null)))
  println(mergeSimple5.merge(Foo("test", None), Foo("foo", Some(123))))
}
