package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Companion.Expand
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Companion.Expand.HEIGHT
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Companion.Expand.WIDTH
import com.github.kornilova_l.util.ProgressBar
import java.util.concurrent.ConcurrentHashMap

/**
 * Generates all possible combinations of tileSet finalN x finalM in kth power of grid.
 * It outputs set of [Tile]. I could not find a good way to make [TileGenerator] output
 * set of some particular tiles (for example [IndependentSetTile]).
 * There are wrappers for [TileGenerator] that convert Set<Tile> to Set<Whatever>
 */
abstract class TileGenerator(private val finalN: Int, private val finalM: Int, initialTiles: Set<Tile>) {
    val tiles: Set<Tile>

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
        println("Expand tiles $currentN x $currentM. Side: $side\nTiles count = ${tiles.size}")
        val progressBar = ProgressBar(tiles.size)
        val expandedTiles: MutableSet<Tile> = ConcurrentHashMap.newKeySet()
        tiles.parallelStream().forEach { tile ->
            addValidExtensionsToSet(tile, expandedTiles, side)
            progressBar.updateProgress(1)
        }
        progressBar.finish()
        println()
        return expandedTiles
    }

    private fun addValidExtensionsToSet(tile: Tile,
                                        expandedTiles: MutableSet<Tile>,
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
