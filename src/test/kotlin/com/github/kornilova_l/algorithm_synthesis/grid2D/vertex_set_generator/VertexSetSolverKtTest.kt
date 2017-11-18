package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class VertexSetSolverKtTest {
    @Test
    fun toDimacsTest() {
        var file1 = File("generated_tiles/3-2-1.txt")
        var file2 = File("generated_tiles/2-3-1.txt")
        var graph = TileDirectedGraph(TileSet(file1), TileSet(file2))
        var clauses = toDimacs(graph, hashSetOf(VertexRule(1)))
//        println(clauses.joinToString("", "", transform={ "${it.joinToString(" ", "")}\n" }))
        var expected = parseClauses(File("src/test/resources/vertexSetSolver/to_dimacs_2_2_1.txt").readText())
        assertTrue(expected == clauses)

        file1 = File("generated_tiles/2-2-1.txt")
        file2 = File("generated_tiles/1-3-1.txt")
        graph = TileDirectedGraph(TileSet(file1), TileSet(file2))
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
    fun requireNeighboursEqualTest() {
        val file1 = File("generated_tiles/2-2-1.txt")
        val file2 = File("generated_tiles/1-3-1.txt")
        val graph = TileDirectedGraph(TileSet(file1), TileSet(file2))
        val simplifiedGraph = getSimplifiedGraph(graph)
        val clauses = HashSet<Set<Int>>()
        for (tile in graph.graph.keys) {
            val node = graph.graph[tile]!!
            val clausesForEqualNeighbours = requireNeighboursEqual(node, graph, simplifiedGraph[tile]!!)
            clauses.addAll(clausesForEqualNeighbours)
        }
        assertEquals(clauses, hashSetOf(
                hashSetOf(-1, 2),
                hashSetOf(1, -2),
                hashSetOf(-1, 3),
                hashSetOf(1, -3)
        ))
    }

    @Test
    fun getLabelingFunctionTest() {
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