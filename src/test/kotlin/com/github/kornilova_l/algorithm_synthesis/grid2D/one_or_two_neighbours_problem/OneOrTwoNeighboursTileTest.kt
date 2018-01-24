package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OneOrTwoNeighboursTileTest {

    @Test
    fun isValid() {
        assertFalse(OneOrTwoNeighboursTile.createInstance("00\n00").isValid())
        assertFalse(OneOrTwoNeighboursTile.createInstance("100\n000").isValid())

        assertTrue(OneOrTwoNeighboursTile.createInstance("10\n00").isValid())
        assertTrue(OneOrTwoNeighboursTile.createInstance("101\n000").isValid())
    }

    @Test
    fun canBeIncludedTest() {
        assertTrue(OneOrTwoNeighboursTile.createInstance("00\n00").canBeIncluded(0, 0))
        assertTrue(OneOrTwoNeighboursTile.createInstance("00\n00").canBeIncluded(1, 0))
        assertTrue(OneOrTwoNeighboursTile.createInstance("00\n00").canBeIncluded(0, 1))
        assertTrue(OneOrTwoNeighboursTile.createInstance("00\n00").canBeIncluded(1, 1))
        assertTrue(OneOrTwoNeighboursTile.createInstance("000\n000").canBeIncluded(1, 1))

        assertFalse(OneOrTwoNeighboursTile.createInstance("10\n01").canBeIncluded(1, 0))
        assertFalse(OneOrTwoNeighboursTile.createInstance("10\n01").canBeIncluded(0, 1))
    }
}