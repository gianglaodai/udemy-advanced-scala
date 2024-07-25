package lectures.part1as

object AdvancedPM extends App {
  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }

  /**
   * - constant
   * - wildcards
   * - case classe
   * - tuples
   * - some special magic like above
   *
   */

  case class Person(val name: String, val age: Int)

  object Person {
    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))

    def unapply(age: Int): Option[String] = Some(if age < 21 then "minor" else "major")
  }

  object PersonPattern {
    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))
  }

  val bob = Person("bob", 18)
  val greeting = bob match {
    case Person(n, a) => s"Hi, myname is $n and I am $a yo."
  }

  val legalStatus = bob.age match
    case Person(status) => s"My legal status is $status"

  println(greeting)
  println(legalStatus)

  val n: Int = 4
  //  val mathProperty = n match {
  //    case x if x < 10 => "single digit"
  //    case x if x % 2 == 0 => "an even number"
  //    case _ => "no property"
  //  }

  object singleDigit:
    def unapply(value: Int): Boolean = value < 10

  object even:
    def unapply(value: Int): Boolean = value % 2 == 0

  val mathProperty = n match
    case singleDigit() => "single digit"
    case even() => "an even number"
    case _ => "no property"

  println(mathProperty)

  // infix patterns ::
  case class Or[A, B](a: A, b: B)

  val either = Or(2, "two")
  val humanDescription = either match
    case number Or string => s"$number is written as $string"

  println(humanDescription)

  // decomposing sequences
  val vararg = numbers match
    case List(1, _*) => "starting with 1"

  println(vararg)

  abstract class MyList[+A]:
    def head: A = ???

    def tail: MyList[A] = ???

  case object Empty extends MyList[Nothing]

  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList:
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match
    case MyList(1, 2, _*) => "starting with 1, 2"
    case _ => "something else"

  println(decomposed)

  // custom return types for unapply need 2 method: isEmpty: Boolean, get: something
  abstract class Wrapper[T]:
    def isEmpty: Boolean

    def get: T

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false

      def get: String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This person's name is $n"
    case _ => "An alien"
  })
}
