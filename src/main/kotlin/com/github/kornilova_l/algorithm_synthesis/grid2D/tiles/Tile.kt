package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import java.util.*

class Tile {
    private val grid: Array<BooleanArray>
    val k: Int
    private val n: Int
    private val m: Int

    /**
     * Check if this tile is valid
     * 1. Expand tile by k cells on each side
     * 2. Try to generate IS in extended tile
     * such that it will cover all uncovered cells in original tile.
     * This proves that internal tile exist but does not tell us that this particular
     * extended tile also exist.
     */
    val isValid: Boolean
        get() {
            val canBeAddedToIS = HashSet<Coordinate>()
            for (i in 0 until getN()) {
                (0 until getM())
                        .filter { !grid[i][it] && canBeI(i, it) }
                        .mapTo(canBeAddedToIS) { Coordinate(i, it) }
            }
            return if (canBeAddedToIS.isEmpty()) {
                true
            } else isTileValidRecursive(Tile(this),
                    changeCoordinatesForExpanded(canBeAddedToIS, k),
                    Coordinate(0, 0))
        }

    internal constructor(n: Int, m: Int, k: Int, `is`: Set<Coordinate>) {
        this.n = n
        this.m = m
        this.k = k
        grid = Array(n) { BooleanArray(m) }
        for (coordinate in `is`) {
            grid[coordinate.x][coordinate.y] = true
        }
    }

    constructor(tile: Tile, part: Part) {
        k = tile.k
        val n: Int
        val m: Int
        when (part) {
            Tile.Part.S, Tile.Part.N -> {
                n = tile.getN() - 1
                m = tile.getM()
            }
            Tile.Part.W, Tile.Part.E -> {
                n = tile.getN()
                m = tile.getM() - 1
            }
        }
        grid = Array(n) { BooleanArray(m) }
        when (part) {
            Tile.Part.N, Tile.Part.W -> for (i in 0 until n) {
                System.arraycopy(tile.grid[i], 0, grid[i], 0, m)
            }
            Tile.Part.S -> for (i in 0 until n) {
                System.arraycopy(tile.grid[i + 1], 0, grid[i], 0, m)
            }
            Tile.Part.E -> for (i in 0 until n) {
                System.arraycopy(tile.grid[i], 1, grid[i], 0, m)
            }
        }
        this.n = n
        this.m = m
    }

    /**
     * Create an empty tile
     *
     * @param n size
     * @param m size
     * @param k power of graph
     */
    constructor(n: Int, m: Int, k: Int) {
        this.n = n
        this.m = m
        grid = Array(n) { BooleanArray(m) }
        this.k = k
    }

    /**
     * Clone tile and change grid[x][y]
     */
    constructor(tile: Tile, x: Int, y: Int) {
        k = tile.k
        this.n = tile.n
        this.m = tile.m
        grid = Array(tile.getN()) { BooleanArray(tile.getM()) }
        for (i in 0 until tile.getN()) {
            System.arraycopy(tile.grid[i], 0, grid[i], 0, tile.getM())
        }
        grid[x][y] = true
    }

    /**
     * Clone and expand tile by k
     */
    constructor(tile: Tile) {
        k = tile.k
        n = tile.getN() + k * 2
        m = tile.getM() + k * 2
        grid = Array(n) { BooleanArray(m) }
        for (i in k until n - k) {
            System.arraycopy(tile.grid[i - k], 0, grid[i], k, tile.getM())
        }
    }

    constructor(independentSet: Array<BooleanArray>, x: Int, y: Int, n: Int, m: Int, k: Int) {
        if (independentSet.size < n || independentSet[0].size < m) {
            throw IllegalArgumentException("Grid is too small")
        }
        this.n = n
        this.m = m
        this.k = k
        val sizeN = independentSet.size
        val sizeM = independentSet[0].size
        grid = Array(n) { BooleanArray(m) }
        for (i in 0 until n) {
            for (j in 0 until m) {
                grid[i][j] = independentSet[(x - n / 2 + i + sizeN) % sizeN][(y - m / 2 + j + sizeM) % sizeM]
            }
        }
    }


    fun getN(): Int = grid.size

    fun getM(): Int = grid[0].size

