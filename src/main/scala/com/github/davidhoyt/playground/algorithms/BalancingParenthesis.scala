package com.github.davidhoyt.playground.algorithms

object BalancingParenthesis extends App {
  import scala.language.implicitConversions

  def balance(chars: List[Char]): Boolean = {
    def foo(remainder: List[Char], parensCount: Int = 0): Int = {
      if (parensCount < 0) parensCount
      else remainder match {
        case '(' :: tail => foo(tail, parensCount + 1)
        case ')' :: tail => foo(tail, parensCount - 1)
        case _ :: tail => foo(tail, parensCount)
        case _ => parensCount
      }
    }
    foo(chars) == 0
  }
  println(balance2("""(()((()a)())""".toList))

  def balance2(s: List[Char]): Boolean = {
    var i = 0
    val (count, result) = s.foldLeft((0, true)) {
      case ((0, _), ')') =>
        (-1, false)
      case ((count, soFar), '(') =>
        (count + 1, soFar)
      case ((count, soFar), ')') =>
        (count - 1, soFar)
      case (continue, _) => continue
    }
    result && count % 2 == 0
  }
}
