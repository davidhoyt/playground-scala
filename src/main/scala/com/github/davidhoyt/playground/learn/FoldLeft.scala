package com.github.davidhoyt.playground.learn

import scala.annotation.tailrec

object FoldLeft extends App {
  val lst = List(0, 1, 2, 3)
  val mySum = lst.foldLeft(0)((last: Int, curr: Int) => last + curr)
  val mySum2 = foldLeft(lst, 0)((last: Int, curr: Int) => last + curr)
  val mySum3 = foldLeft2(lst, 0)((last: Int, curr: Int) => last + curr)

  println(s"$mySum vs $mySum2 vs $mySum3")

  def foldLeft[A, B](lst: List[A], zero: B)(accum: (B, A) => B): B = {
    var last = zero
    for (curr <- lst) {
      last = accum(last, curr)
    }
    last
  }

  @tailrec def foldLeft2[A, B](lst: List[A], zero: B)(accum: (B, A) => B): B = {
    //accum(accum(accum(zero, lst.head), lst.tail.head), lst.tail.tail.head)
    if(lst.isEmpty) zero
    else foldLeft2(lst.tail, accum(zero, lst.head))(accum)
  }

  @tailrec def foldLeft3[A, B](lst: List[A], zero: B)(accum: (B, A) => B): B = lst match {
    case head :: tail => foldLeft3(tail, accum(zero, head))(accum)
    case _ => zero
  }

//  println(reduce(List(2))((x: Int, y: Int) => x + y))
  println(reduce(List(2, 3, 4))(_ + _))
  println(reduce2(List(2, 3, 4))(_ + _))
  println(List(2, 3, 4).reduce(_ + _))

  def reduce[A](lst: List[A])(reducer: (A, A) => A): A = {
    def innerReduce(next: List[A], last: A): A =
      next match {
        case first :: second :: tail => innerReduce(tail, reducer(first, second))
        case head :: tail => reducer(last, head)
        case _ => last
      }
    lst match {
      case first :: second :: tail => innerReduce(tail, reducer(first, second))
      case first :: _ => first
      case _ => throw new IllegalStateException("??????")
    }
  }

  def reduce2[A](lst: List[A])(reducer: (A, A) => A): A = {
    if (lst.isEmpty) throw new IllegalStateException(":(")
    else foldLeft3[A, A](lst.tail, lst.head)(reducer)
  }

  def filter[A](lst: List[A])(pred: (A) => Boolean): List[A] =
    foldLeft3[A, List[A]](lst, List.empty[A])((last: List[A], curr: A) => {
      if (pred(curr)) last :+ curr
      else last
    })

  def filter2[A](lst: List[A])(pred: (A) => Boolean): List[A] =
    foldLeft3(lst, List.empty[A]) {
      case (last, curr) if pred(curr) => last :+ curr
      case (last, _) => last
    }

  println(filter(List(2, 3, 4))(_ % 2 == 0))

  val s = "hello world"
  def test[A](seq: Seq[A]): Int = seq.length
  println(s.filterNot(_ == 'o'))
}
