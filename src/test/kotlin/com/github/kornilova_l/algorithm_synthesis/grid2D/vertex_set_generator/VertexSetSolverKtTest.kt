package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.Problem
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getRulePermutations
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VertexSetSolverKtTest {

    private fun testLabelingFunction(problem: Problem) {
        val iterations = 10000
        val labelingFunction = getLabelingFunction(problem)
        assertNotNull(labelingFunction)
        for (i in 0 until iterations) {
            val grid = generateGrid(10, 10)
            val independentSet = IndependentSetAlgorithm(grid, labelingFunction!!.k).independentSet
            val labeledGrid = labelingFunction.getLabels(independentSet)
            assertNotNull(labeledGrid)
            assertTrue(isRight(labeledGrid!!, problem))
        }
    }

    @Test
    fun labelingFunctionForIS() {
        val independentSetRules = hashSetOf(
                VertexRule("X"),
                VertexRule("N"),
                VertexRule("E"),
                VertexRule("S"),
                VertexRule("W"),
                VertexRule("NE"),
                VertexRule("NS"),
                VertexRule("NW"),
                VertexRule("ES"),
                VertexRule("EW"),
                VertexRule("SW"),
                VertexRule("NES"),
                VertexRule("NEW"),
                VertexRule("NSW"),
                VertexRule("ESW"),
                VertexRule("NESW")
        )
        testLabelingFunction(Problem(independentSetRules))
    }

    @Test
    fun labelingFunctionForInvertedIS() {
        val invertedISRules = hashSetOf(
                VertexRule("X"),
                VertexRule("XN"),
                VertexRule("XE"),
                VertexRule("XS"),
                VertexRule("XW"),
                VertexRule("XNE"),
                VertexRule("XNS"),
                VertexRule("XNW"),
                VertexRule("XES"),
                VertexRule("XEW"),
                VertexRule("XSW"),
                VertexRule("XNES"),
                VertexRule("XNEW"),
                VertexRule("XNSW"),
                VertexRule("XESW"),
                VertexRule("NESW")
        )
        testLabelingFunction(Problem(invertedISRules))
    }

    @Test
    fun someRulesTest() {
        val rules = HashSet<VertexRule>()
        rules.addAll(getRulePermutations(2, true))
        rules.addAll(getRulePermutations(3, true))
        rules.addAll(getRulePermutations(2, false))
        rules.addAll(getRulePermutations(3, false))
        rules.addAll(getRulePermutations(1, false))

        testLabelingFunction(Problem(rules))
    }

    @Test
    fun columnMinimalDominatingSet() {
        val rules = HashSet<VertexRule>()
        rules.addAll(Problem("10?0?").rules)
        rules.addAll(Problem("01?0?").rules)
        rules.addAll(Problem("00?1?").rules)
        rules.addAll(Problem("01?1?").rules)
        rules.addAll(Problem("11?0?").rules)
        rules.addAll(Problem("10?1?").rules)
        testLabelingFunction(Problem(rules))
    }

    private fun isRight(labeledGrid: Array<BooleanArray>, problem: Problem): Boolean {
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
                if (!problem.rules.contains(VertexRule(vertexSet.toString()))) {
                    return false
                }
            }
        }
        return true
    }
}