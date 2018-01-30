package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import com.github.kornilova_l.util.Logger
import java.io.File
import java.nio.file.Paths

private val dir = File("independent_set_tiles")
private val logger = Logger(File("precalculate_is_tiles.log"))

/**
 * Run program without parameters to precalculate all tiles with [getParametersSet] parameters
 * Run program with parameters n, p and k to precalculate specific tile
 * This program outputs precalculated tiles to [dir] directory
 */
fun main(args: Array<String>) {
    createDir(dir, logger)
    if (args.isEmpty()) {
        precalculateAll()
    } else {
        if (args.size != 3) {
            System.err.println("To precalculate specific tile you must specify n, m and k")
            return
        }
        val n = Integer.parseInt(args[0])
        val m = Integer.parseInt(args[1])
        val k = Integer.parseInt(args[2])
        precalculateSpecificTile(n, m, k)
    }
}

fun createDir(dir: File, logger: Logger? = null) {
    if (!dir.exists()) {
        dir.mkdir()
        logger?.info("created directory ${dir.absolutePath}")
    }
}

private fun precalculateSpecificTile(n: Int, m: Int, k: Int) {
    if (IndependentSetTile.getTilesFile(n, m, k, dir) == null) { // if was not precalculated
        println("Calculate $n x $m tile in power $k")
        IndependentSetTileGenerator(n, m, k, dir)
                .export(dir)
    }
}

private fun precalculateAll() {
    val parametersSet = getParametersSet()
    for (parameters in parametersSet) {
        val n = parameters.n
        val m = parameters.m
        val k = parameters.k
        if (Paths.get(dir.toString(), "$n-$m-$k.txt").toFile().exists()) { // if was precalculated
            logger.info("Tiles n = $n m = $m k = $k were already calculated")
        } else {
            logger.info("Calculate $n x $m tile in power $k")
            IndependentSetTileGenerator(n, m, k, dir).export(dir)
        }
    }
}