package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Part.*
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.util.*
import kotlin.collections.HashMap


/**
 * Constructs graph of tiles.
 * This implementation does not save orientation of edges
 * @param ids ids of tiles start with 0
 */
open class SimpleGraphWithTiles(n: Int, m: Int, graph: Map<Int, Set<Int>>, protected val ids: DualHashBidiMap<Tile, Int>) :
        SimpleGraph(n, m, graph) {

    companion object {

        fun createInstance(tileSet1: Set<BinaryTile>, tileSet2: Set<BinaryTile>): SimpleGraphWithTiles {
            val n = getN(tileSet1, tileSet2)
            val m = getM(tileSet1, tileSet2)
            var set1 = tileSet1
            var set2 = tileSet2
            validateTileSets(tileSet1, tileSet2)
            val n1 = tileSet1.first().n
            val n2 = tileSet2.first().n
            if (n1 < n2) {
                val temp = set1
                set1 = set2
                set2 = temp
            }

            assert(set1.first().n == set2.first().n + 1)
            assert(set1.first().m + 1 == set2.first().m)

            val graph = HashMap<Int, MutableSet<Int>>()
            val ids = DualHashBidiMap<Tile, Int>()

            for (tile in set1) { // get vertical neighbours
                val top = tile.clonePart(N)
                val bottom = tile.clonePart(S)
                val topId = getId(top, ids)
                val bottomId = getId(bottom, ids)
                graph.computeIfAbsent(topId) { HashSet() }.add(bottomId)
                graph.computeIfAbsent(bottomId) { HashSet() }.add(topId)
            }
            for (tile in set2) { // get horizontal neighbours
                val left = tile.clonePart(W)
                val right = tile.clonePart(E)
                val leftId = getId(left, ids)
                val rightId = getId(right, ids)
                graph.computeIfAbsent(leftId) { HashSet() }.add(rightId)
                graph.computeIfAbsent(rightId) { HashSet() }.add(leftId)
            }
            if (graph.size == 0) {
                throw IllegalArgumentException("Cannot construct graph")
            }
            return SimpleGraphWithTiles(n, m, graph, ids)
        }

        private fun getN(tileSet1: Set<BinaryTile>, tileSet2: Set<BinaryTile>): Int {
            val n1 = tileSet1.first().n
            val n2 = tileSet2.first().n
            return if (n1 > n2) n2 else n1
        }

        private fun getM(tileSet1: Set<BinaryTile>, tileSet2: Set<BinaryTile>): Int {
            val n1 = tileSet1.first().n
            val m1 = tileSet1.first().m
            val n2 = tileSet2.first().n
            val m2 = tileSet2.first().m
            return if (n1 > n2) m1 else m2
        }

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

        /**
         * Checks if this tile already exists.
         * If so it returns existing id
         * otherwise it adds tile to set and returns new id
         */
        fun getId(tile: Tile, ids: MutableMap<Tile, Int>): Int {
            synchronized(this) {
                return ids.computeIfAbsent(tile, { ids.size })
            }
        }
    }

    fun getTile(tileId: Int): Tile {
        return ids.getKey(tileId)!!
    }
}
