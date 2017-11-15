package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Part
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.util.*

/**
 * Constructs graph of tiles.
 * This implementation does not save orientation of edges
 */
class SimpleTileGraph(tileSet1: TileSet, tileSet2: TileSet) : TileGraph(tileSet1, tileSet2) {
    val graph = HashMap<Tile, HashSet<Tile>>()
    private val ids = DualHashBidiMap<Tile, Int>()

    override val edgeCount: Int
        get() {
            val res = graph.values.sumBy { it.size }
            assert(res % 2 == 0)

            return res / 2
        }

    override val size: Int
        get() = graph.size

    init {
        for (tile in this.tileSet1.validTiles) { // get vertical neighbours
            val top = Tile(tile, Part.N)
            val bottom = Tile(tile, Part.S)
            graph.computeIfAbsent(top) { HashSet() }.add(bottom)
            graph.computeIfAbsent(bottom) { HashSet() }.add(top)
        }
        for (tile in this.tileSet2.validTiles) { // get horizontal neighbours
            val left = Tile(tile, Part.W)
            val right = Tile(tile, Part.E)
            graph.computeIfAbsent(left) { HashSet() }.add(right)
            graph.computeIfAbsent(right) { HashSet() }.add(left)
        }
        if (graph.size == 0) {
            throw IllegalArgumentException("Cannot construct graph")
        }

        assignIds()
    }

    private fun assignIds() {
        for ((i, tile) in graph.keys.withIndex()) {
            ids.put(tile, i)
        }
    }

    fun getId(tile: Tile): Int = ids[tile]!!

    fun getKey(tileId: Int): Tile = ids.getKey(tileId)
}
