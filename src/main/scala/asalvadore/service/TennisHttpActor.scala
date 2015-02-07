package asalvadore.service

import akka.actor.{Actor, ActorLogging, ActorRefFactory, ActorSystem}
import akka.util.Timeout
import asalvadore.dao.{InMemoryTennisDaoImpl, TennisDao}

import scala.concurrent.duration.DurationInt

/**
 * Created by asalvadore on 07/02/15.
 */
class TennisHttpActor extends Actor with ActorLogging with TennisHttpService {

  override implicit def executionContext = context.dispatcher

  override implicit val timeout: Timeout = Timeout(30 seconds)
  override val dao: TennisDao = new InMemoryTennisDaoImpl
  implicit val system = ActorSystem("tennisService")

  override implicit def actorRefFactory: ActorRefFactory = context

  override def receive: Receive = runRoute(tennisRoutes)
}
