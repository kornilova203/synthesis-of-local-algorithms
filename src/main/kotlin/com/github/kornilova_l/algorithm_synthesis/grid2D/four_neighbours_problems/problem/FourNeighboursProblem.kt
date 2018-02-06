package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursProblem.Companion.validatePattern
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem.Problem


class FourNeighboursProblem(rules: Set<FourNeighboursRule>) : Problem<FourNeighboursRule>(rules) {

    /**
     * @param pattern contains 4 characters. Each character must be one of the following: '1', '0' and '?'
     * first character correspond to top left, second - top right, third - bottom right, fourth - bottom left
     */
    constructor(pattern: String) : this(patternToRules(pattern))

    override fun reverse(): FourNeighboursProblem {
        val ids = HashSet<Int>() // convert rules to ids for convenient compare
        rules.mapTo(ids) { it.id }

        val reversedRules = HashSet<FourNeighboursRule>()
        (0 until Math.pow(2.0, 4.0).toInt()).filter { !ids.contains(it) }
                .forEach { reversedRules.add(FourNeighboursRule(it)) }
        return FourNeighboursProblem(reversedRules)
    }

    override fun rotate(rotationsCount: Int): FourNeighboursProblem = FourNeighboursProblem(
            rules.map { rule -> rule.rotate(rotationsCount) }.toSet())

    companion object {

        fun patternToRules(pattern: String): Set<FourNeighboursRule> {
            validatePattern(pattern, 4)
            val arrays = hashSetOf(BooleanArray(4))
            pattern.forEachIndexed { i, c ->
                if (c == '0' || c == '1') {
                    val value = c == '1'
                    arrays.addAll(arrays.map { array -> array[i] = value; array })
                } else { // c == '?'
                    arrays.addAll(arrays.flatMap { array ->
                        val array2 = array.copyOf()
                        array2[i] = true
                        hashSetOf(array, array2)
                    })
                }
            }
            return arrays.map { array -> FourNeighboursRule(array) }.toSet()
        }
    }
}