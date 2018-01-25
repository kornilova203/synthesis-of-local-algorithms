package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        val expectedTiles = setOf(
                BinaryTile.createInstance("10\n00"),
                BinaryTile.createInstance("01\n00"),
                BinaryTile.createInstance("00\n10"),
                BinaryTile.createInstance("00\n01"),
                BinaryTile.createInstance("11\n00"),
                BinaryTile.createInstance("01\n01"),
                BinaryTile.createInstance("00\n11"),
                BinaryTile.createInstance("10\n10"),
                BinaryTile.createInstance("01\n10"),
                BinaryTile.createInstance("10\n01")
        )
        assertEquals(expectedTiles, tileGenerator.tiles)
    }

    @Test
    fun twoSquares() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(2, 3)
        assertEquals(26, tileGenerator.tiles.size)
    }

    @Test
    fun exportAndImportTest() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(3, 5)
        val file = tileGenerator.export(File("."), true)
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
}