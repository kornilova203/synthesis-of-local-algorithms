package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.util.Util
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class OneOrTwoNeighboursTileGeneratorTest {
    private val tempDir = File("temp-dir")

    @Test
    fun twoCells() {
        tempDir.mkdir()
        val tileGenerator = OneOrTwoNeighboursTileGenerator(1, 2, tempDir)
        val expectedTiles = setOf(
                BinaryTile.createInstance("00"),
                BinaryTile.createInstance("01"),
                BinaryTile.createInstance("10"),
                BinaryTile.createInstance("11")
        )
        assertEquals(expectedTiles, tileGenerator.tiles)
        Util.deleteDir(tempDir.toPath())
    }

    @Test
    fun oneSquare() {
        tempDir.mkdir()
        val tileGenerator = OneOrTwoNeighboursTileGenerator(2, 2, tempDir)
        assertEquals(16, tileGenerator.tiles.size)
        Util.deleteDir(tempDir.toPath())
    }

    @Test
    fun twoSquares() {
        tempDir.mkdir()
        val tileGenerator = OneOrTwoNeighboursTileGenerator(2, 3, tempDir)
        assertEquals(49, tileGenerator.tiles.size)
        Util.deleteDir(tempDir.toPath())
    }

    @Test
    fun exportAndImportTest() {
        tempDir.mkdir()
        val tileGenerator = OneOrTwoNeighboursTileGenerator(3, 5, tempDir)
        val file = tileGenerator.file
        Assert.assertNotNull(file)

        val parsedTiles = OneOrTwoNeighboursTile.parseTiles(file)
        Assert.assertNotNull(parsedTiles)

        assertEquals(tileGenerator.tiles, parsedTiles)

        file.delete()
        Util.deleteDir(tempDir.toPath())
    }

    @Test
    fun threeByFourTest() {
        tempDir.mkdir()
        val tileGenerator = OneOrTwoNeighboursTileGenerator(3, 4, tempDir)
        val expectedTile = OneOrTwoNeighboursTile.createInstance("0111\n1000\n1010")
        assertTrue(tileGenerator.tiles.contains(expectedTile))
        Util.deleteDir(tempDir.toPath())
    }

    @Test
    fun threeByThree() {
        tempDir.mkdir()
        val tileGenerator = OneOrTwoNeighboursTileGenerator(3, 3, tempDir)
        assertFalse(tileGenerator.tiles.contains(OneOrTwoNeighboursTile.createInstance("000\n000\n000")))
        assertTrue(tileGenerator.tiles.contains(OneOrTwoNeighboursTile.createInstance("010\n000\n000")))
        Util.deleteDir(tempDir.toPath())
    }
}