package com.github.davidhoyt.playground.miscellaneous

// http://milessabin.com/blog/2011/06/09/scala-union-types-curry-howard/
object UnboxedUnionTypes extends App {
  type ¬[A] = A => Nothing
  type ¬¬[A] = ¬[¬[A]]
  type ∨[T, U] = ¬[¬[T] with ¬[U]]
  type |∨|[T, U] = { type λ[X] = ¬¬[X] <:< (T ∨ U) }

  def size[T: (Int |∨| String)#λ](t: T) =
    t match {
      case i: Int => i
      case s: String => s.length
    }

  type Given[A] = {
    type ThatImpliesFalse[B] = B => Nothing
    type ImpliesFalse = ThatImpliesFalse[A]
    type ImpliesTrue = ThatImpliesFalse[ImpliesFalse]
    type Or[B] = ThatImpliesFalse[ImpliesFalse with ThatImpliesFalse[B]]
  }

  type Prove[T] = {
    type IsEither[A, B] = Given[T]#ImpliesTrue <:< (Given[A]#Or[B])
  }

  trait Writer[-T]
}
