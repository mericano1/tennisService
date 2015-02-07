package asalvadore.dao

import asalvadore.models.{MatchDetails, Player, TennisScore}

/**
 * Created by asalvadore on 07/02/15.
 */
class TennisDaoImpl extends TennisDao{
  override def createMatch(players: List[Player]): String = ???

  override def updateScore(matchId: String, score: TennisScore): Boolean = ???

  override def completeMatch(matchId: String): Unit = ???

  override def getMatchDetails(matchId: String): MatchDetails = ???
}
