package asalvadore.service

/**
 * Created by asalvadore on 07/02/15.
 */
trait GameConstants {
  val NUMBER_OF_SETS = 3
  // min number of matches to win a set
  val MIN_WIN_IN_SET = 5
  // min number of sets to win a game
  val MIN_WIN_IN_GAME = 2

  val MIN_SCORE_DIFF_IN_SET = 2
  val MIN_SCORE_DIFF_IN_GAME = 1

  // min number of times a player has to score to win a game (used in testing)
  val MIN_SCORES_PER_MATCH = 4


}
