package com.github.davidhoyt.playground.cats

object HelloWorld extends App {
  import cats._

  def monadUnit[F[+_] : Monad, A](instance: A): F[A] =
    Monad[F].pure(instance)

  implicit object OptionM extends Monad[Option] {
    override def pure[A](x: A): Option[A] = Option(x)
    override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] = fa flatMap f
  }

  implicit object SeqM extends Monad[Seq] {
    override def pure[A](x: A): Seq[A] = Seq(x)
    override def flatMap[A, B](fa: Seq[A])(f: (A) => Seq[B]): Seq[B] = fa flatMap f
  }

  val unit = monadUnit[Option, String]("test")
  println(unit)
}
