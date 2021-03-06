package asalvadore.service

import akka.util.Timeout
import asalvadore.dao.{MatchNotFoundException, TennisDao}
import asalvadore.models._
import spray.http.StatusCodes
import spray.httpx.PlayJsonSupport
import spray.routing.{ExceptionHandler, HttpService}

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
 * Created by asalvadore on 07/02/15.
 */
trait TennisHttpService extends HttpService with PlayJsonSupport {

  implicit def executionContext: ExecutionContextExecutor

  implicit val timeout: Timeout

  private val scoreCalculator = new GameScoreCalculator

  val exHandler = ExceptionHandler{
    case ex: MatchNotFoundException => complete(StatusCodes.NotFound, ex.getMessage)
    case ex: PlayerNotFoundException => complete(StatusCodes.NotFound, ex.getMessage)
  }

  val dao: TennisDao


  val tennisRoutes = handleExceptions(exHandler){
    pathPrefix("game") {
      post {
        entity(as[NewGameRequest]) { newGame =>
          val matchId = Future {
            NewGameResponse(dao.createMatch(newGame.playerOne, newGame.playerTwo))
          }
          complete(StatusCodes.Created, matchId)
        }
      } ~
        path(JavaUUID) { id =>
          post {
            entity(as[UpdateScoreRequest]) { updScore =>
              val details = dao.getMatchDetails(id.toString)
              val newScores = scoreCalculator.updateScore(details.score, updScore.scoring, id.toString)
              dao.updateScore(id.toString, newScores)
              if (newScores.winner.isDefined){
                dao.completeMatch(id.toString)
              }
              complete(UpdateScoreResponse(newScores.getCurrentGame))
            }
          } ~
            get {
              complete(dao.getMatchDetails(id.toString))
            }
        }
    }
  }


}



