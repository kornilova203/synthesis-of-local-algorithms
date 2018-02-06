package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.rule.FiveNeighboursRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.LabelingFunction
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.FiveNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getRulePermutations
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization.drawLabels

fun main(args: Array<String>) {
//    val problem = independentSet()
//    val problem = columnIS()
//    val problem = idTo FiveNeighboursProblem(1073732864)
//    val problem = parse FiveNeighboursProblem("XN, XE, NE, XNE, XS, NS, XNS, ES, XES, NES, XNES, XW, NW, XNW, EW, XEW, NEW, XNEW, SW, XSW, NSW, XNSW, ESW, XESW")
//    val problem = columnMinimalDominatingSet()
//    val problem = atLeastOneIncludedAndOneExcluded()
//    val problem = test()
//    val problem = gameOfLife()
    val problem = oneOrTwoNeighbours()
//    val problem = invertedIndependentSet()

    tryToSolve(problem)
}

fun tryToSolve(problem: FiveNeighboursProblem) {
    println(problem)

    val result = FiveNeighboursProblemSolver().getLabelingFunction(problem)

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

fun invertedIndependentSet(): FiveNeighboursProblem {
    return FiveNeighboursProblem(hashSetOf(
            FiveNeighboursRule("X"),
            FiveNeighboursRule("XN"),
            FiveNeighboursRule("XE"),
            FiveNeighboursRule("XS"),
            FiveNeighboursRule("XW"),
            FiveNeighboursRule("XNE"),
            FiveNeighboursRule("XNS"),
            FiveNeighboursRule("XNW"),
            FiveNeighboursRule("XES"),
            FiveNeighboursRule("XEW"),
            FiveNeighboursRule("XSW"),
            FiveNeighboursRule("XNES"),
            FiveNeighboursRule("XNEW"),
            FiveNeighboursRule("XNSW"),
            FiveNeighboursRule("XESW"),
            FiveNeighboursRule("NESW")
    ))
}

/**
 * X N XE NE S NS ES NES XW NW XEW NEW SW NSW ESW NESW
 */
fun columnIS(): FiveNeighboursProblem {
    val rules = HashSet<FiveNeighboursRule>()
    rules.addAll(FiveNeighboursProblem("10?0?").rules)
    rules.addAll(FiveNeighboursProblem("01?0?").rules)
    rules.addAll(FiveNeighboursProblem("00?1?").rules)
    rules.addAll(FiveNeighboursProblem("01?1?").rules)
    return FiveNeighboursProblem(rules)
}

fun columnMinimalDominatingSet(): FiveNeighboursProblem {
    val rules = HashSet<FiveNeighboursRule>()
    rules.addAll(FiveNeighboursProblem("10?0?").rules)
    rules.addAll(FiveNeighboursProblem("01?0?").rules)
    rules.addAll(FiveNeighboursProblem("00?1?").rules)
    rules.addAll(FiveNeighboursProblem("01?1?").rules)
    rules.addAll(FiveNeighboursProblem("11?0?").rules)
    rules.addAll(FiveNeighboursProblem("10?1?").rules)
    return FiveNeighboursProblem(rules)
}

fun atLeastOneIncludedAndOneExcluded(): FiveNeighboursProblem {
    val rules = HashSet<FiveNeighboursRule>()
    rules.addAll(getRulePermutations(1, true))
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))

    rules.addAll(getRulePermutations(1, false))
    rules.addAll(getRulePermutations(2, false))
    rules.addAll(getRulePermutations(3, false))
    return FiveNeighboursProblem(rules)
}

fun gameOfLife(): FiveNeighboursProblem {
    val rules = HashSet<FiveNeighboursRule>()
    /* cell survives if it has 2 or 3 neighbours */
    rules.addAll(getRulePermutations(1, true))
//    rules.addAll(getRulePermutations(2, true))
//    rules.addAll(getRulePermutations(3, true))
    /* cell does not survive if it has 4 of 1 neighbour */
    rules.addAll(getRulePermutations(1, false))
//    rules.addAll(getRulePermutations(3, false))
    return FiveNeighboursProblem(rules)
}

fun oneOrTwoNeighbours(): FiveNeighboursProblem {
    val rules = HashSet<FiveNeighboursRule>()
    rules.addAll(getRulePermutations(1, true))
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(1, false))
    rules.addAll(getRulePermutations(2, false))
    return FiveNeighboursProblem(rules)
}

fun test(): HashSet<FiveNeighboursRule> {
    return hashSetOf(
            FiveNeighboursRule("XNESW")
    )
}

fun independentSet(): FiveNeighboursProblem {
    /* independent set */
    return FiveNeighboursProblem(hashSetOf(
            FiveNeighboursRule("X"),
            FiveNeighboursRule("N"),
            FiveNeighboursRule("E"),
            FiveNeighboursRule("S"),
            FiveNeighboursRule("W"),
            FiveNeighboursRule("NE"),
            FiveNeighboursRule("NS"),
            FiveNeighboursRule("NW"),
            FiveNeighboursRule("ES"),
            FiveNeighboursRule("EW"),
            FiveNeighboursRule("SW"),
            FiveNeighboursRule("NES"),
            FiveNeighboursRule("NEW"),
            FiveNeighboursRule("NSW"),
            FiveNeighboursRule("ESW"),
            FiveNeighboursRule("NESW")
    ))
}

