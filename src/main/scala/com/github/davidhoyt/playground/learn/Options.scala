package com.github.davidhoyt.playground.learn

object Options extends App {
  val x: String = "xyz"
  val xOpt: Option[String] = Option(x)
  val xLen: Option[Int] = xOpt.map(_.length)
  val xShouldBe3: Option[Int] = xOpt.map(_.length).filter(_ == 3)
  val xNewOpt: Option[String] = xLen.flatMap((x: Int) => Some("hello " + x))

  val xWithSugar: Option[String] =
    for {
      x <- xOpt
      len = x.length
      if len == 3
    } yield "hello " + len

  println(xWithSugar)
}
