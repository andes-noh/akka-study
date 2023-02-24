import akka.actor.typed.scaladsl.Behaviors
import akka.{Done, NotUsed}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.pattern.StatusReply
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import com.example.CborSerializable

import scala.io.StdIn

object Devices {
  case class Device(id: String, name: String, status: Boolean)
  case class State(devices: Map[String, Device]) extends CborSerializable

  trait Command extends CborSerializable
  case class Create(d: Device, replyTo: ActorRef[StatusReply[Done]]) extends Command
  case class Update(d: Device, replyTo: ActorRef[StatusReply[Done]]) extends Command
  case class Delete(d: Device, replyTo: ActorRef[StatusReply[Done]]) extends Command
  case class Find(id: String, replyTo: ActorRef[Option[Device]]) extends Command
  case class FindAll(replyTo: ActorRef[State]) extends Command

  trait Event extends CborSerializable
  case class Created(id: String, d: Device) extends Event
  case class Updated(id: String, d: Device) extends Event
  case class Deleted(id: String, d: Device) extends Event

  val commandHandler: (State, Command) => Effect[Event, State] = (state, command) =>
    command match {
      case Create(d, replyTo) =>
        if (state.devices.contains(d.id)) {
          replyTo ! StatusReply.Error(s"Item '${d.id}' was already added to devices map'")
          Effect.none
        } else {
          Effect
            .persist(Created(d.id, d))
            .thenRun(_ => replyTo ! StatusReply.Ack)
        }
      case Update(d, replyTo) =>
        if (state.devices.contains(d.id)) {
          replyTo ! StatusReply.Error(s"Cannot update the device: '${d.id}' was not present in the devices map'")
          Effect.none
        } else {
          Effect
            .persist(Updated(d.id, d))
            .thenRun(_ => replyTo ! StatusReply.Ack)
        }
      case Delete(d, replyTo) =>
        if (state.devices.contains(d.id)) {
          replyTo ! StatusReply.Error(s"Cannot delete the device: '${d.id}' was not present in the devices map'")
          Effect.none
        } else {
          Effect
            .persist(Deleted(d.id, d))
            .thenRun(_ => replyTo ! StatusReply.Ack)
        }
      case Find(id, replyTo) =>
        replyTo ! state.devices.get(id)
        Effect.none
      case FindAll(replyTo) =>
        replyTo ! state
        Effect.none
    }

  val eventHandler: (State, Event) => State = (state, event) =>
    event match {
      case Created(id, d) => state.copy(devices = state.devices + (id -> d))
      case Updated(id, d) => state.copy(devices = state.devices + (id -> d))
      case Deleted(id, _) => state.copy(devices = state.devices - id)
    }

  def apply(factId: String): Behavior[Command] =
    EventSourcedBehavior[Command, Event, State] (
      persistenceId = PersistenceId.ofUniqueId(factId),
      State(Map.empty),
      commandHandler,
      eventHandler
    )

  val loggedBehavior: Behavior[Any] = Behaviors.receiveMessage {
    case msg => println(s"$msg")
      Behaviors.same
  }

  val userBehavior: Behavior[NotUsed] = Behaviors.setup { context =>
    val logger = context.spawn(loggedBehavior, "logger")
//    val devices = context.spawn(Devices(), "counter")
    val doosan = context.spawn(Devices("doosan"), "doosan")
    val hanwha = context.spawn(Devices("hanwha"), "hanwha")

    doosan ! Devices.Create(Device("did-d-1", "doo-1", false), logger)
    doosan ! FindAll(logger)

    hanwha ! Devices.Create(Device("did-h-1", "han-1", false), logger)
    hanwha ! FindAll(logger)

//    devices ! Devices.Create(Device("did-1", "name-1", false), logger)
//    devices ! FindAll(logger)

    Behaviors.empty
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem(userBehavior, "DeviceSystem")
    StdIn.readLine()
    system.terminate()
  }
}
