package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.graphs

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FIVE_POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.DirectedGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.FiveNeighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileGraph
import com.github.kornilova_l.util.FileNameCreator
import com.github.kornilova_l.util.ProgressBar
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


class FiveNeighboursGraphWithTiles(n: Int,
                                   m: Int,
                                   k: Int,
                                   neighbourhoods: Set<FiveNeighbourhood>,
                                   ids: DualHashBidiMap<IndependentSetTile, Int>) : DirectedGraphWithTiles<FiveNeighbourhood>(n, m, k, neighbourhoods, ids) {

    override val edgeCount: Int
        get() = neighbourhoods.size * 4

    companion object {
        fun createInstance(tiles: MutableSet<IndependentSetTile>,
                           showProgressBar: Boolean = false): FiveNeighboursGraphWithTiles {
            val n = tiles.first().n - 2
            val m = tiles.first().m - 2
            val k = tiles.first().k
            if (n <= 0 || m <= 0) {
                throw IllegalArgumentException("Each dimension of tiles in set must be at least 3")
            }
            val ids = DualHashBidiMap<IndependentSetTile, Int>()
            val neighbourhoods = HashSet<FiveNeighbourhood>()
            val progressBar = if (showProgressBar) ProgressBar(tiles.size, "Construct graph") else null
            /* There must exist at most one instance of each tile */
            val iterator = tiles.iterator()
            while (iterator.hasNext()) {
                val tile = iterator.next()
                neighbourhoods.add(
                        FiveNeighbourhood(
                                getId(IndependentSetTile.createInstance(tile, FIVE_POSITION.X), ids),
                                getId(IndependentSetTile.createInstance(tile, FIVE_POSITION.N), ids),
                                getId(IndependentSetTile.createInstance(tile, FIVE_POSITION.E), ids),
                                getId(IndependentSetTile.createInstance(tile, FIVE_POSITION.S), ids),
                                getId(IndependentSetTile.createInstance(tile, FIVE_POSITION.W), ids)
                        ))
                progressBar?.updateProgress()
                iterator.remove()
            }
            progressBar?.finish()
            if (neighbourhoods.size == 0) {
                throw IllegalArgumentException("Cannot construct graph")
            }
            return FiveNeighboursGraphWithTiles(n, m, k, neighbourhoods, ids)
        }

        fun createInstance(tilesFile: File, directedGraph: FiveNeighboursDirectedGraph): FiveNeighboursGraphWithTiles {
            val ids = DualHashBidiMap<IndependentSetTile, Int>()
            if (FileNameCreator.getExtension(tilesFile.name) != TileGraph.graphTilesFileExtension) {
                throw IllegalArgumentException("File must contain independent set tiles. File: ${tilesFile.name}")
            }
            val n = FileNameCreator.getIntParameter(tilesFile.name, "n")!!
            val m = FileNameCreator.getIntParameter(tilesFile.name, "m")!!
            val k = FileNameCreator.getIntParameter(tilesFile.name, "k")!!
            val tilesCount = FileNameCreator.getIntParameter(tilesFile.name, "size")!!
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
                    val grid = BinaryTile.parseBitSet(reader.readLine())
                    ids[IndependentSetTile(n, m, k, grid)] = id
                }
                return FiveNeighboursGraphWithTiles(directedGraph.n, directedGraph.m, directedGraph.k, directedGraph.neighbourhoods, ids)
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