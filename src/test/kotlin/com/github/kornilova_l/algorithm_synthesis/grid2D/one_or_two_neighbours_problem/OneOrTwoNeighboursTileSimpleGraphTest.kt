package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleGraphWithTiles
import com.github.kornilova_l.util.FileNameCreator
import org.junit.Assert.assertEquals
import org.junit.Test

class OneOrTwoNeighboursTileSimpleGraphTest {
    @Test
    fun doTest() {
        val tiles67 = OneOrTwoNeighboursTile.parseTiles(FileNameCreator.getFile(OneOrTwoNeighboursTile.defaultTilesDir, 5, 5)!!)
        val tiles58 = OneOrTwoNeighboursTile.parseTiles(FileNameCreator.getFile(OneOrTwoNeighboursTile.defaultTilesDir, 4, 6)!!)
        val expectedGraph = SimpleGraphWithTiles.createInstance(tiles67, tiles58)

        val actualGraph = OneOrTwoNeighboursTileSimpleGraph.createInstance(
                OneOrTwoNeighboursTile.parseTiles(
                        FileNameCreator.getFile(OneOrTwoNeighboursTile.defaultTilesDir, 4, 5)!!
                )
        )
        assertEquals(expectedGraph.size, actualGraph.size)
    }
}