package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.rule

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem.Problem


class FourNeighboursProblem(rules: Set<FourNeighboursRule>) : Problem<FourNeighboursRule>(rules) {

    override fun reverse(): FourNeighboursProblem {
        val ids = HashSet<Int>() // convert rules to ids for convenient compare
        rules.mapTo(ids) { it.id }

        val reversedRules = HashSet<FourNeighboursRule>()
        (0 until Math.pow(2.0, 5.0).toInt()).filter { !ids.contains(it) }
                .forEach { reversedRules.add(FourNeighboursRule(it)) }
        return FourNeighboursProblem(reversedRules)
    }

    override fun rotate(rotationsCount: Int): FourNeighboursProblem = FourNeighboursProblem(
            rules.map { rule -> rule.rotate(rotationsCount) }.toSet())
}