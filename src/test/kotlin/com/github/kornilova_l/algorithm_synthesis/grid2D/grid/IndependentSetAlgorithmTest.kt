package com.github.kornilova_l.algorithm_synthesis.grid2D.grid

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

internal class IndependentSetAlgorithmTest {
    @Test
    fun getIndependentSet() {
        val isAlgorithm1 = IndependentSetAlgorithm(
                Grid2D(File("src/test/resources/grids/01_grid_5-6.txt")),
                1
        )
        val expected1 = File("src/test/resources/grids/01_is1_5-6.txt").readText()
        assertEquals(expected1, isAlgorithm1.toString())

        val isAlgorithm2 = IndependentSetAlgorithm(
                Grid2D(File("src/test/resources/grids/01_grid_5-6.txt")),
                2
        )
        val expected2 = File("src/test/resources/grids/01_is2_5-6.txt").readText()
        assertEquals(expected2, isAlgorithm2.toString())

        val isAlgorithm3 = IndependentSetAlgorithm(
                Grid2D(File("src/test/resources/grids/01_grid_5-6.txt")),
                3
        )
        val expected3 = File("src/test/resources/grids/01_is3_5-6.txt").readText()
        assertEquals(expected3, isAlgorithm3.toString())

        val isAlgorithm4 = IndependentSetAlgorithm(
                Grid2D(File("src/test/resources/grids/03_grid_8-8.txt")),
                3
        )
        val expected4 = File("src/test/resources/grids/03_is3_8-8.txt").readText()
        assertEquals(expected4, isAlgorithm4.toString())
    }

}