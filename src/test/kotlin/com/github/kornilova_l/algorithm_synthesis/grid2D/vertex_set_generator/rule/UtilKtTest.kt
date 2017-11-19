package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule

import org.junit.Test

import org.junit.Assert.*

class UtilKtTest {
    @Test
    fun getRulePermutationsTest() {
        var permutations = getRulePermutations(1, false)
        assertEquals(4, permutations.size)
        assertEquals("[W, N, E, S]", permutations.toString())

        permutations = getRulePermutations(1, true)
        assertEquals(4, permutations.size)
        assertEquals("[XW, XN, XE, XS]", permutations.toString())

        permutations = getRulePermutations(2, false)
        assertEquals(6, permutations.size)
        assertEquals("[NW, EW, NE, SW, NS, ES]", permutations.toString())
    }

}