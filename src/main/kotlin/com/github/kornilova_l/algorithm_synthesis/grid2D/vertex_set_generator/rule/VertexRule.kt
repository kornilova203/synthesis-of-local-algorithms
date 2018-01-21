package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.Positions.Companion.positionIndexes
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.Positions.Companion.positionLetters


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

    fun isIncluded(position: POSITION): Boolean =
            when (position) {
                POSITION.X -> array[0]
                POSITION.N -> array[1]
                POSITION.E -> array[2]
                POSITION.S -> array[3]
                POSITION.W -> array[4]
            }

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