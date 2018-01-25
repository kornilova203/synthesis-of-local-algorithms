package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

/**
 * Constructs graph of tiles.
 * This implementation does not save orientation of edges
 * and does not save actual tiles
 */
open class SimpleGraph(override val n: Int,
                       override val m: Int,
                       val graph: Map<Int, Set<Int>>) : TileGraph() {

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
}
