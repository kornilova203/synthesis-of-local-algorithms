package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Companion.Expand.HEIGHT
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Companion.Expand.WIDTH
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Companion.getAllPossibleExtensions
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.generatePossiblyValidTiles
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths

/**
 * Generates all possible combinations of tileSet finalN x finalM in kth power of grid
 */
class TileGenerator(private val finalN: Int, private val finalM: Int, private val k: Int) {
    val tileSet: TileSet

    init {
        var currentN = if (finalN < 3) finalN else 3
        var currentM = if (finalM < 3) finalM else 3

        var tiles = generatePossiblyValidTiles(currentN, currentM, k)

        tiles = removeNotMaximal(tiles)

        while (currentM < finalM || currentN < finalN) {
            tiles = expandTileSet(tiles)
            currentN = tiles.first().n
            currentM = tiles.first().m
            println("Found $currentN x $currentM tiles")
        }

        if (tiles.isEmpty()) {
            throw IllegalArgumentException("Cannot produce valid set of tiles")
        } else {
            this.tileSet = TileSet(tiles)
        }
    }

    /**
     * Expand each tile by 1 row/column
     * Remove not valid tiles
     */
    private fun expandTileSet(candidateTilesSet: Set<Tile>): Set<Tile> {
        val currentN = candidateTilesSet.first().n
        val currentM = candidateTilesSet.first().m
        if (currentN == finalN && currentM == finalM) {
            throw IllegalArgumentException("Tiles have final size")
        }
        val side = if (currentN < currentM && currentN < finalN || currentM == finalM) HEIGHT else WIDTH
        val expandedTiles = HashSet<Tile>()
        candidateTilesSet
                .map { getAllPossibleExtensions(it, side) }
                .forEach { extensions -> extensions.filterTo(expandedTiles) { it.isValid } }
        return expandedTiles
    }

    override fun toString(): String {
        return finalN.toString() + " " + finalM + " " + k + "\n" +
                tileSet.size() + "\n" +
                tileSet.toString()
    }

    fun exportToFile(dir: File, addTimestampToFileName: Boolean?): File? {
        if (!dir.exists() || !dir.isDirectory) {
            throw IllegalArgumentException("Argument is not a directory or does not exist")
        }
        val filePath = Paths.get(dir.toString(), getFileName(addTimestampToFileName))
        val file = filePath.toFile()
        try {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(toString().toByteArray())
                return file
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun getFileName(addTimestamp: Boolean?): String {
        return if (addTimestamp!!) {
            String.format("%d-%d-%d-%d.txt", finalN, finalM, k, System.currentTimeMillis())
        } else String.format("%d-%d-%d.txt", finalN, finalM, k)
    }

    companion object {

        /**
         * Remove all tileSet which does not have maximal IS
         */
        private fun removeNotMaximal(tiles: Set<Tile>): Set<Tile> {
            val maximalTiles = HashSet<Tile>()
            tiles.filterTo(maximalTiles) { it.isValid }
            return maximalTiles
        }
    }
}
