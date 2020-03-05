package model

import akka.NotUsed
import akka.stream.ThrottleMode
import akka.stream.scaladsl.Source
import play.api.libs.json.{JsNumber, JsPath, JsString, Json, Reads, Writes}
import yahoofinance.{YahooFinance, Stock => YahooStock}

import scala.concurrent.duration._

class Stock(val symbol: StockSymbol) {

  private val stockQuoteGenerator: StockQuoteGenerator = new RealStockQuoteGenerator(symbol)
  private val source: Source[StockQuote, NotUsed] = {
    Source.unfold(stockQuoteGenerator.seed) { last: StockQuote =>
      val next = stockQuoteGenerator.newQuote(last)
      Some(next, next)
    }
  }

  def update: Source[StockUpdate, NotUsed] = {
    source
      .throttle(elements = 1, per = 5.seconds, maximumBurst = 5, ThrottleMode.shaping)
      .map(sq => new StockUpdate(sq.symbol, sq.price, sq.companyName, sq.importance))
  }

  override val toString: String = s"Stock($symbol)"
}

trait StockQuoteGenerator {
  def seed: StockQuote
  def newQuote(lastQuote: StockQuote): StockQuote
}

class RealStockQuoteGenerator(symbol: StockSymbol) extends StockQuoteGenerator {
  val significance: Importance = symbol.raw match {
    case "GOOG" => new Importance("I very much care about technology, and Google has helped me learn.")
    case "BNED" => new Importance("I read constantly, and my favorite place to buy books and drink a coffee is Barnes and Noble.")
    case "SBUX" => new Importance("Coffee is my lifeblood, there's nothing better than a hot cup from Starbucks.")
    case "T" => new Importance("I watch the news religiously, especially CNN.")
    case _ => new Importance("That company sounds cool. Why do you like it?")
  }

  private def retrievedStock: YahooStock = YahooFinance.get(symbol.raw)

  override def seed: StockQuote = {
    StockQuote(symbol, new CompanyName(""), StockPrice(0.0), significance)
  }

  override def newQuote(lastQuote: StockQuote): StockQuote = {

    StockQuote(symbol, new CompanyName(retrievedStock.getName), StockPrice(retrievedStock.getQuote.getPrice.doubleValue()), significance)
  }
}

case class StockQuote(symbol: StockSymbol, companyName: CompanyName, price: StockPrice, importance: Importance)

class StockSymbol private(val raw: String) extends AnyVal {
  override def toString: String = raw
}

object StockSymbol {

  def apply(raw: String) = new StockSymbol(raw)

  implicit val stockSymbolReads: Reads[StockSymbol] = {
    JsPath.read[String].map(StockSymbol(_))
  }

  implicit val stockSymbolWrites: Writes[StockSymbol] = Writes {
    symbol: StockSymbol => JsString(symbol.raw)
  }
}

class StockPrice (val raw: Double) extends AnyVal {
  override def toString: String = raw.toString
}

class CompanyName(val raw: String) extends AnyVal {
  override def toString: String = raw.toString
}

class Importance(val raw: String) extends AnyVal {
  override def toString: String = raw.toString
}

object StockPrice {

  def apply(raw: Double): StockPrice = new StockPrice(raw)

  implicit val stockPriceWrites: Writes[StockPrice] = Writes {
    price: StockPrice => JsNumber(price.raw)
  }
}

case class StockUpdate(symbol: StockSymbol, price: StockPrice, name: CompanyName, importance: Importance)

object StockUpdate {
  implicit val stockUpdateWrites: Writes[StockUpdate] = (update: StockUpdate) => Json.obj(
    "type" -> "stockupdate",
    "symbol" -> update.symbol,
    "price" -> update.price,
    "name" -> update.name.raw,
    "importance" -> update.importance.raw
  )
}
