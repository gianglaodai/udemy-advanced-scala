package exercises

import scala.concurrent.{Future, Promise}
import scala.util.{Random, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

object FutureExcercise extends App {
  /*
  1) fulfill a future IMMEDIATELY with a value
  2) inSequence(fa, fb) run fb after fa
  3) first(fa, fb) => new future with the first value of the two futures
  4) last(fa, fb) => new future with the last value
  5) retryUntil[T] (action: () => Future[T], condition: T => Boolean): Future[T]
   */

  def fulfillImmediately[T](value: T): Future[T] = Future.successful(value)

  def inSequence[A, B](fa: Future[A], fb: Future[B]): Future[B] = fa.flatMap(_ => fb)


  val fast = Future {
    Thread.sleep(100)
    42
  }

  val slow = Future {
    Thread.sleep(1000)
    45
  }

  first(fast, slow).foreach(println)

  last(slow, fast).foreach(println)
  Thread.sleep(1000)
  val random = new Random()
  val action = () => Future {
    val nextValue = random.nextInt(100)
    println("generated " + nextValue)
    nextValue
  }
  retryUntil(action, (x: Int)=> x < 50).foreach(println)
}

def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
  val promise = Promise[A]
  fa.onComplete(promise.tryComplete) // if finish first emit the value , else do nothing
  fb.onComplete(promise.tryComplete) // if finish first emit the value, else do nothing
  promise.future
}

def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
  // 1 promise which both futures will try to complete
  // 2 promise which the LAST future will complete
  val bothPromise = Promise[A]
  val lastPromise = Promise[A]
  //    fa.onComplete(result => {
  //      if (!bothPromise.tryComplete(result))
  //        lastPromise.complete(result)
  //    })
  //    fb.onComplete(result => {
  //      if(!bothPromise.tryComplete(result))
  //        lastPromise.complete(result)
  //    })
  val checkAndComplete = (result: Try[A]) => {

    val tryComplete = bothPromise.tryComplete(result)
    if (!tryComplete) {
      lastPromise.complete(result)
    }
  }
  fa.onComplete(checkAndComplete)
  fb.onComplete(checkAndComplete)
  lastPromise.future
}

def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] =
  action()
    .filter(condition)
    .recoverWith(e => retryUntil(action, condition))

