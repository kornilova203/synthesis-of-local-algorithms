package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import java.io.File

fun main(args: Array<String>) {
    val parametersSet = getParametersSet(1)
    for (parameters in parametersSet) {
        val n = parameters.n
        val m = parameters.m
        val k = parameters.k
        if (File("generated_tiles/$n-$m-$k.txt").exists()) { // if was precalculated
            continue
        }
        println("Calculate $n x $m tile in power $k")
        TileGenerator(n, m, k, File("generated_tiles")).exportToFile(File("generated_tiles"), false)
    }
}