package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.rule.FiveNeighboursRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.rule.POSITION
import kotlin.experimental.and


fun getBit(num: Int, position: Int): Boolean = (num shr position).toByte() and 1 == 1.toByte()

fun getRulePermutations(numberOfIncludedNeighbours: Int, isCenterIncluded: Boolean): Set<FiveNeighboursRule> {
    if (numberOfIncludedNeighbours < 0 || numberOfIncludedNeighbours > 4) {
        throw IllegalArgumentException("Invalid number of neighbours. Must be in [0, 4]")
    }
    val rulePermutations = HashSet<FiveNeighboursRule>()
    when (numberOfIncludedNeighbours) {
        0 -> rulePermutations.add(FiveNeighboursRule("-"))
        1 -> rulePermutations.addAll(hashSetOf(
                FiveNeighboursRule("N"),
                FiveNeighboursRule("E"),
                FiveNeighboursRule("S"),
                FiveNeighboursRule("W")
        ))
        2 -> rulePermutations.addAll(hashSetOf(
                FiveNeighboursRule("NE"),
                FiveNeighboursRule("NS"),
                FiveNeighboursRule("NW"),
                FiveNeighboursRule("ES"),
                FiveNeighboursRule("EW"),
                FiveNeighboursRule("SW")
        ))
        3 -> rulePermutations.addAll(hashSetOf(
                FiveNeighboursRule("NES"),
                FiveNeighboursRule("NEW"),
                FiveNeighboursRule("NSW"),
                FiveNeighboursRule("ESW")
        ))
        4 -> rulePermutations.add(FiveNeighboursRule("NESW"))
    }
    if (!isCenterIncluded) {
        return rulePermutations
    }
    return rulePermutations.map { rule -> FiveNeighboursRule(rule, POSITION.X) }.toSet()
}