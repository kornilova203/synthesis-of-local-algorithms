package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTileGenerator.Companion.removeInvalid
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand
import com.github.kornilova_l.util.FileNameCreator
import com.github.kornilova_l.util.ProgressBar
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths

fun main(args: Array<String>) {
    val tilesDir = File("one_or_two_neighbours_tiles")
    val outputDir = File("one_or_two_neighbours_tiles/expanded")
    OneOrTwoNTileGeneratorLowMemory(FileNameCreator.getFile(tilesDir, 5, 6)!!,
            Expand.HEIGHT, outputDir)
}


/**
 * Class that generates [OneOrTwoNeighboursTile]
 * but does not keep all tiles in memory
 */
class OneOrTwoNTileGeneratorLowMemory(tilesFile: File, side: Expand, outputDir: File) {
    init {
        val parser = OneOrTwoNeighboursTileParser(tilesFile)
        val n: Int
        val m: Int
        if (side == Expand.WIDTH) {
            n = parser.n
            m = parser.m + 1
        } else {
            n = parser.n + 1
            m = parser.m
        }
        val tempOutputFile = Paths.get(outputDir.toString(), "$n-$m-temp.txt").toFile()
        val tilesCount = expandTiles(tempOutputFile, parser, side)
        val outputFile = Paths.get(outputDir.toString(), "${OneOrTwoNeighboursTile.name}-$n-$m-$tilesCount.tiles").toFile()
        tempOutputFile.renameTo(outputFile)
    }

    private fun expandTiles(tempOutputFile: File, parser: OneOrTwoNeighboursTileParser, side: Expand): Int {
        var tilesCount = 0
        val progressBar = ProgressBar(parser.size)
        FileOutputStream(tempOutputFile).use { outputStream ->
            for (oneOrTwoNeighboursTile in parser) {
                val expandedTiles = oneOrTwoNeighboursTile.getAllExpandedTiles(side)
                val validTiles = removeInvalid(expandedTiles)
                tilesCount += validTiles.size
                for (validTile in validTiles) {
                    outputStream.write(validTile.longsToString().toByteArray())
                    outputStream.write("\n".toByteArray())
                }
                progressBar.updateProgress()
            }
        }
        progressBar.finish()
        return tilesCount

    }
}