package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.getLabelingFunction
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getRulePermutations
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getVertexRules
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization.drawLabels

fun main(args: Array<String>) {
//    val rules = independentSet()
//    val rules = columnIS()
    val rules = toSetOfVertexRules(1073732864)
//    val rules = parseRules("XN, XE, NE, XNE, XS, NS, XNS, ES, XES, NES, XNES, XW, NW, XNW, EW, XEW, NEW, XNEW, SW, XSW, NSW, XNSW, ESW, XESW")
//    val rules = columnMinimalDominatingSet()
//    val rules = oneZeroAndOneOne()
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
        val independentSet = IndependentSetAlgorithm(grid, labelingFunction.k).independentSet
        val labeledGrid = labelingFunction.getLabels(independentSet)
        println(grid)
        for (i in 0 until labeledGrid!!.size) {
            (0 until labeledGrid[0].size)
                    .map { if (labeledGrid[i][it]) 1 else 0 }
                    .forEach { print("$it ") }
            println()
        }
        drawLabels(labeledGrid, independentSet)
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

/**
 * X N XE NE S NS ES NES XW NW XEW NEW SW NSW ESW NESW
 */
fun columnIS(): HashSet<VertexRule> {
    val rules = HashSet<VertexRule>()
    rules.addAll(getVertexRules("10?0?"))
    rules.addAll(getVertexRules("01?0?"))
    rules.addAll(getVertexRules("00?1?"))
    rules.addAll(getVertexRules("01?1?"))
    return rules
}

fun columnMinimalDominatingSet(): HashSet<VertexRule> {
    val rules = HashSet<VertexRule>()
    rules.addAll(getVertexRules("10?0?"))
    rules.addAll(getVertexRules("01?0?"))
    rules.addAll(getVertexRules("00?1?"))
    rules.addAll(getVertexRules("01?1?"))
    rules.addAll(getVertexRules("11?0?"))
    rules.addAll(getVertexRules("10?1?"))
    return rules
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

