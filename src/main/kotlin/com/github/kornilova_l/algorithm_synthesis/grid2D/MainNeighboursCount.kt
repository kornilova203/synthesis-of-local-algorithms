package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getRulePermutations
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.tryToFindSolutionForEachProblem


/**
 * List<List<Int>>
 * each element of list contains combination of numbers
 * each number tell how many neighbours will be included.
 * For example, if list contains 0, 1, 4
 * it means that a node may have 0, 1 or 4 included neighbours.
 * So XN, N, NESW, X, ... combinations are valid (it is not specified if center is included)
 * and NS, NSW, ... combinations are invalid
 */
val includedNeighboursCountLists = (1..31).map { i ->
    val list = ArrayList<Int>(5)
    (0..4).forEach { pos ->
        if (i.and(1.shl(pos)) != 0) {
            list.add(pos)
        }
    }
    list
}

/**
 *
 */
fun main(args: Array<String>) {
    val problems = ArrayList<Set<VertexRule>>()
    for (includedNeighboursCountList in includedNeighboursCountLists) {
        val problem = HashSet<VertexRule>()
        for (includedNeighboursCount in includedNeighboursCountList) {
            problem.addAll(getRulePermutations(includedNeighboursCount, true))
            problem.addAll(getRulePermutations(includedNeighboursCount, false))
        }
        problems.add(problem)
    }
    val solvableProblems = tryToFindSolutionForEachProblem(problems)
    println(solvableProblems)
}