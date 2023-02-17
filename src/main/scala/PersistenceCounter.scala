import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import com.example.CborSerializable

object PersistenceCounter {
  final case class State(count: Int) extends CborSerializable

  sealed trait Command extends CborSerializable
  final case object Increment extends Command
  final case class IncrementBy(value: Int) extends Command
  final case class GetValue(replyTo: ActorRef[State]) extends Command
  final case object Clear extends Command

  sealed trait Event extends CborSerializable
  case class Incremented(value: Int) extends Event
  case object Cleared extends Event

  val commandHandler: (State, Command) => Effect[Event, State] = (state, command) =>
    command match {
      case Increment => Effect.persist(Incremented(state.count+1))
      case IncrementBy(by) => Effect.persist(Incremented(state.count+by))
      case GetValue(replyTo) => Effect.reply(replyTo)(state)
      case Clear => Effect.persist(Cleared)
    }

  val eventHandler: (State, Event) => State = {(state, event) =>
    event match {
      case Incremented(data) => state.copy(data)
      case Cleared => State(0)
    }
  }

  def counter(): Behavior[Command] =
    EventSourcedBehavior[Command, Event, State](
      persistenceId = PersistenceId.ofUniqueId("counter"),
      emptyState = State(0),
      commandHandler = commandHandler,
      eventHandler = eventHandler
    )
}

object PlayCounter {
  val loggedBehavior: Behavior[Any] = Behaviors.receiveMessage {
    case msg => println(s"$msg")
      Behaviors.same
  }
}