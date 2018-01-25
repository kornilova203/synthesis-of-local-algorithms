package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileIntersection
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.SatSolver
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import gnu.trove.set.hash.TIntHashSet
import org.apache.lucene.util.OpenBitSet
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


fun parseSet(reader: BufferedReader, n: Int, m: Int): OpenBitSet {
    val grid = OpenBitSet(n * m.toLong())
    var i = 0L
    var line = reader.readLine() // readLine() is faster that read()
    var lineIndex = 0
    while (i < n * m) {
        val c = line[lineIndex++]
        if (line.length == lineIndex) {
            line = reader.readLine()
            lineIndex = 0
        }
        if (c == '1' || c == '0') {
            if (c == '1') {
                grid.set(i)
            }
            i++
        }
    }
    return grid
}

open class IndependentSetTile(n: Int, m: Int, val k: Int, grid: OpenBitSet) : BinaryTile(n, m, grid) {

    override fun createInstanceOfClass(newN: Int, newM: Int, grid: OpenBitSet): BinaryTile = IndependentSetTile(newN, newM, k, grid)

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
            return IndependentSetTile(n, m, k, grid)
        }

        fun createInstance(string: String, k: Int): IndependentSetTile {
            val lines = string.split("\n").filter { it != "" }
            val n = lines.size
            val m = calculateM(lines)
            return IndependentSetTile(n, m, k, parseGrid(n, m, lines))
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

        private fun cellMustStayTheSame(x: Int, y: Int, biggerTile: BinaryTile, satSolver: SatSolver) {
            val value = if (biggerTile.isI(x, y)) {
                biggerTile.getId(x, y)
            } else {
                -biggerTile.getId(x, y)
            }
            satSolver.addClause(value)
        }

        private fun allNeighboursMustBeZero(x: Int, y: Int, biggerTile: BinaryTile, newN: Int, newM: Int, k: Int,
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
        private fun ifCenterIsOneAllOtherAreNot(x: Int, y: Int, biggerTile: BinaryTile, satSolver: SatSolver,
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

        private fun atLeastOneNeighbourMustBeOne(x: Int, y: Int, biggerTile: BinaryTile, newN: Int,
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

        fun parseTiles(file: File): Set<IndependentSetTile> {
            if (!file.exists() || !file.isFile) {
                throw IllegalArgumentException("File does not exist or it is not a file")
            }
            BufferedReader(FileReader(file)).use { reader ->
                val firstLine = reader.readLine()
                val parts = firstLine.split(" ")
                val n = Integer.parseInt(parts[0])
                val m = Integer.parseInt(parts[1])
                val k = Integer.parseInt(parts[2])
                val size = Integer.parseInt(reader.readLine())
                val validTiles = HashSet<IndependentSetTile>(size)
                for (i in 0 until size) {
                    val grid = parseSet(reader, n, m)
                    validTiles.add(IndependentSetTile(n, m, k, grid))
                }
                if (size != validTiles.size) {
                    throw IllegalArgumentException("File contains less tiles that it states in the beginning of the file")
                }
                return validTiles
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
    constructor(n: Int, m: Int, k: Int) : this(n, m, k, OpenBitSet((n * m).toLong()))

    /**
     * @return true if grid[x][y] can be an element of an independent set
     */
    override fun canBeIncluded(x: Int, y: Int): Boolean {
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

    /**
     * @return false if tile is definitely not valid and it is not needed to run SAT solver
     */
    private fun initSatSolverIsTileValid(satSolver: SatSolver): Boolean {
        val newN = n + k * 2
        val newM = m + k * 2
        val biggerTile = this.cloneAndExpand(newN, newM)
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
                                               biggerTile: BinaryTile, satSolver: SatSolver) {
        if (biggerTile.canBeIncluded(x, y)) {
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
                                              biggerTile: BinaryTile, satSolver: SatSolver): Boolean {
        cellMustStayTheSame(x, y, biggerTile, satSolver)
        if (biggerTile.isI(x, y)) {
            allNeighboursMustBeZero(x, y, biggerTile, newN, newM, k, intersection, satSolver)
        } else if (biggerTile.canBeIncluded(x, y) && neighbourhoodIsInsideTile(x, y, newN, newM, k)) {
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

    /**
     * Creates a new tile that equals to the original tile rotated clockwise
     */
    override fun rotate(): IndependentSetTile = IndependentSetTile(m, n, k, rotateGrid(grid))
}
