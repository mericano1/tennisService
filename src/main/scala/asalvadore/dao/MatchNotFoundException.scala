package asalvadore.dao

/**
 * Created by asalvadore on 07/02/15.
 */
class MatchNotFoundException(id: String) extends RuntimeException("Could not find match " + id)
