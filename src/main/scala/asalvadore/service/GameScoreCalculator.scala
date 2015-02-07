package asalvadore.service

import asalvadore.models._

import scala.collection.Map
import scala.collection.immutable.Map

/**
 * Created by asalvadore on 07/02/15.
 */
class GameScoreCalculator {

  case class ScoreUpdate(scored: PlayerPoints, opponent: PlayerPoints, winner: Option[Player] = None)


  def updateScore(score: TennisScore, player: Player, matchId: String): TennisScore = {
    val tennisSets = score.sets
    val lastSet = tennisSets.last
    if (lastSet.winner.isDefined) {
      val lastGame = lastSet.games.last
      val updatedSet = updateSet(TennisSet(List(
        TennisGame.start(lastGame.playerOne.player, lastGame.playerTwo.player)
      )), player, matchId)
      TennisScore(tennisSets :+ updatedSet)
    } else {
      val updatedSet = updateSet(tennisSets.last, player, matchId)
      val updatedSets = tennisSets.updated(tennisSets.length - 1, updatedSet)
      score.copy(sets = updatedSets)
    }
  }


  /**
   * Updates the set or sets one of the player as the winner of the set.
* @param tennisSet
   * @param player
   * @param matchId
   * @return
   */
  def updateSet(tennisSet: TennisSet, player: Player, matchId: String): TennisSet = {
    val tennisGames = tennisSet.games
    val lastGame = tennisGames.last
    if (lastGame.winner.isDefined) {
      val updatedGame = this.updateGame(
        TennisGame.start(lastGame.playerOne.player, lastGame.playerTwo.player), player, matchId
      )
      TennisSet(tennisGames :+ updatedGame)
    } else {
      val updatedGame = this.updateGame(lastGame, player, matchId)
      val updatedGames = tennisGames.updated(tennisGames.length - 1, updatedGame)
      TennisSet(updatedGames, isThereASetWinner(updatedGames))
    }
  }

  /**
   * Updates the game points or sets one of the player as the winner.
   */
  def updateGame(game: TennisGame, player: Player, matchId: String): TennisGame = player match {
    case game.playerOne.player =>
      val scoreUpdate = incrementPoints(game.playerOne, game.playerTwo)
      TennisGame(scoreUpdate.scored, scoreUpdate.opponent, scoreUpdate.winner)
    case game.playerTwo.player =>
      val scoreUpdate = incrementPoints(game.playerTwo, game.playerOne)
      TennisGame(scoreUpdate.opponent, scoreUpdate.scored, scoreUpdate.winner)
    case _ => throw new PlayerNotFoundException(player, matchId)

  }


  /**
   * This method uses the old rules that requires a different of 2 matches to win
   * @param games the list of games played in the current set
   * @return Some(winner) if there is a winner for the current set
   */
  private def isThereASetWinner(games: List[TennisGame]): Option[Player] = {
    if (games.last.winner.isDefined){
      val playerAndMatchesWon = games.groupBy(_.winner)
        .map {case (Some(player), wonGames) => (player -> wonGames.size)}

      val (currentWinner, winnerScore) = playerAndMatchesWon.maxBy(_._2)
      val (currentLoose, looserScore) = playerAndMatchesWon.minBy(_._2)

      if (winnerScore >= 5 && (winnerScore - looserScore) >= 2){
        Some(currentWinner)
      } else {
        None
      }
    } else {
      None
    }
  }

  private def incrementPoints(toIncrement: PlayerPoints, opponent: PlayerPoints): ScoreUpdate = {
    toIncrement.points match {
      case 0 => ScoreUpdate(toIncrement.copy(points = 15), opponent)
      case 15 => ScoreUpdate(toIncrement.copy(points = 30), opponent)
      case 30 => ScoreUpdate(toIncrement.copy(points = 40), opponent)
      case 40 if toIncrement.advantage =>
        ScoreUpdate(toIncrement, opponent, Some(toIncrement.player))
      case 40 if opponent.points != 40 =>
        ScoreUpdate(toIncrement, opponent, Some(toIncrement.player))
      case 40 if opponent.points == 40 && opponent.advantage =>
        ScoreUpdate(toIncrement.copy(advantage = false), opponent.copy(advantage = false))
      case 40 if opponent.points == 40 =>
        ScoreUpdate(toIncrement.copy(advantage = true), opponent.copy(advantage = false))
    }
  }

}
