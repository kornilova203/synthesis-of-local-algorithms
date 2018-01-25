package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile

class ColouringFunction constructor(private val tileColours: Map<BinaryTile, Int>) {

    fun getGraphColoured(independentSet: Array<BooleanArray>): Array<IntArray> {
        val colouredGraph = Array(independentSet.size) { IntArray(independentSet[0].size) }

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
