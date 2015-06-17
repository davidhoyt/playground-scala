package com.github.davidhoyt.playground.learn

object Monoids extends App {
  trait Monoid[A] {
    def zero: A
    def concat(first: A, second: A): A
  }

  object IntegerAdditionMonoid extends Monoid[Int] {
    val zero = 0

    override def concat(first: Int, second: Int): Int =
      first + second
  }

  object IntegerMultiplicationMonoid extends Monoid[Int] {
    override def zero: Int = 1

    override def concat(first: Int, second: Int): Int =
      first * second
  }

  object ListMonoid extends Monoid[List[Int]] {
    override def zero: List[Int] = List.empty[Int]

    override def concat(first: List[Int], second: List[Int]): List[Int] =
      first ++ second
  }

  //val m: Monoid[Int] = IntegerMultiplicationMonoid
//  println(m.concat(2, m.zero) == 2)
//  println(m.concat(m.zero, 2) == 2)
//  println(m.concat(m.zero, m.zero) == m.zero)
  val m: Monoid[List[Int]] = ListMonoid
  println(m.concat(List(1, 2), m.zero) == List(1, 2))
  println(m.concat(m.zero, List(1, 2)) == List(1, 2))
  println(m.concat(m.zero, m.zero) == m.zero)
}
