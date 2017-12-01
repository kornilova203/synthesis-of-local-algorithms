package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TilesIntersectionTest {
    @Test
    fun biggerByOne() {
        val intersection = TilesIntersection(Tile(3, 3, 1), Tile(2, 2, 1))

        assertTrue(intersection.isInside(0, 0))
        assertTrue(intersection.isInside(1, 0))
        assertTrue(intersection.isInside(0, 1))
        assertTrue(intersection.isInside(1, 1))

        assertFalse(intersection.isInside(0, 2))
        assertFalse(intersection.isInside(1, 2))
        assertFalse(intersection.isInside(2, 0))
        assertFalse(intersection.isInside(2, 1))
        assertFalse(intersection.isInside(2, 2))
    }

    @Test
    fun biggerByTwo() {
        val intersection = TilesIntersection(Tile(3, 3, 1), Tile(1, 1, 1))

        assertTrue(intersection.isInside(1, 1))

        assertFalse(intersection.isInside(0, 0))
        assertFalse(intersection.isInside(0, 1))
        assertFalse(intersection.isInside(0, 2))
        assertFalse(intersection.isInside(1, 0))
        assertFalse(intersection.isInside(1, 2))
        assertFalse(intersection.isInside(2, 0))
        assertFalse(intersection.isInside(2, 1))
        assertFalse(intersection.isInside(2, 2))
    }

    @Test
    fun biggerByThree() {
        val intersection = TilesIntersection(Tile(4, 3, 1), Tile(1, 1, 1))

        assertTrue(intersection.isInside(1, 1))

        assertFalse(intersection.isInside(0, 0))
        assertFalse(intersection.isInside(0, 1))
        assertFalse(intersection.isInside(0, 2))
        assertFalse(intersection.isInside(1, 0))
        assertFalse(intersection.isInside(1, 2))
        assertFalse(intersection.isInside(2, 0))
        assertFalse(intersection.isInside(2, 1))
        assertFalse(intersection.isInside(2, 2))
        assertFalse(intersection.isInside(3, 0))
        assertFalse(intersection.isInside(3, 1))
        assertFalse(intersection.isInside(3, 2))
    }

}