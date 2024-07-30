package lectures.part2afp

object Monads extends App {

	class Lazy[+A](value: =>A) {
		def flatMap[B](f: A => Lazy[B]): Lazy[B] = f(value)
	}
	
	object Lazy {
		def apply[A](value: =>A):Lazy[A] = new Lazy(value)
	}
}
