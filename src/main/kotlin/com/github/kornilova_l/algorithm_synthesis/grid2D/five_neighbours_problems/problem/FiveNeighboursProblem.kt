package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem.Problem


fun getNextProblemId(combinationNum: Int, allowedRules: FiveNeighboursNonTrivialProblem): Int? {
    val allowedRulesId = allowedRules.getId()
    for (i in combinationNum - 1 downTo 0) {
        val xor = allowedRulesId.xor(i)
        if (xor.and(i) == 0) {
            return i
        }
    }
    return null
}

open class FiveNeighboursProblem(rules: Set<FiveNeighboursRule>) : Problem<FiveNeighboursRule>(rules) {

    constructor(pattern: String) : this(patternToRules(pattern))

    constructor(combinationNum: Int) : this(idToRules(combinationNum))

    override fun reverse(): FiveNeighboursProblem {
        val ids = HashSet<Int>() // convert rules to ids for convenient compare
        rules.mapTo(ids) { it.id }

        val reversedRules = HashSet<FiveNeighboursRule>()
        (0 until Math.pow(2.0, 5.0).toInt()).filter { !ids.contains(it) }
                .forEach { reversedRules.add(FiveNeighboursRule(it)) }
        return FiveNeighboursProblem(reversedRules)
    }

    override fun rotate(rotationsCount: Int): FiveNeighboursProblem = FiveNeighboursProblem(
            rules.map { rule -> rule.rotate(rotationsCount) }.toSet())

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (rule in rules) {
            stringBuilder.append("$rule ")
        }
        return stringBuilder.toString()
    }

    override fun hashCode(): Int {
        return rules.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is FiveNeighboursProblem) {
            return rules == other.rules
        }
        return false
    }

    companion object {

        fun parseProblem(line: String): FiveNeighboursProblem {
            /* remove '[' and ']' or any other trailing symbols */
            var startIndex = 0
            while (!isRuleChar(line[startIndex])) {
                startIndex++
            }
            var endIndex = line.length
            while (!isRuleChar(line[endIndex - 1])) {
                endIndex--
            }
            val cutLine = line.substring(startIndex, endIndex)
            val parts = if (cutLine.contains(", ")) cutLine.split(", ") else cutLine.split(" ")
            return FiveNeighboursProblem(
                    parts.map { part -> FiveNeighboursRule(part) }.toSet()
            )
        }

        private fun isRuleChar(c: Char): Boolean {
            return c == 'X' || c == 'N' || c == 'E' || c == 'W' || c == 'S' || c == '-'
        }

        private fun patternToRules(pattern: String): Set<FiveNeighboursRule> {
            validatePattern(pattern, 5)
            val arrays = hashSetOf(BooleanArray(5))
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
            return arrays.map { array -> FiveNeighboursRule(array) }.toSet()
        }

        fun validatePattern(pattern: String, length: Int) {
            if (pattern.length != length) {
                throw IllegalArgumentException("Length of pattern must be $length. Patten: $pattern")
            }
            pattern.forEach {
                if (it != '0' && it != '1' && it != '?') {
                    throw IllegalArgumentException("Pattern may contain only '0', '1' and '?'")
                }
            }
        }

        internal fun idToRules(combinationNum: Int): Set<FiveNeighboursRule> {
            val rules = HashSet<FiveNeighboursRule>()
            (0..31).forEach { shift ->
                if (combinationNum.and(1.shl(shift)) > 0) { // if rule is included
                    rules.add(allRulesExceptTrivial[shift])
                }
            }
            return rules
        }
    }
}