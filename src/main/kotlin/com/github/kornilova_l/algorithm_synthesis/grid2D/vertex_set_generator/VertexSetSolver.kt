package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph.Neighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.positions
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.reverseRules
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.rotateProblem
import java.io.File
import java.util.regex.Pattern

val tilesFilePattern = Pattern.compile("\\d+-\\d+-\\d+\\.txt")!!
val graphFilePattern = Pattern.compile("\\d+-\\d+-\\d+\\.graph")!!

/**
 * Try to find tile size such that it is possible to get labels so
 * each vertex has 1-neighbourhood in combinations Set.
 *
 * To use this function all tile sets must be precalculated and stored in generated_tiles directory
 */
fun getLabelingFunction(vertexRules: Set<VertexRule>): LabelingFunction? {
    val files = File("directed_graphs").listFiles()
    for (i in 0 until files.size) {
        val file = files[i]
        if (graphFilePattern.matcher(file.name).matches()) {
            val graph = DirectedGraph.createInstance(file)
            println("n = ${graph.n} m = ${graph.m} k = ${graph.k}")
            val function = getLabelingFunction(vertexRules, graph)

            if (function != null) {
                println("Found")
                return function
            }
        }
    }
    return null
}

fun doesSolutionExist(problem: Set<VertexRule>): Boolean {
    val files = File("directed_graphs").listFiles()
    for (i in 0 until files.size) {
        val file = files[i]
        if (graphFilePattern.matcher(file.name).matches()) {
            val graph = DirectedGraph.createInstance(file)
            println("n = ${graph.n} m = ${graph.m} k = ${graph.k}")
            var solution = tryToFindSolution(problem, graph)
            if (solution != null) { // solution found
                return true
            }

            solution = tryToFindSolution(rotateProblem(problem), graph)
            if (solution != null) { // solution found
                return true
            }
        }
    }
    return false
}

private fun getLabelingFunction(vertexRules: Set<VertexRule>, graph: DirectedGraph): LabelingFunction? {
    var solution = tryToFindSolution(vertexRules, graph)
    if (solution != null) { // solution found
        return LabelingFunction(solution,
                DirectedGraphWithTiles.createInstance(File("directed_graphs/${graph.n}-${graph.m}-${graph.k}.tiles"), graph))
    }

    solution = tryToFindSolution(rotateProblem(vertexRules), graph)
    if (solution != null) { // solution found
        return LabelingFunction(solution,
                DirectedGraphWithTiles.createInstance(File("directed_graphs/${graph.n}-${graph.m}-${graph.k}.tiles"), graph))
                .rotate()
    }
    return null
}

private fun tryToFindSolution(vertexRules: Set<VertexRule>, graph: DirectedGraph): List<Int>? {
    val satSolver = SatSolver()
    addClausesToSatSolver(graph, vertexRules, satSolver)
    return satSolver.solve(graph.size)
}

/**
 * This method is more effective than calling isSolvable for each problem
 * because it constructs a graph only ones for all problems
 * @return solvable problems
 */
fun tryToFindSolutionForEachProblem(problems: List<Set<VertexRule>>): Set<Set<VertexRule>> {
    val solvable = HashSet<Set<VertexRule>>()
    val files = File("directed_graphs").listFiles()
    for (i in 0 until files.size) {
        if (solvable.size == problems.size) { // if everything is solved
            return solvable
        }
        val file = files[i]
        if (graphFilePattern.matcher(file.name).matches()) {
            val graph = DirectedGraph.createInstance(file)
            useGraphToFindSolutions(problems, graph, solvable)
        }
    }
    println("COMPLETE")
    return solvable
}

private fun useGraphToFindSolutions(problems: List<Set<VertexRule>>, graph: DirectedGraph,
                                    solutions: MutableSet<Set<VertexRule>>) {
    println("Try n=${graph.n} m=${graph.m} k=${graph.k}")
    try {
        for (problem in problems) {
            if (solutions.contains(problem)) { // if solution was found
                continue
            }
            var solution = tryToFindSolution(problem, graph)
            if (solution == null && graph.n != graph.m) {
                solution = tryToFindSolution(rotateProblem(problem), graph)
            }
            if (solution != null) {
                println("Found solution for $problem")
                solutions.add(problem)
            }
        }
    } catch (e: OutOfMemoryError) {
        System.err.println("OutOfMemoryError n=${graph.n} m=${graph.m} k=${graph.k}")
    }
}

fun addClausesToSatSolver(graph: DirectedGraph, rules: Set<VertexRule>, satSolver: SatSolver) {
    val reversedRules = reverseRules(rules)
    for (neighbourhood in graph.neighbourhoods) {
        formClause(neighbourhood, reversedRules, satSolver)
    }
}

private fun formClause(neighbourhood: Neighbourhood, reversedRules: Set<VertexRule>, satSolver: SatSolver) {
    for (reversedRule in reversedRules) {
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

