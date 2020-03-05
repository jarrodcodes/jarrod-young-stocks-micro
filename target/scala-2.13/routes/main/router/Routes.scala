// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/jarrod_young/Desktop/jarrod-young-stocks-micro/conf/routes
// @DATE:Thu Mar 05 11:30:14 EST 2020

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:5
  StockController_0: controllers.StockController,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:5
    StockController_0: controllers.StockController
  ) = this(errorHandler, StockController_0, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, StockController_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """ws""", """controllers.StockController.ws"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:5
  private[this] lazy val controllers_StockController_ws0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("ws")))
  )
  private[this] lazy val controllers_StockController_ws0_invoker = createInvoker(
    StockController_0.ws,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.StockController",
      "ws",
      Nil,
      "GET",
      this.prefix + """ws""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:5
    case controllers_StockController_ws0_route(params@_) =>
      call { 
        controllers_StockController_ws0_invoker.call(StockController_0.ws)
      }
  }
}
