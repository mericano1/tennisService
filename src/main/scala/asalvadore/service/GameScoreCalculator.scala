package asalvadore.service

import asalvadore.models._

import scala.collection.Map
import scala.collection.immutable.Map

/**
 * Updates the game scores when a player is scoring
 */
class GameScoreCalculator extends GameConstants {

  def updateScore(score: TennisScore, player: Player, matchId: String): TennisScore = {
    if (score.winner.isDefined) score
    else {
      doUpdateScore(score, player, matchId)
    }
  }

  def doUpdateScore(score: TennisScore, player: Player, matchId: String): TennisScore = {
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
      score.copy(sets = updatedSets, winner = isThereAWinner(updatedSets, MIN_WIN_IN_GAME, MIN_SCORE_DIFF_IN_GAME))
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
      TennisSet(updatedGames, isThereAWinner(updatedGames, MIN_WIN_IN_SET, MIN_SCORE_DIFF_IN_SET))
    }
  }

  private case class ScoreUpdate(scored: PlayerPoints, opponent: PlayerPoints, winner: Option[Player] = None)

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
   * This method uses the old rules that requires a difference of 2 matches to win
   * @param games the list of games played in the current set
   * @return Some(winner) if there is a winner for the current set
   */
  private def isThereAWinner(games: List[Winnable], minScoreToWin: Int, minDiffToWin: Int): Option[Player] = {
    val lastGame = games.last
    if (lastGame.winner.isDefined){
      val playerAndMatchesWon = games.groupBy(_.winner)
        .map {
          case (Some(player), wonGames) => (player -> wonGames.size)
          case (None, _) => return None
      }

      val playerOne = lastGame.playerOne.player
      val playerTwo = lastGame.playerTwo.player
      val playerOneScore = playerAndMatchesWon.getOrElse(playerOne, 0)
      val playerTwoScore = playerAndMatchesWon.getOrElse(playerTwo, 0)
      val (currentWinner, winnerScore, looserScore) = getCurrentWinnerAndLooser(playerOne, playerTwo, playerOneScore, playerTwoScore)

      if (winnerScore >= minScoreToWin && (winnerScore - looserScore) >= minDiffToWin){
        Some(currentWinner)
      } else {
        None
      }
    } else {
      None
    }
  }


  def getCurrentWinnerAndLooser(playerOne: Player, playerTwo: Player, playerOneScore: Int, playerTwoScore: Int): (Player, Int, Int) = {
    if (playerOneScore > playerTwoScore)
      (playerOne, playerOneScore, playerTwoScore)
    else
      (playerTwo, playerTwoScore, playerOneScore)
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
