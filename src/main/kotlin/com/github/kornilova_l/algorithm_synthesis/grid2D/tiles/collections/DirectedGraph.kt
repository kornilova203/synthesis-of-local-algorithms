package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
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
    private var nextTileId = 1

    val graph: HashMap<Tile, HashSet<Neighbourhood>> = HashMap()
    private val ids = DualHashBidiMap<Tile, Int>()
    override val size: Int
        get() = graph.size

    val edgeCount: Int
        get() = graph.values.sumBy { it.size * 4 }

    init {
        if (n <= 0 || m <= 0) {
            throw IllegalArgumentException("Each dimension of tiles in set must be at least 3")
        }
        /* There must exist at most one instance of each tile */
        for (tile in tileSet.validTiles) {
            val center = getIfAlreadyCreated(Tile(tile, POSITION.X))
            val set = graph.computeIfAbsent(center, { HashSet() })
            set.add(Neighbourhood(
                    getIfAlreadyCreated(Tile(tile, POSITION.N)),
                    getIfAlreadyCreated(Tile(tile, POSITION.E)),
                    getIfAlreadyCreated(Tile(tile, POSITION.S)),
                    getIfAlreadyCreated(Tile(tile, POSITION.W)),
                    center
            ))
        }
        if (graph.size == 0) {
            throw IllegalArgumentException("Cannot construct graph")
        }
    }

    /**
     * This method also assigns ids
     * so it is not needed to traverse graph one more time
     * which might be time-expensive
     */
    private fun getIfAlreadyCreated(tile: Tile): Tile {
        val maybeId = ids[tile]
        return if (maybeId != null) {
            assert(maybeId > 0)
            ids.getKey(maybeId)
        } else {
            tile.id = nextTileId
            ids[tile] = nextTileId
            nextTileId++
            tile
        }
    }

    fun getTile(id: Int): Tile? = ids.getKey(id)

    class Neighbourhood(north: Tile, east: Tile, south: Tile, west: Tile, center: Tile) {
        val neighbours = HashMap<POSITION, Tile>()

        init {
            neighbours[POSITION.N] = north
            neighbours[POSITION.E] = east
            neighbours[POSITION.W] = west
            neighbours[POSITION.S] = south
            neighbours[POSITION.X] = center
        }

        override fun hashCode(): Int {
            return Objects.hash(
                    neighbours[POSITION.N],
                    neighbours[POSITION.E],
                    neighbours[POSITION.S],
                    neighbours[POSITION.W]
            )
        }

        override fun toString(): String {
            return StringBuilder()
                    .append(neighbours[POSITION.N]).append("\n")
                    .append(neighbours[POSITION.E]).append("\n")
                    .append(neighbours[POSITION.S]).append("\n")
                    .append(neighbours[POSITION.W]).append("\n")
                    .toString()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Neighbourhood

            return other.neighbours[POSITION.N]!! == neighbours[POSITION.N] &&
                    other.neighbours[POSITION.E]!! == neighbours[POSITION.E] &&
                    other.neighbours[POSITION.S]!! == neighbours[POSITION.S] &&
                    other.neighbours[POSITION.W]!! == neighbours[POSITION.W]
        }
    }
}