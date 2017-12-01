package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.getLabelingFunction
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getRulePermutations
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization.drawLabels

fun main(args: Array<String>) {
//    val rules = independentSet()
//    val rules = columnIS()
    val rules = oneZeroAndOneOne()
//    val rules = test()
//    val rules = gameOfLife()
//    val rules = invertedIndependentSet()

    for (rule in rules) {
        print("$rule ")
    }
    println()

    val labelingFunction = getLabelingFunction(rules)

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

fun invertedIndependentSet(): HashSet<VertexRule> {
    return hashSetOf(
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
    )
}

fun columnIS(): HashSet<VertexRule> {
    return hashSetOf(
            VertexRule("X"),
            VertexRule("XE"),
            VertexRule("XW"),
            VertexRule("XEW"),
            VertexRule("NS"),
            VertexRule("NSE"),
            VertexRule("NSW"),
            VertexRule("NSEW"),
            VertexRule("XN"),
            VertexRule("XNE"),
            VertexRule("XNW"),
            VertexRule("XNEW"),
            VertexRule("XS"),
            VertexRule("XSE"),
            VertexRule("XSW"),
            VertexRule("XSEW"),
            VertexRule("N"),
            VertexRule("NE"),
            VertexRule("NW"),
            VertexRule("NEW"),
            VertexRule("S"),
            VertexRule("SW"),
            VertexRule("SE"),
            VertexRule("SWE")
    )
}

fun oneZeroAndOneOne(): HashSet<VertexRule> {
    val rules = HashSet<VertexRule>()
    rules.addAll(getRulePermutations(1, true))
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))

    rules.addAll(getRulePermutations(1, false))
    rules.addAll(getRulePermutations(2, false))
    rules.addAll(getRulePermutations(3, false))
    return rules
}

fun gameOfLife(): HashSet<VertexRule> {
    val rules = HashSet<VertexRule>()
    /* cell survives if it has 2 or 3 neighbours */
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))
    /* cell does not survive if it has 4 of 1 neighbour */
    rules.addAll(getRulePermutations(0, false))
    rules.addAll(getRulePermutations(1, false))
    rules.addAll(getRulePermutations(4, false))
    return rules
}

fun test(): HashSet<VertexRule> {
    return hashSetOf(
            VertexRule("XNESW")
    )
}

fun independentSet(): HashSet<VertexRule> {
    /* independent set */
    return hashSetOf(
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
    )
}

