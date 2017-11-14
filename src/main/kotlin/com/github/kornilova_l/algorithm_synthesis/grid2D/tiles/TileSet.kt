package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import java.io.File
import java.io.FileInputStream
import java.util.*

/**
 * Contains set of possiblyValidTiles of one size
 */
class TileSet {
    internal var n: Int = 0
    internal var m: Int = 0
    var k: Int = 0
    val possiblyValidTiles = HashSet<Tile>()

    internal val isEmpty: Boolean
        get() = possiblyValidTiles.isEmpty()

    /**
     * Generates set of possibly-valid possiblyValidTiles
     */
    internal constructor(n: Int, m: Int, k: Int) {
        this.n = n
        this.m = m
        this.k = k

        /* It is meaningless to make following piece of code recursive
         * because all candidate tiles must be placed in possiblyValidTiles set
         * and it is not possible to reduce memory consumption using recursive method
         */
        possiblyValidTiles.add(Tile(n, m, k))

        for (i in 0 until n) {
            for (j in 0 until m) {
                val newTileIS = this.possiblyValidTiles
                        .filter { it.canBeI(i, j) }
                        .map { Tile(it, i, j) }
                        .toMutableSet()
                possiblyValidTiles.addAll(newTileIS)
                newTileIS.clear()
            }
        }
    }

    constructor(file: File) {
        if (!file.exists() || !file.isFile) {
            throw IllegalArgumentException("File does not exist or it is not a file")
        }
        Scanner(FileInputStream(file)).use { scanner ->
            n = scanner.nextInt()
            m = scanner.nextInt()
            k = scanner.nextInt()
            val size = scanner.nextInt()
            for (i in 0 until size) {
                val `is` = HashSet<Tile.Coordinate>()
                for (row in 0 until n) {
                    for (column in 0 until m) {
                        if (scanner.nextInt() == 1) {
                            `is`.add(Tile.Coordinate(row, column))
                        }
                    }
                }
                possiblyValidTiles.add(Tile(n, m, k, `is`))
            }
        }
        /* Still cannot understand why try-catch gives not-initialized error */
        if (n == 0) {
            throw IllegalArgumentException("Could not open file")
        }
    }

    internal constructor(possiblyValidTiles: Collection<Tile>) {
        val someTile = possiblyValidTiles.iterator().next()
        n = someTile.getN()
        m = someTile.getM()
        k = someTile.k
        for (tile in possiblyValidTiles) {
            addTile(tile) // check that all tile have same size
        }
    }

    fun size(): Int {
        return possiblyValidTiles.size
    }

    /**
     * Return clone of original set so
     * it is not possible to add to set a tile of different size
     */
    fun getPossiblyValidTiles(): Set<Tile> {
        return HashSet(possiblyValidTiles)
    }

    private fun addTile(tile: Tile) {
        if (tile.k != k || tile.getM() != m || tile.getN() != n) {
            throw IllegalArgumentException("Tile must have the same parameters as tile set")
        }

        this.possiblyValidTiles.add(tile)
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (tile in this.possiblyValidTiles) {
            stringBuilder.append(tile).append("\n")
        }
        return stringBuilder.toString()
    }
}
