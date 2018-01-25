package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.Grid2D
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile

class ColouringFunction constructor(private val tileColours: Map<BinaryTile, Int>, val k: Int) {

    fun getGraphColoured(grid2d: Grid2D): Array<IntArray>? {
        val colouredGraph = Array(grid2d.n) { IntArray(grid2d.m) }

        val independentSet = IndependentSetAlgorithm(grid2d, k).independentSet
        for (i in independentSet.indices) {
            for (j in independentSet[i].indices) {
                val tile = BinaryTile(independentSet, i, j, tileColours.entries.first().key.n, tileColours.entries.first().key.m)
                val colour = tileColours[tile]!!
                colouredGraph[i][j] = colour
            }
        }
        return colouredGraph
    }
}
