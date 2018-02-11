package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.graphs

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.FourNeighbourhood
import com.github.kornilova_l.util.FileNameCreator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FourNeighboursGraphWithTilesTest {

    @Test
    fun smallGraphTest() {
        val tileSet = IndependentSetTile.parseTiles(FileNameCreator.getFile(IndependentSetTile.defaultISTilesDir, 2, 2, 1)!!)
        val graph = FourNeighboursGraphWithTiles.createInstance(tileSet)

        val idOne = graph.getId(IndependentSetTile.createInstance("1", 1))
        val idZero = graph.getId(IndependentSetTile.createInstance("0", 1))

        val expectedGraph = setOf(
                FourNeighbourhood(idZero, idZero, idZero, idZero),
                FourNeighbourhood(idOne, idZero, idZero, idZero),
                FourNeighbourhood(idZero, idOne, idZero, idZero),
                FourNeighbourhood(idZero, idZero, idOne, idZero),
                FourNeighbourhood(idZero, idZero, idZero, idOne),
                FourNeighbourhood(idOne, idZero, idOne, idZero),
                FourNeighbourhood(idZero, idOne, idZero, idOne)
        )

        assertEquals(expectedGraph, graph.neighbourhoods)
    }

    @Test
    fun graphTest() {
        val tileSet = IndependentSetTile.parseTiles(FileNameCreator.getFile(IndependentSetTile.defaultISTilesDir, 4, 4, 1)!!)
        val graph = FourNeighboursGraphWithTiles.createInstance(tileSet)

        assertEquals(3, graph.n)
        assertEquals(3, graph.m)
        assertEquals(1, graph.k)
        assertEquals(39, graph.size)

        val idTopLeft = graph.getId(IndependentSetTile.createInstance("010\n001\n100", 1))
        val idTopRight = graph.getId(IndependentSetTile.createInstance("101\n010\n001", 1))
        val idBottomRight = graph.getId(IndependentSetTile.createInstance("010\n001\n100", 1))
        val idBottomLeft = graph.getId(IndependentSetTile.createInstance("001\n100\n010", 1))

        assertTrue(graph.neighbourhoods.contains(FourNeighbourhood(idTopLeft, idTopRight, idBottomRight, idBottomLeft)))
    }
}