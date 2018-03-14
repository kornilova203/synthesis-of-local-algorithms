package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraph
import com.github.kornilova_l.util.FileNameCreator
import java.io.File
import java.util.*


abstract class DGIterator<out G : IndependentSetDirectedGraph<*>>(dir: File) : Iterator<G> {
    protected val graphFiles = ArrayList<File>()
    protected var index = 0

    init {
        for (file in dir.listFiles()) {
            if (FileNameCreator.getExtension(file.name) == "graph") {
                graphFiles.add(file)
            }
        }
        graphFiles.shuffle()
        removeUseless(graphFiles)
    }

    private fun removeUseless(graphFiles: ArrayList<File>) {
        val useless = ArrayList<File>()
        val checkedParameters = HashSet<Triple<Int, Int, Int>>() // n m and k
        for (graphFile in graphFiles) {
            val n = FileNameCreator.getIntParameter(graphFile.name, "n")!!
            val m = FileNameCreator.getIntParameter(graphFile.name, "m")!!
            val k = FileNameCreator.getIntParameter(graphFile.name, "k")!!
            if (isUseless(n, m, k, checkedParameters)) {
                useless.add(graphFile)
            } else {
                checkedParameters.add(Triple(n, m, k))
            }
        }
        graphFiles.removeAll(useless)
    }

    private fun isUseless(n: Int, m: Int, k: Int, checkedParameters: HashSet<Triple<Int, Int, Int>>): Boolean {
        for (checkedParameter in checkedParameters) {
            if (n <= checkedParameter.first && m <= checkedParameter.second && k == checkedParameter.third) {
                return true
            }
        }
        return false
    }

    override fun hasNext(): Boolean {
        return index < graphFiles.size
    }
}
