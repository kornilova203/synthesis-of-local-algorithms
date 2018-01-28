package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.parseSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem.OneOrTwoNeighboursTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem.OneOrTwoNeighboursTile.Companion.oneOrTwoNeighboursTilesFilePattern
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Part.*
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import kotlin.collections.HashMap


/**
 * Precalculate graphs and export them to files.
 * So [SimpleGraph] instances can be created and used.
 */
fun main(args: Array<String>) {
    val dirName = "one_or_two_neighbours_tiles/simple_graphs"
    val files = File("one_or_two_neighbours_tiles").listFiles()
    for (i in 0 until files.size) {
        val file = files[i]
        if (file.isFile && oneOrTwoNeighboursTilesFilePattern.matcher(file.name).matches()) {
            val parts = file.name.split("-")
            val n = Integer.parseInt(parts[0])
            val m = Integer.parseInt(parts[1].split(".")[0])
            if (n > m) {
                continue
            }
            println("n $n  m $m")
            val file2 = File("one_or_two_neighbours_tiles/${n - 1}-${m + 1}.txt")
            if (!file2.exists()) {
                continue
            }
            val tileSet1 = OneOrTwoNeighboursTile.parseTiles(file)
            val tileSet2 = OneOrTwoNeighboursTile.parseTiles(file2)
            val graph = SimpleGraphWithTiles.createInstance(tileSet1, tileSet2)
            println("Start export")
            graph.exportTiles(File("$dirName/${graph.n}-${graph.m}.tiles"))
            println("Export graph")
            graph.export(File("$dirName/${graph.n}-${graph.m}.graph"))
        }
    }
}

/**
 * Constructs graph of tiles.
 * This implementation does not save orientation of edges
 * @param ids ids of tiles start with 0
 */
class SimpleGraphWithTiles(n: Int, m: Int, graph: Map<Int, Set<Int>>, private val ids: DualHashBidiMap<Tile, Int>) :
        SimpleGraph(n, m, graph) {

    /**
     * Format:
     * <n> <m>
     * <number of tiles>
     * for each tile:
     * <id>
     * <tile>
     */
    fun exportTiles(file: File) {
        file.outputStream().use { outputStream ->
            outputStream.write("$n $m\n".toByteArray())
            outputStream.write("${ids.size}\n".toByteArray())
            for (tileAndId in ids) {
                outputStream.write("${tileAndId.value}\n".toByteArray())
                outputStream.write("${tileAndId.key}\n".toByteArray())
            }
        }
    }

    companion object {

        fun createInstance(tileSet1: Set<BinaryTile>, tileSet2: Set<BinaryTile>): SimpleGraphWithTiles {
            val n = getN(tileSet1, tileSet2)
            val m = getM(tileSet1, tileSet2)
            var set1 = tileSet1
            var set2 = tileSet2
            validateTileSets(tileSet1, tileSet2)
            val n1 = tileSet1.first().n
            val n2 = tileSet2.first().n
            if (n1 < n2) {
                val temp = set1
                set1 = set2
                set2 = temp
            }

            val graph = HashMap<Int, MutableSet<Int>>()
            val ids = DualHashBidiMap<Tile, Int>()

            for (tile in set1) { // get vertical neighbours
                val top = tile.clonePart(N)
                val bottom = tile.clonePart(S)
                val topId = getId(top, ids)
                val bottomId = getId(bottom, ids)
                graph.computeIfAbsent(topId) { HashSet() }.add(bottomId)
                graph.computeIfAbsent(bottomId) { HashSet() }.add(topId)
            }
            for (tile in set2) { // get horizontal neighbours
                val left = tile.clonePart(W)
                val right = tile.clonePart(E)
                val leftId = getId(left, ids)
                val rightId = getId(right, ids)
                graph.computeIfAbsent(leftId) { HashSet() }.add(rightId)
                graph.computeIfAbsent(rightId) { HashSet() }.add(leftId)
            }
            if (graph.size == 0) {
                throw IllegalArgumentException("Cannot construct graph")
            }
            return SimpleGraphWithTiles(n, m, graph, ids)
        }

        private fun getN(tileSet1: Set<BinaryTile>, tileSet2: Set<BinaryTile>): Int {
            val n1 = tileSet1.first().n
            val n2 = tileSet2.first().n
            return if (n1 > n2) n2 else n1
        }

        private fun getM(tileSet1: Set<BinaryTile>, tileSet2: Set<BinaryTile>): Int {
            val n1 = tileSet1.first().n
            val m1 = tileSet1.first().m
            val n2 = tileSet2.first().n
            val m2 = tileSet2.first().m
            return if (n1 > n2) m1 else m2
        }

        private fun validateTileSets(tileSet1: Set<BinaryTile>, tileSet2: Set<BinaryTile>) {
            if (tileSet1.isEmpty() || tileSet2.isEmpty()) {
                throw IllegalArgumentException("At least one set is empty")
            }

            val n1 = tileSet1.first().n
            val m1 = tileSet1.first().m
            val n2 = tileSet2.first().n
            val m2 = tileSet2.first().m
            if (n1 > n2) {
                if (n1 != n2 + 1 || m2 != m1 + 1) {
                    throw IllegalArgumentException("If size of kern tile is n*m then size of two tiles set must be n+1*m and n*m+1 (order does not matter)")
                }
            } else {
                if (n1 + 1 != n2 || m2 + 1 != m1) {
                    throw IllegalArgumentException("If size of kern tile is n*m then size of two tiles set must be n+1*m and n*m+1 (order does not matter)")
                }
            }
        }

        /**
         * Checks if this tile already exists.
         * If so it returns existing id
         * otherwise it adds tile to set and returns new id
         */
        private fun getId(tile: Tile, ids: DualHashBidiMap<Tile, Int>): Int {
            val maybeId = ids[tile]
            return if (maybeId != null) {
                maybeId
            } else {
                val id = ids.size // ids start with 0
                ids[tile] = id
                id
            }
        }

        fun createInstance(tilesFile: File, simpleGraph: SimpleGraph): SimpleGraphWithTiles {
            val ids = DualHashBidiMap<Tile, Int>()
            BufferedReader(FileReader(tilesFile)).use { reader ->
                val firstLine = reader.readLine()
                val parts = firstLine.split(" ")
                val n = Integer.parseInt(parts[0])
                val m = Integer.parseInt(parts[1])
                if (n != simpleGraph.n || m != simpleGraph.m) {
                    throw IllegalArgumentException("Parameters of graph do not match size of tiles. Graph: n = ${simpleGraph.n} " +
                            "m = ${simpleGraph.m}. IndependentSetTile: n = $n m = $m.")
                }
                val tilesCount = Integer.parseInt(reader.readLine())
                for (i in 0 until tilesCount) {
                    var line = reader.readLine()
                    while (line.isEmpty()) {
                        line = reader.readLine()
                    }
                    val id = Integer.parseInt(line)
                    val grid = parseSet(reader, n, m)
                    ids[BinaryTile(n, m, grid)] = id
                }
                return SimpleGraphWithTiles(simpleGraph.n, simpleGraph.m, simpleGraph.graph, ids)
            }
        }

    }

    fun getTile(tileId: Int): Tile {
        return ids.getKey(tileId)!!
    }
}
