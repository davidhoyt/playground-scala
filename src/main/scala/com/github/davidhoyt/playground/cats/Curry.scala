package com.github.davidhoyt.playground.cats

trait Curry[F] {
  type Argument
  type Result
  def apply(f: F)(a: Argument): Result
}

object Curry {
  def apply[F](implicit ev: Curry[F]): Aux[F, ev.Argument, ev.Result] = ev

  type Aux[F, A, R] = Curry[F] {
    type Argument = A
    type Result = R
  }

  implicit def func1[A, R]: Aux[A => R, A, R] = new Curry[A => R] {
    override type Argument = A
    override type Result = R
    override def apply(f: A => R)(a: A): R =
      f(a)
  }
  implicit def func2[A, B, R]: Aux[(A, B) => R, A, B => R] = new Curry[(A, B) => R] {
    override type Argument = A
    override type Result = B => R
    override def apply(f: (A, B) => R)(a: A): B => R =
      b => f(a, b)
  }
  implicit def func3[A, B, C, R]: Aux[(A, B, C) => R, A, (B, C) => R] = new Curry[(A, B, C) => R] {
    override type Argument = A
    override type Result = (B, C) => R
    override def apply(f: (A, B, C) => R)(a: A): (B, C) => R =
      (b, c) => f(a, b, c)
  }
}
