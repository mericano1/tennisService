package asalvadore

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern._
import akka.util.Timeout
import asalvadore.service.TennisHttpActor
import org.slf4j.LoggerFactory
import spray.can.Http

import scala.concurrent.duration.DurationDouble

/**
 * Created by asalvadore on 07/02/15.
 */
object TennisMain extends App {
  private val logger = LoggerFactory.getLogger(this.getClass)
  implicit val system = ActorSystem("redapi-web")

  logger.info(s"Actor system $system is up and running")

  // create and start our service actor
  val service = system.actorOf(Props[TennisHttpActor], "tennis-actor")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)


  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run() = {
      logger.info("Shutting down...")
      IO(Http) ! Http.Unbind
    }
  })

}
