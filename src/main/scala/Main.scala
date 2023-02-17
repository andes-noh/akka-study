import akka.NotUsed
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.io.StdIn

object Main {
  val logBehavior: Behavior[String] = {
    Behaviors.receiveMessage {
      case msg => println(msg)
      Behaviors.same
    }
  }

  val userBehavior: Behavior[NotUsed] = Behaviors.setup { context =>
    val logger = context.spawn(logBehavior, "logger")

    logger ! "hello"

    Behaviors.empty
  }

  def main(args: Array[String]): Unit = {
//    println("Hello world!")

    val system = ActorSystem(userBehavior, "logSystem")
//    system ! "hello akka"
    StdIn.readLine()
    system.terminate()
  }
}