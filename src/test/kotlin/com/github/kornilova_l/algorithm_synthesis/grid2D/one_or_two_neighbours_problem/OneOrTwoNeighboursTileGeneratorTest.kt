package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class OneOrTwoNeighboursTileGeneratorTest {
    @Test
    fun twoCells() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(1, 2)
        val expectedTiles = setOf(
                BinaryTile.createInstance("00"),
                BinaryTile.createInstance("01"),
                BinaryTile.createInstance("10"),
                BinaryTile.createInstance("11")
        )
        assertEquals(expectedTiles, tileGenerator.tiles)
    }

    @Test
    fun oneSquare() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(2, 2)
        assertEquals(16, tileGenerator.tiles.size)
    }

    @Test
    fun twoSquares() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(2, 3)
        assertEquals(49, tileGenerator.tiles.size)
    }

    @Test
    fun exportAndImportTest() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(3, 5)
        val file = tileGenerator.export(File("."))
        Assert.assertNotNull(file)

        val parsedTiles = OneOrTwoNeighboursTile.parseTiles(file!!)
        Assert.assertNotNull(parsedTiles)

        assertEquals(tileGenerator.tiles, parsedTiles)

        file.delete()
    }

    @Test
    fun threeByFourTest() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(3, 4)
        val expectedTile = OneOrTwoNeighboursTile.createInstance("0111\n1000\n1010")
        assertTrue(tileGenerator.tiles.contains(expectedTile))
    }

    @Test
    fun threeByThree() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(3, 3)
        assertFalse(tileGenerator.tiles.contains(OneOrTwoNeighboursTile.createInstance("000\n000\n000")))
        assertTrue(tileGenerator.tiles.contains(OneOrTwoNeighboursTile.createInstance("010\n000\n000")))
    }
}