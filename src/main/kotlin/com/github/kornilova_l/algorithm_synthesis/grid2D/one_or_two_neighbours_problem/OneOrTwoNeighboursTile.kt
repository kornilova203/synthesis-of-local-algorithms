package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import org.apache.lucene.util.OpenBitSet


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
class OneOrTwoNeighboursTile(n: Int, m: Int, grid: OpenBitSet) : Tile(n, m, grid) {

    override fun cloneAndChange(x: Int, y: Int): Tile {
        val grid = grid.clone() as OpenBitSet
        grid.set(getIndex(x.toLong(), y.toLong(), m))
        return OneOrTwoNeighboursTile(n, m, grid)
    }

    override fun cloneAndExpand(newN: Int, newM: Int): Tile {
        val grid = OpenBitSet((newN * newM).toLong())
        for (i in 0L until n * m) {
            if (grid.get(i)) {
                grid.set(getIndex(i / m + (newN - n) / 2, i % m + (newM - m) / 2, newM))
            }
        }
        return OneOrTwoNeighboursTile(newN, newM, grid)
    }

    companion object {
        fun createInstance(string: String): OneOrTwoNeighboursTile {
            val lines = string.split("\n").filter { it != "" }
            val n = lines.size
            val m = calculateM(lines)
            return OneOrTwoNeighboursTile(n, m, parseGrid(n, m, lines))
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