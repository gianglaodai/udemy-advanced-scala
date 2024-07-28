package lectures.part2afp

object CuriesPAF extends App {
	val superAdder: Int => Int => Int = x => y => x + y
	val add3 = superAdder(3)
	println(add3(5))

	// METHOD!
	def curriedAdder(x: Int)(y: Int): Int = x + y

	val add4 = curriedAdder(4) // work on scala 3 only
	// lifting = ETA-EXPANSION
	// functions != methods (JVM limitation)

	val simpleAddFunction = (x: Int, y: Int) => x + y

	def simpleAddMethod(x: Int, y: Int) = x + y

	def curriedAddMethod(x: Int)(y: Int) = x + y

	// add7: Int => Int = y => 7 + y
	val add7 = y => simpleAddFunction(7, y)
	val add7_7 = simpleAddFunction(7, _: Int)
	val add7_2 = simpleAddMethod(7, _)
	val add7_3 = curriedAddMethod(7)
	val add7_4 = y => simpleAddFunction.curried(7)(y)
	val add7_5 = simpleAddFunction.curried(7)
	val add7_6 = simpleAddMethod(7, _: Int)

	// underscores are powerful
	def concatenator(a: String, b: String, c: String) = a + b + c

	val insertName = concatenator("Hello, I'm", _: String, ", how are you")
	println(insertName("Giang"))
	val fillInTheBlanks = concatenator("Hello, ", _: String, _: String)
	println(fillInTheBlanks("Giang", " Scala is nice"))

	/**
	 * 1. Process a list of numbers and return their string representations with different formats
	 * - Use the %4.2f, %8.6f, and %14.12f with a curried formatter funtions
	 * 2. differentce between
	 * - functions vs methods
	 * - parameters: by-name vs 0-lambda
	 *
	 */

	val curriedFormmater = (s: String) => (number: Double) => s.format(number)
	val simpleFormat = curriedFormmater("%4.2f")
	val seriousFormat = curriedFormmater("%8.6f")
	val preciseFormat = curriedFormmater("%14.12f")


	def byName(n: => Int) = n + 1

	def byFunction(f: () => Int) = f() + 1

	def method: Int = 42

	def parentMethod(): Int = 42

	def pf: PartialFunction[Int, Int] = {
		case 1 => 1
		case 2 => 2
	}


	/**
	 * check calling byName and byFunction
	 * - int
	 * - method
	 * - parenMethod
	 * - lambda
	 * - PAF
	 */
	val methodOne = () => 1

	def functionOne() = 1

	println(byName(1))
	println(byName(method))
	println(byName(parentMethod()))
	println(byName((() => 1)()))
	println(byName(pf(1)))
	//byFunction(45)
	//	byFunction(method)
	byFunction(parentMethod)
	byFunction(() => 1)
}
