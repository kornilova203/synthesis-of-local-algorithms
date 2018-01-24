package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

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

    /**
     * If it does not matter if tiles have class [Tile] or [IndependentSetTile] then
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

    fun export(dir: File, addTimestampToFileName: Boolean = false): File? {
        if (!dir.exists() || !dir.isDirectory) {
            throw IllegalArgumentException("Argument is not a directory or does not exist")
        }
        val n = tiles.first().n
        val m = tiles.first().m
        val filePath = Paths.get(dir.toString(), getFileName(n, m, k, addTimestampToFileName))
        val file = filePath.toFile()
        FileOutputStream(file).use { outputStream ->
            outputStream.write("$n $m $k\n${tiles.size}\n".toByteArray())
            tiles.forEach { tile ->
                outputStream.write("$tile\n".toByteArray())
            }
            return file
        }
    }

    private fun getFileName(n: Int, m: Int, k: Int, addTimestampToFileName: Boolean): String {
        return if (addTimestampToFileName) {
            String.format("%d-%d-%d-%d.txt", n, m, k, System.currentTimeMillis())
        } else String.format("%d-%d-%d.txt", n, m, k)
    }

    companion object {

        private fun getInitialTiles(finalN: Int, finalM: Int, k: Int, dir: File?): Set<IndependentSetTile> {
            if (dir == null) {
                return generateNew(finalN, finalM, k)
            }
            var currentN = finalN
            var currentM = finalM
            while (currentN >= 3 && currentM >= 3) {
                val file = Paths.get(dir.toString(), "$currentN-$currentM-$k.txt").toFile()
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
            return generateNew(finalN, finalM, k)
        }

        private fun generateNew(finalN: Int, finalM: Int, k: Int): Set<IndependentSetTile> {
            val currentN = if (finalN < 3) finalN else 3
            val currentM = if (finalM < 3) finalM else 3
            val tiles = generatePossiblyValidTiles(currentN, currentM, k)
            return removeNotMaximal(tiles)
        }

        /**
         * Remove all tileSet which does not have maximal IS
         */
        private fun removeNotMaximal(tiles: Set<IndependentSetTile>): Set<IndependentSetTile> {
            val maximalTiles = HashSet<IndependentSetTile>()
            for (tile in tiles) {
                if (tile.isValid()) {
                    maximalTiles.add(tile)
                }
            }
            return maximalTiles
        }

        /**
         * Generates set of possibly-valid tiles
         */
        private fun generatePossiblyValidTiles(n: Int, m: Int, k: Int): Set<IndependentSetTile> {

            /* It is meaningless to make following piece of code recursive
             * because all candidate tiles must be placed in possiblyValidTiles set
             * and it is not possible to reduce memory consumption using recursive method
             */
            val possiblyValidTiles = HashSet<IndependentSetTile>()
            possiblyValidTiles.add(IndependentSetTile(n, m, k))

            for (i in 0 until n) {
                for (j in 0 until m) {
                    val newTileIS = HashSet<IndependentSetTile>()
                    for (possiblyValidTile in possiblyValidTiles) {
                        if (possiblyValidTile.canBeIncluded(i, j)) {
                            newTileIS.add(possiblyValidTile.cloneAndChange(i, j) as IndependentSetTile)
                        }
                    }
                    possiblyValidTiles.addAll(newTileIS)
                    newTileIS.clear()
                }
            }
            return possiblyValidTiles
        }
    }
}