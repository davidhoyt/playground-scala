package com.github.davidhoyt.playground.learn

import rx.Observable

object ImplicitConversions extends App {
  import rx.functions._
  import rx.lang.scala.ImplicitFunctionConversions._
  import rx.lang.scala.JavaConversions._

  val func1Instance: Func1[Int, String] = new Func1[Int, String] {
    override def call(i: Int): String = i.toString + "!"
  }

  println(func1Instance.call(123))

  trait Context
  trait Index
  trait Source[T] extends rx.functions.Func2[Context, Index, rx.Observable[rx.Observable[T]]]

  implicit def function2ToSource[R](fn: (Context, Index) => rx.lang.scala.Observable[rx.lang.scala.Observable[R]]): Source[R] =
    new Source[R] {
      override def call(context: Context, index: Index): rx.Observable[rx.Observable[R]] = {
        val result = fn(context, index)
        toJavaObservable(result.map(obs => toJavaObservable(obs).asInstanceOf[rx.Observable[R]]))
          .asInstanceOf[rx.Observable[rx.Observable[R]]]
      }
    }

  implicit def byNameToSource[R](fn: => rx.lang.scala.Observable[rx.lang.scala.Observable[R]]): Source[R] =
    function2ToSource((_: Context, _: Index) => fn)

  val s: Source[Long] = (_: Context, _: Index) => rx.lang.scala.Observable.just(rx.lang.scala.Observable.just(1L , 2L, 3L))
  val s2: Source[Long] = rx.lang.scala.Observable.just(rx.lang.scala.Observable.just(1L , 2L, 3L))

//  new Source[Long] {
//    override def call(context: Context, index: Index): rx.Observable[rx.Observable[Long]] = {
//      return rx.Observable.just(rx.Observable.just(1L, 2L, 3L, 4L))
//    }
//  }

  val func1InstanceFromScala: Func1[Int, String] = (i: Int) => i.toString + "!"

  println(func1InstanceFromScala.call(456))

  implicit def func1ToScala[T1, R](fn: Func1[T1, R]): T1 => R =
    (t1: T1) => fn.call(t1)

  val func1InstanceToScala: Int => String = func1Instance

  println(func1InstanceToScala(789))

  val f: Function1[Int, String] = ???


  def needsRxObservable[A](obs: rx.lang.scala.Observable[A]): rx.Observable[_ <: A] =
    toJavaObservable[A](obs)
}
