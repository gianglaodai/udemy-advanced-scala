package exercises

import lectures.part4implicits.TypeClasses.{HTMLSerializer, HTMLWritable}

object EqualityPlayground extends App {

	case class User(name: String, age: Int, email: String) extends HTMLWritable {
		override def toHtml: String = s"<div>$name ($age yo) <a href=$email/> </div>"
	}

	/*
	1 - for the types WE write
	2- ONE implementation out of quite a number
	*/
	// option 2 - pattern matching
	object HTMLSerializerPM {
		def serializeToHtml(value: Any) = value match {
			case User(n, a, e) =>
			//			case Date =>
			case _ =>
		}
	}


	User("John", 32, "john@rockthejvm.com").toHtml
	/**
	 * Equality
	 */

	implicit object UserSerializer extends HTMLSerializer[User] {
		override def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
	}

	val john = User("John", 32, "john@rockthejvm.com")
	println(UserSerializer.serialize(john))
	// 2- we can define MULTIPLE serializers
	object PartialUserSerializer extends HTMLSerializer[User] {
		override def serialize(user: User): String = s"<div>${user.name}</div>"
	}

	trait Equal[T] {
		def apply(first: T, second: T): Boolean
	}

	object Equal {
		def apply[T](first: T, second: T)(implicit equality: Equal[T]) = equality.apply(first, second)
	}

	object NameEqual extends Equal[User] {
		def apply(first: User, second: User): Boolean = first.name == second.name
	}

	implicit object FullEquality extends Equal[User] {
		def apply(first: User, second: User): Boolean = first.name == second.name && first.email == second.email
	}

	println(Equal(User("giang", 30, "giang@gmail.com"), User("giang",20, "giang@gmail.com")))
	println(Equal(User("giang", 30, "giang@gmail.com"), User("giang",20, "hoang@gmail.com")))
	println(HTMLSerializer[User].serialize(john))
	println(HTMLSerializer.serialize(john))
}
