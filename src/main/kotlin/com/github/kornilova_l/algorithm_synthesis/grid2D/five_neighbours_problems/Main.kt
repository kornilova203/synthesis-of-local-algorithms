package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursNonTrivialProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.LabelingFunction
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem.getRulePermutations
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization.drawLabels

private val errMessage = """
Specify problem to solve:
* Id of a problem (example 3452252)
* Number of included neighbours and if center is included (example true=23 false=140)"""

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println(errMessage)
        return
    }
    val problem: FiveNeighboursProblem
    when {
        args.size == 1 -> {
            val problemId = Integer.parseInt(args[0])
            problem = FiveNeighboursProblem(problemId)
        }
        args.size == 2 -> {
            val configForIncluded = get(args, "true") ?: return
            val configForExcluded = get(args, "false") ?: return
            val rules = HashSet<FiveNeighboursRule>()
            for (neighboursCount in configForIncluded) {
                rules.addAll(getRulePermutations(neighboursCount, true))
            }
            for (neighboursCount in configForExcluded) {
                rules.addAll(getRulePermutations(neighboursCount, false))
            }
            problem = FiveNeighboursProblem(rules)
        }
        else -> {
            System.err.println(errMessage)
            return
        }
    }
    tryToSolve(problem)
}

private fun get(args: Array<String>, name: String): List<Int>? {
    var string: String? = null
    for (arg in args) {
        if (arg.startsWith(name)) {
            string = arg
        }
    }
    if (string == null) {
        System.err.println(errMessage)
        return null
    }
    return string.substring(name.length + 1, string.length).split("").filter { it != "" }.map { Integer.parseInt(it) }
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

fun atLeastOneIncludedAndOneExcluded(): FiveNeighboursNonTrivialProblem {
    val rules = HashSet<FiveNeighboursRule>()
    rules.addAll(getRulePermutations(1, true))
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))

    rules.addAll(getRulePermutations(1, false))
    rules.addAll(getRulePermutations(2, false))
    rules.addAll(getRulePermutations(3, false))
    return FiveNeighboursNonTrivialProblem(rules)
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

