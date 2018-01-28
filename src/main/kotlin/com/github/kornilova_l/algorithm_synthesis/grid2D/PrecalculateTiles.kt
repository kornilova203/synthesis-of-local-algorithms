package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTileGenerator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import java.io.File

fun main(args: Array<String>) {
    precalculateSpecificTile()
//    precalculateAll()
}

private fun precalculateSpecificTile() {
    val n = 8
    val m = 9
    val k = 4
    val dir = File("independent_set_tiles")
    if (IndependentSetTile.getTilesFile(n, m, k, dir) == null) { // if was not precalculated
        println("Calculate $n x $m tile in power $k")
        IndependentSetTileGenerator(n, m, k, dir)
                .export(dir)
    }
}

private fun precalculateAll() {
    val parametersSet = getParametersSet(1)
    for (parameters in parametersSet) {
        val n = parameters.n
        val m = parameters.m
        val k = parameters.k
        if (!File("independent_set_tiles/$n-$m-$k.txt").exists()) { // if was precalculated
            println("Calculate $n x $m tile in power $k")
            IndependentSetTileGenerator(n, m, k, File("independent_set_tiles")).export(File("independent_set_tiles"))
        }
    }
}