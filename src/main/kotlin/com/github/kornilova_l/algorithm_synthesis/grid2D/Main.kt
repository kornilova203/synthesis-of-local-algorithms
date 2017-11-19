package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.LabelingFunction
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.getLabelingFunction
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getRulePermutations
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization.drawLabels

fun main(args: Array<String>) {
    val labelingFunction = independentSet()
//    val labelingFunction = test()
//    val labelingFunction = gameOfLife()
//    val labelingFunction = invertedIndependentSet()

    if (labelingFunction == null) {
        println("not found")
    } else {
        println("found")
        val grid = generateGrid(10, 10)
        val labeledGrid = labelingFunction.getLabels(grid)
        println(grid)
        for (i in 0 until labeledGrid!!.size) {
            (0 until labeledGrid[0].size)
                    .map { if (labeledGrid[i][it]) 1 else 0 }
                    .forEach { print("$it ") }
            println()
        }
        drawLabels(labeledGrid)
    }
}

fun invertedIndependentSet(): LabelingFunction? {
    return getLabelingFunction(hashSetOf(
            VertexRule("X"),
            VertexRule("XN"),
            VertexRule("XE"),
            VertexRule("XS"),
            VertexRule("XW"),
            VertexRule("XNE"),
            VertexRule("XNS"),
            VertexRule("XNW"),
            VertexRule("XES"),
            VertexRule("XEW"),
            VertexRule("XSW"),
            VertexRule("XNES"),
            VertexRule("XNEW"),
            VertexRule("XNSW"),
            VertexRule("XESW"),
            VertexRule("NESW")
    ))
}

fun gameOfLife(): LabelingFunction? {
    val rules = HashSet<VertexRule>()
    /* cell survives if it has 2 or 3 neighbours */
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))
    /* cell does not survive if it has 4 of 1 neighbour */
    rules.addAll(getRulePermutations(0, false))
    rules.addAll(getRulePermutations(1, false))
    rules.addAll(getRulePermutations(4, false))
    return getLabelingFunction(rules)
}

fun test(): LabelingFunction? {
    return getLabelingFunction(hashSetOf(
            VertexRule("XNESW")
    ))
}

fun independentSet(): LabelingFunction? {
    /* independent set */
    return getLabelingFunction(hashSetOf(
            VertexRule("X"),
            VertexRule("N"),
            VertexRule("E"),
            VertexRule("S"),
            VertexRule("W"),
            VertexRule("NE"),
            VertexRule("NS"),
            VertexRule("NW"),
            VertexRule("ES"),
            VertexRule("EW"),
            VertexRule("SW"),
            VertexRule("NES"),
            VertexRule("NEW"),
            VertexRule("NSW"),
            VertexRule("ESW"),
            VertexRule("NESW")
    ))
}

