package lectures.part4implicits

import java.util.Date

object TypeClasses extends App {
	trait HTMLWritable {
		def toHtml: String
	}


	/*
	1- lost type safety
	2- need to modify the code every time
	3- still ONE implementation
	 */

	trait HTMLSerializer[T] {
		def serialize(value: T): String
	}


	// 1- we can define serializers for other types

	import java.util.Date

	object DateSerializer extends HTMLSerializer[Date] {
		override def serialize(value: Date): String = s"<div>${value.toString}</div>"
	}


	// part 2
	object HTMLSerializer {
		def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
		def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
	}

	implicit object IntSerializer extends HTMLSerializer[Int] {
		override def serialize(value: Int): String = s"<div style: coler=blue>$value</div>"
	}

	//	println(HTMLSerializer.serialize(42)(IntSerializer))
	println(HTMLSerializer.serialize(42))
	// this called AD-HOC polymorphism
}
