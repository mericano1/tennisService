package asalvadore.service

import akka.actor.ActorRefFactory
import akka.util.Timeout
import spray.testkit.ScalatestRouteTest

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationLong

/**
 * Created by asalvadore on 07/02/15.
 */
trait AkkaTestUtils {
  self: ScalatestRouteTest =>

  implicit def executionContext = ExecutionContext.global

  implicit val timeout: Timeout = Timeout(30 seconds)

  def actorRefFactory: ActorRefFactory = system

}
