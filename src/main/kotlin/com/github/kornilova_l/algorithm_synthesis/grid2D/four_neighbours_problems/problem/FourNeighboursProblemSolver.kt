package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.ProblemSolver
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.SatSolver


class FourNeighboursProblemSolver : ProblemSolver<FourNeighboursProblem>() {
    override fun rotateProblem(problem: FourNeighboursProblem): FourNeighboursProblem = problem.rotate()

    override fun reverseProblem(problem: FourNeighboursProblem): FourNeighboursProblem = problem.reverse()

    override fun formClause(neighbourhood: DirectedGraph.Neighbourhood, reversedProblem: FourNeighboursProblem, satSolver: SatSolver) {
    }
}