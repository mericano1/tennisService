package asalvadore.dao

import asalvadore.models.{MatchDetails, Player, TennisScore}

/**
 * Data access object. I would separate the DAO domain model from the API domain model and use DTO
 * but there is no enough time
 *
 */
trait TennisDao {
  def createMatch(players: List[Player]): String
  def updateScore(matchId: String, score: TennisScore): Boolean
  def completeMatch(matchId: String)
  def getMatchDetails(matchId: String): MatchDetails
}

