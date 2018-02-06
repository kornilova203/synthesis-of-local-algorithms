package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FIVE_POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem.FOUR_POSITION
import java.util.*

abstract class Neighbourhood {
    /**
     * Returns string that is written to file
     * Each id must be places on separate line
     */
    abstract fun outputString(): String

    abstract fun getValues(): Set<Int>
}

class FiveNeighbourhood(private val centerId: Int,
                        private val northId: Int,
                        private val eastId: Int,
                        private val southId: Int,
                        private val westId: Int) : Neighbourhood() {

    fun get(position: FIVE_POSITION): Int {
        return when (position) {
            FIVE_POSITION.X -> centerId
            FIVE_POSITION.N -> northId
            FIVE_POSITION.E -> eastId
            FIVE_POSITION.S -> southId
            FIVE_POSITION.W -> westId
        }
    }

    override fun getValues(): Set<Int> = setOf(centerId, northId, eastId, southId, westId)

    override fun hashCode(): Int {
        return Objects.hash(centerId, northId, eastId, southId, westId)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FiveNeighbourhood

        return other.centerId == centerId &&
                other.northId == northId &&
                other.eastId == eastId &&
                other.southId == southId &&
                other.westId == westId
    }

    override fun toString(): String = "$centerId $northId $eastId $southId $westId"

    override fun outputString(): String = "$centerId\n$northId\n$eastId\n$southId\n$westId"
}

class FourNeighbourhood(private val topLeft: Int,
                        private val topRight: Int,
                        private val bottomRight: Int,
                        private val bottomLeft: Int) : Neighbourhood() {

    fun get(position: FOUR_POSITION): Int {
        return when (position) {
            FOUR_POSITION.TL -> topLeft
            FOUR_POSITION.TR -> topRight
            FOUR_POSITION.BR -> bottomRight
            FOUR_POSITION.BL -> bottomLeft
        }
    }

    override fun getValues(): Set<Int> = setOf(topLeft, topRight, bottomRight, bottomLeft)

    override fun hashCode(): Int {
        return Objects.hash(topLeft, topRight, bottomRight, bottomLeft)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FourNeighbourhood

        return other.topLeft == topLeft &&
                other.topRight == topRight &&
                other.bottomRight == bottomRight &&
                other.bottomLeft == bottomLeft
    }

    override fun toString(): String = "$topLeft $topRight $bottomRight $bottomLeft"

    override fun outputString(): String = "$topLeft\n$topRight\n$bottomRight\n$bottomLeft"
}