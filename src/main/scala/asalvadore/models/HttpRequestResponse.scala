package asalvadore.models

import asalvadore.models.MatchStatus.MatchStatus
import play.api.libs.json._

/**
 * Case classes to create a match.
 * The player class is superfluous right now but it might come useful if more info are required.
 */
case class Player(name: String)

case class NewGameRequest (players: List[Player])
case class NewGameResponse (id: String)

case class PlayerPoints(player: Player, points: Int, advantage: Boolean)
case class UpdateScoreRequest(winningPlayer: Player)
case class UpdateScoreResponse(points: PlayerPoints)

case class TennisGame (points:List[PlayerPoints])
case class TennisSet(games: List[TennisGame])
case class TennisScore(sets: List[TennisSet])
case class MatchDetails(status: MatchStatus, durationInSec: Long, score:TennisScore)
case class MatchStatusResponse(players: List[Player], details: MatchDetails)




// These classes are required for the json serialization/deserialization
object Player{
  implicit val format = Json.format[Player]
}
object NewGameRequest{
  implicit val format = Json.format[NewGameRequest]
}
object NewGameResponse{
  implicit val format = Json.format[NewGameResponse]
}
object UpdateScoreRequest{
  implicit val format = Json.format[UpdateScoreRequest]
}
object PlayerPoints{
  implicit val format = Json.format[PlayerPoints]
}
object UpdateScoreResponse{
  implicit val format = Json.format[UpdateScoreResponse]
}
object TennisGame{
  implicit val format = Json.format[TennisGame]
}
object TennisSet{
  implicit val format = Json.format[TennisSet]
}
object TennisScore{
  implicit val format = Json.format[TennisScore]
}
object MatchDetails{
  implicit val format = Json.format[MatchDetails]
}
object MatchStatusResponse{
  implicit val format = Json.format[MatchStatusResponse]
}

object MatchStatus extends Enumeration {
  type MatchStatus = Value
  val Ongoing = Value(0, "Ongoing")
  val Complete = Value(1, "Complete")

  implicit val enumReads: Reads[MatchStatus] = EnumJsonUtils.enumReads(MatchStatus)

  implicit def enumWrites: Writes[MatchStatus] = EnumJsonUtils.enumWrites

  def fromText(txt: String) = {
    val safeText = Option(txt).map(_.trim.toLowerCase)
    safeText match {
      case Some("ongoing") => Ongoing
      case Some("complete") => Complete
      case _ => throw new IllegalArgumentException
    }
  }

}

/**
 * Json enum reads and writes
 */
object EnumJsonUtils {
  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] =
    new Reads[E#Value] {
      def reads(json: JsValue): JsResult[E#Value] = json match {
        case JsString(s) => {
          try {
            JsSuccess(enum.withName(s))
          } catch {
            case _: NoSuchElementException =>
              JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s ' ")
          }
        }
        case _ => JsError("String value expected")
      }
    }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] =
    new Writes[E#Value] {
      def writes(v: E#Value): JsValue = JsString(v.toString)
    }

  implicit def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(enumReads(enum), enumWrites)
  }
}


