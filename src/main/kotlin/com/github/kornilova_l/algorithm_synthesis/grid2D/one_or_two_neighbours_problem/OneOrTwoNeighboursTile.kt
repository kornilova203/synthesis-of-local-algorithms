package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.parseSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import org.apache.lucene.util.OpenBitSet
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


/**
 * In each square of 4 cells there must be 1 or 2 included cells.
 * Examples:
 * valid tile:
 * 1 0 0
 * 0 1 0
 *
 * invalid tiles:
 * 0 0
 * 0 0
 *
 * 0 1
 * 1 1
 */
class OneOrTwoNeighboursTile(n: Int, m: Int, grid: OpenBitSet) : BinaryTile(n, m, grid) {

    /**
     * Create an empty tile
     *
     * @param n size
     * @param m size
     */
    constructor(n: Int, m: Int) : this(n, m, OpenBitSet((n * m).toLong()))

    override fun createInstanceOfClass(newN: Int, newM: Int, grid: OpenBitSet): BinaryTile = OneOrTwoNeighboursTile(newN, newM, grid)

    override fun rotate(): OneOrTwoNeighboursTile = OneOrTwoNeighboursTile(m, n, rotateGrid(grid))

    companion object {
        fun createInstance(string: String): OneOrTwoNeighboursTile {
            val lines = string.split("\n").filter { it != "" }
            val n = lines.size
            val m = calculateM(lines)
            return OneOrTwoNeighboursTile(n, m, parseGrid(n, m, lines))
        }

        fun parseTiles(file: File): Set<OneOrTwoNeighboursTile> {
            if (!file.exists() || !file.isFile) {
                throw IllegalArgumentException("File does not exist or it is not a file")
            }
            BufferedReader(FileReader(file)).use { reader ->
                val firstLine = reader.readLine()
                val parts = firstLine.split(" ")
                val n = Integer.parseInt(parts[0])
                val m = Integer.parseInt(parts[1])
                val size = Integer.parseInt(reader.readLine())
                val validTiles = HashSet<OneOrTwoNeighboursTile>(size)
                for (i in 0 until size) {
                    val grid = parseSet(reader, n, m)
                    validTiles.add(OneOrTwoNeighboursTile(n, m, grid))
                }
                if (size != validTiles.size) {
                    throw IllegalArgumentException("File contains less tiles that it states in the beginning of the file")
                }
                return validTiles
            }
        }
    }

    /**
     * Simply check each square
     */
    override fun isValid(): Boolean {
        for (i in 0 until n - 1) {
            for (j in 0 until m - 1) {
                val calcIncluded = calcIncluded(i, j)
                if (calcIncluded != 1 && calcIncluded != 2) {
                    return false
                }
            }
        }
        return true
    }

    override fun canBeIncluded(x: Int, y: Int): Boolean {
        if (grid.get(getIndex(x, y))) {
            throw IllegalArgumentException("Cell is already included")
        }
        /* we need to check four squares */
        return calcIncluded(x - 1, y - 1) < 2 &&
                calcIncluded(x, y - 1) < 2 &&
                calcIncluded(x - 1, y) < 2 &&
                calcIncluded(x, y) < 2
    }

    /**
     * @param i coordinate of top left corner
     * @param j coordinate of top left corner
     */
    private fun calcIncluded(i: Int, j: Int): Int {
        var calcIncluded = 0
        if (i >= 0 && j >= 0) {
            if (grid.get(getIndex(i, j))) {
                calcIncluded++
            }
        }
        if (j >= 0 && i < n) {
            if (grid.get(getIndex(i + 1, j))) {
                calcIncluded++
            }
        }
        if (i >= 0 && j < m) {
            if (grid.get(getIndex(i, j + 1))) {
                calcIncluded++
            }
        }
        if (i < n && j < m) {
            if (grid.get(getIndex(i + 1, j + 1))) {
                calcIncluded++
            }
        }
        return calcIncluded
    }
}