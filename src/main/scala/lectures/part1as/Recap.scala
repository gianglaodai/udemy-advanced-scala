package lectures.part1as

import scala.annotation.tailrec

object Recap extends App {
  val aCondition: Boolean = false
  val aCondintionedVal = if (aCondition) 42 else 65
  // instructions vs expressions
  val aCodeBlock = {
    if aCondition then 54
    56
  }

  // Unit = void
  val theUnit = ()

  // fuctions
  def aFuction(x: Int): Int = x + 1

  // recursion: stack and tail
  @tailrec def factorial(n: Int, acc: Int): Int = if n <= 0 then acc else factorial(n - 1, n * acc)

  // oop
  class Animal

  class Dog extends Animal

  val aDog: Animal = new Dog

  trait Carnivore:
    def eat(a: Animal): Unit

  class Crocodile extends Animal with Carnivore:
    override def eat(a: Animal): Unit = println("crunch!")

  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog

  // anonymous classes
  val aCarnivore = new Carnivore:
    override def eat(a: Animal): Unit = println("roar!")

  // generics
  abstract class MyList[+A]

  object MyList

  // case classes
  case class Person(name: String, age: Int)

  //exceptions and try/catch/finally
  val throwsException = throw new RuntimeException
  val aPotentioalFailure = try {
    throw new RuntimeException
  } catch {
    case e: Exception => "I caught an exception"
  } finally {
    println("some logs")
  }

  // packaging and imports

  // functional programming
  val incrementer = new Function1[Int, Int]:
    override def apply(v1: Int): Int = v1 + 1
  incrementer(1)
  val anonymousIncrementer = (x: Int) => x + 1
  List(1, 2, 3).map(anonymousIncrementer)
  // map, flatMap, filter

  // for-comprehension
  val pairs = for {
    num <- List(1, 2, 3) // if condition
    char <- List('a', 'b', 'c')
  } yield num + "-" + char

  // Scala collections: Seqs, Arrays, Lists, Vectors, Maps, Tuples
  val aMap = Map("Gian" -> 879, "Hoa" -> 1234)
  // collections: Options, Try
  val anOption = Some(2)
  // pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hello $n"
  }
}
