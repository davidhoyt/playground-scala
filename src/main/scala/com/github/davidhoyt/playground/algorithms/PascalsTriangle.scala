package com.github.davidhoyt.playground.algorithms

object PascalsTriangle extends App {
  //1
  //1 1
  //1 2 1
  //1 3 3 1
  def pascal(col: Int, row: Int): Int =
    if (col == 0 || col == row) 1
    else pascal(col - 1, row - 1) + pascal(col, row - 1)

  println(pascal(0, 0)) //1
  println(pascal(0, 1)) //1
  println(pascal(1, 1)) //1
  println(pascal(0, 2)) //1
  println(pascal(1, 2)) //2
  println(pascal(2, 2)) //1
}
