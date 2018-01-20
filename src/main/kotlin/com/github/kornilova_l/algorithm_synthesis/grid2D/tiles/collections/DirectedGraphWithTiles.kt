package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import org.apache.commons.collections4.bidimap.DualHashBidiMap


/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges.
 * Also it contains actual tiles ([DirectedGraph] contains only ids)
 * This class should be used when
 * [com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.LabelingFunction] must be created
 */
class DirectedGraphWithTiles(n: Int,
                             m: Int,
                             k: Int,
                             neighbourhoods: HashSet<Neighbourhood>,
                             private val ids: DualHashBidiMap<Tile, Int>) : DirectedGraph(n, m, k, neighbourhoods) {
    override val size: Int
        get() = ids.size

    fun getId(tile: Tile): Int = ids[tile]!!

    fun getTile(id: Int): Tile? = ids.getKey(id)

    companion object {
        fun createInstance(tileSet: TileSet): DirectedGraphWithTiles {
            val n = tileSet.n - 2
            val m = tileSet.m - 2
            val k = tileSet.k
            if (n <= 0 || m <= 0) {
                throw IllegalArgumentException("Each dimension of tiles in set must be at least 3")
            }
            val ids = DualHashBidiMap<Tile, Int>()
            val neighbourhoods = HashSet<Neighbourhood>()
            /* There must exist at most one instance of each tile */
            for (tile in tileSet.validTiles) {
                neighbourhoods.add(
                        Neighbourhood(
                                getId(Tile(tile, POSITION.X), ids),
                                getId(Tile(tile, POSITION.N), ids),
                                getId(Tile(tile, POSITION.E), ids),
                                getId(Tile(tile, POSITION.S), ids),
                                getId(Tile(tile, POSITION.W), ids)
                        ))
            }
            if (neighbourhoods.size == 0) {
                throw IllegalArgumentException("Cannot construct graph")
            }
            return DirectedGraphWithTiles(n, m, k, neighbourhoods, ids)
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
    }
}