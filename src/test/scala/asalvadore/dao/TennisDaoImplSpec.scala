package asalvadore.dao

import asalvadore.models._
import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by asalvadore on 07/02/15.
 */
class TennisDaoImplSpec extends FlatSpec with Matchers {
  private val player1 = Player("elvis presley")
  private val player2 = Player("john paul jones")
  private val dao = new TennisDaoImpl

  "createMatch" should "store a new match" in {
    val id = dao.createMatch(List(player1, player2))
    id should not be (null)
    id.trim should not be("")
  }


  "updateScore and getMatchDetails" should "change the score for a match" in {
    val id = dao.createMatch(List(player1, player2))

    val prevScore = TennisScore(List(
      TennisSet(List(
        TennisGame(List(
          PlayerPoints(player1, 0, false),
          PlayerPoints(player2, 0, false))
        ))
      ))
    )

    dao.getMatchDetails(id).score should be(prevScore)

    val score = TennisScore(List(
      TennisSet(List(
        TennisGame(List(
          PlayerPoints(player1, 15, false),
          PlayerPoints(player2, 0, false))
        ))
      ))
    )

    dao.updateScore(id, score)

    val details = dao.getMatchDetails(id)
    details.status should be(MatchStatus.Ongoing)
    details.score should be(score)
  }



}
