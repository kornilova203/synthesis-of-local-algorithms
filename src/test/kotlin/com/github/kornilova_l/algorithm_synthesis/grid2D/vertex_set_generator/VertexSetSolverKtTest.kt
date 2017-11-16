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
        assertEquals(7, dimacs.size)
        assertEquals("[]", dimacs.toString())
    }
}