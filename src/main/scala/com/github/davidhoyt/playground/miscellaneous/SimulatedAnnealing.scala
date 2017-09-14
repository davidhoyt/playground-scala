package com.github.davidhoyt.playground.miscellaneous

import scala.util.Random

object SimulatedAnnealing extends App {
  //
  def randomVector(n: Int): Vector[Int] = {
    def step(left: Int, soFar: Vector[Int]): Vector[Int] =
      if (left == 0)
        soFar
      else
        step(left - 1, Random.nextInt(1000) +: soFar)
    step(n, Vector.empty)
  }

  val values = randomVector(100)

  import math._

  def findMax(values: Vector[Int]): Int = {
    require(values.nonEmpty)

    def searchLocally(index: Int): Int =
      min(values.length - 1, max(0, index + Random.nextInt(3) - 1))

    def searchLarge(index: Int): Int =
      min(values.length - 1, max(0, index + Random.nextInt(values.length - 1) - (values.length >>> 1)))

    def score(value: Int): Double =
      value.toDouble

    var temp = 1.0
    var lastIndex: Int = 0
    var bestSoFar: Int = values(0)
    var bestScoreSoFar: Double = score(bestSoFar)

    var steps = 0

    while (temp > 0.000001) {
      val nextIndex =
        if (temp > Random.nextDouble())
          searchLarge(lastIndex)
        else
          searchLocally(lastIndex)

      val nextValue = values(nextIndex)
      val nextScore = score(nextValue)

      if (nextScore > bestScoreSoFar) {
        bestSoFar = nextValue
        bestScoreSoFar = nextScore
      }

      lastIndex = nextIndex
      temp *= 0.9

      steps += 1
    }

    //println(steps)
    //println(values.length >>> 1)

    bestSoFar
  }

  def findMaxNormal(values: Vector[Int]): Int =
    values.max

  println(s"Given: $values")
  println(s" Actual max:              ${findMaxNormal(values)}")
  println(s" Simulated annealing max: ${findMax(values)}")
}
