package com.github.kornilova_l.algorithm_synthesis.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.Grid2D
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Coordinate
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.VertexSetSolverKtTest
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
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
        assertFalse(tile.isValid)

        tile = Tile(5, 7, 3)
        assertTrue(tile.isValid)
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
    fun getNextBorderCoordinateTest() {
        val tile = Tile(6, 8, 2)
        assertEquals(Coordinate(1, 0),
                tile.getNextBorderCoordinate(Coordinate(0, 0)))

        assertEquals(Coordinate(0, 1),
                tile.getNextBorderCoordinate(Coordinate(5, 0)))

        assertEquals(Coordinate(4, 2),
                tile.getNextBorderCoordinate(Coordinate(1, 2)))

        assertEquals(Coordinate(2, 0),
                tile.getNextBorderCoordinate(Coordinate(1, 0)))

        assertNull(tile.getNextBorderCoordinate(Coordinate(tile.n - 1, tile.m - 1)))
    }

    @Test
    fun tileFromArrayTest() {
        val grid2D = Grid2D(File("src/test/resources/grids/01_grid_5-6.txt"))
        val independentSet = IndependentSetAlgorithm(grid2D, 2).independentSet

        var tile = Tile(independentSet, 1, 3, 3, 5, 2)

        assertEquals("0 0 0 0 0\n" +
                "0 0 0 1 0\n" +
                "0 1 0 0 0\n", tile.toString())

        tile = Tile(independentSet, 0, 0, 3, 5, 2)

        assertEquals("0 0 0 0 0\n" +
                "0 0 1 0 0\n" +
                "1 0 0 0 0\n", tile.toString())
    }

    @Test
    fun tileConstructorTest() {
        val independentSet1 = hashSetOf(
                Coordinate(1, 1),
                Coordinate(3, 3),
                Coordinate(2, 0)
        )
        val biggerTile = Tile(5, 4, 1, independentSet1)

        val independentSetNorth = hashSetOf(Coordinate(1, 0))
        val tileNorth = Tile(3, 2, 1, independentSetNorth)
        assertEquals(tileNorth, Tile(biggerTile, POSITION.N))

        val independentSetEast = hashSetOf(Coordinate(2, 1))
        val tileEast = Tile(3, 2, 1, independentSetEast)
        assertEquals(tileEast, Tile(biggerTile, POSITION.E))

        val independentSetSouth = hashSetOf<Coordinate>()
        val tileSouth = Tile(3, 2, 1, independentSetSouth)
        assertEquals(tileSouth, Tile(biggerTile, POSITION.S))

        val independentSetWest = hashSetOf(Coordinate(1, 0), Coordinate(0, 1))
        val tileWest = Tile(3, 2, 1, independentSetWest)
        assertEquals(tileWest, Tile(biggerTile, POSITION.W))

        val independentSetCenter = hashSetOf(Coordinate(0, 0))
        val tileCenter = Tile(3, 2, 1, independentSetCenter)
        assertEquals(tileCenter, Tile(biggerTile, POSITION.X))
    }

    @Test
    fun expandTileToDimacsTest() {
        val tile = Tile(2, 2, 1)
        val clauses = tile.toDimacsIsTileValid(3, 3)
        println(clauses.joinToString("", "", transform = { "${it.joinToString(" ", "")}\n" }))

        val expected = VertexSetSolverKtTest.parseClauses(File("src/test/resources/expand_tile/to_dimacs_3_3.txt").readText())
        assertTrue(expected == clauses)
    }
}