package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleGraphWithTiles
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class OneOrTwoNeighboursTileSimpleGraphTest {
    @Test
    fun doTest() {
        val tiles67 = OneOrTwoNeighboursTile.parseTiles(OneOrTwoNeighboursTile.getTilesFile(5, 5, File("one_or_two_neighbours_tiles"))!!)
        val tiles58 = OneOrTwoNeighboursTile.parseTiles(OneOrTwoNeighboursTile.getTilesFile(4, 6, File("one_or_two_neighbours_tiles"))!!)
        val expectedGraph = SimpleGraphWithTiles.createInstance(tiles67, tiles58)

        val actualGraph = OneOrTwoNeighboursTileSimpleGraph.createInstance(
                OneOrTwoNeighboursTile.parseTiles(
                        OneOrTwoNeighboursTile.getTilesFile(4, 5, File("one_or_two_neighbours_tiles"))!!
                )
        )
        assertEquals(expectedGraph.size, actualGraph.size)
    }
}