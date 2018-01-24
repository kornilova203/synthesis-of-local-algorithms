package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.parseTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph.Neighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DirectedGraphTest {
    @Test
    fun graphSize() {
        var tileSet = parseTiles(File("generated_tiles/4-4-1.txt"))
        var graph = DirectedGraphWithTiles.createInstance(tileSet)
        assertEquals(7, graph.size)
        assertEquals(392, graph.edgeCount)

        tileSet = parseTiles(File("generated_tiles/3-3-1.txt"))
        graph = DirectedGraphWithTiles.createInstance(tileSet)
        assertEquals(2, graph.size)
        assertEquals((15 + 1) * 4, graph.edgeCount)
    }

    @Test
    fun twoByThreeTiles() {
        val tileSet = parseTiles(File("generated_tiles/4-5-4.txt"))
        val graph = DirectedGraphWithTiles.createInstance(tileSet)

        val id1 = graph.getId(IndependentSetTile.createInstance("0 0 0\n0 0 0", 4))
        val id2 = graph.getId(IndependentSetTile.createInstance("0 0 1\n0 0 0", 4))
        val id3 = graph.getId(IndependentSetTile.createInstance("0 0 0\n0 0 1", 4))
        val id4 = graph.getId(IndependentSetTile.createInstance("0 0 0\n1 0 0", 4))
        val id5 = graph.getId(IndependentSetTile.createInstance("1 0 0\n0 0 0", 4))
        val id6 = graph.getId(IndependentSetTile.createInstance("0 0 0\n0 1 0", 4))
        val id7 = graph.getId(IndependentSetTile.createInstance("0 1 0\n0 0 0", 4))

        val neighbourhood = Neighbourhood(id4, id1, id1, id5, id6)
        assertTrue(graph.neighbourhoods.contains(neighbourhood))

        val expected = hashSetOf(
                Neighbourhood(id1, id1, id1, id6, id1),
                Neighbourhood(id1, id1, id2, id4, id1),
                Neighbourhood(id1, id1, id3, id1, id1),
                Neighbourhood(id1, id1, id1, id3, id1),
                Neighbourhood(id1, id1, id1, id1, id1),
                Neighbourhood(id1, id1, id1, id4, id1),
                Neighbourhood(id1, id1, id1, id1, id4),
                Neighbourhood(id1, id2, id1, id1, id1),
                Neighbourhood(id1, id1, id2, id1, id1),
                Neighbourhood(id1, id1, id1, id1, id5),
                Neighbourhood(id1, id5, id3, id1, id1),
                Neighbourhood(id1, id1, id3, id1, id5),
                Neighbourhood(id1, id7, id1, id1, id1),
                Neighbourhood(id1, id1, id1, id3, id5),
                Neighbourhood(id1, id2, id1, id1, id4),
                Neighbourhood(id1, id5, id1, id3, id1),
                Neighbourhood(id1, id1, id2, id1, id4),
                Neighbourhood(id1, id5, id1, id1, id1),
                Neighbourhood(id1, id2, id1, id4, id1)
        )
        for (n in expected) {
            assertTrue(graph.neighbourhoods.contains(n))
        }
        val count = graph.neighbourhoods.count { it.get(POSITION.X) == 1 }
        assertEquals(expected.size, count)
    }
}