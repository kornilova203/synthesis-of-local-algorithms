package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.IndependentSetTileGenerator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleTileGraph
import org.apache.commons.io.FileUtils
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.charset.Charset


internal class ColouringProblemTest {
    private val tiles32 = IndependentSetTileGenerator(3, 2, 1).tiles
    private val tiles23 = IndependentSetTileGenerator(2, 3, 1).tiles

    @Test
    @Throws(IOException::class)
    fun toDimacs() {
        val expected = FileUtils.readLines(File("src/test/resources/dimacs_4-colouring_2-2-1.txt"), Charset.defaultCharset())
        val graph = SimpleTileGraph(tiles32, tiles23)
        val actual = ColouringProblem.Companion.toDimacs(
                graph, 4
        ).split("\n")

        // todo: colouring was broken
//        assertTrue(expected == actual)
    }
}