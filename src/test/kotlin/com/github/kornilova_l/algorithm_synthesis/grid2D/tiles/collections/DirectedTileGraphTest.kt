package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import org.junit.Test

import org.junit.Assert.*
import java.io.File

class DirectedTileGraphTest {
    @Test
    fun graphSize() {
        var tileSet = TileSet(File("generated_tiles/4-4-1.txt"))
        var graph = DirectedTileGraph(tileSet)
        assertEquals(7, graph.size)
        assertEquals(392, graph.edgeCount)

        tileSet = TileSet(File("generated_tiles/3-3-1.txt"))
        graph = DirectedTileGraph(tileSet)
        assertEquals(2, graph.size)
        assertEquals((15 + 1) * 4, graph.edgeCount)
    }
}