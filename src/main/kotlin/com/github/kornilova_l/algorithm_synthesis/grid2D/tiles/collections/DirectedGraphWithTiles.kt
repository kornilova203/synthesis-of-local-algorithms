package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile.Companion.independentSetTilesFilePattern
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.parseBitSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern


/**
 * Precalculate graphs and export them to files.
 * So [DirectedGraph] instances can be created and used.
 */
fun main(args: Array<String>) {
    val dirName = "independent_set_tiles/directed_graphs"
    val files = File("independent_set_tiles").listFiles()
    for (i in 0 until files.size) {
        val file = files[i]
        if (independentSetTilesFilePattern.matcher(file.name).matches()) {
            val parts = file.name.split("-")
            val n = Integer.parseInt(parts[1])
            val m = Integer.parseInt(parts[2])
            if (n < 3 || m < 3) {
                continue
            }
            println(file.name)
            val tileSet = IndependentSetTile.parseTiles(file)
            val graph = DirectedGraphWithTiles.createInstance(tileSet)
            println("Start export")
            graph.exportTiles(File(dirName))
            println("Export graph")
            graph.export(File(dirName))
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
     * for each tile:
     * <id>
     * <tile's array of longs>
     */
    fun exportTiles(dir: File) {
        val file = Paths.get(dir.toString(), "${IndependentSetTile.name}-$n-$m-$k-${ids.size}.$tilesFileExtension").toFile()
        file.outputStream().use { outputStream ->
            for (tileAndId in ids) {
                outputStream.write(tileAndId.value.toString().toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(tileAndId.key.longsToString().toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("\n".toByteArray())
            }
        }
    }

    companion object {
        private const val tilesFileExtension = "graphtiles"
        private val tilesFilePattern = Pattern.compile("${IndependentSetTile.name}-\\d+-\\d+-\\d+-\\d+\\.$tilesFileExtension")!!

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

        fun getTilesFile(n: Int, m: Int, k: Int, dir: File): File? {
            for (file in dir.listFiles()) {
                if (file.isDirectory) {
                    continue
                }
                if (!tilesFilePattern.matcher(file.name).matches()) {
                    continue
                }
                if (BinaryTile.parseNumber(file.name, 1) == n &&
                        BinaryTile.parseNumber(file.name, 2) == m &&
                        BinaryTile.parseNumber(file.name, 3) == k) {
                    return file
                }
            }
            return null
        }

        fun createInstance(tilesFile: File, directedGraph: IndependentSetDirectedGraph): DirectedGraphWithTiles {
            val ids = DualHashBidiMap<IndependentSetTile, Int>()
            if (!tilesFilePattern.matcher(tilesFile.name).matches()) {
                throw IllegalArgumentException("File must contain independent set tiles. File: ${tilesFile.name}")
            }
            val n = BinaryTile.parseNumber(tilesFile.name, 1)
            val m = BinaryTile.parseNumber(tilesFile.name, 2)
            val k = BinaryTile.parseNumber(tilesFile.name, 3)
            val tilesCount = BinaryTile.parseNumber(tilesFile.name, 4)
            if (n != directedGraph.n || m != directedGraph.m || k != directedGraph.k) {
                throw IllegalArgumentException("Parameters of graph do not match size of tiles. Graph: n = ${directedGraph.n} " +
                        "m = ${directedGraph.m} k = ${directedGraph.k}. IndependentSetTile: n = $n m = $m k = $k.")
            }
            BufferedReader(FileReader(tilesFile)).use { reader ->
                for (i in 0 until tilesCount) {
                    var line = reader.readLine()
                    while (line.isEmpty()) {
                        line = reader.readLine()
                    }
                    val id = Integer.parseInt(line)
                    val grid = parseBitSet(reader.readLine())
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