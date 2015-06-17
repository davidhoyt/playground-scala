package com.github.davidhoyt.playground.learn

object Currying extends App {
  def myMethod(x: Int, y: String): String = x + y
  val myFunction: (Int, String) => String = myMethod _
  val myCurriedFunction: Int => (String => String) = myFunction.curried
  val myCurriedFunctionDesugared =
    new Function1[Int, Function1[String, String]] {
      def apply(x: Int): Function1[String, String] =
        new Function1[String, String] {
          def apply(y: String): String =
            myMethod(x, y)
        }
    }

  def transform(value: String, fn: String => String): String =
    fn(value) + "!"

  println(transform("hello", (x: String) => x + " world"))

  println(transform("hello", myCurriedFunction(123)))

  val myPartiallyAppliedCurriedFunction = myCurriedFunction(456)

  println(transform("hi", myPartiallyAppliedCurriedFunction))
  println(transform("yo", myPartiallyAppliedCurriedFunction))
}
