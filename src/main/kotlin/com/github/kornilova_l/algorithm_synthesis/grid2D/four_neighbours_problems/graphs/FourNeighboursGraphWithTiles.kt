package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.graphs

import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem.FOUR_POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.DirectedGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.FourNeighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileGraph
import com.github.kornilova_l.util.FileNameCreator
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


class FourNeighboursGraphWithTiles(n: Int,
                                   m: Int,
                                   k: Int,
                                   neighbourhoods: Set<FourNeighbourhood>,
                                   ids: DualHashBidiMap<IndependentSetTile, Int>) : DirectedGraphWithTiles<FourNeighbourhood>(n, m, k, neighbourhoods, ids) {

    override val edgeCount: Int
        get() = neighbourhoods.size * 4

    companion object {
        fun createInstance(tiles: Set<IndependentSetTile>): FourNeighboursGraphWithTiles {
            val n = tiles.first().n - 1
            val m = tiles.first().m - 1
            val k = tiles.first().k
            if (n <= 0 || m <= 0) {
                throw IllegalArgumentException("Each dimension of tiles in set must be at least 2")
            }
            val ids = DualHashBidiMap<IndependentSetTile, Int>()
            val neighbourhoods = HashSet<FourNeighbourhood>()
            /* There must exist at most one instance of each tile */
            for (tile in tiles) {
                neighbourhoods.add(
                        FourNeighbourhood(
                                getId(IndependentSetTile.createInstance(tile, FOUR_POSITION.TL), ids),
                                getId(IndependentSetTile.createInstance(tile, FOUR_POSITION.TR), ids),
                                getId(IndependentSetTile.createInstance(tile, FOUR_POSITION.BR), ids),
                                getId(IndependentSetTile.createInstance(tile, FOUR_POSITION.BL), ids)
                        ))
            }
            if (neighbourhoods.size == 0) {
                throw IllegalArgumentException("Cannot construct graph")
            }
            return FourNeighboursGraphWithTiles(n, m, k, neighbourhoods, ids)
        }

        fun createInstance(tilesFile: File, directedGraph: FourNeighboursDirectedGraph): FourNeighboursGraphWithTiles {
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
                return FourNeighboursGraphWithTiles(directedGraph.n, directedGraph.m, directedGraph.k, directedGraph.neighbourhoods, ids)
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