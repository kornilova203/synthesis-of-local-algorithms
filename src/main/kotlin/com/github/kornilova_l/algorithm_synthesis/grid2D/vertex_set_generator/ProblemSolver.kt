package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem.Problem
import com.github.kornilova_l.util.FileNameCreator
import java.io.File


abstract class ProblemSolver<T : Problem<*>, G : IndependentSetDirectedGraph<*>> {
    abstract val graphsIterator: Iterable<G>
    abstract val dirWithGraphTiles: File
    /**
     * Try to find tile size such that it is possible to get labels so
     * each vertex has 1-neighbourhood in combinations Set.
     *
     * To use this function all tile sets must be precalculated and stored in independent_set_tiles directory
     */
    fun getLabelingFunction(problem: T): Pair<LabelingFunction, Int>? {
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

    private fun getLabelingFunction(problem: T, graph: G): LabelingFunction? {
        var solution = tryToFindSolution(problem, graph)
        if (solution != null) { // solution found
            return LabelingFunction(solution,
                    graph.createGraphWithTiles(
                            FileNameCreator.getFile(dirWithGraphTiles, graph.n, graph.m, graph.k, TileGraph.graphTilesFileExtension)!!
                    )
            )
        }
        solution = tryToFindSolution(rotateProblem(problem), graph)
        if (solution != null) { // solution found
            return LabelingFunction(solution,
                    graph.createGraphWithTiles(
                            FileNameCreator.getFile(dirWithGraphTiles, graph.n, graph.m, graph.k, TileGraph.graphTilesFileExtension)!!
                    )
            ).rotate()
        }
        return null
    }

    /**
     * This method is needed because problem.rotate() returns
     * Problem<VertexRule> and it cannot be safely cast to T
     */
    protected abstract fun rotateProblem(problem: T): T

    /**
     * This method is needed because problem.rotate() returns
     * Problem<VertexRule> and it cannot be safely cast to T
     */
    protected abstract fun reverseProblem(problem: T): T

    private fun tryToFindSolution(problem: T, graph: G): List<Int>? {
        val satSolver = SatSolver()
        addClausesToSatSolver(graph, problem, satSolver)
        return satSolver.solve(graph.size)
    }

    private fun addClausesToSatSolver(graph: G, problem: T, satSolver: SatSolver) {
        val reversedProblem = reverseProblem(problem)
        formClauses(graph, reversedProblem, satSolver)
    }

    /**
     * Implementation of this method must initialize clauses inside satSolver.
     * @param reversedProblem to create clauses reversed problems is needed. Obvious part is that
     *                        we need to make sure that for each neighbourhood at least one should be satisfied:
     *                        (satisfy problem.rule1) V (satisfy problem.rule2) V (...)
     *                        but sat solvers use different format: (x1 V x2 V x3) ^ (x2 V x6) ^ (...).
     *                        So we need to make sure that non of the reversed rules are true:
     *                        (do NOT satisfy reversedProblem.rule1) ^ (do NOT satisfy reversedProblem.rule2) ^ (...)
     *
     * (NOTE: first version of this method returned list of clauses, but there are might be
     * really big number of clauses, so I decided to load clauses directly to sat solver in order to
     * reduce memory consumption and memory spent on objects creation)
     */
    protected abstract fun formClauses(graph: G, reversedProblem: T, satSolver: SatSolver)

    /**
     * @param clause array of integers of size n. First m elements are bigger than 0. m in (0, m]
     */
    protected fun addFirstElementsThatAreNotNull(clause: IntArray, satSolver: SatSolver) {
        var zeroPos = -1
        for (j in 0 until clause.size) {
            if (clause[j] == 0) {
                zeroPos = j
                break
            }
        }
        if (zeroPos == 0) {
            throw IllegalArgumentException("All elements inside clause are 0 $clause")
        }
        if (zeroPos != -1) {
            satSolver.addClause(clause.copyOfRange(0, zeroPos))
        } else {
            satSolver.addClause(clause)
        }
    }

    private fun isSolvable(problem: T, graph: G): Boolean {
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
        val solvable = HashSet<T>()
        for (graph in graphsIterator) {
            if (solvable.size == problems.size) { // if everything is solved
                return solvable
            }
            useGraphToFindSolutions(problems, graph, solvable)
        }
        println("COMPLETE")
        return solvable
    }

    private fun useGraphToFindSolutions(problems: List<T>, graph: G,
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
        for (graph in graphsIterator) {
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