package com.github.davidhoyt.playground.learn

object Implicits extends App {
  //Implicit conversions
  class Foo(val x: String)
  class Bar(val x: Int)

  def giveMeAFoo(foo: Foo): Unit =
    println(foo.x)

  implicit def convertBarToFoo(bar: Bar): Foo =
    new Foo(bar.x.toString)

  implicit val bar = new Bar(111)

  //giveMeAFoo(convertBarToFoo(bar))
  giveMeAFoo(bar)


  def giveMeAFoo2(s: String)(implicit bar: Bar): Unit =
    println(s + "-" + bar.x)

  giveMeAFoo2("hello")

  trait Equals[A] {
    def thisEqualsThat(first: A, second: A): Boolean
  }

  object Equals {
    implicit val intEquals = new Equals[Int] {
      override def thisEqualsThat(first: Int, second: Int): Boolean = first == second
    }
  }

  def areTheseEqual(x: Int, y: Int)(implicit eq: Equals[Int]): Boolean =
    eq.thisEqualsThat(x, y)

  implicit val customIntEquals = new Equals[Int] {
    override def thisEqualsThat(first: Int, second: Int): Boolean = true
  }

  val customIntEquals2 = new Equals[Int] {
    override def thisEqualsThat(first: Int, second: Int): Boolean = false
  }

  println(areTheseEqual(1, 2))
}
