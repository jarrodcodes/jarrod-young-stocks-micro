package controllers

import actors._
import akka.NotUsed
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, Scheduler}
import akka.stream.scaladsl._
import akka.util.Timeout
import javax.inject._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockController @Inject()(userParentActor: ActorRef[RootActor.Create],
                                cc: ControllerComponents)
                              (implicit ec: ExecutionContext, scheduler: Scheduler)
  extends AbstractController(cc) {

  val logger: Logger = play.api.Logger(getClass)

  def ws: WebSocket = WebSocket.acceptOrResult[JsValue, JsValue] {
      wsFutureFlow(_).map { flow =>
        Right(flow)
      }.recover {
        case e: Exception =>
          logger.error("Cannot create websocket", e)
          val jsError = Json.obj("error" -> "Cannot create websocket")
          val result = InternalServerError(jsError)
          Left(result)
      }
  }

  private def wsFutureFlow(request: RequestHeader): Future[Flow[JsValue, JsValue, NotUsed]] = {
    implicit val timeout: Timeout = Timeout(1.second)
    userParentActor.ask(replyTo => RootActor.Create(request.id.toString, replyTo))
  }
}
