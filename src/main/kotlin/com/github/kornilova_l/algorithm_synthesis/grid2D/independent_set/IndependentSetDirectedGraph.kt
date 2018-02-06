package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FIVE_POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Paths
import java.util.*


open class IndependentSetDirectedGraph(n: Int,
                                       m: Int,
                                       val k: Int,
                                       neighbourhoods: Set<Neighbourhood>) : DirectedGraph(n, m, neighbourhoods) {
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
    override fun export(dir: File) {
        val file = Paths.get(dir.toString(), "$n-$m-$k.graph").toFile()
        file.outputStream().use { outputStream ->
            outputStream.write("$n $m $k\n".toByteArray())
            outputStream.write("${neighbourhoods.size}\n".toByteArray())
            for (neighbourhood in neighbourhoods) {
                outputStream.write(("${neighbourhood.get(FIVE_POSITION.X)}\n${neighbourhood.get(FIVE_POSITION.N)}\n" +
                        "${neighbourhood.get(FIVE_POSITION.E)}\n${neighbourhood.get(FIVE_POSITION.S)}\n${neighbourhood.get(FIVE_POSITION.W)}\n\n").toByteArray())
            }
        }
    }

    constructor(graphFile: File) : this(
            parseNumber(graphFile, 0),
            parseNumber(graphFile, 1),
            parseNumber(graphFile, 2),
            parseNeighbours(graphFile)
    )

    companion object {

        private fun parseNeighbours(graphFile: File): Set<Neighbourhood> {
            BufferedReader(FileReader(graphFile)).use { reader ->
                reader.readLine() // skip n, m and k
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
                return neighbourhoods.toSet()
            }
        }

        fun parseNumber(graphFile: File, index: Int): Int {
            BufferedReader(FileReader(graphFile)).use { reader ->
                val firstLine = reader.readLine()
                val parts = firstLine.split(" ")
                return Integer.parseInt(parts[index])
            }
        }

    }
}