package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import java.io.File
import java.util.regex.Pattern


/**
 * Iterates through all graphs in given directory
 */
class DirectedGraphsIterator(val dir: File) : Iterable<DirectedGraph> {

    override fun iterator(): Iterator<DirectedGraph> {
        return DGIterator()
    }

    private inner class DGIterator : Iterator<DirectedGraph> {
        private val graphFilePattern = Pattern.compile("\\d+-\\d+-\\d+\\.graph")!!
        private val graphFiles = ArrayList<File>()
        private var index = 0

        init {
            for (file in dir.listFiles()) {
                if (graphFilePattern.matcher(file.name).matches()) {
                    graphFiles.add(file)
                }
            }
        }

        override fun hasNext(): Boolean {
            return index < graphFiles.size
        }

        override fun next(): DirectedGraph {
            val graph = DirectedGraph.createInstance(graphFiles[index])
            index++
            return graph
        }
    }
}