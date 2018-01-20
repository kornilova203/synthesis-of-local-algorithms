package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.positions
import gnu.trove.set.hash.TIntHashSet
import java.util.*

/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges.
 * This implementation contains only ids of tiles.
 * It should be used if you need to know only if solution of problem exists.
 */
open class DirectedGraph(override val n: Int,
                    override val m: Int,
                    override val k: Int,
                    val neighbourhoods: HashSet<Neighbourhood>) : TileGraph() {

    override val size: Int
        get() = calcUniqueIds(neighbourhoods)

    val edgeCount: Int
        get() = neighbourhoods.size * 4

    private fun calcUniqueIds(neighbourhoods: HashSet<Neighbourhood>): Int {
        val uniqueIds = TIntHashSet()
        for (neighbourhood in neighbourhoods) {
            for (position in positions) {
                uniqueIds.add(neighbourhood.get(position))
            }
        }
        return uniqueIds.size()
    }

    class Neighbourhood(private val northId: Int,
                        private val eastId: Int,
                        private val southId: Int,
                        private val westId: Int,
                        private val centerId: Int) {

        fun get(position: POSITION): Int {
            return when (position) {
                POSITION.X -> centerId
                POSITION.N -> northId
                POSITION.E -> eastId
                POSITION.S -> southId
                POSITION.W -> westId
            }
        }

        override fun hashCode(): Int {
            return Objects.hash(northId, eastId, southId, westId, centerId)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Neighbourhood

            return other.northId == northId &&
                    other.eastId == eastId &&
                    other.southId == southId &&
                    other.westId == westId
        }

        override fun toString(): String = "$northId $eastId $southId $westId $centerId"
    }
}