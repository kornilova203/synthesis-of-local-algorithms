package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths


/**
 * @param dir directory in which precalculated [IndependentSetTile]s are stored
 */
class IndependentSetTileGenerator(finalN: Int,
                                  finalM: Int,
                                  private val k: Int,
                                  dir: File? = null) : TileGenerator(finalN, finalM, getInitialTiles(finalN, finalM, k, dir)) {

    override fun getFileNameWithoutExtension(): String = "$finalN-$finalM-$k"

    override fun export(file: File) {
        FileOutputStream(file).use { outputStream ->
            outputStream.write("$finalN $finalM $k\n${tiles.size}\n".toByteArray())
            tiles.forEach { tile ->
                outputStream.write("$tile\n".toByteArray())
            }
        }
    }

    /**
     * If it does not matter if tiles have class [BinaryTile] or [IndependentSetTile] then
     * use [TileGenerator.tiles]. Because this method copies all tiles to new set
     */
    fun getIndependentSetTiles(): Set<IndependentSetTile> {
        val set = HashSet<IndependentSetTile>()
        for (tile in tiles) {
            if (tile is IndependentSetTile) {
                set.add(tile)
            } else {
                throw AssertionError("Tiles set contains tile that is not an instance of IndependentSetTile")
            }
        }
        return set
    }

    companion object {

        private fun getInitialTiles(finalN: Int, finalM: Int, k: Int, dir: File?): Set<BinaryTile> {
            if (dir == null) {
                return generateNew(finalN, finalM, k)
            }
            var currentN = finalN
            var currentM = finalM
            while (currentN >= 3 && currentM >= 3) {
                val file = Paths.get(dir.toString(), "$currentN-$currentM-$k.txt").toFile()
                if (file.exists()) {
                    println("Found file: $file")
                    return IndependentSetTile.parseTiles(file)
                }
                if (currentM > currentN) {
                    currentM--
                } else {
                    currentN--
                }
            }
            /* if suitable file was not found */
            return generateNew(finalN, finalM, k)
        }

        private fun generateNew(finalN: Int, finalM: Int, k: Int): Set<BinaryTile> {
            val currentN = if (finalN < 3) finalN else 3
            val currentM = if (finalM < 3) finalM else 3
            val tiles = generatePossiblyValidTiles(IndependentSetTile(currentN, currentM, k), currentN, currentM)
            return removeInvalid(tiles)
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