package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.Grid2D
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileMap

class ColouringFunction constructor(private val tileColours: TileMap<Int>) {

    fun getGraphColoured(grid2d: Grid2D): Array<IntArray>? {
        val colouredGraph = Array(grid2d.n) { IntArray(grid2d.m) }

        val independentSet = IndependentSetAlgorithm(grid2d, tileColours.k).independentSet
        for (i in independentSet.indices) {
            for (j in independentSet[i].indices) {
                val tile = IndependentSetTile.createInstance(independentSet, i, j, tileColours.n, tileColours.m, tileColours.k)
                val colour = tileColours[tile]
                colouredGraph[i][j] = colour
            }
        }
        return colouredGraph
    }
}
