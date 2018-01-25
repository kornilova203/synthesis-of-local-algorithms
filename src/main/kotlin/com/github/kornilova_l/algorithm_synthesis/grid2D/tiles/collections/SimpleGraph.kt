package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import java.io.File


/**
 * Constructs graph of tiles.
 * This implementation does not save orientation of edges
 * and does not save actual tiles
 */
open class SimpleGraph(override val n: Int,
                       override val m: Int,
                       val graph: Map<Int, Set<Int>>) : TileGraph() {

    /**
     * Format:
     * <n> <m>
     * <number of neighbourhoods>
     * for each neighbourhood:
     * <id of center>
     * for each neighbour:
     * <id of neighbour>
     * blank line
     */
    fun export(file: File) {
        file.outputStream().use { outputStream ->
            outputStream.write("$n $m\n".toByteArray())
            outputStream.write("${graph.size}\n".toByteArray())
            for (centerId in graph.keys) {
                outputStream.write("$centerId\n".toByteArray())
                for (neighbourId in graph[centerId]!!) {
                    outputStream.write("$neighbourId\n".toByteArray())
                }
                outputStream.write("\n".toByteArray())
            }
        }
    }

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
