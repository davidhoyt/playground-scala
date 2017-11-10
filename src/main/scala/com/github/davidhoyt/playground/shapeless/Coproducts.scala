package com.github.davidhoyt.playground.shapeyless

object Coproducts extends App {
  import shapeless._
  import shapeless.HList
  import scala.reflect._

  type Converter[A] = String => Unit
  type DStream[A] = List[A]
  type EventNamespace = String
  type EventName = String

  case class TypedChannelKey[A](namespace: EventNamespace, name: EventName)(implicit tag: ClassTag[A])

  def gimme[A, H <: Converter[A], T <: HList](given: H :: T): Unit = {
    //
  }

  sealed case class Foo[A](value: A)
  sealed case class Bar[B](value: B)

  type Q = Foo[String] :+: Bar[Int] :+: CNil

  object poly1 extends Poly1 {
    implicit def caseFoo = at[Foo[String]](f => f.value + "!")
    implicit def caseBar = at[Bar[Int]](b => b.value.toString + "?!")
  }

  def run() = {
    val q = shapeless.Coproduct[Q](new Bar(1))
    println(q.select[Bar[Int]])

    q.map(poly1)

//    import record._, union._, syntax.singleton._
//
//    type U = Union.`'i -> String, 's -> String, 'b -> Boolean`.T
//
//    val u = Coproduct[U]('s ->> "foo") // Inject a String into the union at label 's

//    println(u.get('i))
//    println(u.get('s))
//    println(u.get('b))
//    u.get('i) should be()
//    u.get('s) should be(
//    )
//    u.get('b) should be(
//    )
  }

  run()
}
