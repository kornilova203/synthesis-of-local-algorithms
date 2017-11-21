package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import org.junit.Test

import org.junit.Assert.*
import java.io.File

class DirectedTileGraphTest {
    @Test
    fun graphSize() {
        val tileSet1 = TileSet(File("generated_tiles/2-3-1.txt"))
        val tileSet2 = TileSet(File("generated_tiles/3-2-1.txt"))
        val graph = DirectedTileGraph(tileSet1, tileSet2)
        assertEquals(7, graph.size)
        assertEquals(32, graph.edgeCount)
    }
}