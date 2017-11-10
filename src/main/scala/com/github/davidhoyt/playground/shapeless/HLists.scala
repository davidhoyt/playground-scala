package com.github.davidhoyt.playground.shapeless

import algebra.Semigroup
import cats.data.Kleisli

object HLists extends App {
  import _root_.shapeless._

  val hlist = 100 :: "test" :: HNil

  case class Foo(i: Int, s: String)

  val gen = LabelledGeneric[Foo]
  val testHList = gen.to(Foo(100, "test"))
  val testFoo = gen.from(testHList)

  println(Foo(0, "").toString)

//  object shapelessCats {
//    import _root_.shapeless._
//    import cats.Semigroup
//
//    def combine[H <: HList : combineOps.Combiner](h1: H, h2: H) = combineOps(h1, h2)
//
//    private[this] object combineOps {
//
//      def apply[H <: HList : Combiner](t1: H, t2: H) = Combiner[H].combine(t1, t2)
//
//      class Combiner[H <: HList](val combine: (H, H) => H)
//      object Combiner {
//        def apply[H <: HList](implicit combiner: Combiner[H]): Combiner[H] = combiner
//      }
//
//      implicit object HNilCombiner1 extends Combiner[HNil.type]((_, _) => HNil)
//      implicit object HNilCombiner2 extends Combiner[HNil]((_, _) => HNil)
//
//      implicit def NonEmptyCombiner[H : Semigroup, T <: HList : Combiner] = new Combiner[H :: T]({
//        case (h1 :: t1, h2 :: t2) => Semigroup[H].combine(h1, h2) :: combine[T](t1, t2)
//      })
//    }
//
//  }

//  println(combine(h1, h2))

  trait ShapelessCatsSemigroup {
    implicit object HNilSemigroup extends Semigroup[HNil] {
      override def combine(h1: HNil, h2: HNil): HNil = HNil
    }

    implicit def HListSemigroup[H : Semigroup, T <: HList : Semigroup]: Semigroup[H :: T] =
      new Semigroup[H :: T] {
        override def combine(hlist1: H :: T, hlist2: H :: T): H :: T = (hlist1, hlist2) match {
          case (h1 :: t1, h2 :: t2) => Semigroup[H].combine(h1, h2) :: Semigroup[T].combine(t1, t2)
        }
      }
  }

  implicit class ShapelessCatsSemigroupOps[H <: HList](val h1: H) extends AnyVal {
    def combine(h2: H)(implicit semigroup: Semigroup[H]): H = semigroup.combine(h1, h2)
  }

  object ShapelessCats
    extends ShapelessCatsSemigroup

  import _root_.shapeless._
  import cats.std.all._
  import ShapelessCats._

  val h1: String :: Int :: HNil = "foo" :: 2 :: HNil
  val h2: String :: Int :: HNil = "bar" :: 3 :: HNil

  val h3 = "abc" :: h1 :: HNil
  val h4 = "def" :: h2 :: HNil

  println(Semigroup[HNil].combine(HNil, HNil))
  println(Semigroup[String :: Int :: HNil].combine(h1, h2))
  println(h1.combine(h2))
  println(h3.combine(h4))

//  val s = Kleisli.
}
