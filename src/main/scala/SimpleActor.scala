import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.io.StdIn

object SimpleActor {
  val logBehavior:Behavior[String] = {
    println("1111")
    Behaviors.receiveMessage {
      case msg => println(msg)
        Behaviors.same
    }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem(logBehavior, "logSystem")
    system ! "hello akka"
    StdIn.readLine()
    system.terminate()
  }
}
