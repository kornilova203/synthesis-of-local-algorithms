package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph.Neighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
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
        val tileSet = TileSet(File("generated_tiles/4-5-4.txt"))
        val graph = DirectedGraph(tileSet)

        val id1 = graph.getId(Tile("0 0 0\n0 0 0", 4))
        val id2 = graph.getId(Tile("1 0 0\n0 0 0", 4))
        val id3 = graph.getId(Tile("0 1 0\n0 0 0", 4))
        val id4 = graph.getId(Tile("0 0 1\n0 0 0", 4))
        val id5 = graph.getId(Tile("0 0 0\n1 0 0", 4))
        val id6 = graph.getId(Tile("0 0 0\n0 1 0", 4))
        val id7 = graph.getId(Tile("0 0 0\n0 0 1", 4))

        val neighbourhood = Neighbourhood(id1, id1, id2, id6, id5)
        assertTrue(graph.neighbourhoods.contains(neighbourhood))

        val expected = hashSetOf(
                Neighbourhood(id1, id1, id6, id1, id1),
                Neighbourhood(id1, id4, id5, id1, id1),
                Neighbourhood(id1, id7, id1, id1, id1),
                Neighbourhood(id1, id1, id7, id1, id1),
                Neighbourhood(id1, id1, id1, id1, id1),
                Neighbourhood(id1, id1, id5, id1, id1),
                Neighbourhood(id1, id1, id1, id5, id1),
                Neighbourhood(id4, id1, id1, id1, id1),
                Neighbourhood(id1, id4, id1, id1, id1),
                Neighbourhood(id1, id1, id1, id2, id1),
                Neighbourhood(id2, id7, id1, id1, id1),
                Neighbourhood(id1, id7, id1, id2, id1),
                Neighbourhood(id3, id1, id1, id1, id1),
                Neighbourhood(id1, id1, id7, id2, id1),
                Neighbourhood(id4, id1, id1, id5, id1),
                Neighbourhood(id2, id1, id7, id1, id1),
                Neighbourhood(id1, id4, id1, id5, id1),
                Neighbourhood(id2, id1, id1, id1, id1),
                Neighbourhood(id4, id1, id5, id1, id1)
        )
        for (n in expected) {
            assertTrue(graph.neighbourhoods.contains(n))
        }
        val count = graph.neighbourhoods.count { it.get(POSITION.X) == 1 }
        assertEquals(expected.size, count)
    }
}