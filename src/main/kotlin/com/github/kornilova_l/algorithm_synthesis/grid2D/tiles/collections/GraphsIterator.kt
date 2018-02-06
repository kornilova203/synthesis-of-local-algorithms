package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraph
import java.io.File
import java.util.*
import java.util.regex.Pattern


abstract class DGIterator<out G : IndependentSetDirectedGraph<*>>(dir: File) : Iterator<G> {
    private val graphFilePattern = Pattern.compile("\\d+-\\d+-\\d+\\.graph")!!
    protected val graphFiles = ArrayList<File>()
    protected var index = 0

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
        val checkedParameters = HashSet<Triple<Int, Int, Int>>() // n m and k
        for (graphFile in graphFiles) {
            val parts = graphFile.name.split("-")
            val n = Integer.parseInt(parts[0])
            val m = Integer.parseInt(parts[1])
            val k = Integer.parseInt(parts[2].split(".")[0])
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
            if (n <= checkedParameter.first && m <= checkedParameter.second && k >= checkedParameter.third) {
                return true
            }
        }
        return false
    }

    override fun hasNext(): Boolean {
        return index < graphFiles.size
    }
}
