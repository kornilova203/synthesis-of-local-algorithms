package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.util.Util
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class OneOrTwoNeighboursTileParserTest {
    private val tempDir = File("temp-dir")

    @Test
    fun parseTest() {
        tempDir.mkdir()
        val tileGenerator = OneOrTwoNeighboursTileGenerator(3, 5, tempDir)
        val expectedTiles = tileGenerator.tiles
        val file = tileGenerator.file

        val actualTiles = HashSet<BinaryTile>()
        for (oneOrTwoNeighboursTile in OneOrTwoNeighboursTileParser(file)) {
            actualTiles.add(oneOrTwoNeighboursTile)
        }

        assertEquals(expectedTiles, actualTiles)

        file.delete()
        Util.deleteDir(tempDir.toPath())
    }
}