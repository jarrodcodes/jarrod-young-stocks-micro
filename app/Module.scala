import actors._
import akka.actor.typed.ActorRef
import akka.stream.Materializer
import com.google.inject.AbstractModule
import javax.inject.{Inject, Provider, Singleton}
import play.api.libs.concurrent.AkkaGuiceSupport

import scala.concurrent.ExecutionContext

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindTypedActor(StocksActor(), "stocksActor")
    bindTypedActor(RootActor, "rootActor")
    bind(classOf[UserActor.Factory]).toProvider(classOf[UserActorFactoryProvider])
  }
}

@Singleton
class UserActorFactoryProvider @Inject()(
                                          stocksActor: ActorRef[StocksActor.GetStocks],
                                          mat: Materializer,
                                          ec: ExecutionContext,
                                        ) extends Provider[UserActor.Factory] {
  def get(): UserActor.Factory = UserActor(_, stocksActor)(mat, ec)
}
