package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem

import org.junit.Assert.assertEquals
import org.junit.Test

class FourNeighboursProblemTest {
    @Test
    fun patternConstructorTest() {
        var problem = FourNeighboursProblem("1111")
        assertEquals(1, problem.rules.size)

        problem = FourNeighboursProblem("1?01")
        assertEquals(
                setOf(FourNeighboursRule(booleanArrayOf(true, false, false, true)),
                        FourNeighboursRule(booleanArrayOf(true, true, false, true))),
                problem.rules)
    }

    @Test
    fun reverseTest() {
        val problem = FourNeighboursProblem("1???")
        val reversedProblem = problem.reverse()

        assertEquals(
                setOf(FourNeighboursRule(booleanArrayOf(false, false, false, false)),
                        FourNeighboursRule(booleanArrayOf(false, false, false, true)),
                        FourNeighboursRule(booleanArrayOf(false, false, true, false)),
                        FourNeighboursRule(booleanArrayOf(false, false, true, true)),
                        FourNeighboursRule(booleanArrayOf(false, true, false, false)),
                        FourNeighboursRule(booleanArrayOf(false, true, false, true)),
                        FourNeighboursRule(booleanArrayOf(false, true, true, true)),
                        FourNeighboursRule(booleanArrayOf(false, true, true, true))
                ),
                reversedProblem.rules)
    }
}