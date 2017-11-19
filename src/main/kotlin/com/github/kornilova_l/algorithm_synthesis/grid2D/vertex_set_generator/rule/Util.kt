package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule

import kotlin.experimental.and


/**
 * @return new set which contains all possible rules but without
 * the rules which are given as a parameter. Size of new set is 32 - rules.size()
 */
fun reverseRules(rules: Set<VertexRule>): Set<VertexRule> {
    val ids = HashSet<Int>() // convert rules to ids for convenient compare
    rules.mapTo(ids) { it.id }

    val reversedRules = HashSet<VertexRule>()
    (0..31).filter { !ids.contains(it) }
            .forEach { reversedRules.add(VertexRule(it)) }
    return reversedRules
}

fun getBit(num: Int, position: Int): Boolean = (num shr position).toByte() and 1 == 1.toByte()

fun getRulePermutations(numberOfIncludedNeighbours: Int, isCenterIncluded: Boolean): Set<VertexRule> {
    if (numberOfIncludedNeighbours < 0 || numberOfIncludedNeighbours > 4) {
        throw IllegalArgumentException("Invalid number of neighbours. Must be in [0, 4]")
    }
    val rulePermutations = HashSet<VertexRule>()
    when (numberOfIncludedNeighbours) {
        0 -> rulePermutations.add(VertexRule(""))
        1 -> rulePermutations.addAll(hashSetOf(
                VertexRule("N"),
                VertexRule("E"),
                VertexRule("S"),
                VertexRule("W")
        ))
        2 -> rulePermutations.addAll(hashSetOf(
                VertexRule("NE"),
                VertexRule("NS"),
                VertexRule("NW"),
                VertexRule("ES"),
                VertexRule("EW"),
                VertexRule("SW")
        ))
        3 -> rulePermutations.addAll(hashSetOf(
                VertexRule("NES"),
                VertexRule("NEW"),
                VertexRule("NSW"),
                VertexRule("ESW")
        ))
        4 -> rulePermutations.add(VertexRule("NESW"))
    }
    if (!isCenterIncluded) {
        return rulePermutations
    }
    return rulePermutations.map { rule -> VertexRule(rule, POSITION.X) }.toSet()
}