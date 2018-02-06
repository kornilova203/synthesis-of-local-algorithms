package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph.Companion.parseNumber
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


/**
 * Constructs graph of tiles.
 * This implementation does not save orientation of edges
 * and does not save actual tiles
 */
open class SimpleGraph(override val n: Int,
                       override val m: Int,
                       val graph: Map<Int, Set<Int>>) : TileGraph() {

    constructor(graphFile: File) : this(
            parseNumber(graphFile, 0),
            parseNumber(graphFile, 1),
            parseNeighbours(graphFile)
    )

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
                outputStream.write(centerId.toString().toByteArray())
                outputStream.write("\n".toByteArray())
                for (neighbourId in graph[centerId]!!) {
                    outputStream.write(neighbourId.toString().toByteArray())
                    outputStream.write("\n".toByteArray())
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

    companion object {
        private fun parseNeighbours(graphFile: File): Map<Int, Set<Int>> {
            BufferedReader(FileReader(graphFile)).use { reader ->
                reader.readLine() // skip n and m
                val neighbourhoodsCount = Integer.parseInt(reader.readLine())
                val neighbourhoods = HashMap<Int, Set<Int>>(neighbourhoodsCount)
                for (i in 0 until neighbourhoodsCount) {
                    val centerId = Integer.parseInt(reader.readLine())
                    val neighbours = HashSet<Int>()
                    var line = reader.readLine()
                    while (!line.isEmpty()) {
                        neighbours.add(Integer.parseInt(line))
                        line = reader.readLine()
                    }
                    neighbourhoods[centerId] = neighbours
                }
                return neighbourhoods
            }
        }
    }
}
