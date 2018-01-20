package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.*
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.tryToFindSolutionForEachRulesSet


fun main(args: Array<String>) {
    val solvable = parseInts(solvableFile)
    val unsolvable = parseInts(unsolvableFile)
    val rules = atLeastOneIncludedAndOneExcluded()
    var combinationNum: Int? = problemToId(rules)

    val currentIteration = ArrayList<Set<VertexRule>>()
    var i = 0
    while (combinationNum != null) {
        i++
        if (i % 1_000_000 == 0) {
            println(combinationNum)
        }
        if (!isUnsolvable(combinationNum, unsolvable) && !isSolvable(combinationNum, solvable)) {
            println("add $combinationNum")
            currentIteration.add(idToProblem(combinationNum))
        } else {
//            println("Solution exist")
        }
        if (currentIteration.size == 10) {
            val newSolvable = tryToFindSolutionForEachRulesSet(currentIteration)
            updateSolvableAndUnsolvable(solvable, unsolvable, newSolvable, currentIteration)
            currentIteration.clear()
        }
        combinationNum = getNextProblemId(combinationNum, rules)
    }
    if (currentIteration.size != 0) {
        val newSolvable = tryToFindSolutionForEachRulesSet(currentIteration)
        updateSolvableAndUnsolvable(solvable, unsolvable, newSolvable, currentIteration)
    }
}

/**
 * @return all possible combinations of rules
 * where center has two or three neighbours
 */
fun getTwoOrThreeNeighboursRules(): Set<VertexRule> {
    val rules = HashSet<VertexRule>()
    rules.addAll(getRulePermutations(1, true))
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))
    rules.addAll(getRulePermutations(2, false))
    rules.addAll(getRulePermutations(3, false))

    return rules
}

