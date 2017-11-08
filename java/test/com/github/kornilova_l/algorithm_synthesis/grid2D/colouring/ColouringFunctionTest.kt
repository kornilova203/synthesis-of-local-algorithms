package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileSet
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertNotNull

internal class ColouringFunctionTest {
    private val tiles67 = TileSet(File("generated_tiles/6-7-3-1509826576982.txt"))
    private val tiles58 = TileSet(File("generated_tiles/5-8-3-1509826268928.txt"))
    private val tileGraph = TileGraph(tiles67, tiles58)
    private val colouringFunction = ColouringProblem(tileGraph, 4).colouringFunction
    private val iterationsCount = 1000

    @Test
    fun getGraphColoured() {
        for (iter in 0..iterationsCount) {
            val grid2D = generateGrid(8, 8)
            val graphColoured = colouringFunction?.getGraphColoured(grid2D) ?: continue
            assertNotNull(graphColoured)
            val res = isColouringValid(graphColoured)
            if (!res) {
                println("Grid:")
                println(grid2D)
                for (i in 0 until graphColoured.size) {
                    for (j in 0 until graphColoured[0].size) {
                        print(graphColoured[i][j])
                        print(" ")
                    }
                    println()
                }
                println()
            }
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