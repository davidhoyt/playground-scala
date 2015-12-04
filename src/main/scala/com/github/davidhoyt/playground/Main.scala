package com.github.davidhoyt.playground

object Main extends App {
//  println("hello world")

def sum(xs: List[Int]): Int = {
  @annotation.tailrec
  def step(soFar: Int, xs: List[Int]): Int =
    xs match {
      case head :: tail => step(soFar + head, tail)
      case _ => soFar
    }
  step(0, xs)
}

  def sum0(xs: List[Int]): Int  = {
    var sum = 0
    for (value <- xs)
      sum += value
    sum
  }

  //println(sum0(List(1, 2, 3, 4)))

  case class Observable(listeners: List[Any => Any] = List.empty) {
    def emit(value: Any): Unit = listeners foreach (_.apply(value))
    def register(listener: Any => Any) = copy(listeners = listeners :+ listener)
    def flatMap(listener: Any => Observable): Observable =
      register(listener)
    def map(listener: Any => Any): Observable =
      register(listener)
  }

  val source1 = Observable()
  val source2 = Observable()

  val obs1 = for {
    o1 <- source1
  } yield {
    println("I GOT " + o1)
    o1 + "???"
  }
  obs1.emit("A")
  obs1.emit("B")
}

