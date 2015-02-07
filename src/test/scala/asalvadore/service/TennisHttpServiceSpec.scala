package asalvadore.service

import java.util.UUID

import asalvadore.dao.{InMemoryTennisDaoImpl, TennisDao}
import asalvadore.models._
import org.scalatest.{FlatSpec, Matchers}
import spray.http.{ContentTypes, StatusCodes}
import spray.testkit.ScalatestRouteTest

/**
 * Created by asalvadore on 07/02/15.
 */
class TennisHttpServiceSpec extends FlatSpec with ScalatestRouteTest with TennisHttpService with Matchers with AkkaTestUtils with GameConstants{
  private val player1 = Player("elvis presley")
  private val player2 = Player("john paul jones")
  private val goodRequest = NewGameRequest(player1, player2)

  override val dao: TennisDao = new InMemoryTennisDaoImpl
  val randomId = UUID.randomUUID().toString

  "POST to /game" should "create a new match and return the id" in {
    Post("/game", goodRequest) ~> tennisRoutes ~> check {
      status should be(StatusCodes.Created)
      contentType should be(ContentTypes.`application/json`)
      val resp = responseAs[NewGameResponse]
      resp.id.trim should not be ("")
    }
  }

  it should "return a bad request message when the request format is incorrect" in {
    Post("/game", "hi there") ~> sealRoute(tennisRoutes) ~> check {
      status should be(StatusCodes.BadRequest)
    }
  }

  "PUT to /game/{id}" should "update the score when called with which player won the last point" in {
    val createResp = Post("/game", goodRequest) ~> tennisRoutes ~> check {
      responseAs[NewGameResponse]
    }
    val req = UpdateScoreRequest(player1)
    Put("/game/" + createResp.id, req) ~> tennisRoutes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      val resp = responseAs[UpdateScoreResponse]
      resp.game.playerOne.points should be(15)
      resp.game.playerOne.advantage should be(false)
    }
  }

  it should "terminate the game when a winner is found" in {
    val createResp = Post("/game", goodRequest) ~> tennisRoutes ~> check {
      responseAs[NewGameResponse]
    }
    val req = UpdateScoreRequest(player1)

    val game1 = TennisGame(PlayerPoints(player1, 0), PlayerPoints(player2, 0))
    val set = TennisSet(List(game1))
    var score = TennisScore(List(set))
    (1 to MIN_SCORES_PER_MATCH * MIN_WIN_IN_SET * MIN_WIN_IN_GAME).foreach { idx =>
      Put("/game/" + createResp.id, req) ~> tennisRoutes
    }
    Get("/game/" + createResp.id) ~> tennisRoutes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      val resp = responseAs[MatchDetailsResponse]
      resp.status should be (MatchStatus.Complete)
    }

  }

  it should "return 404 when the id was not found" in {
    val req = UpdateScoreRequest(player1)
    Put("/game/" + randomId, req) ~> sealRoute(tennisRoutes) ~> check {
      status should be(StatusCodes.NotFound)
    }
  }

  it should "return 404 when the player was not found" in {
    val createResp = Post("/game", goodRequest) ~> tennisRoutes ~> check {
      responseAs[NewGameResponse]
    }
    val req = UpdateScoreRequest(Player("random name"))
    Put("/game/" + createResp.id, req) ~> sealRoute(tennisRoutes) ~> check {
      status should be(StatusCodes.NotFound)
    }
  }

  it should "return 400 for a bad request format" in {
    Put("/game/" + randomId, "hello") ~> sealRoute(tennisRoutes) ~> check {
      status should be(StatusCodes.BadRequest)
    }
  }


  "GET to /game/{id}" should "fetch the status of any match" in {
    val createResp = Post("/game", goodRequest) ~> tennisRoutes ~> check {
      responseAs[NewGameResponse]
    }

    Put("/game/" + createResp.id, UpdateScoreRequest(player1)) ~> tennisRoutes

    Get("/game/" + createResp.id) ~> tennisRoutes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      val resp = responseAs[MatchDetailsResponse]
      resp.playerOne should be(goodRequest.playerOne)
      resp.playerTwo should be(goodRequest.playerTwo)
      resp.score should be(TennisScore(List(
        TennisSet(List(
          TennisGame(
            PlayerPoints(player1, 15, false),
            PlayerPoints(player2, 0, false)))
        ))
      ))
      resp.status should be(MatchStatus.Ongoing)
      resp.durationInSec should be >= 0L
    }
  }


  it should "return 404 when the id was not found" in {
    Get("/game/" + randomId) ~> sealRoute(tennisRoutes) ~> check {
      status should be(StatusCodes.NotFound)
    }
  }
}
