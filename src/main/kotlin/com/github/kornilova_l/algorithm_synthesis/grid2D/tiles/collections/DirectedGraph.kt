package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.positions
import gnu.trove.set.hash.TIntHashSet
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.util.*
import kotlin.collections.HashSet

/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges
 */
class DirectedGraph(tileSet: TileSet) : TileGraph() {
    override val n: Int = tileSet.n - 2
    override val m: Int = tileSet.m - 2
    override val k: Int = tileSet.k

    val neighbourhoods: HashSet<Neighbourhood> = HashSet()
    /**
     * Optional field
     * To check if solution exists we do not need actual tiles
     * We need only [neighbourhoods] field for this.
     * [ids] field are calculated if labeling function is required
     */
    private val ids: DualHashBidiMap<Tile, Int>?
    override val size: Int
        get() = ids?.size ?: calcUniqueIds(neighbourhoods) // if ids is not null -- return ids.size

    val edgeCount: Int
        get() = neighbourhoods.size * 4

    init {
        if (n <= 0 || m <= 0) {
            throw IllegalArgumentException("Each dimension of tiles in set must be at least 3")
        }
        ids = DualHashBidiMap()
        /* There must exist at most one instance of each tile */
        for (tile in tileSet.validTiles) {
            neighbourhoods.add(
                    Neighbourhood(
                            getId(Tile(tile, POSITION.N), ids),
                            getId(Tile(tile, POSITION.E), ids),
                            getId(Tile(tile, POSITION.S), ids),
                            getId(Tile(tile, POSITION.W), ids),
                            getId(Tile(tile, POSITION.X), ids)
                    ))
        }
        if (neighbourhoods.size == 0) {
            throw IllegalArgumentException("Cannot construct graph")
        }
    }

    private fun calcUniqueIds(neighbourhoods: HashSet<Neighbourhood>): Int {
        val uniqueIds = TIntHashSet()
        for (neighbourhood in neighbourhoods) {
            for (position in positions) {
                uniqueIds.add(neighbourhood.get(position))
            }
        }
        return uniqueIds.size()
    }

    /**
     * Checks if this tile already exists.
     * If so it returns existing id
     * otherwise it adds tile to set and returns new id
     */
    private fun getId(tile: Tile, ids: DualHashBidiMap<Tile, Int>): Int {
        val maybeId = ids[tile]
        return if (maybeId != null) {
            assert(maybeId > 0)
            maybeId
        } else {
            val id = ids.size + 1 // ids start with 1
            ids[tile] = id
            id
        }
    }

    fun getId(tile: Tile): Int = ids!![tile]!!

    fun getTile(id: Int): Tile? = ids!!.getKey(id)

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