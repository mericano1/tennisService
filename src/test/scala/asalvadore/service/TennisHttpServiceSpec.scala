package asalvadore.service

import asalvadore.models._
import org.scalatest.{FlatSpec, Matchers}
import spray.http.{ContentTypes, StatusCodes}
import spray.testkit.ScalatestRouteTest

/**
 * Created by asalvadore on 07/02/15.
 */
class TennisHttpServiceSpec extends FlatSpec with ScalatestRouteTest with TennisHttpService with Matchers with AkkaTestUtils {
  private val player1 = Player("elvis presley")
  private val player2 = Player("john paul jones")
  private val goodRequest = NewGameRequest(List(player1, player2))

  "POST to /game" should "create a new match and return the id" in {
    Post("/game", goodRequest) ~> tennisRoutes ~> check {
      status should be(StatusCodes.Created)
      contentType should be(ContentTypes.`application/json`)
      val resp = responseAs[NewGameResponse]
      resp.id.trim should not be ("")
    }
  }

  it should "return a bad request message when the request format is incorrect" in {
    Post("/game", "hi there") ~> tennisRoutes ~> check {
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
      resp.points.player.name should be(player1.name)
      resp.points.points should be(15)
      resp.points.advantage should be(false)
    }
  }

  it should "return 404 when the id was not found" in {
    val req = UpdateScoreRequest(player1)
    Put("/game/12345", req) ~> tennisRoutes ~> check {
      status should be(StatusCodes.NotFound)
    }
  }

  it should "return 400 for a bad request format" in {
    Put("/game/12345", "hello") ~> tennisRoutes ~> check {
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
      val resp = responseAs[MatchStatusResponse]
      resp.players should be(goodRequest.players)
      resp.details.score should be(TennisScore(List(
        TennisSet(List(
          TennisGame(List(
            PlayerPoints(player1, 15, false),
            PlayerPoints(player2, 0, false)))
        )))
      ))
      resp.details.status should be(MatchStatus.Ongoing)
      resp.details.durationInSec should be >= 0L
    }
  }


  it should "return 404 when the id was not found" in {
    Get("/game/12345") ~> tennisRoutes ~> check {
      status should be(StatusCodes.NotFound)
    }
  }


}
