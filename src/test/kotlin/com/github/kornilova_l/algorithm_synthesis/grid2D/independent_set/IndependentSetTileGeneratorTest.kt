package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.util.Util
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File

class IndependentSetTileGeneratorTest {
    private val tempDir = File("temp-dir")

    @Test
    fun test() {
        tempDir.mkdir()
        val startTime = System.currentTimeMillis()
        var tileGenerator = IndependentSetTileGenerator(7, 5, 3, tempDir)
        assertEquals(2079, tileGenerator.tiles.size)
        println("Time ${System.currentTimeMillis() - startTime}")

        tileGenerator = IndependentSetTileGenerator(5, 7, 3, tempDir) // set of tiles should not depend on orientation
        assertEquals(2079, tileGenerator.tiles.size)
        Util.deleteDir(tempDir.toPath())
    }

    @Test
    fun testEasyTiles() {
        tempDir.mkdir()
        val startTime = System.currentTimeMillis()
        val parametersSet = getParametersSet(1)
        parametersSet.forEach { parameters ->
            val n = parameters.n
            val m = parameters.m
            val k = parameters.k
            val file = IndependentSetTile.getTilesFile(n, m, k, File("src/test/resources/tiles"))
            if (file != null) {
                val tiles = IndependentSetTileGenerator(n, m, k, tempDir).tiles
                val expectedTiles = IndependentSetTile.parseTiles(file)

                assertEquals(expectedTiles, tiles)
            }
        }
        println("Time: ${System.currentTimeMillis() - startTime}")
        Util.deleteDir(tempDir.toPath())
    }

    @Test
    fun exportAndImportTest() {
        tempDir.mkdir()
        val tileGenerator = IndependentSetTileGenerator(3, 5, 3, tempDir)
        val file = tileGenerator.file
        assertNotNull(file)

        val tileIS = IndependentSetTile.parseTiles(file)
        assertNotNull(tileIS)

        assertEquals(tileGenerator.tiles, tileIS)

        file.delete()
        Util.deleteDir(tempDir.toPath())
    }
}