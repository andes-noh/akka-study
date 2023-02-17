import Main.logBehavior
import akka.NotUsed
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.example.CborSerializable

import scala.io.StdIn

trait Command
case object Inc extends Command
case object Dec extends Command
case object Print extends Command

object Counter {


  // 1. 커맨드 정의
  def apply()= active(0)
  def active(i: Int): Behavior[Command] = {
    Behaviors.receive{ (ctx, msg) =>
      msg match {
        case Inc => active(i + 1)
        case Dec => active(i - 1)
        case Print =>
          ctx.log.info(s"$i")
          Behaviors.same
      }
    }
  }

  val userBehavior: Behavior[NotUsed] = Behaviors.setup { context =>
//    val logger = context.spawn(logBehavior, "logger")

    val counter = context.spawn(Counter.apply(), "counter")
//    counter ! Inc
    (1 to 10000).foreach(i => counter ! Inc)
    counter ! Print

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
