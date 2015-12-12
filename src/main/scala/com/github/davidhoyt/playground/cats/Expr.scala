package com.github.davidhoyt.playground.cats

object CatsExpr extends App {
//  import algebra._, std.int._
//  import cats._, free._, Free._
//  import scala.language.implicitConversions
//
//  val f = -1.4534549038922301E307
//  val g = -1.4534549038922291E307
//
////  val f = 100.01
////  val g = 100.0100000023
//
//def withinRange(x: Double, y: Double): Boolean = {
//  val tolerance = 0.0000000000000009
//  math.abs(x - y) <= tolerance * math.abs(y)
//}
//
//  assert(withinRange(-1.4534549038922301E307, -1.4534549038922291E307))
//  assert(withinRange(1.4534549038922291E307, 1.4534549038922301E307))
//  assert(withinRange(100, 100.00000000000001))
//
//  trait Expr[A]
//  case class Const[A](term: A) extends Expr[A]
//  case class Add[A](e1: Expr[A], e2: Expr[A])(implicit val r: Ring[A]) extends Expr[A]
//
//  type Exp[A] = FreeC[Expr, A]
//
//  implicit def int2App(i: Int): Expr[Int] =
//    Const(i)
//
//  def const[A](a: A): Exp[A] =
//    liftFC(Const(a))
//
//  def add[A : Ring](e1: Expr[A], e2: Expr[A]): Exp[A] =
//    liftFC(Add(e1, e2))
//
//  def liftExpr[A](e: Expr[A]): Exp[A] = e match {
//    case Const(e) => const(e)
//    case a @ Add(e1, e2) => add(e1, e2)(a.r)
//  }
//
//  val program: Exp[Int] =
//    for {
//      x <- add(1, 2)
//    } yield x
//
//  implicit object transform extends (Expr ~> Id) {
//    def apply[A](expr: Expr[A]): Id[A] = expr match {
//      case Const(a) => a
//      case a @ Add(e1: Expr[A], e2: Expr[A]) => a.r.plus(apply(e1), apply(e2))
//    }
//  }
//
//  // foldMap() does not work since Expr is not a Functor.
//  // program.foldMap[Id](transform)
//
//  def compile[A](program: Exp[A])(implicit nat: Expr ~> Id): () => A = () =>
//    runFC(program)(nat)
//
//  val compiledProgram = compile(program)
//  println(compiledProgram())
//  println(compiledProgram())
}