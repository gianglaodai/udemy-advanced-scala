package lectures.part3concurrency

import scala.concurrent.Future
import scala.util.{Failure, Random, Success}
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

	Thread.sleep(1000)

	// mini social network
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
			Thread.sleep(3)
			Profile(id, names(id))
		}

		def fetchBestFriend(profile: Profile): Future[Profile] = Future {
			println(profile)
			Thread.sleep(3)
			println("get friend")
			val bfId = friends(profile.id)
			Profile(bfId, names(bfId))
		}
	}

	//	val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
	//	mark.onComplete {
	//		case Success(markProfile) => {
	//			println(markProfile)
	//			val bill = SocialNetwork.fetchBestFriend(markProfile)
	//			bill.onComplete {
	//				case Success(billProfile) => markProfile.poke(billProfile)
	//				case Failure(e) => e.printStackTrace()
	//			}
	//		}
	//		case Failure(e) => e.printStackTrace()
	//	}
	//	Thread.sleep(10000)
	//	val nameOnTheWall = mark.map(profile => profile.name)
	//	val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
	//	val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

	// for-comprehensions
	for {
		mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
		bill <- SocialNetwork.fetchBestFriend(mark)
	} mark.poke(bill)
	Thread.sleep(20000)

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
}
