package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.SatSolver
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import gnu.trove.set.hash.TIntHashSet
import org.apache.lucene.util.OpenBitSet
import java.util.*


open class IndependentSetTile(n: Int, m: Int, val k: Int, grid: OpenBitSet) : Tile(n, m, grid) {

    /**
     * Check if this tile is valid
     */
    override fun isValid(): Boolean {
        val satSolver = SatSolver()
        if (!initSatSolverIsTileValid(satSolver)) {
            return false
        }
        return satSolver.isSolvable()
    }

    companion object {

        /**
         * Creates a subtile of size
         * tile.n - 1 x tile.m
         * or
         * tile.n x tile.m - 1
         */
        fun createInstance(tile: IndependentSetTile, part: Part): IndependentSetTile {
            val k = tile.k
            val n: Int
            val m: Int
            when (part) {
                IndependentSetTile.Part.S, IndependentSetTile.Part.N -> {
                    n = tile.n - 1
                    m = tile.m
                }
                IndependentSetTile.Part.W, IndependentSetTile.Part.E -> {
                    n = tile.n
                    m = tile.m - 1
                }
            }
            val grid = OpenBitSet((n * m).toLong())
            when (part) {
                IndependentSetTile.Part.N -> // copy first tile.n - 1 rows
                    (0L until n * m).filter { i -> tile.grid.get(i) }
                            .forEach { i -> grid.set(i) }
                IndependentSetTile.Part.S -> // copy last tile.n - 1 rows
                    (0L until n * m).filter { i -> tile.grid.get(i + m) }
                            .forEach { i -> grid.set(i) }
                IndependentSetTile.Part.W ->
                    (0L until n * m).filter { i -> tile.grid.get(tile.getIndex(i / m, i % m)) }
                            .forEach { i -> grid.set(i) }
                IndependentSetTile.Part.E ->
                    (0L until n * m).filter { i -> tile.grid.get(tile.getIndex(i / m, i % m) + 1) }
                            .forEach { i -> grid.set(i) }
            }
            return IndependentSetTile(n, m, k, grid)
        }

        /**
         * Created a subtile of size tile.n - 2 x tile.m - 2
         */
        fun createInstance(tile: IndependentSetTile, position: POSITION): IndependentSetTile {
            val k = tile.k
            val n = tile.n - 2
            val m = tile.m - 2
            val grid = OpenBitSet((n * m).toLong())
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
            return IndependentSetTile(n,m,k,grid)
        }

        /**
         * Clone tile and set `grid[x][y]` to true
         */
        fun createInstance(tile: IndependentSetTile, x: Int, y: Int): IndependentSetTile {
            val grid = tile.grid.clone() as OpenBitSet
            grid.set(getIndex(x.toLong(), y.toLong(), tile.m))
            return IndependentSetTile(tile.n, tile.m, tile.k, grid)
        }

        private fun getIndex(x: Long, y: Long, m: Int): Long {
            return x * m + y
        }

        /**
         * Clone and expand tile to newN x newM
         */
        fun createInstance(newN: Int, newM: Int, tile: IndependentSetTile): IndependentSetTile {
            val grid = OpenBitSet((newN * newM).toLong())
            for (i in 0L until tile.n * tile.m) {
                if (tile.grid.get(i)) {
                    grid.set(getIndex(i / tile.m + (newN - tile.n) / 2, i % tile.m + (newM - tile.m) / 2, newM))
                }
            }
            return IndependentSetTile(newN, newM, tile.k, grid)
        }

        fun createInstance(independentSet: Array<BooleanArray>, x: Int, y: Int, n: Int, m: Int, k: Int): IndependentSetTile {
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
            return IndependentSetTile(n, m, k, grid)
        }

        fun createInstance(string: String, k: Int): IndependentSetTile {
            val lines = string.split("\n").filter { it != "" }
            val n = lines.size
            val m = calculateM(lines)
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
            return IndependentSetTile(n, m, k, grid)
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

        enum class Expand {
            HEIGHT,
            WIDTH
        }

        fun getAllPossibleExtensions(tile: IndependentSetTile, side: Expand): Set<IndependentSetTile> {
            if (side == Expand.WIDTH) {
                val newN = tile.n
                val newM = tile.m + 1
                val extensions = HashSet<IndependentSetTile>()
                extensions.add(IndependentSetTile.createInstance(newN, newM, tile))
                for (i in 0 until tile.n) {
                    val newExtensions = HashSet<IndependentSetTile>()
                    for (extension in extensions) {
                        if (extension.canBeI(i, tile.m)) {
                            newExtensions.add(IndependentSetTile.createInstance(extension, i, tile.m))
                        }
                    }
                    extensions.addAll(newExtensions)
                }
                return extensions
            } else {
                val newN = tile.n + 1
                val newM = tile.m
                val extensions = HashSet<IndependentSetTile>()
                extensions.add(IndependentSetTile.createInstance(newN, newM, tile))
                for (j in 0 until tile.m) {
                    val newExtensions = HashSet<IndependentSetTile>()
                    for (extension in extensions) {
                        if (extension.canBeI(tile.n, j)) {
                            newExtensions.add(IndependentSetTile.createInstance(extension, tile.n, j))
                        }
                    }
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

        private fun cellMustStayTheSame(x: Int, y: Int, biggerTile: IndependentSetTile, satSolver: SatSolver) {
            val value = if (biggerTile.isI(x, y)) {
                biggerTile.getId(x, y)
            } else {
                -biggerTile.getId(x, y)
            }
            satSolver.addClause(value)
        }

        private fun allNeighboursMustBeZero(x: Int, y: Int, biggerTile: IndependentSetTile, newN: Int, newM: Int, k: Int,
                                            intersection: TileIntersection, satSolver: SatSolver) {
            for (i in x - k..x + k) {
                for (j in y - k..y + k) {
                    if (!intersection.isInside(i, j) && // cells inside are already ok
                            i >= 0 && j >= 0 && i < newN && j < newM &&
                            !(i == x && j == y) && // not center
                            Math.abs(x - i) + Math.abs(y - j) <= k) {
                        satSolver.addClause(-biggerTile.getId(i, j)) // must be zero
                    }
                }
            }
        }

        /**
         * If (x, y) is 1 then non of it's neighbours is 1
         */
        private fun ifCenterIsOneAllOtherAreNot(x: Int, y: Int, biggerTile: IndependentSetTile, satSolver: SatSolver,
                                                newN: Int, newM: Int, k: Int, intersection: TileIntersection) {
            for (i in x - k..x + k) {
                for (j in y - k..y + k) {
                    if (!intersection.isInside(i, j) && // cells inside cannot be changed
                            i >= 0 && j >= 0 && i < newN && j < newM && !(i == x && j == y) && // not center
                            Math.abs(x - i) + Math.abs(y - j) <= k) {
                        satSolver.addClause(-biggerTile.getId(x, y), -biggerTile.getId(i, j))
                    }
                }
            }
        }

        private fun atLeastOneNeighbourMustBeOne(x: Int, y: Int, biggerTile: IndependentSetTile, newN: Int,
                                                 newM: Int, k: Int, intersection: TileIntersection): TIntHashSet {
            val clause = TIntHashSet()
            for (i in x - k..x + k) {
                for (j in y - k..y + k) {
                    if (!intersection.isInside(i, j) && // inside tiles cannot be changed
                            i >= 0 && j >= 0 && i < newN && j < newM &&
                            !(i == x && j == y) && // not center
                            Math.abs(x - i) + Math.abs(y - j) <= k) {
                        clause.add(biggerTile.getId(i, j))
                    }
                }
            }
            return clause
        }

    }

    /**
     * Create an empty tile
     *
     * @param n size
     * @param m size
     * @param k power of graph
     */
    constructor(n: Int, m: Int, k: Int) : this(n,m,k,OpenBitSet((n * m).toLong()))

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
            for (j in Math.max(0, y - k)..endY) {
                if (Math.abs(i - x) + Math.abs(j - y) <= k && grid.get(getIndex(i, j))) {
                    return false
                }
            }
        }
        return true
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until n) {
            for (j in 0 until m) {
                stringBuilder.append(if (grid.get(getIndex(i, j))) 1 else 0)
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    fun getId(x: Int, y: Int): Int {
        return x * m + y + 1
    }

    /**
     * @return false if tile is definitely not valid and it is not needed to run SAT solver
     */
    private fun initSatSolverIsTileValid(satSolver: SatSolver): Boolean {
        val newN = n + k * 2
        val newM = m + k * 2
        val biggerTile = IndependentSetTile.createInstance(newN, newM, this)
        /* intersection is used to check if we can change a cell */
        val intersection = TileIntersection(newN, newM, n, m)

        /* for each cell in bigger tile */
        for (x in 0 until newN) {
            for (y in 0 until newM) {
                if (intersection.isInside(x, y)) { // if we cannot change cell
                    if (!processCellInsideIntersection(x, y, newN, newM, intersection, biggerTile, satSolver)) {
                        return false
                    }
                } else {
                    processCellOutsideIntersection(x, y, newN, newM, intersection, biggerTile, satSolver)
                }
            }
        }
        return true
    }

    private fun processCellOutsideIntersection(x: Int, y: Int, newN: Int,
                                               newM: Int, intersection: TileIntersection,
                                               biggerTile: IndependentSetTile, satSolver: SatSolver) {
        if (biggerTile.canBeI(x, y)) {
            ifCenterIsOneAllOtherAreNot(x, y, biggerTile, satSolver, newN, newM, k, intersection)
            if (neighbourhoodIsInsideTile(x, y, newN, newM, k)) {
                val clause = atLeastOneNeighbourMustBeOne(x, y, biggerTile, newN, newM, k, intersection)
                clause.add(biggerTile.getId(x, y)) // center may also be in IS
                satSolver.addClause(clause.toArray())
            }
        } // there is not else branch because if internal cell is in IS then all neighbours are zero
    }

    /**
     * @return true if tile may be valid
     */
    private fun processCellInsideIntersection(x: Int, y: Int,
                                              newN: Int, newM: Int,
                                              intersection: TileIntersection,
                                              biggerTile: IndependentSetTile, satSolver: SatSolver): Boolean {
        cellMustStayTheSame(x, y, biggerTile, satSolver)
        if (biggerTile.isI(x, y)) {
            allNeighboursMustBeZero(x, y, biggerTile, newN, newM, k, intersection, satSolver)
        } else if (biggerTile.canBeI(x, y) && neighbourhoodIsInsideTile(x, y, newN, newM, k)) {
            val clause = atLeastOneNeighbourMustBeOne(x, y, biggerTile, newN, newM, k, intersection)
            if (clause.isEmpty) { // this cannot be satisfied
                return false
            }
            satSolver.addClause(clause.toArray())
        }
        return true
    }

    /**
     * For tests
     */
    override fun equals(other: Any?): Boolean {
        if (other is String) {
            return other == toString()
        }
        if (other !is IndependentSetTile) {
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
    fun rotate(): IndependentSetTile {
        val rotatedGrid = OpenBitSet(n * m.toLong())
        for (i in 0L until n) {
            for (j in 0L until m) {
                if (grid.get(getIndex(i, j))) {
                    rotatedGrid.set(getIndex(j, n - i - 1, n))
                }
            }
        }
        return IndependentSetTile(m, n, k, rotatedGrid)
    }
}
