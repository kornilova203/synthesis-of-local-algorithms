package com.github.kornilova_l.algorithm_synthesis.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.colouring.ColouringProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleTileGraph
import org.apache.commons.io.FileUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.io.IOException
import java.lang.String.join


internal class ColouringProblemTest {
    private val tiles32 = TileGenerator(3, 2, 1).tileSet
    private val tiles23 = TileGenerator(2, 3, 1).tileSet

    @Test
    @Throws(IOException::class)
    fun toDimacs() {
        // TODO: compare set of strings
        /* note: this test may fail because order of lines matters */
        val expected = join("\n",
                FileUtils.readLines(File("src/test/resources/dimacs_4-colouring_2-2-1.txt"), null as String?)) + "\n"
        val graph = SimpleTileGraph(tiles32, tiles23)
        val actual = ColouringProblem.Companion.toDimacs(
                graph, 4
        )
        assertEquals(expected, actual)
    }

}