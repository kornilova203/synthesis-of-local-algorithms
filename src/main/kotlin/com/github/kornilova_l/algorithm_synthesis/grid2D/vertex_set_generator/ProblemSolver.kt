package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.DirectedGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraphsIterator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.Problem
import java.io.File


abstract class ProblemSolver<T : Problem<*>> {
    /**
     * Try to find tile size such that it is possible to get labels so
     * each vertex has 1-neighbourhood in combinations Set.
     *
     * To use this function all tile sets must be precalculated and stored in independent_set_tiles directory
     */
    fun getLabelingFunction(problem: T): Pair<LabelingFunction, Int>? {
        val graphsIterator = IndependentSetDirectedGraphsIterator(File("independent_set_tiles/directed_graphs"))
        for (graph in graphsIterator) {
            println("n = ${graph.n} m = ${graph.m} k = ${graph.k}")
            val function = getLabelingFunction(problem, graph)

            if (function != null) {
                println("Found")
                return Pair(function, graph.k)
            }
        }
        return null
    }

    private fun getLabelingFunction(problem: T, graph: IndependentSetDirectedGraph): LabelingFunction? {
        var solution = tryToFindSolution(problem, graph)
        if (solution != null) { // solution found
            return LabelingFunction(solution,
                    DirectedGraphWithTiles.createInstance(
                            DirectedGraphWithTiles.getTilesFile(graph.n, graph.m, graph.k, File("independent_set_tiles/directed_graphs/"))!!,
                            graph))
        }
        solution = tryToFindSolution(rotateProblem(problem), graph)
        if (solution != null) { // solution found
            return LabelingFunction(solution,
                    DirectedGraphWithTiles.createInstance(
                            DirectedGraphWithTiles.getTilesFile(graph.n, graph.m, graph.k, File("independent_set_tiles/directed_graphs/"))!!,
                            graph))
                    .rotate()
        }
        return null
    }

    /**
     * This method is needed because problem.rotate() returns
     * Problem<VertexRule> and it cannot be safely cast to T
     */
    abstract fun rotateProblem(problem: T): T

    /**
     * This method is needed because problem.rotate() returns
     * Problem<VertexRule> and it cannot be safely cast to T
     */
    abstract fun reverseProblem(problem: T): T

    private fun tryToFindSolution(problem: T, graph: IndependentSetDirectedGraph): List<Int>? {
        val satSolver = SatSolver()
        addClausesToSatSolver(graph, problem, satSolver)
        return satSolver.solve(graph.size)
    }

    private fun addClausesToSatSolver(graph: IndependentSetDirectedGraph, problem: T, satSolver: SatSolver) {
        val reversedProblem = reverseProblem(problem)
        for (neighbourhood in graph.neighbourhoods) {
            formClause(neighbourhood, reversedProblem, satSolver)
        }
    }

    protected abstract fun formClause(neighbourhood: DirectedGraph.Neighbourhood, reversedProblem: T, satSolver: SatSolver)

    private fun isSolvable(problem: T, graph: IndependentSetDirectedGraph): Boolean {
        val satSolver = SatSolver()
        addClausesToSatSolver(graph, problem, satSolver)
        return satSolver.isSolvable()
    }

    /**
     * This method is more effective than calling isSolvable for each problem
     * because it constructs a graph only ones for all problems
     * @return solvable problems
     */
    fun tryToFindSolutionForEachProblem(problems: List<T>): Set<T> {
        val directedGraphsParser = IndependentSetDirectedGraphsIterator(File("independent_set_tiles/directed_graphs"))
        val solvable = HashSet<T>()
        for (graph in directedGraphsParser) {
            if (solvable.size == problems.size) { // if everything is solved
                return solvable
            }
            useGraphToFindSolutions(problems, graph, solvable)
        }
        println("COMPLETE")
        return solvable
    }

    private fun useGraphToFindSolutions(problems: List<T>, graph: IndependentSetDirectedGraph,
                                        solvedProblems: MutableSet<T>) {
        println("Try n=${graph.n} m=${graph.m} k=${graph.k}")
        try {
            for (problem in problems) {
                if (solvedProblems.contains(problem)) { // if solution was found
                    continue
                }
                var isSolvable = isSolvable(problem, graph)
                if (!isSolvable && graph.n != graph.m) {
                    isSolvable = isSolvable(rotateProblem(problem), graph)
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

    fun doesSolutionExist(problem: T): Boolean {
        val directedGraphsParser = IndependentSetDirectedGraphsIterator(File("independent_set_tiles/directed_graphs"))
        for (graph in directedGraphsParser) {
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

        return false
    }
}