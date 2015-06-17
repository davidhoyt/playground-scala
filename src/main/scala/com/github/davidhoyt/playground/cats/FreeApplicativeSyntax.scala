package com.github.davidhoyt.playground.cats

object FreeApplicativeSyntax {
  import cats._, free._

  implicit class Ops[F[_], G](val fa: FreeApplicative[F, G]) extends AnyVal {
    def <*>[A, R](fa2: FreeApplicative[F, A])(implicit curry: Curry.Aux[G, A, R]): FreeApplicative[F, R] = {
      val bar: G => A => R = curry.apply _
      val foo: FreeApplicative[F, A => R] = fa map bar
      fa2.ap(fa map curry.apply)
    }
  }
}
