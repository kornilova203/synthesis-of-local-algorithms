package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.IndependentSetTile
import org.apache.lucene.util.OpenBitSet
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

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
    val validTiles: ArrayList<IndependentSetTile>

    internal val isEmpty: Boolean
        get() = validTiles.isEmpty()

    constructor(file: File) {
        if (!file.exists() || !file.isFile) {
            throw IllegalArgumentException("File does not exist or it is not a file")
        }
        var validTiles: ArrayList<IndependentSetTile>? = null
        BufferedReader(FileReader(file)).use { reader ->
            val firstLine = reader.readLine()
            val parts = firstLine.split(" ")
            n = Integer.parseInt(parts[0])
            m = Integer.parseInt(parts[1])
            k = Integer.parseInt(parts[2])
            val size = Integer.parseInt(reader.readLine())
            validTiles = ArrayList(size)
            for (i in 0 until size) {
                val grid = parseSet(reader, n, m)
                validTiles!!.add(IndependentSetTile(n, m, k, grid))
            }
            if (size != validTiles!!.size) {
                throw IllegalArgumentException("File contains less tiles that it states in the beginning of the file")
            }
        }
        if (validTiles == null) {
            throw IllegalArgumentException("Cannot read files from file")
        }
        this.validTiles = validTiles!!
    }

    companion object {
        fun parseSet(reader: BufferedReader, n: Int, m: Int): OpenBitSet {
            val grid = OpenBitSet(n * m.toLong())
            var i = 0L
            while (i < n * m) {
                val c = reader.read().toChar()
                if (c == '1' || c == '0') {
                    if (c == '1') {
                        grid.set(i)
                    }
                    i++
                }
            }
            return grid
        }
    }

    internal constructor(validTiles: Collection<IndependentSetTile>) {
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
