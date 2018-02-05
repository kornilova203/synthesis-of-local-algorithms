package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.rule.FiveNeighboursRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.rule.allRulesExceptTrivial


fun getNextProblemId(combinationNum: Int, allowedRules: FiveNeighboursProblem): Int? {
    val allowedRulesId = allowedRules.getId()
    for (i in combinationNum - 1 downTo 0) {
        val xor = allowedRulesId.xor(i)
        if (xor.and(i) == 0) {
            return i
        }
    }
    return null
}

class FiveNeighboursProblem(rules: Set<FiveNeighboursRule>) : Problem<FiveNeighboursRule>(rules) {

    constructor(pattern: String) : this(patternToRules(pattern))

    constructor(combinationNum: Int) : this(idToRules(combinationNum))

    override fun reverse(): FiveNeighboursProblem {
        val ids = HashSet<Int>() // convert rules to ids for convenient compare
        rules.mapTo(ids) { it.id }

        val reversedRules = HashSet<FiveNeighboursRule>()
        (0..31).filter { !ids.contains(it) }
                .forEach { reversedRules.add(FiveNeighboursRule(it)) }
        return FiveNeighboursProblem(reversedRules)
    }

    override fun rotate(rotationsCount: Int): FiveNeighboursProblem = FiveNeighboursProblem(
            rules.map { rule -> rule.rotate(rotationsCount) }.toSet())


    override fun getId(): Int {
        var setId = 0
        for (rule in rules) {
            var ruleId = -1
            for (i in 0 until allRulesExceptTrivial.size) {
                if (rule == allRulesExceptTrivial[i]) {
                    ruleId = i
                    break
                }
            }
            assert(ruleId != -1)
            setId = setId.or(1.shl(ruleId))
        }
        return setId
    }

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
            validatePattern(pattern)
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

        private fun validatePattern(pattern: String) {
            if (pattern.length != 5) {
                throw IllegalArgumentException("Length of pattern must be 5")
            }
            pattern.forEach {
                if (it != '0' && it != '1' && it != '?') {
                    throw IllegalArgumentException("Pattern may contain only '0', '1' and '?'")
                }
            }
        }

        private fun idToRules(combinationNum: Int): Set<FiveNeighboursRule> {
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