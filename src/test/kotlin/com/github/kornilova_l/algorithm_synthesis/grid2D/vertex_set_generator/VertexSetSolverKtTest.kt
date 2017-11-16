package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class VertexSetSolverKtTest {
    @Test
    fun toDimacsTest() {
        val file1 = File("generated_tiles/3-2-1.txt")
        val file2 = File("generated_tiles/2-3-1.txt")
        val graph = TileDirectedGraph(TileSet(file1), TileSet(file2))
        val dimacs = toDimacs(graph, hashSetOf(VertexRule(1)))
        assertEquals((32 - 1) * 7, dimacs.size) // number of reverted rules multiplied by number of nodes in graph
        val expected = File("src/test/resources/vertexSetSolver/to_dimacs_2_2_1.txt").readText()
        assertEquals(expected, dimacs.joinToString("", "", transform={ "${it.joinToString(" ", "")}\n" }))
    }
}