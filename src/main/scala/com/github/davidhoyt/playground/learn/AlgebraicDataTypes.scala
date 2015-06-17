package com.github.davidhoyt.playground.learn

//ADTs for short
object AlgebraicDataTypes extends App {
  sealed trait TrafficLight
  case object Red extends TrafficLight
  case object Yellow extends TrafficLight
  case object Green extends TrafficLight

  def canGo(light: TrafficLight): Boolean =
    light match {
      case Red => false
      case Yellow | Green => true
    }
}