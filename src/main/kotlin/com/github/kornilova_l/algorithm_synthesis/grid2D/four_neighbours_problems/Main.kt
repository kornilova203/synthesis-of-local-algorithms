package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.printResult
import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem.FourNeighboursProblem


fun main(args: Array<String>) {
    val problem = FourNeighboursProblem("0001", "0010", "0100", "1000", "0011", "0101", "0110", "1001",
            "1010", "1100")
    val labelingFunction = FourNeighboursProblemSolver().getLabelingFunction(problem) ?: return
    printResult(labelingFunction.first, labelingFunction.second)
}