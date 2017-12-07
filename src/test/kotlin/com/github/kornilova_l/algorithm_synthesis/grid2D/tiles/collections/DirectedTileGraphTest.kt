package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedTileGraph.Neighbourhood
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

        val graph = DirectedTileGraph(tileSet).graph

        val neighbourhoods5 = graph[tile5]
        assertEquals(hashSetOf(Neighbourhood(tile1, tile1, tile2, tile6, tile5)), neighbourhoods5)

        val neighbourhoods1 = graph[tile1]
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
        assertEquals(expected, neighbourhoods1)
    }
}