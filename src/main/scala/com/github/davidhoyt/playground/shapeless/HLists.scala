package com.github.davidhoyt.playground.shapeless

object HLists extends App {
  import _root_.shapeless._

  val hlist = 100 :: "test" :: HNil

  case class Foo(i: Int, s: String)

  val gen = LabelledGeneric[Foo]
  val testHList = gen.to(Foo(100, "test"))
  val testFoo = gen.from(testHList)

  println(Foo(0, "").toString)
}
