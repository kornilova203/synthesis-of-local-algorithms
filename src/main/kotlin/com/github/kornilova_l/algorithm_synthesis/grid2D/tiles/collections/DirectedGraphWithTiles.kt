package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.parseSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import java.util.regex.Pattern


private val tilesFilePattern = Pattern.compile("\\d+-\\d+-\\d+\\.txt")!!

/**
 * Precalculate graphs and export them to files.
 * So [DirectedGraph] instances can be created and used.
 */
fun main(args: Array<String>) {
    val dirName = "directed_graphs"
    val files = File("generated_tiles").listFiles()
    for (i in 0 until files.size) {
        val file = files[i]
        if (tilesFilePattern.matcher(file.name).matches()) {
            val parts = file.name.split("-")
            val n = Integer.parseInt(parts[0])
            val m = Integer.parseInt(parts[1])
            val k = Integer.parseInt(parts[2].split(".")[0])
            if (n < 3 || m < 3 ||
                    n > m ||
                    k == 2) {
                continue
            }
            print("n $n  m $m  k $k\n")
            val tileSet = IndependentSetTile.parseTiles(file)
            val graph = DirectedGraphWithTiles.createInstance(tileSet)
            println("Start export")
            graph.exportTiles(File("$dirName/${graph.n}-${graph.m}-${graph.k}.tiles"))
            println("Export graph")
            graph.export(File("$dirName/${graph.n}-${graph.m}-${graph.k}.graph"))
        }
    }
}

/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges.
 * Also it contains actual tiles ([DirectedGraph] contains only ids)
 * This class should be used when
 * [com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.LabelingFunction] must be created
 */
class DirectedGraphWithTiles(n: Int,
                             m: Int,
                             k: Int,
                             neighbourhoods: Set<Neighbourhood>,
                             private val ids: DualHashBidiMap<IndependentSetTile, Int>) : IndependentSetDirectedGraph(n, m, k, neighbourhoods) {
    override val size: Int
        get() = ids.size

    fun getId(tile: IndependentSetTile): Int = ids[tile]!!

    fun getTile(id: Int): IndependentSetTile? = ids.getKey(id)

    /**
     * Format:
     * <n> <m> <k>
     * <number of tiles>
     * for each tile:
     * <id>
     * <tile>
     */
    fun exportTiles(file: File) {
        file.outputStream().use { outputStream ->
            outputStream.write("$n $m $k\n".toByteArray())
            outputStream.write("${ids.size}\n".toByteArray())
            for (tileAndId in ids) {
                outputStream.write("${tileAndId.value}\n".toByteArray())
                outputStream.write("${tileAndId.key}\n".toByteArray())
            }
        }
    }

    companion object {
        fun createInstance(tiles: Set<IndependentSetTile>): DirectedGraphWithTiles {
            val n = tiles.first().n - 2
            val m = tiles.first().m - 2
            val k = tiles.first().k
            if (n <= 0 || m <= 0) {
                throw IllegalArgumentException("Each dimension of tiles in set must be at least 3")
            }
            val ids = DualHashBidiMap<IndependentSetTile, Int>()
            val neighbourhoods = HashSet<Neighbourhood>()
            /* There must exist at most one instance of each tile */
            for (tile in tiles) {
                neighbourhoods.add(
                        Neighbourhood(
                                getId(IndependentSetTile.createInstance(tile, POSITION.X), ids),
                                getId(IndependentSetTile.createInstance(tile, POSITION.N), ids),
                                getId(IndependentSetTile.createInstance(tile, POSITION.E), ids),
                                getId(IndependentSetTile.createInstance(tile, POSITION.S), ids),
                                getId(IndependentSetTile.createInstance(tile, POSITION.W), ids)
                        ))
            }
            if (neighbourhoods.size == 0) {
                throw IllegalArgumentException("Cannot construct graph")
            }
            return DirectedGraphWithTiles(n, m, k, neighbourhoods, ids)
        }

        fun createInstance(tilesFile: File, directedGraph: IndependentSetDirectedGraph): DirectedGraphWithTiles {
            val ids = DualHashBidiMap<IndependentSetTile, Int>()
            BufferedReader(FileReader(tilesFile)).use { reader ->
                val firstLine = reader.readLine()
                val parts = firstLine.split(" ")
                val n = Integer.parseInt(parts[0])
                val m = Integer.parseInt(parts[1])
                val k = Integer.parseInt(parts[2])
                if (n != directedGraph.n || m != directedGraph.m || k != directedGraph.k) {
                    throw IllegalArgumentException("Parameters of graph do not match size of tiles. Graph: n = ${directedGraph.n} " +
                            "m = ${directedGraph.m} k = ${directedGraph.k}. IndependentSetTile: n = $n m = $m k = $k.")
                }
                val tilesCount = Integer.parseInt(reader.readLine())
                for (i in 0 until tilesCount) {
                    var line = reader.readLine()
                    while (line.isEmpty()) {
                        line = reader.readLine()
                    }
                    val id = Integer.parseInt(line)
                    val grid = parseSet(reader, n, m)
                    ids[IndependentSetTile(n, m, k, grid)] = id
                }
                return DirectedGraphWithTiles(directedGraph.n, directedGraph.m, directedGraph.k, directedGraph.neighbourhoods, ids)
            }
        }

        /**
         * Checks if this tile already exists.
         * If so it returns existing id
         * otherwise it adds tile to set and returns new id
         */
        private fun getId(tile: IndependentSetTile, ids: DualHashBidiMap<IndependentSetTile, Int>): Int {
            val maybeId = ids[tile]
            return if (maybeId != null) {
                assert(maybeId > 0)
                maybeId
            } else {
                val id = ids.size + 1 // ids start with 1
                ids[tile] = id
                id
            }
        }
    }
}