package lectures.part4implicits

object PimpMyLibrary extends App {
	// 2.isPrime
	implicit class RichInt(val value: Int) extends AnyVal {
		def isEven: Boolean = value % 2 == 0

		def sqrt: Double = Math.sqrt(value)
	}

	new RichInt(42).sqrt
	42.isEven // new RichInt(42).isEvent
	// type enrchiment = pimping
	1 to 10

	import scala.concurrent.duration._

	3.seconds

	// compiler doesn't do multiple implicit searches
	implicit class RicherInt(richInt: RichInt) {
		def isOdd: Boolean = !richInt.value.isEven
	}

	//	42.isOdd -> not compile
	/*
		Enrich the String class
		- asInt
		- encrypt
			"John" -> Lqjp
	 */
	implicit class RichString(val value: String) extends AnyVal {
		def asInt: Int = value.toInt

		def encrypt(cypherDistance: Int): String = value.map(c => (c + cypherDistance).asInstanceOf[Char])
	}

	println("42".asInt)
	println("John".encrypt(2))

	implicit def stringToInt(string: String): Int = Integer.valueOf(string)

	println("6" / 2)
	// keep type enchichment to implicit classes and type classes
	// avoid implicit defs as much as possible
	// package implicits clearly, bring into scope only what you need
	// IF you need conversions, make them specific
}
