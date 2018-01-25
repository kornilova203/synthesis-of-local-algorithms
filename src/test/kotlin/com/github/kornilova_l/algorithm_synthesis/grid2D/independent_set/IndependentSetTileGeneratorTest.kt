package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File

class IndependentSetTileGeneratorTest {
    @Test
    fun test() {
        val startTime = System.currentTimeMillis()
        var tileGenerator = IndependentSetTileGenerator(7, 5, 3)
        assertEquals(2079, tileGenerator.tiles.size)
        println("Time ${System.currentTimeMillis() - startTime}")

        tileGenerator = IndependentSetTileGenerator(5, 7, 3) // set of tiles should not depend on orientation
        assertEquals(2079, tileGenerator.tiles.size)
    }

    @Test
    fun testEasyTiles() {
        val startTime = System.currentTimeMillis()
        val parametersSet = getParametersSet(1)
        parametersSet.forEachIndexed { i, parameters ->
            val n = parameters.n
            val m = parameters.m
            val k = parameters.k
            val file = File("src/test/resources/tiles/$n-$m-$k.txt")
            if (file.exists()) {
                val tiles = IndependentSetTileGenerator(n, m, k).tiles
                val expectedTiles = IndependentSetTile.parseTiles(file)

                assertEquals(expectedTiles, tiles)
            }
            println("$i / ${parametersSet.size}")
        }
        println("Time: ${System.currentTimeMillis() - startTime}")
    }

    @Test
    fun exportAndImportTest() {
        val tileGenerator = IndependentSetTileGenerator(3, 5, 3)
        val file = tileGenerator.export(File("."), true)
        assertNotNull(file)

        val tileIS = IndependentSetTile.parseTiles(file!!)
        assertNotNull(tileIS)

        assertEquals(tileGenerator.tiles, tileIS)

        file.delete()
    }
}