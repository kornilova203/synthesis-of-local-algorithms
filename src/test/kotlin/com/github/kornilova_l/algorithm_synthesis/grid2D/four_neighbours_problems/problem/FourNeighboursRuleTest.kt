package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem

import org.junit.Assert.assertEquals
import org.junit.Test

class FourNeighboursRuleTest {

    @Test
    fun getId() {
        assertEquals(1, FourNeighboursRule(booleanArrayOf(true, false, false, false)).id)
        assertEquals(2, FourNeighboursRule(booleanArrayOf(false, true, false, false)).id)
        assertEquals(7, FourNeighboursRule(booleanArrayOf(true, true, true, false)).id)
    }

    @Test
    fun constructFromId() {
        assertEquals(FourNeighboursRule(booleanArrayOf(true, false, false, false)), FourNeighboursRule(1))
        assertEquals(FourNeighboursRule(booleanArrayOf(false, true, false, false)), FourNeighboursRule(2))
        assertEquals(FourNeighboursRule(booleanArrayOf(true, true, true, false)), FourNeighboursRule(7))
    }
}