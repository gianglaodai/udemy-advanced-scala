package lectures.part3concurrency

object OrganizingImplicits extends App {
	println(List(1, 4, 5, 3, 2).sorted)
	// scala will look up implicit value from
	// scala.Predef
	implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
	println(List(1, 4, 5, 3, 2).sorted)

	/* Implicits:
	- val/var
	- object
	- accessor method = defs with no parentheses
	 */
	// Exercise
	case class Person(name: String, age: Int)

	val persons = List(
		Person("Steve", 30),
		Person("Amy", 22),
		Person("John", 66)
	)

	//	implicit val nameOrdering: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.name.compareTo(p2.name) < 0)
	//	println(persons.sorted)
	/*
	Implicit scope
	- normal scope = LOCAL SCOPE
	- imported scope
	- companions of all types involved in the method signature
	   - List
	   - Ordering
     - all the types involved = A or any supertype
	 */
	object AlphabeticNameOrdering {
		implicit val nameOrdering: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.name.compareTo(p2.name) < 0)
	}

	object AgeOrdering {
		implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.age < p2.age)
	}

	import AgeOrdering._

	println(persons.sorted)

	/*
	Exercise.
	- totalPrice = most used
	- by unit count
	- by unit price
	 */
	case class Purchase(nUnits: Int, unitPrice: Double)

	object Purchase {
		implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((p1, p2) => p1.unitPrice * p1.nUnits < p2.unitPrice * p2.nUnits)
	}

	object UnitCountOrdering {
		implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan((p1, p2) => p1.nUnits < p2.nUnits)
	}

	object UnitPriceOrdering {
		implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((p1, p2) => p1.unitPrice < p2.unitPrice)
	}
}
