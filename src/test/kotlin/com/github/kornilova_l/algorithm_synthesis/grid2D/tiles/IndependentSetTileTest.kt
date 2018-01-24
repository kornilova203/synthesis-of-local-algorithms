package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.Grid2D
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Companion.Part.*
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import org.junit.Assert.*
import org.junit.Test
import java.io.File

internal class IndependentSetTileTest {
    @Test
    fun canBeI() {
        var tile: Tile = IndependentSetTile(5, 6, 2)

        assertTrue(tile.canBeIncluded(4, 4))
        tile = tile.cloneAndChange(4, 3)
        assertFalse(tile.canBeIncluded(4, 4))

        IndependentSetTile(5, 6, 2)

        /* On diagonal: */
        assertFalse(tile.canBeIncluded(3, 2))
        /* On opposite side: */
        assertTrue(tile.canBeIncluded(0, 3))
        /* On diagonal and on opposite side: */
        assertTrue(tile.canBeIncluded(0, 2))

        assertTrue(tile.canBeIncluded(1, 2))
        assertTrue(tile.canBeIncluded(0, 1))
    }

    @Test
    fun isValidTest() { // may take some time
        var tile = IndependentSetTile(5, 6, 2)
        assertFalse(tile.isValid())

        tile = IndependentSetTile(5, 7, 3)
        assertTrue(tile.isValid())
    }

    @Test
    fun expandingConstructor() {
        var tile: Tile = IndependentSetTile(3, 4, 3)
        tile = tile.cloneAndChange(0, 1)
        var expected: Tile = IndependentSetTile(9, 10, 3)

        expected = expected.cloneAndChange(3, 4)
        assertEquals(expected, tile.cloneAndExpand(9, 10))
    }

    @Test
    fun tileFromArrayTest() {
        val grid2D = Grid2D(File("src/test/resources/grids/01_grid_5-6.txt"))
        val independentSet = IndependentSetAlgorithm(grid2D, 2).independentSet

        var tile = Tile(independentSet, 1, 3, 3, 5)

        assertEquals(Tile.createInstance("0 0 0 0 0\n0 0 0 1 0\n0 1 0 0 0\n"), tile)

        tile = Tile(independentSet, 0, 0, 3, 5)

        assertEquals(Tile.createInstance("0 0 0 0 0\n0 0 1 0 0\n1 0 0 0 0\n"), tile)
    }

    @Test
    fun tileConstructorWithPosition() {
        val biggerTile = IndependentSetTile.createInstance("0000\n0 1 0 0\n1 0 0 0\n0 0 0 1", 1)

        val tileNorth = IndependentSetTile.createInstance("0 0\n1 0", 1)
        assertEquals(tileNorth, IndependentSetTile.createInstance(biggerTile, POSITION.N))

        val tileEast = IndependentSetTile.createInstance("0 0\n0 0", 1)
        assertEquals(tileEast, IndependentSetTile.createInstance(biggerTile, POSITION.E))

        val tileSouth = IndependentSetTile.createInstance("0 0\n0 0", 1)
        assertEquals(tileSouth, IndependentSetTile.createInstance(biggerTile, POSITION.S))

        val tileWest = IndependentSetTile.createInstance("0 1\n1 0", 1)
        assertEquals(tileWest, IndependentSetTile.createInstance(biggerTile, POSITION.W))

        val tileCenter = IndependentSetTile.createInstance("1 0\n0 0", 1)
        assertEquals(tileCenter, IndependentSetTile.createInstance(biggerTile, POSITION.X))
    }

    @Test
    fun rotateTest() {
        val tile = IndependentSetTile.createInstance("010\n000\n000\n100", 1)
        assertEquals(IndependentSetTile.createInstance("1000\n0001\n0000", 1), tile.rotate())
    }

    @Test
    fun tileConstructorWithPart() {
        val biggerTile = IndependentSetTile.createInstance("0 0 0 0\n0 1 0 0\n1 0 0 0\n0 0 0 1", 1)

        val tileNorth = IndependentSetTile.createInstance("0 0 0 0\n0 1 0 0\n1 0 0 0", 1)
        assertEquals(tileNorth, biggerTile.clonePart(N))

        val tileEast = IndependentSetTile.createInstance("0 0 0\n0 1 0\n1 0 0\n0 0 0", 1)
        assertEquals(tileEast, biggerTile.clonePart(W))

        val tileSouth = IndependentSetTile.createInstance("0 1 0 0\n1 0 0 0\n0 0 0 1", 1)
        assertEquals(tileSouth, biggerTile.clonePart(S))

        val tileWest = IndependentSetTile.createInstance("0 0 0\n1 0 0\n0 0 0\n0 0 1", 1)
        assertEquals(tileWest, biggerTile.clonePart(E))
    }
}