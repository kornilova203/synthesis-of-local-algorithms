package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.graphs.FiveNeighboursDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.graphs.FiveNeighboursGraphsIterator
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FIVE_POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.FiveNeighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.ProblemSolver
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.SatSolver
import java.io.File


class FiveNeighboursProblemSolver : ProblemSolver<FiveNeighboursProblem, FiveNeighboursDirectedGraph>() {

    override val dirWithGraphTiles: File = File("independent_set_tiles/five_neighbours_directed_graphs")
    override val graphsIterator = FiveNeighboursGraphsIterator(dirWithGraphTiles)

    override fun reverseProblem(problem: FiveNeighboursProblem): FiveNeighboursProblem = problem.reverse()

    override fun rotateProblem(problem: FiveNeighboursProblem): FiveNeighboursProblem = problem.rotate()

    override fun formClauses(graph: FiveNeighboursDirectedGraph,
                             reversedProblem: FiveNeighboursProblem,
                             satSolver: SatSolver) {
        for (neighbourhood in graph.neighbourhoods) {
            formClause(neighbourhood, reversedProblem, satSolver)
        }
    }

    private fun formClause(neighbourhood: FiveNeighbourhood, reversedProblem: FiveNeighboursProblem, satSolver: SatSolver) {
        for (reversedRule in reversedProblem.rules) {
            var i = 0
            val clause = IntArray(5)
            var isAlwaysTrue = false
            for (position in FIVE_POSITION.values()) {
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
                addFirstElementsThatAreNotNull(clause, satSolver)
            }
        }
    }
}