package com.github.davidhoyt.playground.learn

object ETAExpansion extends App {
   def myMethod(x: Int, y: String): String = x + y
   val myFunction = myMethod _
   val myFunctionAsObject = new Function2[Int, String, String] {
     def apply(x: Int, y: String): String = myMethod(x, y)
   }
 }
