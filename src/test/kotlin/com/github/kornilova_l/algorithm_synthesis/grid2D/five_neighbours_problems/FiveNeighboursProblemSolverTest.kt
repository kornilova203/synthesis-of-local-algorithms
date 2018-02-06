package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem.getRulePermutations
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

open class FiveNeighboursProblemSolverTest {
    private fun testLabelingFunction(problem: FiveNeighboursProblem) {
        val iterations = 10000
        val labelingFunction = FiveNeighboursProblemSolver().getLabelingFunction(problem)
        assertNotNull(labelingFunction)
        labelingFunction!!
        for (i in 0 until iterations) {
            val grid = generateGrid(10, 10)
            val independentSet = IndependentSetAlgorithm(grid, labelingFunction.second).independentSet
            val labeledGrid = labelingFunction.first.getLabels(independentSet)
            assertNotNull(labeledGrid)
            assertTrue(isRight(labeledGrid!!, problem))
        }
    }

    @Test
    fun labelingFunctionForIS() {
        val independentSetRules = hashSetOf(
                FiveNeighboursRule("X"),
                FiveNeighboursRule("N"),
                FiveNeighboursRule("E"),
                FiveNeighboursRule("S"),
                FiveNeighboursRule("W"),
                FiveNeighboursRule("NE"),
                FiveNeighboursRule("NS"),
                FiveNeighboursRule("NW"),
                FiveNeighboursRule("ES"),
                FiveNeighboursRule("EW"),
                FiveNeighboursRule("SW"),
                FiveNeighboursRule("NES"),
                FiveNeighboursRule("NEW"),
                FiveNeighboursRule("NSW"),
                FiveNeighboursRule("ESW"),
                FiveNeighboursRule("NESW")
        )
        testLabelingFunction(FiveNeighboursProblem(independentSetRules))
    }

    @Test
    fun labelingFunctionForInvertedIS() {
        val invertedISRules = hashSetOf(
                FiveNeighboursRule("X"),
                FiveNeighboursRule("XN"),
                FiveNeighboursRule("XE"),
                FiveNeighboursRule("XS"),
                FiveNeighboursRule("XW"),
                FiveNeighboursRule("XNE"),
                FiveNeighboursRule("XNS"),
                FiveNeighboursRule("XNW"),
                FiveNeighboursRule("XES"),
                FiveNeighboursRule("XEW"),
                FiveNeighboursRule("XSW"),
                FiveNeighboursRule("XNES"),
                FiveNeighboursRule("XNEW"),
                FiveNeighboursRule("XNSW"),
                FiveNeighboursRule("XESW"),
                FiveNeighboursRule("NESW")
        )
        testLabelingFunction(FiveNeighboursProblem(invertedISRules))
    }

    @Test
    fun someRulesTest() {
        val rules = HashSet<FiveNeighboursRule>()
        rules.addAll(getRulePermutations(2, true))
        rules.addAll(getRulePermutations(3, true))
        rules.addAll(getRulePermutations(2, false))
        rules.addAll(getRulePermutations(3, false))
        rules.addAll(getRulePermutations(1, false))

        testLabelingFunction(FiveNeighboursProblem(rules))
    }

    @Test
    fun columnMinimalDominatingSet() {
        val rules = HashSet<FiveNeighboursRule>()
        rules.addAll(FiveNeighboursProblem("10?0?").rules)
        rules.addAll(FiveNeighboursProblem("01?0?").rules)
        rules.addAll(FiveNeighboursProblem("00?1?").rules)
        rules.addAll(FiveNeighboursProblem("01?1?").rules)
        rules.addAll(FiveNeighboursProblem("11?0?").rules)
        rules.addAll(FiveNeighboursProblem("10?1?").rules)
        testLabelingFunction(FiveNeighboursProblem(rules))
    }

    private fun isRight(labeledGrid: Array<BooleanArray>, problem: FiveNeighboursProblem): Boolean {
        val n = labeledGrid.size
        val m = labeledGrid[0].size
        for (i in 0 until n) {
            for (j in 0 until m) {
                val vertexSet = StringBuilder()
                if (labeledGrid[i][j]) {
                    vertexSet.append("X")
                }
                if (labeledGrid[(i + 1) % n][j]) {
                    vertexSet.append("S")
                }
                if (labeledGrid[i][(j + 1) % m]) {
                    vertexSet.append("E")
                }
                if (labeledGrid[(i - 1 + n) % n][j]) {
                    vertexSet.append("N")
                }
                if (labeledGrid[i][(j - 1 + m) % m]) {
                    vertexSet.append("W")
                }
                if (!problem.rules.contains(FiveNeighboursRule(vertexSet.toString()))) {
                    return false
                }
            }
        }
        return true
    }
}