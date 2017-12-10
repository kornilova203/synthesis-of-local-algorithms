package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.isSolvable
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import org.apache.lucene.util.OpenBitSet
import java.util.*

private fun getIndex(x: Long, y: Long, m: Int): Long {
    return x * m + y
}

open class Tile {
    private val grid: OpenBitSet
    val k: Int
    val n: Int
    val m: Int
    /**
     * Optional field. It is used for building clauses
     * So it is not needed to search id in map
     * 0 means that id was not set
     */
    var id = 0

    /**
     * Check if this tile is valid
     */
    fun isValid(): Boolean {
        val clauses = toDimacsIsTileValid() ?: return false
        return isSolvable(clauses)
    }

    private fun getIndex(x: Int, y: Int): Long {
        return (x * m + y).toLong()
    }

    private fun getIndex(x: Long, y: Long): Long {
        return x * m + y
    }

    /**
     * Creates a subtile of size
     * tile.n - 1 x tile.m
     * or
     * tile.n x tile.m - 1
     */
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
        grid = OpenBitSet((n * m).toLong())
        when (part) {
            Tile.Part.N -> // copy first tile.n - 1 rows
                (0L until n * m).filter { i -> tile.grid.get(i) }
                        .forEach { i -> this.grid.set(i) }
            Tile.Part.S -> // copy last tile.n - 1 rows
                (0L until n * m).filter { i -> tile.grid.get(i + m) }
                        .forEach { i -> this.grid.set(i) }
            Tile.Part.W ->
                (0L until n * m).filter { i -> tile.grid.get(tile.getIndex(i / m, i % m)) }
                        .forEach { i -> this.grid.set(i) }
            Tile.Part.E ->
                (0L until n * m).filter { i -> tile.grid.get(tile.getIndex(i / m, i % m) + 1) }
                        .forEach { i -> this.grid.set(i) }
        }
        this.n = n
        this.m = m
    }

    /**
     * Created a subtile of size tile.n - 2 x tile.m - 2
     */
    constructor(tile: Tile, position: POSITION) {
        k = tile.k
        n = tile.n - 2
        m = tile.m - 2
        grid = OpenBitSet((n * m).toLong())
        when (position) {
            POSITION.N ->
                (0L until n * m).filter { i -> tile.grid.get(tile.getIndex(i / m, i % m + 1)) }
                        .forEach { i -> grid.set(i) }
            POSITION.E ->
                (0L until n * m).filter { i -> tile.grid.get(tile.getIndex(i / m + 1, i % m + 2)) }
                        .forEach { i -> grid.set(i) }
            POSITION.S ->
                (0L until n * m).filter { i -> tile.grid.get(tile.getIndex(i / m + 2, i % m + 1)) }
                        .forEach { i -> grid.set(i) }
            POSITION.W ->
                (0L until n * m).filter { i -> tile.grid.get(tile.getIndex(i / m + 1, i % m)) }
                        .forEach { i -> grid.set(i) }
            POSITION.X ->
                (0L until n * m).filter { i -> tile.grid.get(tile.getIndex(i / m + 1, i % m + 1)) }
                        .forEach { i -> grid.set(i) }
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
        grid = OpenBitSet((n * m).toLong())
        this.k = k
    }

    /**
     * Clone tile and set `grid[x][y]` to true
     */
    constructor(tile: Tile, x: Int, y: Int) {
        k = tile.k
        n = tile.n
        m = tile.m
        grid = tile.grid.clone() as OpenBitSet
        grid.set(getIndex(x, y))
    }

    /**
     * Clone and expand tile to newN x newM
     */
    constructor(newN: Int, newM: Int, tile: Tile) {
        k = tile.k
        n = newN
        m = newM
        grid = OpenBitSet((newN * newM).toLong())
        (0L until tile.n * tile.m).filter { i -> tile.grid.get(i) }
                .forEach { i -> grid.set(getIndex(i / tile.m + (newN - tile.n) / 2, i % tile.m + (newM - tile.m) / 2)) }
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
        grid = OpenBitSet((n * m).toLong())
        for (i in 0 until n) {
            (0 until m)
                    .filter { j -> independentSet[(x - n / 2 + i + sizeN) % sizeN][(y - m / 2 + j + sizeM) % sizeM] }
                    .forEach { j -> grid.set(getIndex(i, j)) }
        }
    }

    constructor(string: String, k: Int) {
        this.k = k
        val lines = string.split("\n").filter { it != "" }
        n = lines.size
        m = calculateM(lines)
        grid = OpenBitSet((n * m).toLong())
        lines.forEachIndexed { i, line ->
            var j = 0
            line.forEach { c ->
                if (c == '1') {
                    grid.set(getIndex(i, j))
                    j++
                } else if (c == '0') {
                    j++
                }
            }
        }
    }

    constructor(grid: OpenBitSet, n: Int, m: Int, k: Int) {
        this.grid = grid
        this.n = n
        this.m = m
        this.k = k
    }

    private fun calculateM(lines: List<String>): Int {
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

    /**
     * @return true if grid[x][y] can be an element of an independent set
     */
    fun canBeI(x: Int, y: Int): Boolean {
        if (grid.get(getIndex(x, y))) { // if already I
            return true
        }
        val endX = Math.min(n - 1, x + k)
        val endY = Math.min(m - 1, y + k)
        for (i in Math.max(0, x - k)..endX) {
            (Math.max(0, y - k)..endY)
                    .filter { j -> Math.abs(i - x) + Math.abs(j - y) <= k && grid.get(getIndex(i, j)) }
                    .forEach { return false }
        }
        return true
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until n) {
            for (j in 0 until m) {
                stringBuilder.append(if (grid.get(getIndex(i, j))) 1 else 0).append(if (j == m - 1) "" else " ")
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    fun getId(x: Int, y: Int): Int {
        return x * m + y + 1
    }

    internal fun toDimacsIsTileValid(): Set<Set<Int>>? {
        val newN = n + k * 2
        val newM = m + k * 2
        val biggerTile = Tile(newN, newM, this)
        val intersection = TileIntersection(newN, newM, n, m)

        val clauses = HashSet<Set<Int>>()

        for (x in 0 until newN) {
            for (y in 0 until newM) {
                if (intersection.isInside(x, y)) {
                    clauses.add(cellMustStayTheSame(x, y, biggerTile))
                    if (biggerTile.isI(x, y)) {
                        clauses.addAll(allNeighboursMustBeZero(x, y, biggerTile, newN, newM, k, intersection))
                    } else if (biggerTile.canBeI(x, y) && neighbourhoodIsInsideTile(x, y, newN, newM, k)) {
                        val clause = atLeastOneNeighbourMustBeOne(x, y, biggerTile, newN, newM, k, intersection)
                        if (clause.size == 0) { // this cannot be satisfied
                            return null
                        }
                        clauses.add(clause)
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
            return false
        }
        for (i in 0 until n) {
            (0 until m)
                    .filter { j -> grid.get(getIndex(i, j)) != other.grid.get(getIndex(i, j)) }
                    .forEach { return false }
        }
        return true
    }

    override fun hashCode(): Int = grid.hashCode()

    enum class Part {
        N,
        S,
        W,
        E
    }

    fun isI(i: Int, j: Int): Boolean {
        return grid.get(getIndex(i, j))
    }

    /**
     * Creates a new tile that equals to the original tile rotated clockwise
     */
    fun rotate(): Tile {
        val rotatedGrid = OpenBitSet(n * m.toLong())
        (0L until n).forEach { i ->
            (0L until m).filter { j ->
                grid.get(getIndex(i, j))
            }.forEach { j ->
                rotatedGrid.set(getIndex(j, n - i - 1, n))
            }
        }
        return Tile(rotatedGrid, m, n, k)
    }
}
