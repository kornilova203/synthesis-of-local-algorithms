package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.graphs

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.DirectedGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.FourNeighbourhood
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

/**
 * @param neighbourhoods each neighbourhood contains possible combination of neighbours.
 *                       Graph cannot be stored as node with sets of nodes on the north, south, ...
 *                       because it is important to know what exact combinations of neighbours are possible
 */
class FourNeighboursDirectedGraph(n: Int, m: Int, k: Int,
                                  neighbourhoods: Set<FourNeighbourhood>) : IndependentSetDirectedGraph<FourNeighbourhood>(n, m, k, neighbourhoods) {

    override val edgeCount: Int
        get() = neighbourhoods.size * 4

    constructor(graphFile: File) : this(
            parseNumber(graphFile, 0),
            parseNumber(graphFile, 1),
            parseNumber(graphFile, 2),
            parseNeighbours(graphFile)
    )

    override fun createGraphWithTiles(tilesFile: File): DirectedGraphWithTiles<FourNeighbourhood> =
            FourNeighboursGraphWithTiles.createInstance(tilesFile, this)

    companion object {
        private fun parseNeighbours(graphFile: File): Set<FourNeighbourhood> {
            BufferedReader(FileReader(graphFile)).use { reader ->
                reader.readLine() // skip n, m and k
                val neighbourhoodsCount = Integer.parseInt(reader.readLine())
                val neighbourhoods = ArrayList<FourNeighbourhood>(neighbourhoodsCount)
                for (i in 0 until neighbourhoodsCount) {
                    val topLeft = Integer.parseInt(reader.readLine())
                    val topRight = Integer.parseInt(reader.readLine())
                    val bottomRight = Integer.parseInt(reader.readLine())
                    val bottomLeft = Integer.parseInt(reader.readLine())
                    reader.readLine()
                    neighbourhoods.add(FourNeighbourhood(topLeft, topRight, bottomRight, bottomLeft))
                }
                return neighbourhoods.toSet()
            }
        }
    }
}