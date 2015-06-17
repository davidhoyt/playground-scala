package com.github.davidhoyt.playground.learn

object DefValLazyVal extends App {
  class Foo {
    def defX: Int = {
      println("defx")
      0
    }

    val valX: Int = {
      println("valx")
      1
    }

    lazy val lazyValX: Int = {
      println("lazyValX")
      2
    }
  }

  val foo = new Foo()
  foo.defX
  foo.defX
  foo.lazyValX
  foo.lazyValX
}
