package com.github.davidhoyt.playground.cats

import cats.data.Const
import scala.reflect.runtime.universe._

//https://github.com/alexknvl/free-applicative-cats/blob/master/src/main/scala/Main.scala

object CommandLineParser extends App {
  import cats._, free._, syntax.all._

//  create_user --username john \
//    --fullname "John Doe" \
//    --id 1002

//  data User = User
//  { username :: String
//    , fullname :: String
//    , id :: Int}
//  deriving Show

  case class User(username: String, fullname: String, id: Int)
  object User extends Show[User] {
    override def show(f: User): String = f.toString
  }

//  data Option a = Option
//  { optName :: String
//    , optDefault :: Maybe a
//    , optReader :: String → Maybe a}
//  deriving Functor

  case class CmdLineOption[A](optName: String, optDefault: Option[A], optReader: String => Option[A])
  object CmdLineOption {
    implicit object functor extends Functor[CmdLineOption] {
      override def map[A, B](fa: CmdLineOption[A])(f: A => B): CmdLineOption[B] = {
        val fb: String => Option[B] = (s: String) => fa.optReader(s) map f
        val defb: Option[B] = fa.optDefault map f
        fa.copy(optDefault = defb, optReader = fb)
      }
    }
  }

  //  a “generic smart constructor”:
  //  one :: Option a → FreeA Option a
  def one[A](given: CmdLineOption[A])(implicit tag: TypeTag[A]): FreeApplicative[CmdLineOption, A] =
    FreeApplicative.lift(given)

  val u = one(CmdLineOption("username", None, Some.apply))
  val f = one(CmdLineOption("fullname", Some(""), Some.apply))
  val id = one(CmdLineOption("id", None, (s: String) => Some(s.toInt)))

  import scala.util.control.Exception._
  def readInt(s: String): Int =
    catching(classOf[NumberFormatException])(s.toInt)

  import FreeApplicativeSyntax._

  val curriedUser: (String, String, Int) => User = User.apply _
  val userFA: FreeApplicative.FA[CmdLineOption, (String, String, Int) => User] = FreeApplicative.pure(curriedUser)

  trait FreeApplicativeBuilder[F[_]] {
    def apply[A](fn: A): FreeApplicative[F, A] =
      FreeApplicative.pure(fn)
  }

  object FreeApplicativeBuilder extends FreeApplicativeBuilder[Nothing] { }

  def begin[F[_]]: FreeApplicativeBuilder[F] =
    FreeApplicativeBuilder.asInstanceOf[FreeApplicativeBuilder[F]]

  val step1: FreeApplicative.FA[CmdLineOption, (String, String, Int) => User] = begin[CmdLineOption](User.apply _)
  val step2: FreeApplicative.FA[CmdLineOption, (String, Int) => User] = step1 <*> u
  val step3: FreeApplicative.FA[CmdLineOption, (Int) => User] = step2 <*> f
  val step4: FreeApplicative.FA[CmdLineOption, User] = step3 <*> id

  //implicit def liftIt[F[_], G](curry: Curry[_]): FreeApplicative
  val userP: FreeApplicative[CmdLineOption, User] =
    begin[CmdLineOption](User.apply _) <*>
      u <*>
      f <*>
      id

  import cats.std.option._
  def optDefaults[A](fa: FreeApplicative[CmdLineOption, A]): Option[A] =
    fa.run(new (CmdLineOption ~> Option) {
      override def apply[B](fa: CmdLineOption[B]): Option[B] =
        fa.optDefault
    })

  import cats.std.list._
  def allOptions[A](a: FreeApplicative[CmdLineOption, A]): List[String] = {
    type G[B] = Const[List[String], B]
    a.run(new (CmdLineOption ~> G) {
      override def apply[B](b: CmdLineOption[B]): G[B] =
        Const(List(b.optName))
    }).getConst
  }

  trait Animal {
    def name: String
  }

  sealed trait Foo {
    type A
    val a: A
  }

  class FooImpl[A2](val a: A2) extends Foo {
    type A = A2
  }

  object Foo {
    def unapply[A2](foo: Foo { type A = A2 }): Option[foo.A] =
      foo match {
        case x: FooImpl[_] => Some(x.a)
      }
  }

  def test[T <: Animal](foo: Foo { type A = T }) = foo match {
    case Foo(a) => a.name
  }

//  import FreeApplicative
  def matchOpt[A](opt: String, value: String, fa: FreeApplicative[CmdLineOption, A]): Option[FreeApplicative[CmdLineOption, A]] = {
////    val foo = FreeApplicative.Ap.unapply(fa.asInstanceOf[FreeApplicative.Ap[CmdLineOption, A]])
////    val (pivot, fn) = foo.get
////    require(pivot.optDefault.isDefined)
////    fa match {
////      case FreeApplicative.Ap(pivot, fn) => println(pivot.optName)
////    }
////    None
//    fa match {
//      case FreeApplicative.Pure(_) =>
//        None
//      case FreeApplicative.Ap(pivot, fn) if "--" + pivot.optName == opt =>
//        pivot.optReader(value).map(FreeApplicative.Pure(_).ap(fn))
//      case x: FreeApplicative.Ap[CmdLineOption, A] =>
//        matchOpt(opt, value, x.fn).map { FreeApplicative.lift(x.pivot)(x.tag).ap }
//    }
  ???
  }

  def runParser[A](p: FreeApplicative[CmdLineOption, A], args: List[String]): Option[A] = args match {
    case opt :: value :: rest => matchOpt(opt, value, p).flatMap(runParser(_, rest))
    case Nil => optDefaults(p)
    case _ => None
  }

  println(optDefaults(userP))
  println(allOptions(userP))

  println(runParser(userP, Nil))
  println(runParser(userP, List(
    "--")))
  println(runParser(userP, List(
    "--blah", "1")))
  println(runParser(userP, List(
    "--username", "alexknvl")))

  println(runParser(userP, List(
    "--username", "alexknvl",
    "--id", "0")))

  println(runParser(userP, List(
    "--username", "alexknvl",
    "--fullname", "test",
    "--id", "0")))

  println(runParser(userP, List(
    "--fullname", "test",
    "--username", "alexknvl",
    "--id", "0")))


//  val z: FreeApplicative[Bimonad, cats.Id] = FreeApplicative.lift(cats.Id)
//  val y = z.map(_.asInstanceOf[User])
  //z.
  import cats.syntax.all._

//  val foo = FreeApplicative.pure((User.apply _).curried)
//  val g = f ap u
//  val userP: FreeApplicative[CmdLineOption, User] =



//  userP :: FreeA Option User
//  userP = User
//  <$> one (Option "username" Nothing Just)
//  <*> one (Option "fullname" (Just "") Just)
//  <*> one (Option "id" Nothing readInt)
//  readInt :: String → Maybe Int
}
