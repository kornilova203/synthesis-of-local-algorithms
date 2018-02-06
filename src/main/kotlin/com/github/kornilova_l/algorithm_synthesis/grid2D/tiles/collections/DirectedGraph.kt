package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import gnu.trove.set.hash.TIntHashSet
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges.
 * This implementation contains only ids of tiles.
 * It should be used if you need to know only if solution of problem exists.
 */
abstract class DirectedGraph<out N : Neighbourhood>(override val n: Int,
                                                    override val m: Int,
                                                    val neighbourhoods: Set<N>) : TileGraph() {
    private var cachedSize = -1

    abstract val edgeCount: Int

    override val size: Int
        get() {
            if (cachedSize == -1) {
                cachedSize = calcUniqueIds(neighbourhoods)
            }
            return cachedSize
        }

    protected fun calcUniqueIds(neighbourhoods: Set<Neighbourhood>): Int {
        val uniqueIds = TIntHashSet()
        for (neighbourhood in neighbourhoods) {
            uniqueIds.addAll(neighbourhood.getValues())
        }
        return uniqueIds.size()
    }

    /**
     * Format:
     * <n> <m>
     * <number of neighbourhoods>
     * for each neighbourhood:
     * <id of center>
     * <id of north>
     * <id of east>
     * <id of south>
     * <id of west>
     * blank line
     */
    abstract fun export(dir: File)

    companion object {
        fun parseNumber(graphFile: File, index: Int): Int {
            BufferedReader(FileReader(graphFile)).use { reader ->
                val firstLine = reader.readLine()
                val parts = firstLine.split(" ")
                return Integer.parseInt(parts[index])
            }
        }
    }
}