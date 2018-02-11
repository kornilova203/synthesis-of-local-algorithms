package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import com.github.kornilova_l.util.FileNameCreator
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths


/**
 * @param dir directory in which precalculated [IndependentSetTile]s are stored
 */
class IndependentSetTileGenerator(finalN: Int,
                                  finalM: Int,
                                  k: Int,
                                  dir: File) : TileGenerator<IndependentSetTile>(finalN, finalM, getInitialTiles(finalN, finalM, k, dir), dir, ISTilesParserFactory(), ISTilesFileNameCreator(k)) {

    override fun addValidExtensionsToSet(tile: IndependentSetTile,
                                         expandedTiles: MutableSet<IndependentSetTile>,
                                         side: Expand) {
        val newTiles = tile.getAllExpandedTiles(side)
        for (newTile in newTiles) {
            if (newTile.isValid()) {
                if (newTile !is IndependentSetTile) {
                    throw AssertionError("IndependentSetTile.getAllExpandedTiles() should produce set of IndependentSetTile")
                }
                expandedTiles.add(newTile)
            }
        }
    }

    companion object {

        private fun getInitialTiles(finalN: Int, finalM: Int, k: Int, dir: File): File {
            var currentN = finalN
            var currentM = finalM
            while (currentN >= 3 && currentM >= 3) {
                val file = FileNameCreator.getFile(dir, currentN, currentM, k)
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
            return generateNew(Math.min(3, finalN), Math.min(3, finalM), k, dir)
        }

        private fun generateNew(n: Int, m: Int, k: Int, dir: File): File {
            val tiles = generatePossiblyValidTiles(IndependentSetTile(n, m, k), n, m)
            val validTiles = removeInvalid(tiles)
            val file = Paths.get(dir.toString(), ISTilesFileNameCreator(k).getFileName(n, m, validTiles.size)).toFile()
            FileOutputStream(file).use { stream ->
                for (validTile in validTiles) {
                    stream.write(validTile.longsToString().toByteArray())
                    stream.write("\n".toByteArray())
                }
            }
            return file
        }

        /**
         * Remove all tileSet which does not have maximal IS
         */
        fun removeInvalid(tiles: Set<BinaryTile>): Set<BinaryTile> {
            val validTiles = HashSet<BinaryTile>()
            for (tile in tiles) {
                if (tile.isValid()) {
                    validTiles.add(tile)
                }
            }
            return validTiles
        }

        /**
         * Generates set of possibly-valid tiles
         */
        fun generatePossiblyValidTiles(emptyTile: BinaryTile, n: Int, m: Int): Set<BinaryTile> {

            /* It is meaningless to make following piece of code recursive
             * because all candidate tiles must be placed in possiblyValidTiles set
             * and it is not possible to reduce memory consumption using recursive method
             */
            val possiblyValidTiles = HashSet<BinaryTile>()
            possiblyValidTiles.add(emptyTile)

            for (i in 0 until n) {
                for (j in 0 until m) {
                    val newTiles = HashSet<BinaryTile>()
                    for (possiblyValidTile in possiblyValidTiles) {
                        if (possiblyValidTile.canBeIncluded(i, j)) {
                            newTiles.add(possiblyValidTile.cloneAndChange(i, j))
                        }
                    }
                    possiblyValidTiles.addAll(newTiles)
                    newTiles.clear()
                }
            }
            return possiblyValidTiles
        }
    }
}