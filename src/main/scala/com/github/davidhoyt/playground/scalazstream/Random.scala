package com.github.davidhoyt.playground.scalazstream

import scalaz._

trait Sort[F[_]] {
  def sortBy[A, B](given: F[A])(fn: A => B)(implicit order: Order[B]): F[A]
}

object Sort {
  def apply[F[_]](implicit S: Sort[F]): Sort[F] = S

  implicit object list extends Sort[List] {
    override def sortBy[A, B](given: List[A])(fn: A => B)(implicit order: Order[B]): List[A] =
      given.sortBy(fn)(order.toScalaOrdering)
  }

  implicit object vector extends Sort[Vector] {
    override def sortBy[A, B](given: Vector[A])(fn: (A) => B)(implicit order: Order[B]): Vector[A] =
      given.sortBy(fn)(order.toScalaOrdering)
  }
}

trait Random[F[_]] extends Applicative[F] with Bind[F] with Traverse[F] with Zip[F] with Sort[F] { self =>
  def shuffle[A](fa: F[A]): F[A] = {
    val shuffled: F[F[A]] =
      shuffleE[F, Boolean, A](fa)(_ => self.point(scala.util.Random.nextBoolean))(self, scalaz.std.anyVal.booleanInstance)

    self.join(shuffled)
  }

  def shuffleE[M[_] : Applicative, E : Order, A](fa: F[A])(entropy: A => M[E]): M[F[A]] =
    Applicative[M].map(traverse(fa)(entropy)) { fe =>
      map(sortBy(zip(fe, fa))(_._1))(_._2)
    }
}

object Random extends RandomLowPriorityImplicits {
  import scalaz.std.list._
  import scalaz.std.vector._

  def apply[F[_]](implicit R: Random[F]): Random[F] =
    R

  def shuffle[R[_], A](given: R[A])(implicit R: Random[R]): R[A] =
    R.shuffle(given)

  implicit val list = materialize[List]
  implicit val vector = materialize[Vector]
}

trait RandomLowPriorityImplicits {
  implicit def materialize[F[_]](implicit A: Applicative[F], B: Bind[F], T: Traverse[F], Z: Zip[F], S: Sort[F]): Random[F] = new Random[F] {
    override def point[A](a: =>A): F[A] = A.point(a)
    override def bind[A, B](fa: F[A])(f: (A) => F[B]): F[B] = B.bind(fa)(f)
    override def zip[A, B](a: => F[A], b: => F[B]): F[(A, B)] = Z.zip(a, b)
    override def sortBy[A, B](given: F[A])(fn: A => B)(implicit order: Order[B]): F[A] = S.sortBy(given)(fn)(order)
    override def traverseImpl[G[_], A, B](fa: F[A])(f: A => G[B])(implicit AG: Applicative[G]): G[F[B]] = T.traverseImpl(fa)(f)(AG)
  }
}

object RandomTest {
  import scalaz.std.list._
  import scalaz.std.vector._
  import Random._

  def main(args: Array[String]): Unit = {
    val shuffled = Random.shuffle(List(1, 2, 3, 4, 5))
    println(shuffled)
  }
}