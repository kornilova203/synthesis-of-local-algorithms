package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand.HEIGHT
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand.WIDTH
import com.github.kornilova_l.util.ProgressBar
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap

/**
 * Generates all possible combinations of tileSet finalN x finalM in kth power of grid.
 * It outputs set of [BinaryTile]. I could not find a good way to make [TileGenerator] output
 * set of some particular tiles (for example [IndependentSetTile]).
 * There are wrappers for [TileGenerator] that convert Set<BinaryTile> to Set<Whatever>
 */
abstract class TileGenerator(protected val finalN: Int, protected val finalM: Int, initialTiles: Set<BinaryTile>) {
    val tiles: Set<BinaryTile>

    fun export(dir: File, addTimestampToFileName: Boolean = false): File? {
        if (!dir.exists() || !dir.isDirectory) {
            throw IllegalArgumentException("Argument is not a directory or does not exist")
        }
        val filePath = Paths.get(dir.toString(),
                "${getFileNameWithoutExtension()}-${tiles.size}${if (addTimestampToFileName) "-" + System.currentTimeMillis().toString() else ""}.txt")
        val file = filePath.toFile()
        export(file)
        return file
    }

    open fun getFileNameWithoutExtension(): String = "$finalN-$finalM"

    protected abstract fun export(file: File)

    /**
     * Expand each tile by 1 row/column
     * Remove not valid tiles
     */
    private fun expandTileSet(tiles: Set<BinaryTile>): Set<BinaryTile> {
        val currentN = tiles.first().n
        val currentM = tiles.first().m
        if (currentN == finalN && currentM == finalM) {
            throw IllegalArgumentException("Tiles have final size")
        }
        val side = if (currentN < currentM && currentN < finalN || currentM == finalM) HEIGHT else WIDTH
        println("Expand tiles $currentN x $currentM. Side: $side\nTiles count = ${tiles.size}")
        val progressBar = ProgressBar(tiles.size)
        val expandedTiles: MutableSet<BinaryTile> = ConcurrentHashMap.newKeySet()
        for (tile in tiles) {
            addValidExtensionsToSet(tile, expandedTiles, side)
            progressBar.updateProgress(1)
        }
        progressBar.finish()
        println()
        return expandedTiles
    }

    private fun addValidExtensionsToSet(tile: BinaryTile,
                                        expandedTiles: MutableSet<BinaryTile>,
                                        side: Expand) {
        val extensions = tile.getAllExpandedTiles(side)
        for (extension in extensions) {
            if (extension.isValid()) {
                expandedTiles.add(extension)
            }
        }
    }

    /**
     * Do not use this method if set of tiles is big ( > 1 million)
     */
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder
                .append(finalN).append(" ")
                .append(finalM).append("\n")
                .append(tiles.size).append("\n")
        for (tile in tiles) {
            stringBuilder.append(tile).append("\n")
        }
        return stringBuilder.toString()
    }

    init {
        var tiles = initialTiles
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
            this.tiles = tiles
        }
    }
}
