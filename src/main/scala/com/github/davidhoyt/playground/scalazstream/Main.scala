package com.github.davidhoyt.playground.scalazstream

object Main extends App {
  // https://dl.dropboxusercontent.com/u/1679797/NYT/Reactive%20in%20Reverse.pdf
  import scalaz._, concurrent._

  object exploringTask {
    def theBasics: Unit = {
      val now = Task.now { println(Thread.currentThread().getName) ; println("executes immediately") }
      val later = Task.delay { println(Thread.currentThread().getName) ; println("executes later") }

      println("this should print out after \"executes immediately\"")
      println("\"executes later\" should not be printed out at this point...")

      // akin to Future.sequence -- tasks are not sequenced and trampolined on the same thread when using gatherUnordered().
      val all: Task[Seq[Unit]] = Task.gatherUnordered(Seq(now, later))
      all.unsafePerformSync

      // since now is call-by-value, this logic has already been executed. So the result of that evaluation is what is
      // being lifted into a Task.
      now.unsafePerformSync
      // executes the logic again
      later.unsafePerformSync

      val withError = Task.delay[Int] {
        require(false)
        5
      }

      val withErrorTask: Task[Throwable \/ Int] = withError.attempt

      val result: Throwable \/ Int = withError.unsafePerformSyncAttempt

      println(result)

      // timed tasks

      import scala.concurrent.duration._
      val longRunningTask = Task.delay {
        println(Thread.currentThread().getName)
        Thread.sleep(5000L)
        println("DONE!")
      }

      val timedTask = longRunningTask.unsafePerformTimed(1 second)
      println("following should include TimeoutException")
      println(timedTask.unsafePerformSyncAttempt)

      longRunningTask.unsafePerformAsync(_ => ())
    }

    def run: Unit = {
      val t = Task.delay {
        println("this is a task")
      }
    }
  }

  // The equivalent of ExecutionContext w/ Futures is actually through scalaz-stream Process.

  exploringTask.theBasics
  exploringTask.run
}
