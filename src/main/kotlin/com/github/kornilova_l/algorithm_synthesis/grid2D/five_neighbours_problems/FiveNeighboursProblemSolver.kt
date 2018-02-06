package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FIVE_POSITION
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.ProblemSolver
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.SatSolver


class FiveNeighboursProblemSolver : ProblemSolver<FiveNeighboursProblem>() {

    override fun reverseProblem(problem: FiveNeighboursProblem): FiveNeighboursProblem = problem.reverse()

    override fun rotateProblem(problem: FiveNeighboursProblem): FiveNeighboursProblem = problem.rotate()

    override fun formClause(neighbourhood: DirectedGraph.Neighbourhood,
                            reversedProblem: FiveNeighboursProblem,
                            satSolver: SatSolver) {
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
}