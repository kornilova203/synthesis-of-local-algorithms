package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.util.FileNameCreator
import com.github.kornilova_l.util.Logger
import java.io.File

private val dir = IndependentSetTile.defaultISTilesDir
private val logger = Logger(File("precalculate_is_tiles.log"))

/**
 * Run program without parameters to precalculate all tiles with [getParametersSet] parameters
 * Run program with parameters n, p and k to precalculate specific tile
 * This program outputs precalculated tiles to [dir] directory
 */
fun main(args: Array<String>) {
    createDir(dir, logger)
    if (args.size == 1) {
        precalculateAll(getParametersSet(Integer.parseInt(args[0])))
    } else if (args.size == 3) {
        val n = Integer.parseInt(args[0])
        val m = Integer.parseInt(args[1])
        val k = Integer.parseInt(args[2])
        precalculateSpecificTile(n, m, k)
    }
    System.err.println("Specify difficulty or n, m and k")
}

fun createDir(dir: File, logger: Logger? = null) {
    if (!dir.exists()) {
        dir.mkdir()
        logger?.info("created directory ${dir.absolutePath}")
    }
}

private fun precalculateSpecificTile(n: Int, m: Int, k: Int) {
    if (FileNameCreator.getFile(dir, n, m, k) == null) { // if was not precalculated
        println("Calculate $n x $m tile in power $k")
        IndependentSetTileGenerator(n, m, k, dir)
    }
}

private fun precalculateAll(parametersSet: List<Parameters>) {
    for (parameters in parametersSet) {
        val n = parameters.n
        val m = parameters.m
        val k = parameters.k
        if (FileNameCreator.getFile(dir, n, m, k) != null) { // if was precalculated
            logger.info("Tiles n = $n m = $m k = $k were already calculated")
        } else {
            logger.info("Calculate $n x $m tile in power $k")
            IndependentSetTileGenerator(n, m, k, dir)
        }
    }
}