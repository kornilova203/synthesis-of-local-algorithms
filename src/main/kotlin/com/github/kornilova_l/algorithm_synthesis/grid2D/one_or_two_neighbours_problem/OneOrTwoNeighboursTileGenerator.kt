package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTileGenerator.Companion.generatePossiblyValidTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTileGenerator.Companion.removeInvalid
import com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem.OneOrTwoNeighboursTile.Companion.getTilesFile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths


class OneOrTwoNeighboursTileGenerator(finalN: Int,
                                      finalM: Int,
                                      dir: File) : TileGenerator<OneOrTwoNeighboursTile>(finalN, finalM, getInitialTiles(finalN, finalM, dir), dir, OneOrTwoNeighboursTilesFactory(), OneOrTwoNeighboursTilesFileNameCreator()) {

    override fun addValidExtensionsToSet(tile: OneOrTwoNeighboursTile,
                                         expandedTiles: MutableSet<OneOrTwoNeighboursTile>,
                                         side: Expand) {
        val newTiles = tile.getAllExpandedTiles(side)
        for (newTile in newTiles) {
            if (newTile.isValid()) {
                if (newTile !is OneOrTwoNeighboursTile) {
                    throw AssertionError("OneOrTwoNeighboursTile.getAllExpandedTiles() should produce set of OneOrTwoNeighboursTile")
                }
                expandedTiles.add(newTile)
            }
        }
    }

    companion object {

        private fun getInitialTiles(finalN: Int, finalM: Int, dir: File): File {
            var currentN = finalN
            var currentM = finalM
            while (currentN >= 3 && currentM >= 3) {
                val file = getTilesFile(currentN, currentM, dir)
                if (file != null) {
                    println("Found file: $file")
                    return file
                }
                if (currentM > currentN) {
                    currentM--
                } else {
                    currentN--
                }
            }
            /* if suitable file was not found */
            return generateNew(Math.min(3, finalN), Math.min(3, finalM), dir)
        }

        private fun generateNew(n: Int, m: Int, dir: File): File {
            val tiles = generatePossiblyValidTiles(OneOrTwoNeighboursTile(n, m), n, m)
            val validTiles = removeInvalid(tiles)
            val file = Paths.get(dir.toString(), "name-$n-$m.tiles").toFile()
            FileOutputStream(file).use { stream ->
                for (validTile in validTiles) {
                    stream.write(validTile.longsToString().toByteArray())
                    stream.write("\n".toByteArray())
                }
            }
            return file
        }
    }
}