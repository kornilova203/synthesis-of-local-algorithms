package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.graphs.FiveNeighboursDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.graphs.FiveNeighboursGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.graphs.FourNeighboursDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.graphs.FourNeighboursGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.DirectedGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem.OneOrTwoNeighboursTileSimpleGraph


/**
 * - [TileGraph] abstract
 * --- [SimpleGraph]
 * ------ [SimpleGraphWithTiles]
 * --------- [OneOrTwoNeighboursTileSimpleGraph]
 * --- [DirectedGraph] abstract
 * ------ [IndependentSetDirectedGraph] abstract
 * --------- [FourNeighboursDirectedGraph]
 * --------- [FiveNeighboursDirectedGraph]
 * --------- [DirectedGraphWithTiles] abstract
 * ------------ [FiveNeighboursGraphWithTiles]
 * ------------ [FourNeighboursGraphWithTiles]
 */
abstract class TileGraph {
    abstract val size: Int
    abstract val n: Int
    abstract val m: Int
}