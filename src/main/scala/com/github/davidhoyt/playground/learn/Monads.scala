package com.github.davidhoyt.playground.learn

object Monads extends App{

  trait Monoid[T] {
    val zero: T
    def concat(A: T,B: T): T
  }

  implicit object intAdd extends Monoid[Int]{
    override val zero: Int = 0

    override def concat(A: Int, B: Int): Int = A + B
  }

  implicit  object intMultiply extends Monoid[Int]{
    override val zero: Int = 1

    override def concat(A: Int, B: Int): Int = A * B
  }

  implicit object stringM extends Monoid[String]{
    override val zero: String = ""

    override def concat(A: String, B: String): String = A + B
  }

  println(combineN("hello", " world"))

  def combineThree[T](x: T, y: T, z: T)( implicit m: Monoid[T]): T = {
    m.concat(m.concat(x,y),z)
  }

  def combineN[T](x: T*)(implicit m: Monoid[T]): T = x match {
    case Seq() => m.zero
    case Seq(x) => x
    case _ => x.reduce((a: T,b: T) => m.concat(a, b))
  }

  def combineNFold[T](x: T*)(implicit m: Monoid[T]): T = x.foldLeft(m.zero)(m.concat)

  println(combineNFold(2,5,1)(intMultiply))

  trait Functor[T[_]] {
    def map[A,B](a: T[A])(fn: A => B): T[B]
  }
  trait Monad[T[_]] extends Functor[T] {
    def point[U](a: U): T[U]

    override def map[A, B](a: T[A])(fn: A => B): T[B] = bind(a)(fn andThen point[B])

    def bind[A, B](C: T[A])(fn: A => T[B]): T[B]
  }

  def composeFunctions[A, B, C](fn1: A => B, fn2: B => C): A => C =
    (x: A) => fn2(fn1(x))

  object listMonad extends Monad[List]{
    override def point[U](a: U): List[U] = List(a)

    override def bind[A, B](C: List[A])(fn: (A) => List[B]): List[B] = C.flatMap(fn)
  }

  trait NaturalTransformation[F[_], G[_]] {
    def transform[A](f: F[A]): G[A]
  }

  //((* -> *), (* -> *)) -> *

  type ~>[F[_], G[_]] = NaturalTransformation[F, G]

  implicit def identityNaturalTrans[F[_]]: NaturalTransformation[F, F] = new NaturalTransformation[F, F] {
    override def transform[A](f: F[A]): F[A] = f
  }

  implicit object ListToVectorNaturalTrans extends (List ~> Vector) {
    override def transform[A](f: List[A]): Vector[A] = f.toVector
  }

  implicit object listFunctor extends Functor[List]{
    override def map[A, B](a: List[A])(fn: (A) => B): List[B] =
      a.foldLeft(List.empty[B]) {
        case (lst, curr) => fn(curr) +: lst
      }.reverse
  }

  def mapAnything[A, B, T[_]](t: T[A])(fn: A => B)(implicit myFunctor: Functor[T]): T[B] ={
    myFunctor.map(t)(fn)
  }

  def mapAnythingNatTrans[A, B, F[_], G[_]](f: F[A])(fn: A => B)(implicit myFunctor: Functor[F], natTrans: NaturalTransformation[F, G]): G[B] ={
    natTrans.transform(myFunctor.map(f)(fn))
  }

  def printVector(v: Vector[String]): Unit =
    println(v)

//  printVector(mapAnythingNatTrans(List(0, 1, 2))(_ + "**"))

  println(mapAnythingNatTrans(List(0,1,2))(_ + "--")(listFunctor, ListToVectorNaturalTrans))

  trait Monadic[F[_], +A] {
    def point[B](a: B): F[B]
    def filter[A1 >: A](fn: A => Boolean): F[A1]
    def map[B](fn: A => B): F[B] = flatMap(fn andThen point[B])
    def flatMap[B](fn: A => F[B]): F[B]
  }

  sealed trait joeOption[+A] extends Monadic[joeOption, A] {
    override def point[B](a: B): joeOption[B] = SomeJoeOption(a)

    override def filter[A1 >: A](fn: A => Boolean): joeOption[A1] = this match {
      case NoneJoeOption => NoneJoeOption
      case SomeJoeOption(a) =>
        if (fn(a)) this
        else NoneJoeOption
    }

    override def flatMap[B](fn: A => joeOption[B]): joeOption[B] = this match{
      case NoneJoeOption => NoneJoeOption
      case SomeJoeOption(a) => fn(a)
    }
  }

  case class SomeJoeOption[A](a: A) extends joeOption[A]
  object NoneJoeOption extends joeOption[Nothing]

  val jOpt = SomeJoeOption(1)
  val jOpt2 = SomeJoeOption(101)

  val result =
    for {
      myInt <- jOpt
      myInt2 <- jOpt2
      if myInt2 > 100
    } yield (myInt + myInt2).toString + "???"

  println(result)
}
