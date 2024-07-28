package lectures.part2afp

object LazyEvaluation extends App {
	// lazy DELAYS the evaluation of values
	//	lazy val x: Int = throw new RuntimeException
	lazy val x: Int = {
		println("hello")
		32
	}
	println(x)
	println(x)

	// examples of implications
	// side effects
	def sideEffectCondition: Boolean = {
		println("Boo")
		true
	}

	def simpleCondition: Boolean = false

	lazy val lazyCondition = sideEffectCondition
	println(if simpleCondition && lazyCondition then "yes" else "no") // no Boo print out because lazyCondition will not be evaluated

	// in conjuction with call by name
	def byNameMethod(n: => Int): Int = n + n + n + 1

	def retrieveMagicValue = {
		println("waiting")
		Thread.sleep(1000)
		42
	}

	//	println(byNameMethod(retrieveMagicValue)) // the evaluation take 3 times
	// use lazy vals
	def byNameMethodLazy(n: => Int): Int = {
		lazy val t = n
		t + t + t + 1
	}
	//	println(byNameMethodLazy(retrieveMagicValue)) // CALL BY NEED

	// filtering with lazy vals
	def lessThan30(i: Int): Boolean = {
		println(s"$i is less than 30?")
		i < 30
	}

	def greaterThan20(i: Int): Boolean = {
		println(s"$i is greater than 20?")
		i > 20
	}

	val numbers = List(1, 25, 40, 5, 23)
	val lt30 = numbers.filter(lessThan30) // List(1,25,5,23)
	val gt20 = lt30.filter(greaterThan20) // List(25,23)

	println(gt20)
	val lt30Lazy = numbers.withFilter(lessThan30)
	val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
	gt20Lazy.foreach(println)

	// for-comprehensnions use withFilter with guards
	for {
		a <- List(1, 2, 3) if a % 2 == 0 // use lazy vals!
	} yield a + 1
	List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1)

	/**
	 * Exercise: implement a lazily evaluated, singly linked STREAM of elements
	 */
	abstract class MyStream[+A] {
		def isEmpty: Boolean
		def head: A
		def tail: MyStream[A]

		def #::[B >:A] (el: B):MyStream[B] // prepend operator
		def ++[B >: A](stream: MyStream[B]): MyStream[B] // concat two streams

		def foreach(f: A => Unit): Unit
		def map[B](f: A => B): MyStream[B]
		def flatMap[B](f: A => MyStream[B]): MyStream[B]
		def filter(predicate: A => Boolean): MyStream[A]
		
		def take(n: Int): MyStream[A] // takes the first n elements out of this stream
		def takeAsList(n: Int): List[A]
	}
	
	object MyStream {
		def from[A](start: A)(generator: A => A): MyStream[A] = ???
	}
}
