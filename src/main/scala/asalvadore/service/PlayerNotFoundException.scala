package asalvadore.service

import asalvadore.models.Player

/**
 * Created by asalvadore on 07/02/15.
 */
class PlayerNotFoundException (player: Player, matchId: String) extends RuntimeException(s"Cannot find the player $player in the match $matchId")
