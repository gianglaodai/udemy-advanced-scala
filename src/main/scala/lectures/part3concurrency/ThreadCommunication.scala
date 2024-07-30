package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {
	/*
	the producer - consumer problem
	producer -> [x] -> consumer
	consumer should wait producer
	 */
	class SimpleContainer {
		private var value: Int = 0

		def isEmpty: Boolean = value == 0

		def get = {
			val result = value
			value = 0
			result
		}

		def set(newValue: Int) = value = newValue
	}

	def naiveProdCons(): Unit = {
		val container = new SimpleContainer
		val consumer = new Thread(() => {
			println("[consumer] waiting...")
			while (container.isEmpty) {
				println("[consumer] actively waiting...")
			}
			println("[consumer] I have consumed " + container.get)
		})

		val producer = new Thread(() => {
			println("[producer] computing...")
			Thread.sleep(500)
			val value = 42
			println("[producer] I have produced, after long work, the value " + value)
			container.set(value)
		})
		consumer.start()
		producer.start()
	}

	//	naiveProdCons()

	// wait and notify
	def smartProdCons(): Unit = {
		val container = new SimpleContainer
		val consumer = new Thread(() => {
			println("[consumer] waiting...")
			container.synchronized {
				container.wait()
			}

			// container must have some value
			println("[consumer] I have consumed " + container.get)
		})

		val producer = new Thread(() => {
			val value = 42
			println("[producer] Hard at work...")
			Thread.sleep(2000)
			container.synchronized {
				println("[producer] I'm producing " + value)
				container.set(value)
				container.notify()
			}
		})

		consumer.start()
		producer.start()
	}

	//	smartProdCons()
	/*
	producer -> [? ? ?] -> consumer
	producer should stop if consumer is too slow and can not consume all value
	consumer should wait if producer did not produce new value
	 */
	def prodConsLargeBuffer(): Unit = {
		val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
		val capacity = 3
		val consumer = new Thread(() => {
			val random = new Random()
			while (true) {
				buffer.synchronized {
					if (buffer.isEmpty) {
						println("[consumer] buffer empty, waiting...")
						buffer.wait()
					}

					// there must be at least ONE value in the buffer
					val x = buffer.dequeue()
					println("[consumer] consumed " + x)
					buffer.notify()
				}
				Thread.sleep(random.nextInt(500))
			}
		})
		val producer = new Thread(() => {
			val random = new Random()
			var i = 0
			while (true) {
				buffer.synchronized {
					if (buffer.size == capacity) {
						println("[producer] buffer is full, waiting...")
						buffer.wait()
					}

					// there must be at least ONE EMPTY SPACE in the buffer
					println("[producer] producing " + i)
					buffer.enqueue(i)
					i += 1
					buffer.notify()
				}

				Thread.sleep(random.nextInt(500))
			}
		})

		consumer.start()
		producer.start()
	}

	//	prodConsLargeBuffer()

	/*
	Prod-cons, level 3
	producer1 -> [? ? ?] -> consumer1
	producer2 ----^    ^--- consumer2
	 */

	//	def multiProdConsLargeBuffer(): Unit = {
	//		val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
	//		val capacity = 3
	//		val consumerRunner: Runnable = () => {
	//			val random = new Random()
	//			while (true) {
	//				buffer.synchronized {
	//					if (buffer.isEmpty) {
	//						println("[consumer] buffer empty, waiting...")
	//						buffer.wait()
	//					}
	//
	//					// there must be at least ONE value in the buffer
	//					val x = buffer.dequeue()
	//					println("[consumer] consumed " + x)
	//					buffer.notifyAll()
	//				}
	//				Thread.sleep(random.nextInt(500))
	//			}
	//		}
	//		val producerRunner: Runnable = () => {
	//			val random = new Random()
	//			var i = 0
	//			while (true) {
	//				buffer.synchronized {
	//					if (buffer.size == capacity) {
	//						println("[producer] buffer is full, waiting...")
	//						buffer.wait()
	//					}
	//
	//					// there must be at least ONE EMPTY SPACE in the buffer
	//					println("[producer] producing " + i)
	//					buffer.enqueue(i)
	//					i += 1
	//					buffer.notifyAll()
	//				}
	//
	//				Thread.sleep(random.nextInt(500))
	//			}
	//		}
	//		val consumer1 = new Thread(consumerRunner)
	//		val consumer2 = new Thread(consumerRunner)
	//		val producer1 = new Thread(producerRunner)
	//		val producer2 = new Thread(producerRunner)
	//
	//		consumer1.start()
	//		consumer2.start()
	//		producer1.start()
	//		producer2.start()
	//	}

	//	multiProdConsLargeBuffer()
	class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
		override def run(): Unit = {
			val random = new Random()
			while (true) {
				buffer.synchronized {
					while (buffer.isEmpty) {
						println(s"[consumer $id] buffer empty, waiting...")
						buffer.wait()
					}

					// there must be at least ONE value in the buffer
					val x = buffer.dequeue()
					println(s"[consumer $id] consumed " + x)
					buffer.notify()
				}
				Thread.sleep(random.nextInt(500))
			}
		}
	}

	class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
		override def run(): Unit = {
			val random = new Random()
			var i = 0
			while (true) {
				buffer.synchronized {
					while (buffer.size == capacity) {
						println(s"[producer $id] buffer is full, waiting...")
						buffer.wait()
					}

					// there must be at least ONE EMPTY SPACE in the buffer
					println(s"[producer $id] producing " + i)
					buffer.enqueue(i)
					i += 1
					buffer.notify()
				}

				Thread.sleep(random.nextInt(500))
			}
		}
	}

	def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
		val buffer = new mutable.Queue[Int]()
		val capacity = 3
		(1 to nConsumers).foreach(new Consumer(_, buffer).start())
		(1 to nProducers).foreach(new Producer(_, buffer, capacity).start())
	}

	multiProdCons(4,4)
}
