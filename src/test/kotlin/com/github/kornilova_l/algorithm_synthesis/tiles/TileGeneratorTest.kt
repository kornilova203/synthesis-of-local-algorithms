package com.github.kornilova_l.algorithm_synthesis.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import org.apache.commons.collections4.CollectionUtils
import org.junit.Assert.*
import org.junit.Test
import java.io.File

internal class TileGeneratorTest {
    @Test
    fun test() {
        val startTime = System.currentTimeMillis()
        var tileGenerator = TileGenerator(7, 5, 3)
        assertEquals(2079, tileGenerator.tileSet.size())
        println("Time ${System.currentTimeMillis() - startTime}")

        tileGenerator = TileGenerator(5, 7, 3) // set of tiles should not depend on orientation
        assertEquals(2079, tileGenerator.tileSet.size())
    }

    @Test
    fun testEasyTiles() {
        val startTime = System.currentTimeMillis()
        val parametersSet = getParametersSet(1)
        for (parameters in parametersSet) {
            val n = parameters.n
            val m = parameters.m
            val k = parameters.k
            val file = File("src/test/resources/tiles/$n-$m-$k.txt")
            if (!file.exists()) {
                continue
            }
            val tiles = TileGenerator(n, m, k).tileSet
            val expectedTiles = TileSet(file)

            assertEquals(expectedTiles, tiles)
        }
        println("Time: ${System.currentTimeMillis() - startTime}")
    }

    @Test
    fun exportAndImportTest() {
        val tileGenerator = TileGenerator(3, 5, 3)
        val file = tileGenerator.exportToFile(File("."), true)
        assertNotNull(file)

        val tileIS = TileSet(file!!).validTiles
        assertNotNull(tileIS)

        assertTrue(CollectionUtils.isEqualCollection(tileGenerator.tileSet.validTiles, tileIS))


        file.delete()
    }
}