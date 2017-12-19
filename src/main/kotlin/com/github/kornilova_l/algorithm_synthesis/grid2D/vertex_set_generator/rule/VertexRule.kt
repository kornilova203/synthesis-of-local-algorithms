package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.Positions.Companion.positionIndexes
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.Positions.Companion.positionLetters

/**
 * @param pattern that describes set of rules
 * Each character tells if this position XNESW is included (1), not included (0) or may be any (?)
 * For example this pattern 11?0? will generate set of 4 rules
 */
fun patternToProblem(pattern: String): Set<VertexRule> {
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
    return arrays.map { array -> VertexRule(array) }.toSet()
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

fun rotateProblem(rules: Set<VertexRule>, rotationsCount: Int = 1): Set<VertexRule> {
    return rules.map { rule -> rule.rotate(rotationsCount) }.toSet()
}

fun parseProblem(line: String): Set<VertexRule> {
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
    return parts.map { part -> VertexRule(part) }.toSet()
}

private fun isRuleChar(c: Char): Boolean {
    return c == 'X' || c == 'N' || c == 'E' || c == 'W' || c == 'S' || c == '-'
}

fun idToProblem(combinationNum: Long): Set<VertexRule> {
    val rules = HashSet<VertexRule>()
    (0..31).forEach { shift ->
        if (combinationNum.and(1.toLong().shl(shift)) > 0) { // if rule is included
            rules.add(allRulesExceptTrivial[shift])
        }
    }
    return rules
}

fun getNextProblemId(combinationNum: Long, allowedRules: Set<VertexRule>): Long? {
    val allowedRulesId = problemToId(allowedRules)
    for (i in combinationNum - 1 downTo 0) {
        val xor = allowedRulesId.xor(i)
        if (xor.and(i) == 0L) {
            return i
        }
    }
    return null
}

fun problemToId(rules: Set<VertexRule>): Long {
    var setId: Long = 0
    for (rule in rules) {
        var ruleId = -1
        for (i in 0 until allRulesExceptTrivial.size) {
            if (rule == allRulesExceptTrivial[i]) {
                ruleId = i
                break
            }
        }
        assert(ruleId != -1)
        setId = setId.or(1.shl(ruleId).toLong())
    }
    return setId
}

val allRulesExceptTrivial = arrayOf(
        VertexRule("N"),
        VertexRule("E"),
        VertexRule("S"),
        VertexRule("W"),
        VertexRule("NE"),
        VertexRule("NS"),
        VertexRule("NW"),
        VertexRule("ES"),
        VertexRule("EW"),
        VertexRule("SW"),
        VertexRule("NES"),
        VertexRule("ESW"),
        VertexRule("SWN"),
        VertexRule("WNE"),
        VertexRule("NESW"),
        VertexRule("X"),
        VertexRule("XN"),
        VertexRule("XE"),
        VertexRule("XS"),
        VertexRule("XW"),
        VertexRule("XNE"),
        VertexRule("XNS"),
        VertexRule("XNW"),
        VertexRule("XES"),
        VertexRule("XEW"),
        VertexRule("XSW"),
        VertexRule("XNES"),
        VertexRule("XESW"),
        VertexRule("XSWN"),
        VertexRule("XWNE")
)

class VertexRule {
    /**
     * XNESW
     */
    val array = BooleanArray(5)
    val id: Int

    constructor(id: Int) {
        this.id = id
        if (id >= 32) {
            throw IllegalArgumentException("Id must be smaller than 32")
        }
        (0..4).filter { getBit(id, it) }
                .forEach { array[it] = true }
    }

    constructor(rule: String) {
        if (rule == "") {
            throw IllegalArgumentException("To construct an empty rule use \"-\" string")
        }
        if (rule == "-") {
            this.id = 0
            return
        }
        var id = 0
        for (i in 0 until rule.length) {
            val c = rule[i]
            val position = positionLetters.getKey(c)!!
            val index = positionIndexes[position]!!
            array[index] = true
            id += Math.pow(2.toDouble(), index.toDouble()).toInt()
        }
        this.id = id
    }

    constructor (array: BooleanArray) {
        System.arraycopy(array, 0, this.array, 0, this.array.size)
        var tempId = 0
        (0 until 5).forEach { if (array[it]) tempId += Math.pow(2.toDouble(), it.toDouble()).toInt() }
        id = tempId
    }

    /**
     * Copies `rule` and toggles position
     */
    constructor(rule: VertexRule, position: POSITION) {
        System.arraycopy(rule.array, 0, this.array, 0, this.array.size)
        array[positionIndexes[position]!!] = !array[positionIndexes[position]!!] // toggle position
        var tempId = 0
        (0 until 5).forEach { if (array[it]) tempId += Math.pow(2.toDouble(), it.toDouble()).toInt() }
        id = tempId
    }

    fun isIncluded(position: POSITION): Boolean = array[positionIndexes[position]!!]

    fun rotate(rotationsCount: Int = 1): VertexRule {
        val array = array.copyOf()
        for (i in 0 until 4) {
            array[i % 4 + 1] = this.array[(i - rotationsCount + 4) % 4 + 1]
        }
        return VertexRule(array)
    }

    fun toHumanReadableSting(): String {
        val stringBuilder = StringBuilder()
        array.indices
                .filter { array[it] }
                .forEach { stringBuilder.append(positionLetters[positionIndexes.getKey(it)]) }
        if (stringBuilder.isEmpty()) {
            return "-"
        }
        return stringBuilder.toString()
    }

    override fun toString(): String = toHumanReadableSting()

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VertexRule

        if (id != other.id) return false

        return true
    }
}