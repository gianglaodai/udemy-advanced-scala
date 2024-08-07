package lectures.part3concurrency

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Random, Success}
import scala.concurrent.duration.*
// important for futures
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesPromises extends App {
  def calculateMeaningOfLife: Int = {
    //		Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife
  } //(global) which is passed by the compiler

  println(aFuture.value)
  println("Waiting on the future")
  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"the meaning of life is $meaningOfLife")
    case Failure(e) => println(s"I have failed with $e")
  } // call by SOME thread

  //  Thread.sleep(1000)

  // mini social network


  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  mark.onComplete {
    case Success(markProfile) => {
      println(markProfile)
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(e) => e.printStackTrace()
      }
    }
    case Failure(e) => e.printStackTrace()
  }
  Thread.sleep(3000)
  val nameOnTheWall = mark.map(profile => profile.name)
  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  //	 for-comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)
  //	Thread.sleep(2000)

  // fallbacks - no exception
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "FA")
  }

  // exception of the second result
  val aFetchProfileNoMatterWhat = SocialNetwork.fetchProfile("unknow id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  // keep the exception of the first result if both fail
  val fallbackResult = SocialNetwork.fetchProfile("unknow id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  // online banking app

  println(BankingApp.purchase("Giang", "iPhone", "Daniel", 3000))

  // promise
  val promise = Promise[Int]() // "controller" of the future
  val future = promise.future

  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println("[consumer] I've received " + r)
  }

  // thread 2 - "producer"
  val producer = new Thread(()=> {
    println("[producer] crunching numbers...")
    Thread.sleep(1000)
    // "fullfilling" the promise
    promise.success(42)
    println("[producer] done")
  })
  Thread.sleep(2000)
  producer.start()
}

case class Profile(id: String, name: String) {
  def poke(anotherProfile: Profile): Unit = println(s"${this.name} poking ${anotherProfile.name}")
}

object SocialNetwork {
  // "database"
  val names: Map[String, String] = Map(
    "fb.id.1-zuck" -> "Mark",
    "fb.id.2-bill" -> "Bill",
    "fb.id.0-dummy" -> "Dummy",
  )
  val friends: Map[String, String] = Map(
    "fb.id.1-zuck" -> "fb.id.2-bill"
  )

  val random = new Random()

  // API
  def fetchProfile(id: String): Future[Profile] = Future {
    // fetching from the DB
    Thread.sleep(300)
    Profile(id, names(id))
  }

  def fetchBestFriend(profile: Profile): Future[Profile] = Future {
    println(profile)
    Thread.sleep(300)
    val bfId = friends(profile.id)
    Profile(bfId, names(bfId))
  }
}

case class User(name: String)

case class Transaction(sender: String, receiver: String, amount: Double, status: String)

object BankingApp {
  val name = "Rock the JVM banking"

  def fetchUser(name: String): Future[User] = Future {
    // simulate fething from the DB
    Thread.sleep(300)
    User(name)
  }

  def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
    Thread.sleep(400)
    Transaction(user.name, merchantName, amount, "SUCCESS")
  }

  def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
    // fetch the user from the DB
    // create a transaction
    // WAIT for the transaction to finish
    val transactionStatusFuture = for {
      user <- fetchUser(username)
      transaction <- createTransaction(user, merchantName, cost)
    } yield transaction.status

    Await.result(transactionStatusFuture, 60.seconds) // implicit conversions -> pimp my library
  }
}
