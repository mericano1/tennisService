package asalvadore.models

import asalvadore.models.MatchStatus.MatchStatus
import play.api.libs.json._

/**
 * Case classes to create a match.
 * The player class is superfluous right now but it might come useful if more info are required.
 */
case class Player(name: String)

case class NewGameRequest (playerOne: Player, playerTwo: Player)
case class NewGameResponse (id: String)

case class PlayerPoints(player: Player, points: Int, advantage: Boolean = false)
case class UpdateScoreRequest(scoring: Player)
case class UpdateScoreResponse(game: TennisGame)

trait Winnable {
  def winner: Option[Player]
  def playerOne: PlayerPoints
  def playerTwo: PlayerPoints
}
case class TennisGame (playerOne: PlayerPoints, playerTwo: PlayerPoints, winner: Option[Player] = None) extends Winnable
case class TennisSet(games: List[TennisGame], winner: Option[Player] = None) extends Winnable {
  override def playerOne: PlayerPoints = games.last.playerOne
  override def playerTwo: PlayerPoints = games.last.playerTwo
}
case class TennisScore(sets: List[TennisSet], winner: Option[Player] = None) extends Winnable {
  def getCurrentGame = this.sets.last.games.last
  override def playerOne: PlayerPoints = getCurrentGame.playerOne
  override def playerTwo: PlayerPoints = getCurrentGame.playerTwo
}
case class MatchDetailsResponse(playerOne: Player, playerTwo: Player, status: MatchStatus, durationInSec: Long, score:TennisScore)



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
object TennisGame{
  implicit val format = Json.format[TennisGame]

  def start(playerOne: Player, playerTwo: Player) = {
    TennisGame(
      PlayerPoints(playerOne, 0),
      PlayerPoints(playerTwo, 0)
    )
  }
}
object UpdateScoreResponse{
  implicit val format = Json.format[UpdateScoreResponse]
}
object TennisSet{
  implicit val format = Json.format[TennisSet]
}
object TennisScore{
  implicit val format = Json.format[TennisScore]
}
object MatchDetailsResponse{
  implicit val format = Json.format[MatchDetailsResponse]
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


