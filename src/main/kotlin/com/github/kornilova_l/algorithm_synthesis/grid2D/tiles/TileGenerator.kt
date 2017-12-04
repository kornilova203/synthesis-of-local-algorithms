package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Companion.Expand.HEIGHT
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Companion.Expand.WIDTH
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Companion.getAllPossibleExtensions
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.generatePossiblyValidTiles
import com.github.kornilova_l.util.ProgressBar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths

/**
 * Generates all possible combinations of tileSet finalN x finalM in kth power of grid
 * @param dir that contains precalculated tiles of smaller size
 */
class TileGenerator(private val finalN: Int, private val finalM: Int, private val k: Int, dir: File? = null) {
    val tileSet: TileSet

    init {
        var tiles = getInitialTiles(dir)
        var currentN = tiles.first().n
        var currentM = tiles.first().m

        while (currentM < finalM || currentN < finalN) {
            tiles = expandTileSet(tiles)
            currentN = tiles.first().n
            currentM = tiles.first().m
        }

        if (tiles.isEmpty()) {
            throw IllegalArgumentException("Cannot produce valid set of tiles")
        } else {
            this.tileSet = TileSet(tiles)
        }
    }

    private fun getInitialTiles(dir: File?): Set<Tile> {
        var currentN = finalN
        var currentM = finalM
        while (currentN >= 3 && currentM >= 3) {
            val file = Paths.get(dir.toString(), "$currentN-$currentM-$k.txt").toFile()
            if (file.exists()) {
                println("Found file: $file")
                return TileSet(file).validTiles
            }
            if (currentM > currentN) {
                currentM--
            } else {
                currentN--
            }
        }
        currentN = if (finalN < 3) finalN else 3
        currentM = if (finalM < 3) finalM else 3
        val tiles = generatePossiblyValidTiles(currentN, currentM, k)
        return removeNotMaximal(tiles)
    }

    /**
     * Expand each tile by 1 row/column
     * Remove not valid tiles
     */
    private fun expandTileSet(tiles: Set<Tile>): Set<Tile> {
        val currentN = tiles.first().n
        val currentM = tiles.first().m
        if (currentN == finalN && currentM == finalM) {
            throw IllegalArgumentException("Tiles have final size")
        }
        val side = if (currentN < currentM && currentN < finalN || currentM == finalM) HEIGHT else WIDTH
        println("Expand tiles $currentN x $currentM k: $k. Side: $side\nTiles count = ${tiles.size}")
        val progressBar = ProgressBar(tiles.size)
        val expandedTiles = HashSet<Tile>()
        for (tile in tiles) {
            addValidExtensionsToSet(tile, expandedTiles, side)
            progressBar.updateProgress(1)
        }
        progressBar.finish()
        println()
        return expandedTiles
    }

    private fun addValidExtensionsToSet(tile: Tile, expandedTiles: HashSet<Tile>, side: Tile.Companion.Expand) {
        val extensions = getAllPossibleExtensions(tile, side)
        extensions.filterTo(expandedTiles) { it.isValid() }
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
            tiles.filterTo(maximalTiles) { tile -> tile.isValid() }
            return maximalTiles
        }
    }
}
