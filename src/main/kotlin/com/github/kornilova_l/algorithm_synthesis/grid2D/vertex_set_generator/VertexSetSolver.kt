package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraphsIterator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph.Neighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.Problem
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.positions
import java.io.File

/**
 * Try to find tile size such that it is possible to get labels so
 * each vertex has 1-neighbourhood in combinations Set.
 *
 * To use this function all tile sets must be precalculated and stored in independent_set_tiles directory
 */
fun getLabelingFunction(problem: Problem): Pair<LabelingFunction, Int>? {
    val directedGraphsParser = IndependentSetDirectedGraphsIterator(File("independent_set_tiles/directed_graphs"))
    for (graph in directedGraphsParser) {
        println("n = ${graph.n} m = ${graph.m} k = ${graph.k}")
        val function = getLabelingFunction(problem, graph)

        if (function != null) {
            println("Found")
            return Pair(function, graph.k)
        }
    }
    return null
}

fun doesSolutionExist(problem: Problem): Boolean {
    val directedGraphsParser = IndependentSetDirectedGraphsIterator(File("independent_set_tiles/directed_graphs"))
    for (graph in directedGraphsParser) {
        println("n = ${graph.n} m = ${graph.m} k = ${graph.k}")
        var solution = tryToFindSolution(problem, graph)
        if (solution != null) { // solution found
            return true
        }

        solution = tryToFindSolution(problem.rotate(), graph)
        if (solution != null) { // solution found
            return true
        }
    }

    return false
}

private fun getLabelingFunction(problem: Problem, graph: IndependentSetDirectedGraph): LabelingFunction? {
    var solution = tryToFindSolution(problem, graph)
    if (solution != null) { // solution found
        return LabelingFunction(solution,
                DirectedGraphWithTiles.createInstance(
                        DirectedGraphWithTiles.getTilesFile(graph.n, graph.m, graph.k, File("independent_set_tiles/directed_graphs/"))!!,
                        graph))
    }

    solution = tryToFindSolution(problem.rotate(), graph)
    if (solution != null) { // solution found
        return LabelingFunction(solution,
                DirectedGraphWithTiles.createInstance(
                        DirectedGraphWithTiles.getTilesFile(graph.n, graph.m, graph.k, File("independent_set_tiles/directed_graphs/"))!!,
                        graph))
                .rotate()
    }
    return null
}

private fun tryToFindSolution(problem: Problem, graph: DirectedGraph): List<Int>? {
    val satSolver = SatSolver()
    addClausesToSatSolver(graph, problem, satSolver)
    return satSolver.solve(graph.size)
}

private fun isSolvable(problem: Problem, graph: DirectedGraph): Boolean {
    val satSolver = SatSolver()
    addClausesToSatSolver(graph, problem, satSolver)
    return satSolver.isSolvable()
}

/**
 * This method is more effective than calling isSolvable for each problem
 * because it constructs a graph only ones for all problems
 * @return solvable problems
 */
fun tryToFindSolutionForEachProblem(problems: List<Problem>): Set<Problem> {
    val directedGraphsParser = IndependentSetDirectedGraphsIterator(File("independent_set_tiles/directed_graphs"))
    val solvable = HashSet<Problem>()
    for (graph in directedGraphsParser) {
        if (solvable.size == problems.size) { // if everything is solved
            return solvable
        }
        useGraphToFindSolutions(problems, graph, solvable)
    }
    println("COMPLETE")
    return solvable
}

private fun useGraphToFindSolutions(problems: List<Problem>, graph: IndependentSetDirectedGraph,
                                    solvedProblems: MutableSet<Problem>) {
    println("Try n=${graph.n} m=${graph.m} k=${graph.k}")
    try {
        for (problem in problems) {
            if (solvedProblems.contains(problem)) { // if solution was found
                continue
            }
            var isSolvable = isSolvable(problem, graph)
            if (!isSolvable && graph.n != graph.m) {
                isSolvable = isSolvable(problem.rotate(), graph)
            }
            if (isSolvable) {
                println("Found solution for $problem")
                solvedProblems.add(problem)
            }
        }
    } catch (e: OutOfMemoryError) {
        System.err.println("OutOfMemoryError n=${graph.n} m=${graph.m} k=${graph.k}")
    }
}

fun addClausesToSatSolver(graph: DirectedGraph, problem: Problem, satSolver: SatSolver) {
    val reversedProblem = problem.reverse()
    for (neighbourhood in graph.neighbourhoods) {
        formClause(neighbourhood, reversedProblem, satSolver)
    }
}

private fun formClause(neighbourhood: Neighbourhood, reversedProblem: Problem, satSolver: SatSolver) {
    for (reversedRule in reversedProblem.rules) {
        var i = 0
        val clause = IntArray(5)
        var isAlwaysTrue = false
        for (position in positions) {
            val id = neighbourhood.get(position)
            if (id == 0) {
                throw AssertionError("id must be bigger than 0")
            }
            var value = id
            if (reversedRule.isIncluded(position)) {
                value = -id
            }
            if (clause.contains(-value)) { // if clause is always true
                isAlwaysTrue = true
                break
            }
            if (!clause.contains(value)) { // if does not contain duplicate
                clause[i] = value
                i++
            }
        }
        if (!isAlwaysTrue) {
            var zeroPos = -1
            for (j in 0 until clause.size) {
                if (clause[j] == 0) {
                    zeroPos = j
                    break
                }
            }
            if (zeroPos != -1) {
                satSolver.addClause(clause.copyOfRange(0, zeroPos))
            } else {
                satSolver.addClause(clause)
            }
        }
    }
}