    /**
     * @return true if grid[x][y] can be an element of an independent set
     */
    fun canBeI(x: Int, y: Int): Boolean {
        if (grid[x][y]) { // if already I
            return true
        }
        val endX = Math.min(getN() - 1, x + k)
        val endY = Math.min(getM() - 1, y + k)
        for (i in Math.max(0, x - k)..endX) {
            (Math.max(0, y - k)..endY)
                    .filter { Math.abs(i - x) + Math.abs(it - y) <= k && grid[i][it] }
                    .forEach { return false }
        }
        return true
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until getN()) {
            for (j in 0 until getM()) {
                stringBuilder.append(if (grid[i][j]) 1 else 0).append(if (j == getM() - 1) "" else " ")
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    /**
     * @return true if it is possible to generate expanded tile
     * which cover all in uncovered cells in internal tile.
     */
    private fun isTileValidRecursive(expandedTile: Tile,
                                     canBeAddedToIS: MutableSet<Coordinate>,
                                     curCoordinate: Coordinate?): Boolean {
        if (curCoordinate == null) { // if on previous step there was last coordinate
            return false
        }
        if (isTileValidRecursive(expandedTile, canBeAddedToIS, expandedTile.getNextBorderCoordinate(curCoordinate))) {
            return true
        }
        if (expandedTile.canBeI(curCoordinate.x, curCoordinate.y)) { // it cell can be added to the tile
            val covered = expandedTile.getCovered(curCoordinate, canBeAddedToIS)
            if (covered != null) {
                canBeAddedToIS.removeAll(covered)
                if (canBeAddedToIS.isEmpty()) { // if covers all
                    return true
                }
                val newTile = Tile(expandedTile, curCoordinate.x, curCoordinate.y)
                val res = isTileValidRecursive(newTile, canBeAddedToIS, newTile.getNextBorderCoordinate(curCoordinate))
                canBeAddedToIS.addAll(covered)
                return res
            }
        }
        return false
    }

    /**
     * @return next coordinate which does not belong to internal tile
     * or null if it was last tile
     */
    fun getNextBorderCoordinate(curCoordinate: Coordinate): Coordinate? {
        var x = curCoordinate.x
        var y = curCoordinate.y
        if (x == getN() - 1 && y == getM() - 1) { // if last coordinate
            return null
        }
        if (x < getN() - 1) { // if not the last in this row
            x++
            if (y >= k && y < getM() - k) {
                if (x >= k && x < getN() - k) { // if inside internal tile
                    x = getN() - k
                }
            }
        } else {
            y++
            x = 0
        }
        return Coordinate(x, y)
    }

    /**
     * Returns true if (x, y) covers at least one point in cells
     */
    private fun getCovered(coordinate: Coordinate, cells: Set<Coordinate>): Set<Coordinate>? {
        val x = coordinate.x
        val y = coordinate.y
        var covered: MutableSet<Coordinate>? = null
        for (cell in cells) {
            if (Math.abs(x - cell.x) + Math.abs(y - cell.y) <= k) {
                if (covered == null) {
                    covered = HashSet()
                }
                covered.add(cell)
            }
        }
        return covered
    }

    /**
     * Add k to each coordinate
     */
    private fun changeCoordinatesForExpanded(canBeAddedToIS: Set<Coordinate>, k: Int): MutableSet<Coordinate> {
        return canBeAddedToIS
                .map { Coordinate(it.x + k, it.y + k) }
                .toMutableSet()
    }

    /**
     * For tests
     */
    override fun equals(other: Any?): Boolean {
        if (other is String) {
            return other == toString()
        }
        if (other !is Tile) {
            return false
        }
        if (other.getN() != getN() || other.getM() != getM()) {
            throw IllegalArgumentException("Size of tile is different")
        }
        for (i in 0 until getN()) {
            (0 until getM())
                    .filter { grid[i][it] != other.grid[i][it] }
                    .forEach { return false }
        }
        return true
    }

    override fun hashCode(): Int = Arrays.deepHashCode(grid)

    enum class Part {
        N,
        S,
        W,
        E
    }

    class Coordinate(internal val x: Int, internal val y: Int) {

        override fun toString(): String = "($x, $y)"

        override fun equals(other: Any?): Boolean {
            return if (other !is Coordinate) {
                false
            } else x == other.x && y == other.y
        }

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            return result
        }
    }
}
