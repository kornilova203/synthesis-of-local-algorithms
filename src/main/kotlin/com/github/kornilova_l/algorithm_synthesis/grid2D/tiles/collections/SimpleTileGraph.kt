package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Part.*
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.util.*

/**
 * Constructs graph of tiles.
 * This implementation does not save orientation of edges
 */
class SimpleTileGraph(tileSet1: Set<BinaryTile>, tileSet2: Set<BinaryTile>) : TileGraph() {
    override val n: Int
    override val m: Int
    val graph = HashMap<BinaryTile, HashSet<BinaryTile>>()
    private val ids = DualHashBidiMap<BinaryTile, Int>()
    private val tileSet1: Set<BinaryTile>
    private val tileSet2: Set<BinaryTile>


    val edgeCount: Int
        get() {
            var res = 0
            for (value in graph.values) {
                res += value.size
            }
            assert(res % 2 == 0)

            return res / 2
        }

    override val size: Int
        get() = graph.size

    init {
        validateTileSets(tileSet1, tileSet2)
        val n1 = tileSet1.first().n
        val m1 = tileSet1.first().m
        val n2 = tileSet2.first().n
        val m2 = tileSet2.first().m
        if (n1 > n2) {
            n = n2
            m = m1
            this.tileSet1 = tileSet1
            this.tileSet2 = tileSet2
        } else {
            n = n1
            m = m2
            this.tileSet1 = tileSet2
            this.tileSet2 = tileSet1
        }

        for (tile in tileSet1) { // get vertical neighbours
            val top = tile.clonePart(N)
            val bottom = tile.clonePart(S)
            graph.computeIfAbsent(top) { HashSet() }.add(bottom)
            graph.computeIfAbsent(bottom) { HashSet() }.add(top)
        }
        for (tile in this.tileSet2) { // get horizontal neighbours
            val left = tile.clonePart(W)
            val right = tile.clonePart(E)
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
            ids[tile] = i
        }
    }

    fun getId(tile: BinaryTile): Int = ids[tile]!!

    fun getKey(tileId: Int): BinaryTile = ids.getKey(tileId)

    private fun validateTileSets(tileSet1: Set<BinaryTile>, tileSet2: Set<BinaryTile>) {
        if (tileSet1.isEmpty() || tileSet2.isEmpty()) {
            throw IllegalArgumentException("At least one set is empty")
        }

        val n1 = tileSet1.first().n
        val m1 = tileSet1.first().m
        val n2 = tileSet2.first().n
        val m2 = tileSet2.first().m
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
}
