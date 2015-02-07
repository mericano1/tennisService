package asalvadore.dao

import java.util.UUID

import asalvadore.models.MatchStatus.MatchStatus
import asalvadore.models._
import org.joda.time.{Seconds, DateTime}

/**
 * Dao implementation. Will start with an in-memory version and replace it with a db version if I have time
 */
class InMemoryTennisDaoImpl extends TennisDao {

  case class Match(playerOne: Player, playerTwo: Player, matchScore: TennisScore, startTime: DateTime, matchStatus: MatchStatus = MatchStatus.Ongoing)

  private var matches = scala.collection.mutable.Map[String, Match]()

  override def createMatch(playerOne: Player, playerTwo: Player): String = {
    val id = UUID.randomUUID().toString
    matches += id -> Match(playerOne, playerTwo, TennisScore(List(
      TennisSet(List(
        TennisGame(PlayerPoints(playerOne, 0), PlayerPoints(playerTwo, 0))
      ))
    )), DateTime.now())
    id
  }

  override def updateScore(matchId: String, score: TennisScore): Boolean = {
    matches.get(matchId).map { element =>
      matches += matchId -> element.copy(matchScore = score)
      true
    }.getOrElse {
      throw new MatchNotFoundException(matchId)
    }
  }

  override def completeMatch(matchId: String): Boolean = {
    matches.get(matchId).map { element =>
      matches += matchId -> element.copy(matchStatus = MatchStatus.Complete)
      true
    }.getOrElse {
      throw new MatchNotFoundException(matchId)
    }
  }

  override def getMatchDetails(matchId: String): MatchDetailsResponse = {
    matches.get(matchId).map { element =>
      MatchDetailsResponse(
        element.playerOne,
        element.playerTwo,
        element.matchStatus,
        Seconds.secondsBetween(element.startTime, DateTime.now()).getSeconds.toLong,
        element.matchScore)
    }.getOrElse {
      throw new MatchNotFoundException(matchId)
    }
  }
}
