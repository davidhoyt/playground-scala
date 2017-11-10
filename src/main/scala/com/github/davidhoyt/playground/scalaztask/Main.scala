package com.github.davidhoyt.playground.scalaztask

import java.lang.Thread.UncaughtExceptionHandler
import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.forkjoin.{ForkJoinPool, ForkJoinWorkerThread}
import scala.concurrent.forkjoin.ForkJoinPool.ForkJoinWorkerThreadFactory
import scalaz.Nondeterminism
import scalaz.concurrent.Task

object Main {
  import scalaz._, Scalaz._, Free.liftF

  sealed trait StuffOp[A]
  case class Greet(greeting: String) extends StuffOp[Unit]

  type StuffIO[A] = Free[StuffOp, A]

  def freeGreeting(greeting: String): StuffIO[Unit] = liftF(Greet(greeting))

  val freeInterpreter: StuffOp ~> Task =
    new (StuffOp ~> Task) {
      override def apply[A](fa: StuffOp[A]) =
        fa match {
          case Greet(greeting) => Task.delay(println(greeting))
          case _ => ???
        }
    }

  val freeProgram: StuffIO[Unit] = freeGreeting("hello free!") >> freeGreeting("hello from free again!")

  freeProgram.foldMap(freeInterpreter).unsafePerformSync

  def finallyTaglessGreet[F[_] : Applicative](greeting: String): F[Unit] =
    Applicative[F].pure(println(greeting))

  val taglessProgram: Task[Unit] = finallyTaglessGreet[Task]("hello tagless!") >> finallyTaglessGreet[Task]("hello from tagless again!")

  taglessProgram.unsafePerformSync

  val freeAndTaglessTogetherForever: Task[Unit] = freeProgram.foldMap(freeInterpreter) >> taglessProgram

  freeAndTaglessTogetherForever.unsafePerformSync


//  val forkJoinWorkerThreadFactory = new ForkJoinWorkerThreadFactory {
//    val num = new AtomicInteger(1)
//    override def newThread(forkJoinPool: ForkJoinPool): ForkJoinWorkerThread = {
//      val t = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(forkJoinPool)
//      t.setName(s"my-forkjoin-thread-${num.getAndIncrement()}")
//      t
//    }
//  }
//
//  val pool = new ForkJoinPool(16, forkJoinWorkerThreadFactory, new UncaughtExceptionHandler { override def uncaughtException(t: Thread, e: Throwable): Unit = () }, true)

  def main(args: Array[String]): Unit = {
//    val task = Task.fork(Task.delay {
//      println(Thread.currentThread().getName)
//    })(pool)
//
//    val tasks = Seq.fill(30)(task)
//
//    Nondeterminism[Task].gatherUnordered(tasks).unsafePerformSync
  }
}
