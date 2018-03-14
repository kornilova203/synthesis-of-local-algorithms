package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.graphs.FourNeighboursDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.graphs.FourNeighboursGraphsIterator
import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem.FOUR_POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem.FourNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.FourNeighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.ProblemSolver
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.SatSolver
import java.io.File


class FourNeighboursProblemSolver : ProblemSolver<FourNeighboursProblem, FourNeighboursDirectedGraph>() {
    override val dirWithGraphTiles: File = File("independent_set_tiles/four_neighbours_directed_graphs")
    override val graphsIterator = FourNeighboursGraphsIterator(dirWithGraphTiles)

    override fun rotateProblem(problem: FourNeighboursProblem): FourNeighboursProblem = problem.rotate()

    override fun reverseProblem(problem: FourNeighboursProblem): FourNeighboursProblem = problem.reverse()

    override fun formClauses(graph: FourNeighboursDirectedGraph,
                             reversedProblem: FourNeighboursProblem,
                             satSolver: SatSolver, showProgressBar: Boolean) {
        for (neighbourhood in graph.neighbourhoods) {
            formClause(neighbourhood, reversedProblem, satSolver)
        }
    }

    private fun formClause(neighbourhood: FourNeighbourhood, reversedProblem: FourNeighboursProblem, satSolver: SatSolver) {
        for (reversedRule in reversedProblem.rules) {
            var i = 0
            val clause = IntArray(FOUR_POSITION.values().size)
            var isAlwaysTrue = false
            for (position in FOUR_POSITION.values()) {
                val tileId = neighbourhood.get(position)
                if (tileId == 0) {
                    throw AssertionError("id must be bigger than 0")
                }
                var value = tileId
                if (reversedRule.isIncluded(position)) {
                    value = -tileId
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