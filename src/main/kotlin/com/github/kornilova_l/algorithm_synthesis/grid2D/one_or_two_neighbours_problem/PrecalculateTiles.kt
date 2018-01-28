package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import java.io.File

const val dirName = "one_or_two_neighbours_tiles"

fun main(args: Array<String>) {
//    precalculateSpecificTile()
    precalculateAll()
}

private fun precalculateSpecificTile() {
    val n = 3
    val m = 4
    if (OneOrTwoNeighboursTile.getTilesFile(n, m, File(dirName)) == null) { // if was not precalculated
        println("Calculate $n x $m")
        OneOrTwoNeighboursTileGenerator(n, m, File(dirName))
                .export(File(dirName))
    }
}

private fun precalculateAll() {
    val parametersSet = getParametersSet(1)
    for (parameters in parametersSet) {
        val n = parameters.n
        val m = parameters.m
        if (OneOrTwoNeighboursTile.getTilesFile(n, m, File(dirName)) == null) { // if was not precalculated
            println("Calculate $n x $m")
            OneOrTwoNeighboursTileGenerator(n, m, File(dirName)).export(File(dirName))
        }
    }
}