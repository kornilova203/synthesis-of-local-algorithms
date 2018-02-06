package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FIVE_POSITION
import gnu.trove.set.hash.TIntHashSet
import java.io.File
import java.util.*

/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges.
 * This implementation contains only ids of tiles.
 * It should be used if you need to know only if solution of problem exists.
 */
abstract class DirectedGraph(override val n: Int,
                         override val m: Int,
                         val neighbourhoods: Set<Neighbourhood>) : TileGraph() {
    private var cachedSize = -1

    override val size: Int
        get() {
            if (cachedSize == -1) {
                cachedSize = calcUniqueIds(neighbourhoods)
            }
            return cachedSize
        }

    val edgeCount: Int
        get() = neighbourhoods.size * 4

    private fun calcUniqueIds(neighbourhoods: Set<Neighbourhood>): Int {
        val uniqueIds = TIntHashSet()
        for (neighbourhood in neighbourhoods) {
            for (position in FIVE_POSITION.values()) {
                uniqueIds.add(neighbourhood.get(position))
            }
        }
        return uniqueIds.size()
    }

    /**
     * Format:
     * <n> <m>
     * <number of neighbourhoods>
     * for each neighbourhood:
     * <id of center>
     * <id of north>
     * <id of east>
     * <id of south>
     * <id of west>
     * blank line
     */
    abstract fun export(dir: File)

    class Neighbourhood(private val centerId: Int,
                        private val northId: Int,
                        private val eastId: Int,
                        private val southId: Int,
                        private val westId: Int) {

        fun get(position: FIVE_POSITION): Int {
            return when (position) {
                FIVE_POSITION.X -> centerId
                FIVE_POSITION.N -> northId
                FIVE_POSITION.E -> eastId
                FIVE_POSITION.S -> southId
                FIVE_POSITION.W -> westId
            }
        }

        override fun hashCode(): Int {
            return Objects.hash(centerId, northId, eastId, southId, westId)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Neighbourhood

            return other.centerId == centerId &&
                    other.northId == northId &&
                    other.eastId == eastId &&
                    other.southId == southId &&
                    other.westId == westId
        }

        override fun toString(): String = "$centerId $northId $eastId $southId $westId"
    }
}