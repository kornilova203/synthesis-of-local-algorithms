package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.SatSolverProcessManager
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import java.util.*
import kotlin.collections.HashSet

class Tile {
    private val grid: Array<BooleanArray>
    val k: Int
    val n: Int
    val m: Int

    /**
     * Check if this tile is valid
     */
    fun isValid(satManager: SatSolverProcessManager): Boolean {
        val clauses = toDimacsIsTileValid()
        val solution = satManager.solveWithSatSolver(clauses, (n + k * 2) * (m + k * 2))
        if (solution != null) {
            return true
        }
        return false
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
                n = tile.n - 1
                m = tile.m
            }
            Tile.Part.W, Tile.Part.E -> {
                n = tile.n
                m = tile.m - 1
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

    constructor(tile: Tile, position: POSITION) {
        k = tile.k
        n = tile.n - 2
        m = tile.m - 2
        grid = Array(n) { BooleanArray(m) }
        when (position) {
            POSITION.N -> for (i in 0 until n) {
                System.arraycopy(tile.grid[i], 1, grid[i], 0, m)
            }
            POSITION.E -> for (i in 0 until n) {
                System.arraycopy(tile.grid[i + 1], 2, grid[i], 0, m)
            }
            POSITION.S -> for (i in 0 until n) {
                System.arraycopy(tile.grid[i + 2], 1, grid[i], 0, m)
            }
            POSITION.W -> for (i in 0 until n) {
                System.arraycopy(tile.grid[i + 1], 0, grid[i], 0, m)
            }
            POSITION.X -> for (i in 0 until n) {
                System.arraycopy(tile.grid[i + 1], 1, grid[i], 0, m)
            }
        }
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
        grid = Array(tile.n) { BooleanArray(tile.m) }
        for (i in 0 until tile.n) {
            System.arraycopy(tile.grid[i], 0, grid[i], 0, tile.m)
        }
        grid[x][y] = true
    }

    constructor(tile: Tile, newN: Int, newM: Int, solution: Set<Int>) {
        k = tile.k
        n = newN
        m = newM
        grid = Array(n) { BooleanArray(m) }
        for (i in (newN - tile.n) / 2 until tile.n + (newN - tile.n) / 2) {
            System.arraycopy(tile.grid[i - (newN - tile.n) / 2], 0, grid[i], (newM - tile.m) / 2, tile.m)
        }
        solution
                .filter { i -> i > 0 }
                .map { id -> getCoordinate(id) }
                .forEach { coordinate -> grid[coordinate.x][coordinate.y] = true }
    }

    /**
     * Clone and expand tile to newN x newM
     */
    constructor(newN: Int, newM: Int, tile: Tile) {
        k = tile.k
        n = newN
        m = newM
        grid = Array(n) { BooleanArray(m) }
        for (i in (newN - tile.n) / 2 until tile.n + (newN - tile.n) / 2) {
            System.arraycopy(tile.grid[i - (newN - tile.n) / 2], 0, grid[i], (newM - tile.m) / 2, tile.m)
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

    /**
     * @return true if grid[x][y] can be an element of an independent set
     */
    fun canBeI(x: Int, y: Int): Boolean {
        if (grid[x][y]) { // if already I
            return true
        }
        val endX = Math.min(n - 1, x + k)
        val endY = Math.min(m - 1, y + k)
        for (i in Math.max(0, x - k)..endX) {
            (Math.max(0, y - k)..endY)
                    .filter { Math.abs(i - x) + Math.abs(it - y) <= k && grid[i][it] }
                    .forEach { return false }
        }
        return true
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until n) {
            for (j in 0 until m) {
                stringBuilder.append(if (grid[i][j]) 1 else 0).append(if (j == m - 1) "" else " ")
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    /**
     * @return next coordinate which does not belong to internal tile
     * or null if it was last tile
     */
    fun getNextBorderCoordinate(curCoordinate: Coordinate): Coordinate? {
        var x = curCoordinate.x
        var y = curCoordinate.y
        if (x == n - 1 && y == m - 1) { // if last coordinate
            return null
        }
        if (x < n - 1) { // if not the last in this row
            x++
            if (y >= k && y < m - k) {
                if (x >= k && x < n - k) { // if inside internal tile
                    x = n - k
                }
            }
        } else {
            y++
            x = 0
        }
        return Coordinate(x, y)
    }

    fun getId(x: Int, y: Int): Int {
        return x * m + y + 1
    }

    private fun getCoordinate(id: Int): Coordinate {
        val num = id - 1
        return Coordinate(num / m, num % m)
    }

    internal fun toDimacsIsTileValid(): Set<Set<Int>> {
        val newN = n + k * 2
        val newM = n + k * 2
        val biggerTile = Tile(newN, newM, this)
        val intersection = TileIntersection(n, m, newN, newM)

        val clauses = HashSet<Set<Int>>()

        for (x in 0 until newN) {
            for (y in 0 until newM) {
                if (intersection.isInside(x, y)) {
                    clauses.add(cellMustStayTheSame(x, y, biggerTile))
                    if (biggerTile.isI(x, y)) {
                        clauses.addAll(allNeighboursMustBeZero(x, y, biggerTile, newN, newM, k, intersection))
                    } else if (biggerTile.canBeI(x, y) && neighbourhoodIsInsideTile(x, y, newN, newM, k)) {
                        clauses.add(atLeastOneNeighbourMustBeOne(x, y, biggerTile, newN, newM, k, intersection))
                    }

                } else {
                    if (biggerTile.canBeI(x, y)) {
                        ifCenterIsOneAllOtherAreNot(x, y, biggerTile, clauses, newN, newM, k, intersection)
                        if (neighbourhoodIsInsideTile(x, y, newN, newM, k)) {
                            val clause = atLeastOneNeighbourMustBeOne(x, y, biggerTile, newN, newM, k, intersection)
                            clause.add(biggerTile.getId(x, y)) // center may also be in IS
                            clauses.add(clause)
                        }
                    } // there is not else branch because if internal cell is in IS then all neighbours are zero
                }
            }
        }
        return clauses
    }

    companion object {
        enum class Expand {
            HEIGHT,
            WIDTH
        }

        fun getAllPossibleExtensions(tile: Tile, side: Expand): Set<Tile> {
            if (side == Expand.WIDTH) {
                val newN = tile.n
                val newM = tile.m + 1
                val extensions = HashSet<Tile>()
                extensions.add(Tile(newN, newM, tile))
                for (i in 0 until tile.n) {
                    val newExtensions = HashSet<Tile>()
                    extensions
                            .filter { it.canBeI(i, tile.m) }
                            .mapTo(newExtensions) { Tile(it, i, tile.m) }
                    extensions.addAll(newExtensions)
                }
                return extensions
            } else {
                val newN = tile.n + 1
                val newM = tile.m
                val extensions = HashSet<Tile>()
                extensions.add(Tile(newN, newM, tile))
                for (j in 0 until tile.m) {
                    val newExtensions = HashSet<Tile>()
                    extensions
                            .filter { it.canBeI(tile.n, j) }
                            .mapTo(newExtensions) { Tile(it, tile.n, j) }
                    extensions.addAll(newExtensions)
                }
                return extensions
            }
        }

        private fun neighbourhoodIsInsideTile(x: Int, y: Int, n: Int, m: Int, k: Int): Boolean {
            if (x - k < 0 || y - k < 0) {
                return false
            }
            if (x + k >= n || y + k >= m) {
                return false
            }
            return true
        }

        private fun cellMustStayTheSame(x: Int, y: Int, biggerTile: Tile): Set<Int> {
            return if (biggerTile.isI(x, y)) {
                hashSetOf(biggerTile.getId(x, y))
            } else {
                hashSetOf(-biggerTile.getId(x, y))
            }
        }

        private fun allNeighboursMustBeZero(x: Int, y: Int, biggerTile: Tile, newN: Int, newM: Int, k: Int, intersection: TileIntersection): Set<Set<Int>> {
            val clauses = HashSet<Set<Int>>()
            for (i in x - k..x + k) {
                (y - k..y + k)
                        .filter { j ->
                            !intersection.isInside(i, j) && // cells inside are already ok
                                    i >= 0 && j >= 0 && i < newN && j < newM &&
                                    !(i == x && j == y) && // not center
                                    Math.abs(x - i) + Math.abs(y - j) <= k
                        }
                        .mapTo(clauses) { j -> hashSetOf(-biggerTile.getId(i, j)) } // must be zero
            }
            return clauses
        }

        /**
         * If (x, y) is 1 then non of it's neighbours is 1
         */
        private fun ifCenterIsOneAllOtherAreNot(x: Int, y: Int, biggerTile: Tile, clauses: HashSet<Set<Int>>,
                                                newN: Int, newM: Int, k: Int, intersection: TileIntersection) {
            for (i in x - k..x + k) {
                (y - k..y + k)
                        .filter { j ->
                            !intersection.isInside(i, j) && // cells inside cannot be changed
                                    i >= 0 && j >= 0 && i < newN && j < newM && !(i == x && j == y) && // not center
                                    Math.abs(x - i) + Math.abs(y - j) <= k
                        }
                        .mapTo(clauses) { j -> hashSetOf(-biggerTile.getId(x, y), -biggerTile.getId(i, j)) }
            }
        }

        private fun atLeastOneNeighbourMustBeOne(x: Int, y: Int, biggerTile: Tile, newN: Int,
                                                 newM: Int, k: Int, intersection: TileIntersection): HashSet<Int> {
            val clause = HashSet<Int>()
            for (i in x - k..x + k) {
                (y - k..y + k)
                        .filter { j ->
                            !intersection.isInside(i, j) && // inside tiles cannot be changed
                                    i >= 0 && j >= 0 && i < newN && j < newM &&
                                    !(i == x && j == y) && // not center
                                    Math.abs(x - i) + Math.abs(y - j) <= k
                        }
                        .mapTo(clause) { j -> biggerTile.getId(i, j) }
            }
            return clause
        }
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
        if (other.n != n || other.m != m) {
            throw IllegalArgumentException("Size of tile is different")
        }
        for (i in 0 until n) {
            (0 until m)
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

    fun isI(i: Int, j: Int): Boolean {
        return grid[i][j]
    }
}
