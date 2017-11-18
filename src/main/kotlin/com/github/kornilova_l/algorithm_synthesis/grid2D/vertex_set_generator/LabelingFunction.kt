package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.Grid2D
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileMap


class LabelingFunction {
    private val tileLabels: TileMap<Boolean>

    constructor(tileLabels: TileMap<Boolean>) {
        this.tileLabels = tileLabels
    }

    constructor(solution: List<Int>, graph: TileDirectedGraph) {
        tileLabels = TileMap(graph.n, graph.m, graph.k)
        for (index in solution) {
            val id = Math.abs(index)
            tileLabels.put(graph.getTile(id)!!, index > 0)
        }
    }

    fun getLabels(grid2d: Grid2D): Array<BooleanArray>? {
        val colouredGraph = Array(grid2d.n) { BooleanArray(grid2d.m) }

        val independentSet = IndependentSetAlgorithm(grid2d, tileLabels.k).independentSet
        for (i in independentSet.indices) {
            for (j in independentSet[i].indices) {
                val tile = Tile(independentSet, i, j, tileLabels.n, tileLabels.m, tileLabels.k)
                val colour = tileLabels[tile]
                colouredGraph[i][j] = colour
            }
        }
        return colouredGraph
    }
}