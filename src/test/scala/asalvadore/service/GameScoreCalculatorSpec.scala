package asalvadore.service

import asalvadore.models._
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by asalvadore on 07/02/15.
 */
class GameScoreCalculatorSpec extends FlatSpec with Matchers {
  val scoreUpd = new GameScoreCalculator
  val playerA = Player("a")
  val playerB = Player("b")

  "updateGame" should "increment first player to 15 when score is on 0-0" in {
    val game = TennisGame(PlayerPoints(playerA, 0), PlayerPoints(playerB, 0))
    val result = scoreUpd.updateGame(game, playerA, "")
    result.playerOne.points should be(15)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(0)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }

  it should "increment second player to 15 when score is on 0-0" in {
    val game = TennisGame(PlayerPoints(playerA, 0), PlayerPoints(playerB, 0))
    val result = scoreUpd.updateGame(game, playerB, "")
    result.playerOne.points should be(0)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(15)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }

  it should "increment second player to 30 when score is on 0-15" in {
    val game = TennisGame(PlayerPoints(playerA, 0), PlayerPoints(playerB, 15))
    val result = scoreUpd.updateGame(game, playerB, "")
    result.playerOne.points should be(0)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(30)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }

  it should "increment second player to 40 when score is on 15-30" in {
    val game = TennisGame(PlayerPoints(playerA, 15), PlayerPoints(playerB, 30))
    val result = scoreUpd.updateGame(game, playerB, "")
    result.playerOne.points should be(15)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(40)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }

  it should "increment first player to 40 when score is on 30-30" in {
    val game = TennisGame(PlayerPoints(playerA, 30), PlayerPoints(playerB, 30))
    val result = scoreUpd.updateGame(game, playerA, "")
    result.playerOne.points should be(40)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(30)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }

