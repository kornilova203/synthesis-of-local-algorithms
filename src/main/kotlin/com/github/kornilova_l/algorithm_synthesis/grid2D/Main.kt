package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.LabelingFunction
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.getLabelingFunction
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.Problem
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getRulePermutations
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization.drawLabels

fun main(args: Array<String>) {
//    val problem = independentSet()
//    val problem = columnIS()
//    val problem = idToProblem(1073732864)
//    val problem = parseProblem("XN, XE, NE, XNE, XS, NS, XNS, ES, XES, NES, XNES, XW, NW, XNW, EW, XEW, NEW, XNEW, SW, XSW, NSW, XNSW, ESW, XESW")
//    val problem = columnMinimalDominatingSet()
//    val problem = atLeastOneIncludedAndOneExcluded()
//    val problem = test()
//    val problem = gameOfLife()
    val problem = oneOrTwoNeighbours()
//    val problem = invertedIndependentSet()

    tryToSolve(problem)
}

fun tryToSolve(problem: Problem) {
    println(problem)

    val result = getLabelingFunction(problem)

    if (result == null) {
        println("not found")
    } else {
        println("found")
        printResult(result.first, result.second)
    }
}

fun printResult(labelingFunction: LabelingFunction, k: Int) {
    val grid = generateGrid(10, 10)
    val independentSet = IndependentSetAlgorithm(grid, k).independentSet
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

fun invertedIndependentSet(): Problem {
    return Problem(hashSetOf(
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

/**
 * X N XE NE S NS ES NES XW NW XEW NEW SW NSW ESW NESW
 */
fun columnIS(): Problem {
    val rules = HashSet<VertexRule>()
    rules.addAll(Problem("10?0?").rules)
    rules.addAll(Problem("01?0?").rules)
    rules.addAll(Problem("00?1?").rules)
    rules.addAll(Problem("01?1?").rules)
    return Problem(rules)
}

fun columnMinimalDominatingSet(): Problem {
    val rules = HashSet<VertexRule>()
    rules.addAll(Problem("10?0?").rules)
    rules.addAll(Problem("01?0?").rules)
    rules.addAll(Problem("00?1?").rules)
    rules.addAll(Problem("01?1?").rules)
    rules.addAll(Problem("11?0?").rules)
    rules.addAll(Problem("10?1?").rules)
    return Problem(rules)
}

fun atLeastOneIncludedAndOneExcluded(): Problem {
    val rules = HashSet<VertexRule>()
    rules.addAll(getRulePermutations(1, true))
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))

    rules.addAll(getRulePermutations(1, false))
    rules.addAll(getRulePermutations(2, false))
    rules.addAll(getRulePermutations(3, false))
    return Problem(rules)
}

fun gameOfLife(): Problem {
    val rules = HashSet<VertexRule>()
    /* cell survives if it has 2 or 3 neighbours */
    rules.addAll(getRulePermutations(1, true))
//    rules.addAll(getRulePermutations(2, true))
//    rules.addAll(getRulePermutations(3, true))
    /* cell does not survive if it has 4 of 1 neighbour */
    rules.addAll(getRulePermutations(1, false))
//    rules.addAll(getRulePermutations(3, false))
    return Problem(rules)
}

fun oneOrTwoNeighbours(): Problem {
    val rules = HashSet<VertexRule>()
    rules.addAll(getRulePermutations(1, true))
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(1, false))
    rules.addAll(getRulePermutations(2, false))
    return Problem(rules)
}

fun test(): HashSet<VertexRule> {
    return hashSetOf(
            VertexRule("XNESW")
    )
}

fun independentSet(): Problem {
    /* independent set */
    return Problem(hashSetOf(
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

