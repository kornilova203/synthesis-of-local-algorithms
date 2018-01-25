package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class OneOrTwoNeighboursTileGeneratorTest {
    @Test
    fun twoCells() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(1, 2)
        val expectedTiles = setOf(
                Tile.createInstance("00"),
                Tile.createInstance("01"),
                Tile.createInstance("10"),
                Tile.createInstance("11")
        )
        assertEquals(expectedTiles, tileGenerator.tiles)
    }

    @Test
    fun oneSquare() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(2, 2)
        val expectedTiles = setOf(
                Tile.createInstance("10\n00"),
                Tile.createInstance("01\n00"),
                Tile.createInstance("00\n10"),
                Tile.createInstance("00\n01"),
                Tile.createInstance("11\n00"),
                Tile.createInstance("01\n01"),
                Tile.createInstance("00\n11"),
                Tile.createInstance("10\n10"),
                Tile.createInstance("01\n10"),
                Tile.createInstance("10\n01")
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
}