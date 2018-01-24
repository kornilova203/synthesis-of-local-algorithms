package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTileGenerator.Companion.generatePossiblyValidTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTileGenerator.Companion.removeInvalid
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.parseTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import java.io.File
import java.nio.file.Paths


class OneOrTwoNeighboursTileGenerator(finalN: Int,
                                      finalM: Int,
                                      dir: File? = null) : TileGenerator(finalN, finalM, getInitialTiles(finalN, finalM, dir)) {
    /**
     * If it does not matter if tiles have class [Tile] or [OneOrTwoNeighboursTileGenerator] then
     * use [OneOrTwoNeighboursTileGenerator.tiles]. Because this method copies all tiles to new set
     */
    fun getIndependentSetTiles(): Set<OneOrTwoNeighboursTile> {
        val set = HashSet<OneOrTwoNeighboursTile>()
        for (tile in tiles) {
            if (tile is OneOrTwoNeighboursTile) {
                set.add(tile)
            } else {
                throw AssertionError("Tiles set contains tile that is not an instance of IndependentSetTile")
            }
        }
        return set
    }

    companion object {

        private fun getInitialTiles(finalN: Int, finalM: Int, dir: File?): Set<Tile> {
            if (dir == null) {
                return generateNew(finalN, finalM)
            }
            var currentN = finalN
            var currentM = finalM
            while (currentN >= 3 && currentM >= 3) {
                val file = Paths.get(dir.toString(), "$currentN-$currentM.txt").toFile()
                if (file.exists()) {
                    println("Found file: $file")
                    return parseTiles(file)
                }
                if (currentM > currentN) {
                    currentM--
                } else {
                    currentN--
                }
            }
            /* if suitable file was not found */
            return generateNew(finalN, finalM)
        }

        private fun generateNew(finalN: Int, finalM: Int): Set<Tile> {
            val currentN = if (finalN < 3) finalN else 3
            val currentM = if (finalM < 3) finalM else 3
            val tiles = generatePossiblyValidTiles(OneOrTwoNeighboursTile(currentN, currentM), currentN, currentM)
            return removeInvalid(tiles)
        }
    }
}