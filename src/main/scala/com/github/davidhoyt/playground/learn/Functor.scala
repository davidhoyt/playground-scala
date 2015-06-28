package com.github.davidhoyt.playground.learn

object Functors extends App {

  trait Functor[F[_]] {
    def map[A, B](given: F[A])(fn: A => B): F[B]
  }

  object ListFunctor extends Functor[List] {
    override def map[A, B](given: List[A])(fn: A => B): List[B] =
      given map fn
  }

  object SetFunctor extends Functor[Set] {
    override def map[A, B](given: Set[A])(fn: A => B): Set[B] =
      given map fn
  }

  object ListOfSetsFunctor extends Functor[({type F[A] = List[Set[A]]})#F] {
    override def map[A, B](given: List[Set[A]])(fn: A => B): List[Set[B]] =
      given map (_ map fn)
  }
}
