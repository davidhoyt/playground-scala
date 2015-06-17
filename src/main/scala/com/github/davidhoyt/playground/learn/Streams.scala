package com.github.davidhoyt.playground.learn

object Streams extends App {
  //
  def sieve(s: Stream[Int]): Stream[Int] = {
    s.head #:: sieve(s.tail.filter(_ % s.head != 0))
  }

  // All primes as a lazy sequence
  val primes = sieve(Stream.from(2))

  // Dumping the first five primes
  print(primes.take(5).toList)
}
