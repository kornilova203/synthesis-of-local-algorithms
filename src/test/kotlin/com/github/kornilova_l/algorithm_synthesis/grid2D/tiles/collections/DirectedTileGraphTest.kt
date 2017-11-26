package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import org.junit.Test

import org.junit.Assert.*
import java.io.File

class DirectedTileGraphTest {
    @Test
    fun graphSize() {
        val tileSet = TileSet(File("generated_tiles/4-4-1.txt"))
        val graph = DirectedTileGraph(tileSet)
        assertEquals(7, graph.size)
        assertEquals(tileSet.validTiles.size * 4, graph.edgeCount)
    }
}