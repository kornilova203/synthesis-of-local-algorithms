package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.positions
import gnu.trove.set.hash.TIntHashSet
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges.
 * This implementation contains only ids of tiles.
 * It should be used if you need to know only if solution of problem exists.
 */
open class DirectedGraph(override val n: Int,
                         override val m: Int,
                         override val k: Int,
                         val neighbourhoods: Set<Neighbourhood>) : TileGraph() {
    private var cachedSize = -1

    override val size: Int
        get() {
            if (cachedSize == -1) {
                cachedSize = calcUniqueIds(neighbourhoods)
            }
            return cachedSize
        }

    val edgeCount: Int
        get() = neighbourhoods.size * 4

    private fun calcUniqueIds(neighbourhoods: Set<Neighbourhood>): Int {
        val uniqueIds = TIntHashSet()
        for (neighbourhood in neighbourhoods) {
            for (position in positions) {
                uniqueIds.add(neighbourhood.get(position))
            }
        }
        return uniqueIds.size()
    }

    /**
     * Format:
     * <n> <m> <k>
     * <number of neighbourhoods>
     * for each neighbourhood:
     * <id of center>
     * <id of north>
     * <id of east>
     * <id of south>
     * <id of west>
     * blank line
     */
    fun export(file: File) {
        file.outputStream().use { outputStream ->
            outputStream.write("$n $m $k\n".toByteArray())
            outputStream.write("${neighbourhoods.size}\n".toByteArray())
            for (neighbourhood in neighbourhoods) {
                outputStream.write(("${neighbourhood.get(POSITION.X)}\n${neighbourhood.get(POSITION.N)}\n" +
                        "${neighbourhood.get(POSITION.E)}\n${neighbourhood.get(POSITION.S)}\n${neighbourhood.get(POSITION.W)}\n\n").toByteArray())
            }
        }
    }

    class Neighbourhood(private val centerId: Int,
                        private val northId: Int,
                        private val eastId: Int,
                        private val southId: Int,
                        private val westId: Int) {

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
            return Objects.hash(centerId, northId, eastId, southId, westId)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Neighbourhood

            return other.centerId == centerId &&
                    other.northId == northId &&
                    other.eastId == eastId &&
                    other.southId == southId &&
                    other.westId == westId
        }

        override fun toString(): String = "$centerId $northId $eastId $southId $westId"
    }

    companion object {
        fun createInstance(graphFile: File): DirectedGraph {
            BufferedReader(FileReader(graphFile)).use { reader ->
                val firstLine = reader.readLine()
                val parts = firstLine.split(" ")
                val n = Integer.parseInt(parts[0])
                val m = Integer.parseInt(parts[1])
                val k = Integer.parseInt(parts[2])
                val neighbourhoodsCount = Integer.parseInt(reader.readLine())
                val neighbourhoods = ArrayList<Neighbourhood>(neighbourhoodsCount)
                for (i in 0 until neighbourhoodsCount) {
                    val centerId = Integer.parseInt(reader.readLine())
                    val northId = Integer.parseInt(reader.readLine())
                    val eastId = Integer.parseInt(reader.readLine())
                    val southId = Integer.parseInt(reader.readLine())
                    val westId = Integer.parseInt(reader.readLine())
                    reader.readLine()
                    neighbourhoods.add(Neighbourhood(centerId, northId, eastId, southId, westId))
                }
                return DirectedGraph(n, m, k, neighbourhoods.toSet())
            }
        }
    }
}