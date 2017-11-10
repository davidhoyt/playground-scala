package com.github.davidhoyt.playground.shapeless

trait Merge[T] {
  def merge(first: T, second: T): T
}

object Merge extends LowPriorityMergeImplicits {
  import scalaz.Semigroup

  @inline def summon[T](implicit M: Merge[T]) = M

//  implicit def biFunction[T](implicit M: Merge[T]): (T, T) => T =
//    (first, second) => M.merge(first, second)
//
//  implicit def curried[T](implicit M: Merge[T]): T => T => T =
//    first => second => M.merge(first, second)
//
  implicit def optionMerge[T]: Merge[Option[T]] =
    new Merge[Option[T]] {
      override def merge(first: Option[T], second: Option[T]): Option[T] = {
        println("OPTION MERGE")
        first orElse second
      }
    }
//
//  implicit def fromSemigroup[T : Semigroup]: Merge[T] =
//    new Merge[T] {
//      override def merge(first: T, second: T): T =
//        Semigroup[T].append(first, second)
//    }


}

trait LowPriorityMergeImplicits {
  implicit val stringMerge = new Merge[String] {
    override def merge(first: String, second: String): String = {
      println(s"STRING MERGE: $first vs $second")
      if (first eq null) second
      else first
    }
  }

  import _root_.shapeless.{HNil, HList, Lazy, Generic, ::}

  implicit object hnilMerge extends Merge[HNil] {
    override def merge(first: HNil, second: HNil): HNil = HNil
  }

  implicit def hconsMerge[H, T <: HList](implicit
                                         mergeHead: Merge[H],
                                         mergeTail: Merge[T]
                                        ): Merge[H :: T] =
    new Merge[H :: T] {
      override def merge(first: H :: T, second: H :: T): H :: T = {
        println(s"hlistMerge: ${first.head} vs ${second.head}")
        mergeHead.merge(first.head, second.head) :: mergeTail.merge(first.tail, second.tail)
      }
    }

  implicit def deriveMerge[A, M](implicit gen: Generic.Aux[A, M], reprMerge: Lazy[Merge[M]]): Merge[A] =
    new Merge[A] {
      override def merge(first: A, second: A): A = {
        println(s"$first vs $second")
        gen.from(reprMerge.value.merge(gen.to(first), gen.to(second)))
      }
    }

//  def anyRefMerge[T <: AnyRef]: Merge[T] =
//    new Merge[T] {
//      override def merge(first: T, second: T): T = {
//        println(s":( $first $second ")
//        if (first eq null) second
//        else first
//      }
//    }
}
