package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleTileGraph
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap

/**
 * Solves colouring problem
 * Converts graph to dimacs format to solve n-colouring problem
 * Starts python script which starts SAT solver
 */
class ColouringProblem(graph: SimpleTileGraph, coloursCount: Int, k: Int) {
    /**
     * Value is null if there is no proper colouring
     */
    val colouringFunction: ColouringFunction? // it is public because it is value

    init {
        var tileColours: Map<BinaryTile, Int>? = null
        val dimacsFile = exportDimacs(graph, coloursCount, File("dimacs/"))
        val builder = ProcessBuilder("python",
                File("python_sat/sat/start_sat.py").toString(),
                dimacsFile!!.toPath().toAbsolutePath().toString())

        builder.redirectErrorStream(true)
        try {
            val process = builder.start()
            val scanner = Scanner(process.inputStream)
            process!!.waitFor()
            if (scanner.nextLine() == "OK") {
                tileColours = getResult(scanner, graph, coloursCount)
            } else {
                println("Something went wrong while running python script")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        dimacsFile.delete()
        colouringFunction = if (tileColours == null) null else ColouringFunction(tileColours, k)
    }

    private fun getResult(scanner: Scanner, graph: SimpleTileGraph, coloursCount: Int): Map<BinaryTile, Int>? {
        val resultColours = HashMap<BinaryTile, Int>()
        val possibleColours = HashMap<BinaryTile, BooleanArray>()
        for (tile in graph.graph.keys) {
            possibleColours[tile] = BooleanArray(4)
        }
        while (scanner.hasNextInt()) {
            val tileColourId = scanner.nextInt()
            if (tileColourId < 0) { // all colouringFunction are false by default
                continue
            }
            val tileId = getTileId(tileColourId, coloursCount)
            val colourId = getColourId(tileColourId, coloursCount)
            val tile = graph.getKey(tileId)
            possibleColours[tile]!![colourId] = true // this should not produce NPE. If it does then fix the code
        }
        for (tile in possibleColours.keys) {
            val colours = possibleColours[tile]
            val tileColour: Int? = colours!!.indices.firstOrNull {
                colours[it]
            }
            if (tileColour == null) {
                System.err.println("Cannot find colour for tile:\n" + tile)
                return null
            }
            resultColours[tile] = tileColour
        }
        return resultColours
    }

    /**
     * @return one if {0, 1, .., coloursCount - 1}
     */
    private fun getColourId(tileColourId: Int, coloursCount: Int): Int = (tileColourId - 1) % coloursCount

    private fun getTileId(tileColourId: Int, coloursCount: Int): Int = (tileColourId - 1) / coloursCount

    companion object {

        fun toDimacs(graph: SimpleTileGraph, coloursCount: Int): String {
            val stringBuilder = StringBuilder()
            val clausesCount = graph.size + graph.edgeCount * coloursCount
            stringBuilder.append("p cnf ").append(graph.size * coloursCount).append(" ").append(clausesCount).append("\n")

            val visitedEdges = HashMap<BinaryTile, HashSet<BinaryTile>>() // to count each edge only ones

            for (tile in graph.graph.keys) {
                addTileClause(stringBuilder, graph.getId(tile), coloursCount)
                val neighbours = graph.graph[tile]
                for (neighbour in neighbours!!) { // null value should never happen. If it throws NPE -> find mistake
                    if (visitedEdges.computeIfAbsent(neighbour) { HashSet() }.contains(tile)) { // if this edge was visited
                        continue
                    }
                    visitedEdges.computeIfAbsent(tile) { HashSet() }.add(neighbour)
                    addEdgeClauses(stringBuilder, graph.getId(tile), graph.getId(neighbour), coloursCount)
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
            return stringBuilder.toString()
        }

        fun exportDimacs(graph: SimpleTileGraph, coloursCount: Int, dir: File): File? {
            if (!dir.exists() || !dir.isDirectory) {
                throw IllegalArgumentException("File must be a directory and must exist")
            }
            val filePath = Paths.get(dir.toString(), getFileName(coloursCount))
            try {
                FileOutputStream(filePath.toFile())
                        .use { outputStream -> outputStream.write(toDimacs(graph, coloursCount).toByteArray()) }
                return filePath.toFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        private fun getFileName(coloursCount: Int): String =
                "dimacs_" + coloursCount + "-colouring_" + System.currentTimeMillis() + ".txt"

        private fun addEdgeClauses(stringBuilder: StringBuilder, vertex1Id: Int, vertex2Id: Int, coloursCount: Int) {
            for (i in 0 until coloursCount) {
                stringBuilder.append("-").append(getVarId(vertex1Id, coloursCount, i))
                        .append(" -").append(getVarId(vertex2Id, coloursCount, i)).append("\n")
            }
        }

        /**
         * Id of any variable must not be 0
         */
        private fun getVarId(vertexId: Int, coloursCount: Int, currentVar: Int): Int =
                vertexId * coloursCount + currentVar + 1

        private fun addTileClause(stringBuilder: StringBuilder, id: Int?, coloursCount: Int) {
            for (i in 0 until coloursCount) {
                stringBuilder.append(getVarId(id!!, coloursCount, i)).append(" ")
            }
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
            stringBuilder.append("\n")
        }
    }
}