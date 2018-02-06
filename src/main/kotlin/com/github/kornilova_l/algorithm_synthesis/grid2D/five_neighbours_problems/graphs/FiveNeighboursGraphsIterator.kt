package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.graphs

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DGIterator
import java.io.File


/**
 * Iterates through all graphs in given directory
 */
class FiveNeighboursGraphsIterator(val dir: File) : Iterable<FiveNeighboursDirectedGraph> {
    override fun iterator(): Iterator<FiveNeighboursDirectedGraph> {
        return MyIterator()
    }

    inner class MyIterator : DGIterator<FiveNeighboursDirectedGraph>(dir) {
        override fun next(): FiveNeighboursDirectedGraph {
            val graph = FiveNeighboursDirectedGraph(graphFiles[index])
            index++
            return graph
        }
    }
}