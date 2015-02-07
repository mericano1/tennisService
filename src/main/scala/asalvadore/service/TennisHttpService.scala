package asalvadore.service

import akka.util.Timeout
import spray.http.StatusCodes
import spray.httpx.PlayJsonSupport
import spray.routing.HttpService

import scala.concurrent.ExecutionContextExecutor

/**
 * Created by asalvadore on 07/02/15.
 */
trait TennisHttpService extends HttpService with PlayJsonSupport {

  implicit def executionContext: ExecutionContextExecutor

  implicit val timeout: Timeout

  val tennisRoutes = {
    pathPrefix("game") {
      post {
        complete(StatusCodes.BadRequest, "Not yet implemented")
      }
    }
  }
}



