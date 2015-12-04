package com.github.davidhoyt.playground.learn

class FreeMonads extends App {
  trait Monad[F[_]] {
    def point[A](a: A): F[A]
    def bind[A, B](given: F[A])(fn: A => F[B]): F[B]
    def flatMap[A, B](given: F[A])(fn: A => F[B]): F[B] = bind(given)(fn)

    def map[A, B](given: F[A])(fn: A => B): F[B] = bind[A, B](given)(fn andThen point)
  }

  object Monad {
    def apply[F[_] : Monad]: Monad[F] = implicitly[Monad[F]]
  }

  object ListMonad extends Monad[List] {
    override def point[A](a: A): List[A] = List(a)

    override def bind[A, B](given: List[A])(fn: A => List[B]): List[B] = given flatMap fn
  }

  sealed trait FreeMonad[F[_], A] {

  }

//  case class Return[A](given : A) extends FreeMonad[Return, A]
//  case class Suspend[A](next: => A) extends FreeMonad[Suspend, A]
}
