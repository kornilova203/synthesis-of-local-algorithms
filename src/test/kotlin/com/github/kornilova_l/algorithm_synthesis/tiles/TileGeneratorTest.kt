package com.github.kornilova_l.algorithm_synthesis.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.VertexSetSolverKtTest.Companion.parseClauses
import org.apache.commons.collections4.CollectionUtils
import org.junit.Assert.*
import org.junit.Test
import java.io.File

internal class TileGeneratorTest {
    @Test
    fun test() {
        var tileGenerator = TileGenerator(7, 5, 3)
        assertEquals(2079, tileGenerator.tileSet.size())

        tileGenerator = TileGenerator(5, 7, 3) // set of tiles should not depend on orientation
        assertEquals(2079, tileGenerator.tileSet.size())
    }

    @Test
    fun testEasyTiles() {
        val parametersSet = getParametersSet(1)
        for (parameters in parametersSet) {
            val n = parameters.n
            val m = parameters.m
            val k = parameters.k
            val tiles = TileGenerator(n, m, k).tileSet
            val expectedTiles = TileSet(File("src/test/resources/tiles/$n-$m-$k.txt"))

            assertEquals(expectedTiles, tiles)
        }
    }

    @Test
    fun exportAndImportTest() {
        val tileGenerator = TileGenerator(5, 7, 3)
        val file = tileGenerator.exportToFile(File("."), true)
        assertNotNull(file)

        val tileIS = TileSet(file!!).validTiles
        assertNotNull(tileIS)

        assertTrue(CollectionUtils.isEqualCollection(tileGenerator.tileSet.validTiles, tileIS))


        file.delete()
    }

    @Test
    fun expandTileToDimacsTest() {
        val clauses = TileGenerator.Companion.toDimacs(Tile(2, 2, 1), 3, 3)
        println(clauses.joinToString("", "", transform={ "${it.joinToString(" ", "")}\n" }))

        val expected = parseClauses(File("src/test/resources/expand_tile/to_dimacs_3_3.txt").readText())
        assertTrue(expected == clauses)
    }
}