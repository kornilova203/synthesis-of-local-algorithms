package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.graphs.FiveNeighboursDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.graphs.FiveNeighboursGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.createDir
import com.github.kornilova_l.util.FileNameCreator
import com.github.kornilova_l.util.Logger
import java.io.File
import java.nio.file.Paths

private val tilesDir = IndependentSetTile.defaultISTilesDir
private val graphsDir = Paths.get(tilesDir.toString(), "five_neighbours_directed_graphs").toFile()
private val logger = Logger(File("precalculate_is_tiles.log"))

/**
 * Precalculate graphs and export them to files.
 * So [FiveNeighboursDirectedGraph] instances can be created and used.
 */
fun main(args: Array<String>) {
    if (!tilesDir.exists()) {
        logger.error("Tiles directory does not exist. ${tilesDir.absolutePath}")
        return
    }
    createDir(graphsDir, logger)
    val files = tilesDir.listFiles()
    for (i in 0 until files.size) {
        val file = files[i]
        if (FileNameCreator.getExtension(file.name) == "tiles") {
            val n = FileNameCreator.getIntParameter(file.name, "n")!!
            val m = FileNameCreator.getIntParameter(file.name, "m")!!
            if (n < 3 || m < 3) {
                continue
            }
            if (FileNameCreator.getFile(graphsDir, n - 2, m - 2) != null) { // graph tiles are smaller than sourse tile
                logger.info("Graph was already calculated. Tiles file: ${file.name}")
                continue
            }
            logger.info("Process tiles from file: ${file.name}")
            val tileSet = IndependentSetTile.parseTiles(file)
            val graph = FiveNeighboursGraphWithTiles.createInstance(tileSet)
            logger.info("Start exporting tiles...")
            graph.exportTiles(graphsDir)
            println("Start exporting graph...")
            graph.export(graphsDir)
        }
    }
}