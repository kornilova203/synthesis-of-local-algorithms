package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.util.FileNameCreator
import com.github.kornilova_l.util.ProgressBar
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths

fun main(args: Array<String>) {
    val files = IndependentSetTile.defaultISTilesDir.listFiles()
    val progressBar = ProgressBar(files.size)
    for (file in files) {
        progressBar.updateProgress()
        if (file.isDirectory) {
            continue
        }
        val n = FileNameCreator.getIntParameter(file.name, "n")!!
        val m = FileNameCreator.getIntParameter(file.name, "m")!!
        if (n <= m) {
            convert(file, File("independent_set_tiles/binary"))
        }
    }
    progressBar.finish()
}

private fun convert(file: File, outputDir: File) {
    val tiles = IndependentSetTile.parseTiles(file)
    val n = tiles.first().n
    val m = tiles.first().m
    val k = tiles.first().k
    val outputFile = createOutputFile(outputDir, n, m, k, tiles.size)
    FileOutputStream(outputFile).use { outputStream ->
        for (tile in tiles) {
            outputStream.write(tile.longsToString().toByteArray())
            outputStream.write("\n".toByteArray())

        }
    }
}

private fun createOutputFile(outputDir: File, n: Int, m: Int, k: Int, size: Int): File =
        Paths.get(outputDir.toString(), "${IndependentSetTile.name}-$n-$m-$k-$size.tiles").toFile()

