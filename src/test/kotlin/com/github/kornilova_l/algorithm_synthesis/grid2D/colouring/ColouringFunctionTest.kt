package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleTileGraph
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

internal class ColouringFunctionTest {
    private val tiles67 = IndependentSetTile.parseTiles(File("generated_tiles/6-7-3.txt"))
    private val tiles58 = IndependentSetTile.parseTiles(File("generated_tiles/5-8-3.txt"))
    private val tileGraph = SimpleTileGraph(tiles67, tiles58)
    private val colouringFunction = ColouringProblem(tileGraph, 4, 3).colouringFunction
    private val iterationsCount = 1000

    @Test
    fun getGraphColoured() {
        for (iter in 0..iterationsCount) {
            val grid2D = generateGrid(8, 8)
            val graphColoured = colouringFunction?.getGraphColoured(grid2D) ?: continue
            assertNotNull(graphColoured)
            assertTrue(isColouringValid(graphColoured))
        }
    }

    private fun isColouringValid(graphColoured: Array<IntArray>): Boolean {
        val n = graphColoured.size
        val m = graphColoured[0].size
        for (i in 0 until n) {
            for (j in 0 until m) {
                val colour = graphColoured[i][j]
                if (colour == graphColoured[(i + 1) % n][j] ||
                        colour == graphColoured[i][(j + 1) % m] ||
                        colour == graphColoured[(i - 1 + n) % n][j] ||
                        colour == graphColoured[i][(j - 1 + m) % m]) {
                    return false
                }
            }
        }
        return true
    }
}