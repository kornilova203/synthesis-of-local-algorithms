package com.github.kornilova_l.algorithm_synthesis.grid2D

import gnu.trove.list.array.TLongArrayList
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File


class CountSolvableAndUnsolvableTest {
    private val combinationsCount = Math.pow(2.toDouble(), 30.toDouble()).toLong()

    @Test
    fun countSolvableRecursivelyTest() {
        val solvable = TLongArrayList()
        solvable.add(3)
        solvable.add(5)
        solvable.add(9)
        val solvableCount = countSolvableRecursively(solvable, solvable.size() - 1, 4)
        assertEquals(7, solvableCount)
    }

    @Test
    fun calcSolvableTest() {
        val solvable = parseLongs(File("src/test/resources/calc_solvable_and_unsolvable/solvable.txt"))
        val expectedSolvableCount = countSolvableExpected(solvable)
        assertEquals(expectedSolvableCount, countSolvable(solvable))
    }

    private fun countSolvableExpected(solvable: TLongArrayList): Long {
        var solvableCount = 0L
        for (i in 0 until combinationsCount) {
            if (isSolvable(i, solvable)) {
                solvableCount++
            }
        }
        return solvableCount
    }

    private fun countUnSolvableExpected(unsolvable: TLongArrayList): Long {
        var unsolvableCount = 0L
        for (i in 0 until combinationsCount) {
            if (isUnsolvable(i, unsolvable)) {
                unsolvableCount++
            }
        }
        return unsolvableCount
    }
}