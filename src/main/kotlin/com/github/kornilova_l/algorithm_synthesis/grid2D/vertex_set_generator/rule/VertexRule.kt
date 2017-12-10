package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.Positions.Companion.positionIndexes
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.Positions.Companion.positionLetters

/**
 * @param pattern that describes set of rules
 * Each character tells if this position XNESW is included (1), not included (0) or may be any (?)
 * For example this pattern 11?0? will generate set of 4 rules
 */
fun getVertexRules(pattern: String): Set<VertexRule> {
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

fun rotateRuleSet(rules: Set<VertexRule>, rotationsCount: Int = 1): Set<VertexRule> {
    return rules.map { rule -> rule.rotate(rotationsCount) }.toSet()
}

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