package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileMap


class LabelingFunction {
    private val tileLabels: TileMap<Boolean>
    val k: Int

    constructor(tileLabels: TileMap<Boolean>) {
        this.tileLabels = tileLabels
        k = tileLabels.k
    }

    constructor(solution: List<Int>, graph: DirectedGraphWithTiles) {
        tileLabels = TileMap(graph.n, graph.m, graph.k)
        for (index in solution) {
            val id = Math.abs(index)
            tileLabels.put(graph.getTile(id)!!, index > 0)
        }
        k = tileLabels.k
    }

    fun getLabels(independentSet: Array<BooleanArray>): Array<BooleanArray>? {
        val colouredGraph = Array(independentSet.size) { BooleanArray(independentSet[0].size) }
        for (i in independentSet.indices) {
            for (j in independentSet[i].indices) {
                val tile = Tile(independentSet, i, j, tileLabels.n, tileLabels.m, tileLabels.k)
                val colour = tileLabels[tile]
                colouredGraph[i][j] = colour
            }
        }
        return colouredGraph
    }

    fun rotate(): LabelingFunction {
        return LabelingFunction(tileLabels.rotate())
    }
}