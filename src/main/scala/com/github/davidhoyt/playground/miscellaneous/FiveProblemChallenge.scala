package com.github.davidhoyt.playground.miscellaneous

object FiveProblemChallenge extends App {
  object problem1 {
    def withFor(xs: List[Int]): Long = {
      var sum = 0
      for (x <- xs) {
        sum += x
      }
      sum
    }

    def withWhile(xs: List[Int]): Long = {
      var sum = 0
      var index = 0
      while (index < xs.length) {
        sum += xs(index)
        index += 1
      }
      sum
    }

    def withRecursion(xs: List[Int]): Long = {
      def step(soFar: Int, xs: List[Int]): Long =
        if (xs.isEmpty)
          soFar
        else
          step(soFar + xs.head, xs.tail)

      step(0, xs)
    }
  }

  object problem2 {
    def zipLists[A, B](xs: List[A], ys: List[B]): List[Any] = {
      def step(soFar: List[Any], xs: List[A], ys: List[B]): List[Any] =
        if (xs.isEmpty || ys.isEmpty) soFar
        else step(soFar :+ xs.head :+ ys.head, xs.tail, ys.tail)
      step(List.empty, xs, ys)
    }
  }

  object problem3 {
    def fib(): Stream[BigInt] = {
      lazy val s: Stream[BigInt] = 0 #:: 1 #:: s.zip(s.tail).map { case (x, y) => x + y }
      //0 #:: 1 #:: fib.zip(fib.tail).map(p => p._1 + p._2)
      s
    }

    def fib100(): List[BigInt] = {
      val s = fib()
      fib.take(100).toList
    }
  }

  object problem4 {
    def listToInt(xs: List[Int]): Long =
      xs.foldLeft("")(_ + _.toString).toInt

    def largestPossible(xs: List[Int]): Long = {
      xs.permutations.map(listToInt).map { l => println(l); l}.toList.sorted.last
    }
  }

  object problem5 {
//    def allPossibilities()
  }

  println(problem1.withFor(List(3, 4, 5)))
  println(problem1.withWhile(List(3, 4, 5)))
  println(problem1.withRecursion(List(3, 4, 5)))

  println(problem2.zipLists(List("a", "b", "c"), List(1, 2, 3)))

  println(problem3.fib100())

  println(problem4.largestPossible(List(50, 2, 1, 9)))
}
