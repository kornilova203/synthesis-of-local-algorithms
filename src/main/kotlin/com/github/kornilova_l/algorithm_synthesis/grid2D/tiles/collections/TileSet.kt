package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import org.apache.lucene.util.OpenBitSet
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.util.*
import kotlin.collections.ArrayList

/**
 * Contains set of validTiles of one size
 * For performance this class does not make sure that
 * all tiles are unique
 */
class TileSet {
    internal var n: Int = 0
    internal var m: Int = 0
    var k: Int = 0
    /**
     * Do not modify this set externally
     */
    val validTiles: ArrayList<Tile>

    internal val isEmpty: Boolean
        get() = validTiles.isEmpty()

    constructor(file: File) {
        if (!file.exists() || !file.isFile) {
            throw IllegalArgumentException("File does not exist or it is not a file")
        }
        var size = 0
        Scanner(FileInputStream(file)).use { scanner ->
            n = scanner.nextInt()
            m = scanner.nextInt()
            k = scanner.nextInt()
            size = scanner.nextInt()
        }
        if (n == 0) {
            throw IllegalArgumentException("Could not open file")
        }
        validTiles = ArrayList(size)
        BufferedReader(FileReader(file)).use { reader ->
            var i = 0L
            val n = n.toLong()
            val m = m.toLong()
            var grid = OpenBitSet(n * m)
            skipTwoLines(reader)
            var r = reader.read()
            while (r != -1) {
                val c = r.toChar()
                if (c == '1' || c == '0') {
                    if (c == '1') {
                        grid.set(i)
                    }
                    i++
                    if (i == m * n) {
                        validTiles.add(Tile(grid, this.n, this.m, k))
                        i = 0
                        grid = OpenBitSet(n * m)
                    }
                }
                r = reader.read()
            }
            if (size != validTiles.size) {
                throw IllegalArgumentException("File contains less tiles that it states in the beginning of the file")
            }
        }
    }

    private fun skipTwoLines(reader: BufferedReader) {
        var r = reader.read()
        var linesCount = 0
        while (r != -1) {
            val c = r.toChar()
            if (c == '\n') {
                linesCount++
                if (linesCount == 2) {
                    return
                }
            }
            r = reader.read()
        }
    }

    internal constructor(validTiles: Collection<Tile>) {
        val someTile = validTiles.iterator().next()
        n = someTile.n
        m = someTile.m
        k = someTile.k
        this.validTiles = ArrayList()
        this.validTiles.addAll(validTiles)
    }

    val size: Int
        get() = validTiles.size

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (tile in this.validTiles) {
            stringBuilder.append(tile).append("\n")
        }
        return stringBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other is TileSet) {
            if (validTiles.size != other.validTiles.size) {
                return false
            }
            val count = validTiles.count { tile -> other.validTiles.contains(tile) }
            return validTiles.size == count
        }
        return false
    }

    override fun hashCode(): Int {
        return validTiles.hashCode()
    }
}

/**
 * Generates set of possibly-valid validTiles
 */
fun generatePossiblyValidTiles(n: Int, m: Int, k: Int): Set<Tile> {

    /* It is meaningless to make following piece of code recursive
     * because all candidate tiles must be placed in possiblyValidTiles set
     * and it is not possible to reduce memory consumption using recursive method
     */
    val possiblyValidTiles = HashSet<Tile>()
    possiblyValidTiles.add(Tile(n, m, k))

    for (i in 0 until n) {
        for (j in 0 until m) {
            val newTileIS = possiblyValidTiles
                    .filter { it.canBeI(i, j) }
                    .map { Tile(it, i, j) }
                    .toMutableSet()
            possiblyValidTiles.addAll(newTileIS)
            newTileIS.clear()
        }
    }
    return possiblyValidTiles
}
