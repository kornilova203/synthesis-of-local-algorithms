package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import java.io.File
import java.util.*
import java.util.regex.Pattern


/**
 * Iterates through all instances of [SimpleGraph]
 * that are stored in given directory.
 */
class SimpleGraphIterator(val dir: File) : Iterable<SimpleGraph> {

    override fun iterator(): Iterator<SimpleGraph> = SGIterator()

    private inner class SGIterator : Iterator<SimpleGraph> {
        private val graphFilePattern = Pattern.compile("\\d+-\\d+\\.graph")!!
        private val graphFiles = ArrayList<File>()
        private var index = 0

        init {
            for (file in dir.listFiles()) {
                if (graphFilePattern.matcher(file.name).matches()) {
                    graphFiles.add(file)
                }
            }
            Collections.shuffle(graphFiles)
            removeUseless(graphFiles)
        }

        private fun removeUseless(graphFiles: ArrayList<File>) {
            val useless = ArrayList<File>()
            val checkedParameters = HashSet<Pair<Int, Int>>() // n and m
            for (graphFile in graphFiles) {
                val parts = graphFile.name.split("-")
                val n = Integer.parseInt(parts[0])
                val m = Integer.parseInt(parts[1].split(".")[0])
                if (isUseless(n, m, checkedParameters)) {
                    useless.add(graphFile)
                } else {
                    checkedParameters.add(Pair(n, m))
                }
            }
            graphFiles.removeAll(useless)
        }

        private fun isUseless(n: Int, m: Int, checkedParameters: HashSet<Pair<Int, Int>>): Boolean {
            for (checkedParameter in checkedParameters) {
                if (n <= checkedParameter.first && m <= checkedParameter.second) {
                    return true
                }
            }
            return false
        }

        override fun hasNext(): Boolean {
            return index < graphFiles.size
        }

        override fun next(): SimpleGraph {
            val graph = SimpleGraph(graphFiles[index])
            index++
            return graph
        }

    }
}