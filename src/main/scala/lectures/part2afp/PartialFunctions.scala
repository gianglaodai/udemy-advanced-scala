package lectures.part2afp

object PartialFunctions extends App {
  val aFunction = (x: Int) => x + 1

  val aFussyFunction: Int => Int = {
    case 1 => 42
    case 2 => 56
    case 3 => 999
  }
  // {1,2,3} => Int

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 3 => 999
  } // partial function value

  // PF utilties
  println(aPartialFunction.isDefinedAt(67))

  // lift
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2)) // Some(56)
  println(lifted(98)) // None

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(pfChain(2))
  println(pfChain(45))

  // FP extend normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOFs accept partial functions as well
  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)

  /**
   * Note: PF can only have ONE parameter type
   *
   * 1- construct a PF instance (anonymous class
   * 2 - dumb chatbot as a PF: write thing in the console and chatbot will reply with only a limited number of potential answers
   */

  val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 42
      case 2 => 65
      case 3 => 999
    }

    override def isDefinedAt(x: Int): Boolean = x == 1 || x == 2 || x == 3
  }

  val chatbot: PartialFunction[String,String] = {
    case "hello" => "Hi, my name is chatbot"
    case "goodbye" => "goodbye, have a nice day"
    case "call me maybe" => "please, call me"
  }
  // implement chatbot with scala.io.Source.stdin.getLines().foreach(line => println("you said: " + line))

  scala.io.Source.stdin.getLines()
    .map(chatbot)
    .map(s => s"chatbot say: $s")
    .foreach(println)
}
