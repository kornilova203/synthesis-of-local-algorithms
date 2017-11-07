package com.github.kornilova_l.formal_da.implementation.grid.tiles

import com.github.kornilova_l.formal_da.implementation.grid.tiles.TileGraphBuilder.countEdges
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
class ColouringProblem(graph: Map<Tile, HashSet<Tile>>, coloursCount: Int) {
    /**
     * Value is null if there is no proper colouring
     */
    private val colours: Map<Tile, Int>?

    init {
        var tempColours: Map<Tile, Int>? = null
        val ids = assignIds(graph)
        val dimacsFile = exportDimacs(graph, ids, coloursCount, File("dimacs/"))
        val builder = ProcessBuilder("python",
                File("python_sat/sat/start_sat.py").toString(),
                dimacsFile!!.toPath().toAbsolutePath().toString())

        builder.redirectErrorStream(true)
        try {
            val process = builder.start()
            val scanner = Scanner(process.inputStream)
            process!!.waitFor()
            if (scanner.nextLine() == "OK") {
                tempColours = getResult(scanner)
            } else {
                println("Something went wrong while running python script")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        dimacsFile.delete()
        colours = tempColours
    }

    private fun getResult(scanner: Scanner): Map<Tile, Int> {
        while (scanner.hasNextInt()) {
            val tileColour = scanner.nextInt()
            println(tileColour)
        }
        return HashMap()
    }

    companion object {

        fun toDimacs(graph: Map<Tile, HashSet<Tile>>, ids: Map<Tile, Int>, coloursCount: Int): String {
            val stringBuilder = StringBuilder()
            val clausesCount = graph.size + countEdges(graph) * coloursCount
            stringBuilder.append("p cnf ").append(graph.size * coloursCount).append(" ").append(clausesCount).append("\n")

            val visitedEdges = HashMap<Tile, HashSet<Tile>>() // to count each edge only ones

            for (tile in graph.keys) {
                addTileClause(stringBuilder, ids[tile], coloursCount)
                val neighbours = graph[tile]
                for (neighbour in neighbours!!) { // null value should never happen. If it throws NPE -> find mistake
                    if (visitedEdges.computeIfAbsent(neighbour) { HashSet() }.contains(tile)) { // if this edge was visited
                        continue
                    }
                    visitedEdges.computeIfAbsent(tile) { HashSet() }.add(neighbour)
                    addEdgeClauses(stringBuilder, ids[tile]!!, ids[neighbour]!!, coloursCount) // ids also must be not null
                }
            }
            return stringBuilder.toString()
        }

        fun exportDimacs(graph: Map<Tile, HashSet<Tile>>, ids: Map<Tile, Int>, coloursCount: Int, dir: File): File? {
            if (!dir.exists() || !dir.isDirectory) {
                throw IllegalArgumentException("File must be a directory and must exist")
            }
            val filePath = Paths.get(dir.toString(), getFileName(coloursCount))
            try {
                FileOutputStream(filePath.toFile())
                        .use { outputStream -> outputStream.write(toDimacs(graph, ids, coloursCount).toByteArray()) }
                return filePath.toFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        private fun getFileName(coloursCount: Int): String {
            return "dimacs_" + coloursCount + "-colouring_" + System.currentTimeMillis() + ".txt"
        }

        private fun addEdgeClauses(stringBuilder: StringBuilder, vertex1Id: Int, vertex2Id: Int, coloursCount: Int) {
            for (i in 0 until coloursCount) {
                stringBuilder.append("-").append(getVarId(vertex1Id, coloursCount, i))
                        .append(" -").append(getVarId(vertex2Id, coloursCount, i)).append("\n")
            }
        }

        /**
         * Id of any variable must not be 0
         */
        private fun getVarId(vertexId: Int, coloursCount: Int, currentVar: Int): Int {
            return vertexId * coloursCount + currentVar + 1
        }

        private fun addTileClause(stringBuilder: StringBuilder, id: Int?, coloursCount: Int) {
            for (i in 0 until coloursCount) {
                stringBuilder.append(getVarId(id!!, coloursCount, i)).append(" ")
            }
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
            stringBuilder.append("\n")
        }

        fun assignIds(graph: Map<Tile, HashSet<Tile>>): Map<Tile, Int> {
            val ids = HashMap<Tile, Int>()
            for ((id, tile) in graph.keys.withIndex()) {
                ids.put(tile, id)
            }
            return ids
        }
    }
}