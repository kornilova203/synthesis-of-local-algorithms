package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class OneOrTwoNeighboursTileParserTest {
    @Test
    fun parseTest() {
        val tileGenerator = OneOrTwoNeighboursTileGenerator(3, 5)
        val expectedTiles = tileGenerator.tiles
        val file = tileGenerator.export(File("."))!!

        val actualTiles = HashSet<BinaryTile>()
        for (oneOrTwoNeighboursTile in OneOrTwoNeighboursTileParser(file)) {
            actualTiles.add(oneOrTwoNeighboursTile)
        }

        assertEquals(expectedTiles, actualTiles)

        file.delete()
    }
}