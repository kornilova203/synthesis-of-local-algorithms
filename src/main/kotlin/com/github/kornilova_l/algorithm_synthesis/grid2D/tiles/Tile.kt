package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import org.apache.lucene.util.OpenBitSet
import java.util.*

open class Tile(val n: Int,
                    val m: Int,
                    protected val grid: OpenBitSet) {

    constructor(independentSet: Array<BooleanArray>, x: Int, y: Int, n: Int, m: Int) : this(n, m, getGrid(independentSet, x, y, n, m))

    open fun isValid(): Boolean = true

    /**
     * @return true if grid[x][y] can be included.
     * so tile does not become invalid if grid[x][y] is included and
     * it has a change to become a valid tile if we include some other cells.
     */
    open fun canBeIncluded(x: Int, y: Int): Boolean = true

    /**
     * Creates a new tile that equals to the original tile rotated clockwise
     */
    open fun rotate(): Tile = Tile(m, n, rotateGrid(grid))

    protected fun rotateGrid(grid: OpenBitSet): OpenBitSet {
        val rotatedGrid = OpenBitSet(n * m.toLong())
        for (i in 0L until n) {
            for (j in 0L until m) {
                if (grid.get(getIndex(i, j))) {
                    rotatedGrid.set(getIndex(j, n - i - 1, n))
                }
            }
        }
        return rotatedGrid
    }

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

    /**
     * Create instance of class and copy all extra information.
     */
    protected open fun createInstanceOfClass(newN: Int, newM: Int, grid: OpenBitSet): Tile = Tile(newN, newM, grid)

    fun cloneAndExpand(newN: Int, newM: Int): Tile {
        val grid = OpenBitSet((newN * newM).toLong())
        for (i in 0L until n * m) {
            if (grid.get(i)) {
                grid.set(getIndex(i / m + (newN - n) / 2, i % m + (newM - m) / 2, newM))
            }
        }
        return createInstanceOfClass(newN, newM, grid)
    }

    /**
     * Clone tile and set `grid[x][y]` to true
     */
    fun cloneAndChange(x: Int, y: Int): Tile {
        val grid = grid.clone() as OpenBitSet
        grid.set(getIndex(x.toLong(), y.toLong(), m))
        return createInstanceOfClass(n, m, grid)
    }

    /**
     * Creates a subtile of size
     * tile.n - 1 x tile.m
     * or
     * tile.n x tile.m - 1
     */
    fun clonePart(part: Part): Tile {
        val newN = getNewN(part)
        val newM = getNewM(part)
        return createInstanceOfClass(newN, newM, cloneGridPart(part, newN, newM))
    }

    private fun getNewN(part: Part): Int = when (part) {
        Part.S, Part.N -> n - 1
        Part.W, Part.E -> n
    }

    private fun getNewM(part: Part): Int = when (part) {
        Part.S, Part.N -> m
        Part.W, Part.E -> m - 1
    }

    private fun cloneGridPart(part: Part, newN: Int, newM: Int): OpenBitSet {
        val newGrid = OpenBitSet((newN * newM).toLong())
        when (part) {
            Part.N -> // copy first tile.n - 1 rows
                (0L until newN * newM).filter { i -> grid.get(i) }
                        .forEach { i -> newGrid.set(i) }
            Part.S -> // copy last tile.n - 1 rows
                (0L until newN * newM).filter { i -> grid.get(i + newM) }
                        .forEach { i -> newGrid.set(i) }
            Part.W ->
                (0L until newN * newM).filter { i -> grid.get(getIndex(i / newM, i % newM)) }
                        .forEach { i -> newGrid.set(i) }
            Part.E ->
                (0L until newN * newM).filter { i -> grid.get(getIndex(i / newM, i % newM) + 1) }
                        .forEach { i -> newGrid.set(i) }
        }
        return newGrid
    }

    protected fun getIndex(x: Int, y: Int): Long {
        return (x * m + y).toLong()
    }

    protected fun getIndex(x: Long, y: Long): Long {
        return x * m + y
    }

    companion object {

        private fun getGrid(independentSet: Array<BooleanArray>, x: Int, y: Int, n: Int, m: Int): OpenBitSet {
            if (independentSet.size < n || independentSet[0].size < m) {
                throw IllegalArgumentException("Grid is too small")
            }
            val sizeN = independentSet.size
            val sizeM = independentSet[0].size
            val grid = OpenBitSet((n * m).toLong())
            for (i in 0 until n) {
                for (j in 0 until m) {
                    if (independentSet[(x - n / 2 + i + sizeN) % sizeN][(y - m / 2 + j + sizeM) % sizeM]) {
                        grid.set(getIndex(i.toLong(), j.toLong(), m))
                    }
                }
            }
            return grid
        }

        enum class Part {
            N,
            S,
            W,
            E
        }

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