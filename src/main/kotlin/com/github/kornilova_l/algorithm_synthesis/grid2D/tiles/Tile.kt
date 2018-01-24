package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import org.apache.lucene.util.OpenBitSet
import java.util.*


abstract class Tile(val n: Int,
                    val m: Int,
                    protected val grid: OpenBitSet) {

    abstract fun isValid(): Boolean

    /**
     * @return true if grid[x][y] can be included.
     * so tile does not become invalid if grid[x][y] is included and
     * it has a change to become a valid tile if we include some other cells.
     */
    abstract fun canBeIncluded(x: Int, y: Int): Boolean

    /**
     * Expand tile.
     * if side == HEIGHT then add extra row to the bottom of tile
     * if side == WIDTH then add extra column to the left side of tile
     * You do not need to check if expanded tiles are valid.
     * So the easiest way to get expanded tiles is to create a new tile
     * for each possible row / column. So if column contains 5 cells then
     * there will be 32 expanded tiles.
     * It is good not to create tiles that are definitely not valid.
     */
    fun getAllExpandedTiles(side: Expand): Set<Tile> {
        val extensions = HashSet<Tile>()
        if (side == Expand.WIDTH) {
            val newN = n
            val newM = m + 1
            extensions.add(this.cloneAndExpand(newN, newM))
            for (i in 0 until n) {
                val newExtensions = HashSet<Tile>()
                for (extension in extensions) {
                    if (extension.canBeIncluded(i, m)) {
                        newExtensions.add(extension.cloneAndChange(i, m))
                    }
                }
                extensions.addAll(newExtensions)
            }
            return extensions
        } else {
            val newN = n + 1
            val newM = m
            extensions.add(this.cloneAndExpand(newN, newM))
            for (j in 0 until m) {
                val newExtensions = HashSet<Tile>()
                for (extension in extensions) {
                    if (extension.canBeIncluded(n, j)) {
                        newExtensions.add(extension.cloneAndChange(n, j))
                    }
                }
                extensions.addAll(newExtensions)
            }
            return extensions
        }
    }

    fun getId(x: Int, y: Int): Int {
        return x * m + y + 1
    }

    fun isI(i: Int, j: Int): Boolean {
        return grid.get(getIndex(i, j))
    }

    abstract fun cloneAndExpand(newN: Int, newM: Int): Tile

    /**
     * Clone tile and set `grid[x][y]` to true
     */
    abstract fun cloneAndChange(x: Int, y: Int): Tile

    protected fun getIndex(x: Int, y: Int): Long {
        return (x * m + y).toLong()
    }

    protected fun getIndex(x: Long, y: Long): Long {
        return x * m + y
    }

    companion object {
        enum class Expand {
            HEIGHT,
            WIDTH
        }

        fun parseGrid(n: Int, m: Int, lines: List<String>): OpenBitSet {
            val grid = OpenBitSet((n * m).toLong())
            lines.forEachIndexed { i, line ->
                var j = 0
                line.forEach { c ->
                    if (c == '1') {
                        grid.set(getIndex(i.toLong(), j.toLong(), m))
                        j++
                    } else if (c == '0') {
                        j++
                    }
                }
            }
            return grid
        }

        fun getIndex(x: Long, y: Long, m: Int): Long {
            return x * m + y
        }

        fun calculateM(lines: List<String>): Int {
            var calcM = 0
            lines.first().forEach { c ->
                if (c == '1' || c == '0') {
                    calcM++
                }
            }
            val m = calcM
            /* check that all lines have the same number of cells */
            lines.forEach { line ->
                calcM = 0
                line.forEach { c ->
                    if (c == '1' || c == '0') {
                        calcM++
                    }
                }
                if (calcM != m) {
                    throw IllegalArgumentException("Rows have different number of cells")
                }
            }
            return m
        }
    }
}