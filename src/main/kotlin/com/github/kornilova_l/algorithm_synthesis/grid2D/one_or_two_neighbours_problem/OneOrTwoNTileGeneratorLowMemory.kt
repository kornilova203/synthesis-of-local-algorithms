package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTileGenerator.Companion.removeInvalid
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand
import com.github.kornilova_l.util.ProgressBar
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.nio.file.Paths

fun main(args: Array<String>) {
    val outputDir = File("one_or_two_neighbours_tiles/expanded")
    OneOrTwoNTileGeneratorLowMemory(File("one_or_two_neighbours_tiles/6-6.txt"),
            Expand.WIDTH, outputDir)

    OneOrTwoNTileGeneratorLowMemory(File("one_or_two_neighbours_tiles/5-7.txt"),
            Expand.WIDTH, outputDir)
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
        val outputFile = Paths.get(outputDir.toString(), "$n-$m.txt").toFile()
        FileOutputStream(outputFile).use { outputStream ->
            outputStream.write("$n $m\n".toByteArray())
            outputStream.write("$tilesCount\n".toByteArray())

            BufferedReader(FileReader(tempOutputFile)).use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    outputStream.write(line.toString().toByteArray())
                    outputStream.write("\n".toByteArray())
                    line = reader.readLine()
                }
            }
        }

        tempOutputFile.delete()
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
                    outputStream.write(validTile.toString().toByteArray())
                    outputStream.write("\n".toByteArray())
                }
                progressBar.updateProgress()
            }
        }
        progressBar.finish()
        return tilesCount

    }
}