  it should "mark first player as winner when score is on 40-30" in {
    val game = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 30))
    val result = scoreUpd.updateGame(game, playerA, "")
    result.playerOne.points should be(40)
    result.playerOne.advantage should be(false)
    result.winner should be(Some(playerA))
    result.playerTwo.points should be(30)
    result.playerTwo.advantage should be(false)
  }

  it should "mark second player as winner when score is on 30-40" in {
    val game = TennisGame(PlayerPoints(playerA, 30), PlayerPoints(playerB, 40))
    val result = scoreUpd.updateGame(game, playerB, "")
    result.playerOne.points should be(30)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(40)
    result.winner should be(Some(playerB))
    result.playerTwo.advantage should be(false)
  }

  it should "give advantage to second player when score is on 40-40" in {
    val game = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 40))
    val result = scoreUpd.updateGame(game, playerB, "")
    result.playerOne.points should be(40)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(40)
    result.playerTwo.advantage should be(true)
    result.winner should be(None)
  }

  it should "clear advantage when score is on 40-40A and playerA scores" in {
    val game = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 40, true))
    val result = scoreUpd.updateGame(game, playerA, "")
    result.playerOne.points should be(40)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(40)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }

  it should "clear advantage when score is on 40A-40 and playerB scores" in {
    val game = TennisGame(PlayerPoints(playerA, 40, true), PlayerPoints(playerB, 40))
    val result = scoreUpd.updateGame(game, playerB, "")
    result.playerOne.points should be(40)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(40)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }

  it should "mark player A as winner when score is on 40A-40 and playerA scores" in {
    val game = TennisGame(PlayerPoints(playerA, 40, true), PlayerPoints(playerB, 40))
    val result = scoreUpd.updateGame(game, playerA, "")
    result.playerOne.points should be(40)
    result.playerOne.advantage should be(true)
    result.playerTwo.points should be(40)
    result.playerTwo.advantage should be(false)
    result.winner should be(Some(playerA))
  }

  it should "mark player B as winner when score is on 40-40A and playerB scores" in {
    val game = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 40, true))
    val result = scoreUpd.updateGame(game, playerB, "")
    result.playerOne.points should be(40)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(40)
    result.playerTwo.advantage should be(true)
    result.winner should be(Some(playerB))
  }


  "updateSet" should "update the last game" in {
    val game = TennisGame(PlayerPoints(playerA, 0), PlayerPoints(playerB, 0))
    val set = TennisSet(List(game))
    val updSet = scoreUpd.updateSet(set, playerB, "")
    val result = updSet.games.last
    result.playerOne.points should be(0)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(15)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }

  it should "create a new match if the previous one was finished" in {
    val game = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 40, true), Some(playerB))
    val set = TennisSet(List(game))
    val updSet = scoreUpd.updateSet(set, playerB, "")
    updSet.games.size should be(2)
    val result = updSet.games.last
    result.playerOne.points should be(0)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(15)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }

  it should "mark the set as won if one player has 6 wins or there is a diff of 2" in {
    val game1 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game2 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game3 = TennisGame(PlayerPoints(playerA, 15), PlayerPoints(playerB, 40), Some(playerB))
    val game4 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game5 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game6 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game7 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15))
    val set = TennisSet(List(game1, game2, game3, game4, game5, game6, game7))
    val updSet = scoreUpd.updateSet(set, playerA, "")
    val result = updSet.games.last
    result.playerOne.points should be(40)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(15)
    result.playerTwo.advantage should be(false)
    result.winner should be(Some(playerA))
    updSet.winner should be(Some(playerA))
  }

  it should "NOT mark the set as won if one player has 6 wins and the other is at 5" in {
    val game1 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game2 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game3 = TennisGame(PlayerPoints(playerA, 15), PlayerPoints(playerB, 40), Some(playerB))
    val game4 = TennisGame(PlayerPoints(playerA, 15), PlayerPoints(playerB, 40), Some(playerB))
    val game5 = TennisGame(PlayerPoints(playerA, 15), PlayerPoints(playerB, 40), Some(playerB))
    val game6 = TennisGame(PlayerPoints(playerA, 15), PlayerPoints(playerB, 40), Some(playerB))
    val game7 = TennisGame(PlayerPoints(playerA, 15), PlayerPoints(playerB, 40), Some(playerB))
    val game8 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game9 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game10 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game11 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15))
    val set = TennisSet(List(game1, game2, game3, game4, game5, game6, game7, game8, game9, game10, game11))
    val updSet = scoreUpd.updateSet(set, playerA, "")
    val result = updSet.games.last
    result.playerOne.points should be(40)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(15)
    result.playerTwo.advantage should be(false)
    result.winner should be(Some(playerA))
    updSet.winner should be(None)
  }


  "updateScore" should "update the last game" in {
    val game = TennisGame(PlayerPoints(playerA, 0), PlayerPoints(playerB, 15))
    val set = TennisSet(List(game))
    val score = TennisScore(List(set))
    val updScore = scoreUpd.updateScore(score, playerB, "")
    val result = updScore.sets.last.games.last
    result.playerOne.points should be(0)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(30)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }

  it should "create a new set when the previous one has finished" in {
    val game1 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game2 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game3 = TennisGame(PlayerPoints(playerA, 15), PlayerPoints(playerB, 40), Some(playerB))
    val game4 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game5 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game6 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val game7 = TennisGame(PlayerPoints(playerA, 40), PlayerPoints(playerB, 15), Some(playerA))
    val set = TennisSet(List(game1, game2, game3, game4, game5, game6, game7), Some(playerA))
    val score = TennisScore(List(set))
    val updScore = scoreUpd.updateScore(score, playerB, "")
    updScore.sets.size should be(2)
    val result = updScore.sets.last.games.last
    result.playerOne.points should be(0)
    result.playerOne.advantage should be(false)
    result.playerTwo.points should be(15)
    result.playerTwo.advantage should be(false)
    result.winner should be(None)
  }










}
