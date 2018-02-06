package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.DirectedGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile


class LabelingFunction {
    private val tileLabels: Map<BinaryTile, Boolean>
    private val n: Int
    private val m: Int

    constructor(tileLabels: Map<BinaryTile, Boolean>) {
        this.tileLabels = tileLabels
        this.n = tileLabels.entries.first().key.n
        this.m = tileLabels.entries.first().key.m
    }

    constructor(solution: List<Int>, graph: DirectedGraphWithTiles<*>) {
        tileLabels = HashMap()
        for (index in solution) {
            val id = Math.abs(index)
            tileLabels[graph.getTile(id)!!] = index > 0
        }
        this.n = tileLabels.entries.first().key.n
        this.m = tileLabels.entries.first().key.m
    }

    fun getLabels(independentSet: Array<BooleanArray>): Array<BooleanArray>? {
        val colouredGraph = Array(independentSet.size) { BooleanArray(independentSet[0].size) }
        for (i in independentSet.indices) {
            for (j in independentSet[i].indices) {
                val tile = BinaryTile(independentSet, i, j, n, m)
                val colour = tileLabels[tile]!!
                colouredGraph[i][j] = colour
            }
        }
        return colouredGraph
    }

    fun rotate(): LabelingFunction {
        val rotatedTileMap = HashMap<BinaryTile, Boolean>()
        for (tileLabel in tileLabels) {
            rotatedTileMap[tileLabel.key.rotate()] = tileLabel.value
        }
        return LabelingFunction(rotatedTileMap)
    }
}