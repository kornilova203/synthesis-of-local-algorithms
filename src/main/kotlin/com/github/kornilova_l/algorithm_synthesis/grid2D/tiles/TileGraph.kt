package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Part
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.util.*

class TileGraph(tileSet1: TileSet, tileSet2: TileSet) {
    val graph = HashMap<Tile, HashSet<Tile>>()
    private val ids = DualHashBidiMap<Tile, Int>()
    val n: Int
    val m: Int
    val k: Int

    val edgeCount: Int
        get() {
            val res = graph.values.sumBy { it.size }
            assert(res % 2 == 0)

            return res / 2
        }

    val size: Int
        get() = graph.size

    init {
        validateTileSets(tileSet1, tileSet2)
        val n1 = tileSet1.n
        val m1 = tileSet1.m
        val n2 = tileSet2.n
        val m2 = tileSet2.m
        if (n1 > n2) {
            n = n2
            m = m1
        } else {
            n = n1
            m = m2
        }

        k = tileSet1.k

        for (tile in tileSet1.possiblyValidTiles) { // get vertical neighbours
            val top = Tile(tile, Part.TOP)
            val bottom = Tile(tile, Part.BOTTOM)
            graph.computeIfAbsent(top) { HashSet() }.add(bottom)
            graph.computeIfAbsent(bottom) { HashSet() }.add(top)
        }
        for (tile in tileSet2.possiblyValidTiles) { // get horizontal neighbours
            val left = Tile(tile, Part.LEFT)
            val right = Tile(tile, Part.RIGHT)
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

    fun getId(tile: Tile): Int {
        return ids[tile]!!
    }

    private fun validateTileSets(tileSet1: TileSet, tileSet2: TileSet) {
        if (tileSet1.k != tileSet2.k) {
            throw IllegalArgumentException("Graph power is different in two sets")
        }
        if (tileSet1.isEmpty || tileSet2.isEmpty) {
            throw IllegalArgumentException("At least one set is empty")
        }

        val n1 = tileSet1.n
        val m1 = tileSet1.m
        val n2 = tileSet2.n
        val m2 = tileSet2.m
        if (n1 > n2) {
            if (n1 != n2 + 1 || m2 != m1 + 1) {
                throw IllegalArgumentException("If size of kern tile is n*m then size of two tiles set must be n+1*m and n*m+1 (order does not matter)")
            }
        } else {
            if (n1 + 1 != n2 || m2 + 1 != m1) {
                throw IllegalArgumentException("If size of kern tile is n*m then size of two tiles set must be n+1*m and n*m+1 (order does not matter)")
            }
        }
    }

    fun getKey(tileId: Int): Tile {
        return ids.getKey(tileId)
    }
}
