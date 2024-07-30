package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {
	// JVM threads
	val aThread = new Thread(() => println("Running in parallel"))

	aThread.start() // gives the signal to the JVM to start a JVM thread
	// create a JVM thread => OS thread
	aThread.join() // blocks until aThread finishes running

	val threadHello = new Thread(() => (1 to 5).foreach(_ => println("Hello")))
	val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("Goodbye")))
	threadHello.start()
	threadGoodbye.start()
	// different runs produce different results!
	// executors
	val pool = Executors.newFixedThreadPool(10)
	pool.execute(() => println("something in the thread pool"))

	pool.execute(() => {
		Thread.sleep(1000)
		println("done after 1 second")
	})
	pool.execute(() => {
		Thread.sleep(1000)
		println("almost done")
		Thread.sleep(1000)
		println("done after 2 seconds")
	})
	
	pool.shutdown() // don't accept any more action
//	pool.execute(()=> println("should not appear")) throw exception
	// pool.shutdownNow() => will throw exception because some thread is running
	println(pool.isShutdown)
}
