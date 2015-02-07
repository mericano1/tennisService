package asalvadore.dao

import asalvadore.models.{MatchDetailsResponse, Player, TennisScore}

/**
 * Data access object. I would separate the DAO domain model from the API domain model and use DTO
 * but there is no enough time
 *
 */
trait TennisDao {
  def createMatch(playerOne: Player, playerTwo: Player): String
  def updateScore(matchId: String, score: TennisScore): Boolean
  def completeMatch(matchId: String):Boolean
  def getMatchDetails(matchId: String): MatchDetailsResponse
}

