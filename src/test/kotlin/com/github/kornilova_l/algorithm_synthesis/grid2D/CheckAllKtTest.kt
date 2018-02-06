package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.*
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursRule
import gnu.trove.list.array.TIntArrayList
import org.junit.Assert.*
import org.junit.Test

class CheckAllKtTest {

    @Test
    fun isUnsolvableTest() {
        val unsolvable = TIntArrayList()
        unsolvable.add(15)
        assertTrue(isUnsolvable(7, unsolvable))

        unsolvable.clear()
        unsolvable.add(15)
        assertFalse(isUnsolvable(31, unsolvable))
    }

    @Test
    fun isSolvableTest() {
        val solvable = TIntArrayList()
        solvable.add(15)
        assertFalse(isSolvable(7, solvable))

        solvable.clear()
        solvable.add(7)
        assertTrue(isSolvable(15, solvable))

        solvable.clear()
        solvable.add(8)
        assertTrue(isSolvable(9, solvable))
    }

    @Test
    fun toSetOfVertexRulesTest() {
        val problem = FiveNeighboursProblem(7)
        assertEquals(hashSetOf(FiveNeighboursRule("N"), FiveNeighboursRule("E"), FiveNeighboursRule("S")), problem.rules)
    }

    /* this test takes too much time */
//    @Test
//    fun checkAllSolvable() {
//        val solvable = parseInts(solvableFile)
//        val problems = ArrayList<Problem>()
//        for (s in solvable) {
//            problems.add(Problem(s))
//        }
//        println(solvable.size())
//        val newSolvable = tryToFindSolutionForEachProblem(problems)
//        assertEquals(solvable.size(), newSolvable.size)
//    }

    @Test
    fun checkSolvableWithUnsolvable() {
        val solvable = parseInts(solvableFile)
        val unsolvable = parseInts(unsolvableFile)
        for (s in solvable) {
            if (isUnsolvable(s, unsolvable)) {
                assertTrue("Solvable $s is unsolvable :(", false)
            }
        }
    }
}