package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.positions
import gnu.trove.set.hash.TIntHashSet
import java.io.File
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
                         val neighbourhoods: HashSet<Neighbourhood>) : TileGraph() {

    override val size: Int
        get() = calcUniqueIds(neighbourhoods)

    val edgeCount: Int
        get() = neighbourhoods.size * 4

    private fun calcUniqueIds(neighbourhoods: HashSet<Neighbourhood>): Int {
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
     * <id of center> <id of north> <id of east> <id of south> <id of west>
     */
    fun export(file: File) {
        file.outputStream().use { outputStream ->
            outputStream.write("$n $m $k\n".toByteArray())
            outputStream.write("${neighbourhoods.size}\n".toByteArray())
            for (neighbourhood in neighbourhoods) {
                outputStream.write("$neighbourhood\n".toByteArray())
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
            graphFile.inputStream().use { inputStream ->
                val scanner = Scanner(inputStream)
                val n = scanner.nextInt()
                val m = scanner.nextInt()
                val k = scanner.nextInt()
                val neighbourhoodsCount = scanner.nextInt()

                val neighbourhoods = HashSet<Neighbourhood>()
                for (i in 0 until neighbourhoodsCount) {
                    val centerId = scanner.nextInt()
                    val northId = scanner.nextInt()
                    val eastId = scanner.nextInt()
                    val southId = scanner.nextInt()
                    val westId = scanner.nextInt()
                    neighbourhoods.add(Neighbourhood(centerId, northId, eastId, southId, westId))
                }
                return DirectedGraph(n, m, k, neighbourhoods)
            }
        }
    }
}