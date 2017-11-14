package com.github.kornilova_l.algorithm_synthesis.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.Grid2D
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Coordinate
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
        assertEquals(expected, Tile(tile))
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

        assertNull(tile.getNextBorderCoordinate(Coordinate(tile.getN() - 1, tile.getM() - 1)))
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
}