package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile.Companion.parseTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTileGenerator
import com.github.kornilova_l.util.FileNameCreator
import com.github.kornilova_l.util.Util
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

internal class SimpleGraphWithTilesTest {
    private val tempDir = File("temp-dir")
    private val tiles67 = parseTiles(FileNameCreator.getFile(IndependentSetTile.defaultISTilesDir, 6, 7, 3)!!)
    private val tiles58 = parseTiles(FileNameCreator.getFile(IndependentSetTile.defaultISTilesDir, 5, 8, 3)!!)

    @Test
    fun getGraph() {
        tempDir.mkdir()
        val tiles32 = IndependentSetTileGenerator(3, 2, 1, tempDir).tiles
        val tiles23 = IndependentSetTileGenerator(2, 3, 1, tempDir).tiles
        var tileGraph = SimpleGraphWithTiles.createInstance(tiles32, tiles23)
        assertEquals(7, tileGraph.size)

        assertEquals(15, tileGraph.edgeCount)

        tileGraph = SimpleGraphWithTiles.createInstance(tiles67, tiles58)
        assertEquals(2079, tileGraph.size)
        Util.deleteDir(tempDir.toPath())
    }

    @Test
    fun exportAndImport() {
        tempDir.mkdir()
        val tiles32 = IndependentSetTileGenerator(3, 2, 1, tempDir).tiles
        val tiles23 = IndependentSetTileGenerator(2, 3, 1, tempDir).tiles
        val graph = SimpleGraphWithTiles.createInstance(tiles32, tiles23)
        val file = File("temp.txt")
        graph.export(file)

        val parsedGraph = SimpleGraph(file)
        assertEquals(graph.graph, parsedGraph.graph)

        file.delete()
        Util.deleteDir(tempDir.toPath())
    }
}