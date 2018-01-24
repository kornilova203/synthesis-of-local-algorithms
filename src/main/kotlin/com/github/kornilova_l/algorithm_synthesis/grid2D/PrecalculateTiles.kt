package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.IndependentSetTileGenerator
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
    if (!File("generated_tiles/$n-$m-$k.txt").exists()) { // if was precalculated
        println("Calculate $n x $m tile in power $k")
        IndependentSetTileGenerator(n, m, k, File("generated_tiles"))
                .export(File("generated_tiles"))
    }
}

private fun precalculateAll() {
    val parametersSet = getParametersSet(1)
    for (parameters in parametersSet) {
        val n = parameters.n
        val m = parameters.m
        val k = parameters.k
        if (!File("generated_tiles/$n-$m-$k.txt").exists()) { // if was precalculated
            println("Calculate $n x $m tile in power $k")
            IndependentSetTileGenerator(n, m, k, File("generated_tiles")).export(File("generated_tiles"))
        }
    }
}