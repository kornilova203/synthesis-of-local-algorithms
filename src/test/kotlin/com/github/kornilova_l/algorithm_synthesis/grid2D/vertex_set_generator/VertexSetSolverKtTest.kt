package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedTileGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class VertexSetSolverKtTest {
    @Test
    fun toDimacsTest() {
        var file = File("generated_tiles/3-3-1.txt")
        var graph = DirectedTileGraph(TileSet(file))
        var clauses = toDimacs(graph, hashSetOf(VertexRule(1)))
//        println(clauses.joinToString("", "", transform={ "${it.joinToString(" ", "")}\n" }))
        var expected = parseClauses(File("src/test/resources/vertexSetSolver/to_dimacs_1_1_1.txt").readText())
        assertTrue(expected == clauses)

        file = File("generated_tiles/3-4-1.txt")
        graph = DirectedTileGraph(TileSet(file))
        clauses = toDimacs(graph, hashSetOf(VertexRule(1)))
//        println(clauses.joinToString("", "", transform={ "${it.joinToString(" ", "")}\n" }))
        expected = parseClauses(File("src/test/resources/vertexSetSolver/to_dimacs_1_2_1.txt").readText())
        assertTrue(expected == clauses)
    }

    private fun parseClauses(text: String): Set<Set<Int>> {
        val lines = text.split("\n")
        val clauses = HashSet<Set<Int>>()
        for (line in lines) {
            val clause = HashSet<Int>()
            line.split(" ").filter { it != "" }.mapTo(clause) { Integer.parseInt(it) }
            clauses.add(clause)
        }
        return clauses
    }

    @Test
    fun labelingFunctionForIS() {
        val iterations = 1000
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
        val labelingFunction = getLabelingFunction(independentSetRules)
        assertNotNull(labelingFunction)
        for (i in 0 until iterations) {
            val grid = generateGrid(10, 10)
            val labeledGrid = labelingFunction!!.getLabels(grid)
            assertNotNull(labeledGrid)
            assertTrue(isRight(labeledGrid!!, independentSetRules))
        }
    }

    @Test
    fun labelingFunctionForInvertedIS() {
        val iterations = 1000
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
        val labelingFunction = getLabelingFunction(invertedISRules)
        assertNotNull(labelingFunction)
        for (i in 0 until iterations) {
            val grid = generateGrid(10, 10)
            val labeledGrid = labelingFunction!!.getLabels(grid)
            assertNotNull(labeledGrid)
            assertTrue(isRight(labeledGrid!!, invertedISRules))
        }
    }

    private fun isRight(labeledGrid: Array<BooleanArray>, rules: HashSet<VertexRule>): Boolean {
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
                if (!rules.contains(VertexRule(vertexSet.toString()))) {
                    return false
                }
            }
        }
        return true
    }
}