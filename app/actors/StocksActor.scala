package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import model._

import scala.collection.mutable

object StocksActor {

  final case class Stocks(stocks: Set[Stock]) {
    require(stocks.nonEmpty, "Must specify at least one stock!")
  }

  final case class GetStocks(symbols: Set[StockSymbol], replyTo: ActorRef[Stocks])

  def apply(
             stocksMap: mutable.Map[StockSymbol, Stock] = mutable.HashMap(),
           ): Behavior[GetStocks] = {
    Behaviors.logMessages(
      Behaviors.receiveMessage {
        case GetStocks(symbols, replyTo) =>
          val stocks = symbols.map(symbol => stocksMap.getOrElseUpdate(symbol, new Stock(symbol)))
          replyTo ! Stocks(stocks)
          Behaviors.same
      }
    )
  }
}
