package actors

import actors.StocksActor.{GetStocks, Stocks}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop, Scheduler}
import akka.stream._
import akka.stream.scaladsl._
import akka.util.Timeout
import akka.{Done, NotUsed}
import javax.inject._
import org.slf4j.Logger
import play.api.libs.json._
import model._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class UserActor @Inject()(id: String, stocksActor: ActorRef[GetStocks])(implicit
                                                                        context: ActorContext[UserActor.Message],
) {

  import UserActor._

  val log: Logger = context.log

  implicit val timeout: Timeout = Timeout(50.millis)
  implicit val system: ActorSystem[Nothing] = context.system

  import context.executionContext

  val (hubSink, hubSource) = MergeHub.source[JsValue](perProducerBufferSize = 16)
    .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
    .run()

  private var stocksMap: Map[StockSymbol, UniqueKillSwitch] = Map.empty

  private val jsonSink: Sink[JsValue, Future[Done]] = Sink.foreach { json =>
    val symbol: StockSymbol = (json \ "symbol").as[StockSymbol]

    if (json.toString().contains("add")) {
      addStocks(Set(symbol))
    }
    if (json.toString().contains("remove")) {
      unwatchStocks(Set(symbol))
    }
  }

  def behavior: Behavior[Message] = {
    Behaviors.receiveMessage[Message] {

      case WatchStocks(symbols, replyTo) =>
        addStocks(symbols)
        replyTo ! websocketFlow
        Behaviors.same

      case UnwatchStocks(symbols) =>
        unwatchStocks(symbols)
        Behaviors.same

      case InternalStop =>
        Behaviors.stopped
    }.receiveSignal {
      case (_, PostStop) =>
        log.info("Stopping actor {}", context.self)
        unwatchStocks(stocksMap.keySet)
        Behaviors.same
    }
  }

  private lazy val websocketFlow: Flow[JsValue, JsValue, NotUsed] = {
    Flow.fromSinkAndSourceCoupled(jsonSink, hubSource).watchTermination() { (_, termination) =>
      context.pipeToSelf(termination)((_: Try[Done]) => InternalStop)
      NotUsed
    }
  }

  private def addStocks(symbols: Set[StockSymbol]): Future[Unit] = {
    import akka.actor.typed.scaladsl.AskPattern._

    val future = stocksActor.ask(replyTo => GetStocks(symbols, replyTo))

    future.map { newStocks: Stocks =>
      newStocks.stocks.foreach { stock =>
        if (!stocksMap.contains(stock.symbol)) {
          log.info("Adding stock {}", stock)
          addStock(stock)
        }
      }
    }
  }

  private def addStock(stock: Stock): Unit = {
    val updateSource = stock.update.map(su => Json.toJson(su))

    val killswitchFlow: Flow[JsValue, JsValue, UniqueKillSwitch] = {
      Flow.apply[JsValue]
        .joinMat(KillSwitches.singleBidi[JsValue, JsValue])(Keep.right)
        .backpressureTimeout(1.seconds)
    }

    val graph: RunnableGraph[UniqueKillSwitch] = {
      updateSource
        .viaMat(killswitchFlow)(Keep.right)
        .to(hubSink)
        .named(s"stock-${stock.symbol}-$id")
    }

    val killSwitch = graph.run()

    stocksMap += (stock.symbol -> killSwitch)
  }

  def unwatchStocks(symbols: Set[StockSymbol]): Unit = {
    symbols.foreach { symbol =>
      stocksMap.get(symbol).foreach { killSwitch =>
        killSwitch.shutdown()
      }
      stocksMap -= symbol
    }
  }
}

object UserActor {

  sealed trait Message

  case class WatchStocks(symbols: Set[StockSymbol], replyTo: ActorRef[Flow[JsValue, JsValue, NotUsed]]) extends Message {
    require(symbols.nonEmpty, "Must specify at least one symbol!")
  }

  case class UnwatchStocks(symbols: Set[StockSymbol]) extends Message {
    require(symbols.nonEmpty, "Must specify at least one symbol!")
  }

  private case object InternalStop extends Message

  trait Factory {
    def apply(id: String): Behavior[Message]
  }

  def apply(id: String, stocksActor: ActorRef[GetStocks])(implicit
                                                          mat: Materializer,
                                                          ec: ExecutionContext,
  ): Behavior[Message] = {
    Behaviors.setup { implicit context =>
      implicit val scheduler: Scheduler = context.system.scheduler
      new UserActor(id, stocksActor).behavior
    }
  }
}
