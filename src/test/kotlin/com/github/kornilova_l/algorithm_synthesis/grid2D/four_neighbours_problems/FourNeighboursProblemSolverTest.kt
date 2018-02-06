package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem.FourNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem.FourNeighboursRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FourNeighboursProblemSolverTest {

    @Test
    fun simpleTest() {
        val all = hashSetOf(
                FourNeighboursRule("TL TR BR BL")
        )
        testLabelingFunction(FourNeighboursProblem(all))
    }

    private fun testLabelingFunction(problem: FourNeighboursProblem) {
        val iterations = 10000
        val labelingFunction = FourNeighboursProblemSolver().getLabelingFunction(problem)
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

    private fun isRight(labeledGrid: Array<BooleanArray>, problem: FourNeighboursProblem): Boolean {
        val n = labeledGrid.size
        val m = labeledGrid[0].size
        for (i in 0 until n) {
            for (j in 0 until m) {
                val vertexSet = StringBuilder()
                if (labeledGrid[i][j]) {
                    vertexSet.append("TL ")
                }
                if (labeledGrid[(i + 1) % n][j]) {
                    vertexSet.append("BL ")
                }
                if (labeledGrid[i][(j + 1) % m]) {
                    vertexSet.append("TR ")
                }
                if (labeledGrid[(i + 1) % n][(j + 1) % m]) {
                    vertexSet.append("BR ")
                }
                if (!problem.rules.contains(FourNeighboursRule(vertexSet.toString()))) {
                    return false
                }
            }
        }
        return true
    }
}