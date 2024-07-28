package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
	infix def apply(el: A): Boolean = contains(el)

	infix def contains(el: A): Boolean

	@`inline` infix def +(el: A): MySet[A]

	@`inline` infix def ++(set: MySet[A]): MySet[A]

	@`inline` infix def -(el: A): MySet[A]

	infix def &(set: MySet[A]): MySet[A]

	infix def --(set: MySet[A]): MySet[A]

	infix def map[B](f: A => B): MySet[B]

	infix def flatMap[B](f: A => MySet[B]): MySet[B]

	infix def filter(p: A => Boolean): MySet[A]

	infix def foreach(f: A => Unit): Unit

	def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
	override def contains(el: A) = false

	@`inline` override def +(el: A): NonEmptySet[A] = NonEmptySet[A](el, this)

	@`inline` override def ++(set: MySet[A]): MySet[A] = set

	@`inline` override def --(set: MySet[A]): MySet[A] = this

	@`inline` override def &(set: MySet[A]): MySet[A] = this

	@`inline` override def -(el: A): MySet[A] = this

	override def map[B](f: A => B): MySet[B] = new EmptySet[B]

	override def flatMap[B](f: A => MySet[B]) = new EmptySet[B]

	override def filter(p: A => Boolean): EmptySet[A] = this

	override def foreach(f: A => Unit): Unit = ()

	def unary_! : MySet[A] = new PropertyBasedSet[A](x => true)
}

class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
	override infix def contains(el: A): Boolean = property(el)

	override infix def +(el: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || x == el)

	override infix def ++(set: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || set(x))

	override infix def -(el: A): MySet[A] = filter(x => x != el)

	override infix def &(set: MySet[A]): MySet[A] = filter(set)

	override infix def --(set: MySet[A]): MySet[A] = filter(!set)

	override infix def map[B](f: A => B): MySet[B] = politelyFail

	override infix def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

	override infix def filter(p: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && p(x))

	override infix def foreach(f: A => Unit): Unit = politelyFail

	override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

	def politelyFail: Nothing = throw new IllegalArgumentException("Really deep rabbit hole!")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
	override infix def apply(el: A): Boolean = contains(el)

	override def contains(el: A): Boolean = head == el || tail.contains(el)

	override def +(el: A): NonEmptySet[A] = if contains(el) then this else new NonEmptySet(el, this)

	override def ++(set: MySet[A]): MySet[A] = tail ++ set + head

	override def -(el: A): MySet[A] = if head == el then tail else tail - el + head

	override def --(set: MySet[A]): MySet[A] = if set(head) then tail -- set else new NonEmptySet(head, tail -- set)

	override def &(set: MySet[A]): MySet[A] = filter(set)

	override def map[B](f: A => B): MySet[B] = (tail map f) + f(head)

	override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)

	override def filter(p: A => Boolean): MySet[A] = if p(head) then tail.filter(p) + head else tail.filter(p)

	override def foreach(f: A => Unit): Unit = {
		f(head)
		tail.foreach(f)
	}

	override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))
}

object MySet {
	def apply[A](values: A*): MySet[A] = {
		@tailrec
		def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
			if valSeq.isEmpty then acc else buildSet(valSeq.tail, acc + valSeq.head)

		buildSet(values.toSeq, new EmptySet[A])
	}
}

object MySetPlayground extends App {
	val firstSet: MySet[Int] = NonEmptySet[Int](1, NonEmptySet[Int](2, NonEmptySet[Int](3, new EmptySet[Int])))
	val secondSet: MySet[Int] = NonEmptySet[Int](2, NonEmptySet[Int](3, NonEmptySet[Int](4, new EmptySet[Int])))
	firstSet -- secondSet foreach println
}