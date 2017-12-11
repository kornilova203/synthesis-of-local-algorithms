package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.Grid2D
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.VertexSetSolverKtTest
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import gnu.trove.list.array.TIntArrayList
import org.junit.Assert.*
import org.junit.Test
import java.io.File

internal class TileTest {
    @Test
    fun canBeI() {
        var tile = Tile(5, 6, 2)

        assertTrue(tile.canBeI(4, 4))
        tile = Tile(tile, 4, 3)
        assertFalse(tile.canBeI(4, 4))

        Tile(5, 6, 2)

        /* On diagonal: */
        assertFalse(tile.canBeI(3, 2))
        /* On opposite side: */
        assertTrue(tile.canBeI(0, 3))
        /* On diagonal and on opposite side: */
        assertTrue(tile.canBeI(0, 2))

        assertTrue(tile.canBeI(1, 2))
        assertTrue(tile.canBeI(0, 1))
    }

    @Test
    fun isValidTest() { // may take some time
        var tile = Tile(5, 6, 2)
        assertFalse(tile.isValid())

        tile = Tile(5, 7, 3)
        assertTrue(tile.isValid())
    }

    @Test
    fun expandingConstructor() {
        var tile = Tile(3, 4, 3)
        tile = Tile(tile, 0, 1)
        var expected = Tile(9, 10, 3)

        expected = Tile(expected, 3, 4)
        assertEquals(expected, Tile(9, 10, tile))
    }

    @Test
    fun tileFromArrayTest() {
        val grid2D = Grid2D(File("src/test/resources/grids/01_grid_5-6.txt"))
        val independentSet = IndependentSetAlgorithm(grid2D, 2).independentSet

        var tile = Tile(independentSet, 1, 3, 3, 5, 2)

        assertEquals(Tile("0 0 0 0 0\n0 0 0 1 0\n0 1 0 0 0\n", 2), tile)

        tile = Tile(independentSet, 0, 0, 3, 5, 2)

        assertEquals(Tile("0 0 0 0 0\n0 0 1 0 0\n1 0 0 0 0\n", 2), tile)
    }

    @Test
    fun tileConstructorWithPosition() {
        val biggerTile = Tile("0000\n0 1 0 0\n1 0 0 0\n0 0 0 1", 1)

        val tileNorth = Tile("0 0\n1 0", 1)
        assertEquals(tileNorth, Tile(biggerTile, POSITION.N))

        val tileEast = Tile("0 0\n0 0", 1)
        assertEquals(tileEast, Tile(biggerTile, POSITION.E))

        val tileSouth = Tile("0 0\n0 0", 1)
        assertEquals(tileSouth, Tile(biggerTile, POSITION.S))

        val tileWest = Tile("0 1\n1 0", 1)
        assertEquals(tileWest, Tile(biggerTile, POSITION.W))

        val tileCenter = Tile("1 0\n0 0", 1)
        assertEquals(tileCenter, Tile(biggerTile, POSITION.X))
    }

    @Test
    fun rotateTest() {
        val tile = Tile("010\n000\n000\n100", 1)
        assertEquals(Tile("1000\n0001\n0000", 1), tile.rotate())
    }

    @Test
    fun tileConstructorWithPart() {
        val biggerTile = Tile("0 0 0 0\n0 1 0 0\n1 0 0 0\n0 0 0 1", 1)

        val tileNorth = Tile("0 0 0 0\n0 1 0 0\n1 0 0 0", 1)
        assertEquals(tileNorth, Tile(biggerTile, Tile.Part.N))

        val tileEast = Tile("0 0 0\n0 1 0\n1 0 0\n0 0 0", 1)
        assertEquals(tileEast, Tile(biggerTile, Tile.Part.W))

        val tileSouth = Tile("0 1 0 0\n1 0 0 0\n0 0 0 1", 1)
        assertEquals(tileSouth, Tile(biggerTile, Tile.Part.S))

        val tileWest = Tile("0 0 0\n1 0 0\n0 0 0\n0 0 1", 1)
        assertEquals(tileWest, Tile(biggerTile, Tile.Part.E))
    }

    @Test
    fun expandTileToDimacsTest() {
        var tile = Tile(2, 2, 1)
        var clauses = tile.toDimacsIsTileValid()!!

        var expected = VertexSetSolverKtTest.parseClauses(File("src/test/resources/expand_tile/to_dimacs_3_3.txt").readText())
        assertEquals(Clauses(expected), Clauses(Companion.flatSetToSetOfSet(clauses)))


        tile = Tile(tile, 0, 1)
        clauses = tile.toDimacsIsTileValid()!!

        expected = VertexSetSolverKtTest.parseClauses(File("src/test/resources/expand_tile/to_dimacs_3_3_has_one.txt").readText())
        assertEquals(Clauses(expected), Clauses(Companion.flatSetToSetOfSet(clauses)))


        tile = Tile("1 0 0 0\n0 1 0 1\n0 0 1 0\n", 1)
        clauses = tile.toDimacsIsTileValid()!!

        expected = VertexSetSolverKtTest.parseClauses(File("src/test/resources/expand_tile/to_dimacs_3_4.txt").readText())
        assertEquals(Clauses(expected), Clauses(Companion.flatSetToSetOfSet(clauses)))
    }

    /**
     * This class is used for tests
     * it allows to see difference between clauses conveniently
     */
    @Suppress("EqualsOrHashCode")
    class Clauses(private val clauses: Set<Set<Int>>) {
        override fun equals(other: Any?): Boolean {
            if (other !is Clauses) {
                return false
            }
            return clauses == other.clauses
        }

        override fun toString(): String {
            val sorted = ArrayList<List<Int>>()
            for (clause in clauses) {
                val sortedClause = ArrayList<Int>(clause)
                sortedClause.sort()
                sorted.add(sortedClause)
            }
            val res = sorted.sortedWith(Comparator { o1, o2 ->
                when {
                    o1.size < o2.size -> -1
                    o1.size > o2.size -> 1
                    else -> when {
                        Math.abs(o1[0]) < Math.abs(o2[0]) -> -1
                        Math.abs(o1[0]) > Math.abs(o2[0]) -> 1
                        else -> 0
                    }
                }

            })
            return res.joinToString("", "", transform = { "${it.joinToString(" ", "")}\n" })
        }
    }

    companion object {
        fun flatSetToSetOfSet(clausesList: List<TIntArrayList>): Set<Set<Int>> {
            val newClauses = HashSet<Set<Int>>()
            for(clauses in clausesList) {
                var currentClause = HashSet<Int>()
                for (i in 0 until clauses.size()) {
                    val value = clauses[i]
                    if (value != 0) {
                        currentClause.add(value)
                    } else {
                        newClauses.add(currentClause)
                        currentClause = HashSet()
                    }
                }
            }
            return newClauses
        }
    }
}