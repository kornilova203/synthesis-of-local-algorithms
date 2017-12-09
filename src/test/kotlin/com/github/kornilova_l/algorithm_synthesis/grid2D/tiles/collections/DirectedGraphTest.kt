package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph.Neighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import org.junit.Test

import org.junit.Assert.*
import java.io.File

class DirectedGraphTest {
    @Test
    fun graphSize() {
        var tileSet = TileSet(File("generated_tiles/4-4-1.txt"))
        var graph = DirectedGraph(tileSet)
        assertEquals(7, graph.size)
        assertEquals(392, graph.edgeCount)

        tileSet = TileSet(File("generated_tiles/3-3-1.txt"))
        graph = DirectedGraph(tileSet)
        assertEquals(2, graph.size)
        assertEquals((15 + 1) * 4, graph.edgeCount)
    }

    @Test
    fun twoByThreeTiles() {
        val tile1 = Tile("0 0 0\n0 0 0", 4)
        val tile2 = Tile("1 0 0\n0 0 0", 4)
        val tile3 = Tile("0 1 0\n0 0 0", 4)
        val tile4 = Tile("0 0 1\n0 0 0", 4)
        val tile5 = Tile("0 0 0\n1 0 0", 4)
        val tile6 = Tile("0 0 0\n0 1 0", 4)
        val tile7 = Tile("0 0 0\n0 0 1", 4)

        val tileSet = TileSet(File("generated_tiles/4-5-4.txt"))

        val graph = DirectedGraph(tileSet).neighbourhoods

        val neighbourhood = Neighbourhood(tile1, tile1, tile2, tile6, tile5)
        assertTrue(graph.contains(neighbourhood))

        val expected = hashSetOf(
                Neighbourhood(tile1, tile1, tile6, tile1, tile1),
                Neighbourhood(tile1, tile4, tile5, tile1, tile1),
                Neighbourhood(tile1, tile7, tile1, tile1, tile1),
                Neighbourhood(tile1, tile1, tile7, tile1, tile1),
                Neighbourhood(tile1, tile1, tile1, tile1, tile1),
                Neighbourhood(tile1, tile1, tile5, tile1, tile1),
                Neighbourhood(tile1, tile1, tile1, tile5, tile1),
                Neighbourhood(tile4, tile1, tile1, tile1, tile1),
                Neighbourhood(tile1, tile4, tile1, tile1, tile1),
                Neighbourhood(tile1, tile1, tile1, tile2, tile1),
                Neighbourhood(tile2, tile7, tile1, tile1, tile1),
                Neighbourhood(tile1, tile7, tile1, tile2, tile1),
                Neighbourhood(tile3, tile1, tile1, tile1, tile1),
                Neighbourhood(tile1, tile1, tile7, tile2, tile1),
                Neighbourhood(tile4, tile1, tile1, tile5, tile1),
                Neighbourhood(tile2, tile1, tile7, tile1, tile1),
                Neighbourhood(tile1, tile4, tile1, tile5, tile1),
                Neighbourhood(tile2, tile1, tile1, tile1, tile1),
                Neighbourhood(tile4, tile1, tile5, tile1, tile1)
        )
        for (n in expected) {
            assertTrue(graph.contains(n))
        }
        val count = graph.count { it.neighbours[POSITION.X] == tile1 }
        assertEquals(expected.size, count)
    }
}