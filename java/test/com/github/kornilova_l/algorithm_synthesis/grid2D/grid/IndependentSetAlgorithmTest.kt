package com.github.kornilova_l.algorithm_synthesis.grid2D.grid

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

internal class IndependentSetAlgorithmTest {
    @Test
    fun getIndependentSet() {
        val independentSetAlgorithm = IndependentSetAlgorithm(Grid2D(File("java/test_resources/grids/01_grid_5-6.txt")))
        val expected = File("java/test_resources/grids/01_is_5-6.txt").readText()
        assertEquals(expected, independentSetAlgorithm.toString())
    }

}