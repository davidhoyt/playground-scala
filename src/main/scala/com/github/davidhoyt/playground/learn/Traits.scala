package com.github.davidhoyt.playground.learn

object Traits extends App {
  trait Foo {
    def x: Int
    val y: Int
  }

  class MyFoo extends Foo {
    def x = 0
    val y = 1
  }

  class MyFoo2 extends Foo {
    val x = 0
    val y = 1
  }

  class MyFoo3 extends Foo {
    lazy val x = 0
    lazy val y = 1
  }

  trait Bar {
    val z: Int
  }

  class MyFooWithBar(val x: Int = 0, val y: Int = 1, val z: Int = 2) extends Foo with Bar

  new MyFooWithBar(y = 10)

  trait ProvidesA {
    val a: Int
  }

  trait ProvidesB {
    val b: Int
  }

  trait NeedsAandB { self: ProvidesA with ProvidesB =>
    def foo = a + b + 2
  }

  class MyC(val a: Int = 0, val b: Int = 1) extends NeedsAandB with ProvidesA with ProvidesB
}
