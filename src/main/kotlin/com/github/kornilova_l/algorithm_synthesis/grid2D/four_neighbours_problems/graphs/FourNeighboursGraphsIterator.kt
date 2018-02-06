package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.graphs

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DGIterator
import java.io.File

/**
 * Iterates through all graphs in given directory
 */
class FourNeighboursGraphsIterator(val dir: File) : Iterable<FourNeighboursDirectedGraph> {
    override fun iterator(): Iterator<FourNeighboursDirectedGraph> {
        return MyIterator()
    }

    inner class MyIterator : DGIterator<FourNeighboursDirectedGraph>(dir) {
        override fun next(): FourNeighboursDirectedGraph {
            val graph = FourNeighboursDirectedGraph(graphFiles[index])
            index++
            return graph
        }
    }
}