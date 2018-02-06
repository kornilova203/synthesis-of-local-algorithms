package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.Neighbourhood
import java.io.File
import java.nio.file.Paths


abstract class IndependentSetDirectedGraph<out N : Neighbourhood>(n: Int,
                                                                  m: Int,
                                                                  val k: Int,
                                                                  edges: Set<N>) : DirectedGraph<N>(n, m, edges) {

    /**
     * Format:
     * <n> <m> <k>
     * <number of neighbourhoods>
     * for each neighbourhood:
     * <neighbourhood> (each id on separate line. See details in [Neighbourhood.outputString])
     * blank line
     */
    override fun export(dir: File) {
        val file = Paths.get(dir.toString(), "$n-$m-$k.graph").toFile()
        file.outputStream().use { outputStream ->
            outputStream.write("$n $m $k\n".toByteArray())
            outputStream.write("${neighbourhoods.size}\n".toByteArray())
            for (neighbourhood in neighbourhoods) {
                outputStream.write(("${neighbourhood.outputString()}\n\n").toByteArray())
            }
        }
    }

    abstract fun createGraphWithTiles(tilesFile: File): DirectedGraphWithTiles<N>
}