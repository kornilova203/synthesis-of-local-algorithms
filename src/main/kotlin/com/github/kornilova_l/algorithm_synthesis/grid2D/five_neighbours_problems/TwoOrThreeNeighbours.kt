package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursNonTrivialProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.getNextProblemId
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem.getRulePermutations


fun main(args: Array<String>) {
    val solvable = parseInts(solvableFile)
    val unsolvable = parseInts(unsolvableFile)
    val problem = atLeastOneIncludedAndOneExcluded()
    var combinationNum: Int? = problem.getId()

    val currentIteration = ArrayList<FiveNeighboursNonTrivialProblem>()
    var i = 0
    while (combinationNum != null) {
        i++
        if (i % 1_000_000 == 0) {
            println(combinationNum)
        }
        if (!isUnsolvable(combinationNum, unsolvable) && !isSolvable(combinationNum, solvable)) {
            println("add $combinationNum")
            currentIteration.add(FiveNeighboursNonTrivialProblem(combinationNum))
        } else {
//            println("Solution exist")
        }
        if (currentIteration.size == 10) {
            val newSolvable = FiveNeighboursProblemSolver().tryToFindSolutionForEachProblem(currentIteration)
            updateSolvableAndUnsolvable(solvable, unsolvable, newSolvable, currentIteration)
            currentIteration.clear()
        }
        combinationNum = getNextProblemId(combinationNum, problem)
    }
    if (currentIteration.size != 0) {
        val newSolvable = FiveNeighboursProblemSolver().tryToFindSolutionForEachProblem(currentIteration)
        updateSolvableAndUnsolvable(solvable, unsolvable, newSolvable, currentIteration)
    }
}

/**
 * @return all possible combinations of rules
 * where center has two or three neighbours
 */
fun getTwoOrThreeNeighboursRules(): FiveNeighboursProblem {
    val rules = HashSet<FiveNeighboursRule>()
    rules.addAll(getRulePermutations(1, true))
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))
    rules.addAll(getRulePermutations(2, false))
    rules.addAll(getRulePermutations(3, false))

    return FiveNeighboursProblem(rules)
}

