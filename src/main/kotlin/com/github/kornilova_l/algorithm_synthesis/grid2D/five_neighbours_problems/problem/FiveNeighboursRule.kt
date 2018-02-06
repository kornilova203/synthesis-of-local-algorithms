package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FourPositions.Companion.positionIndexes
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FourPositions.Companion.positionLetters
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem.VertexRule


val allRulesExceptTrivial = arrayOf(
        FiveNeighboursRule("N"),
        FiveNeighboursRule("E"),
        FiveNeighboursRule("S"),
        FiveNeighboursRule("W"),
        FiveNeighboursRule("NE"),
        FiveNeighboursRule("NS"),
        FiveNeighboursRule("NW"),
        FiveNeighboursRule("ES"),
        FiveNeighboursRule("EW"),
        FiveNeighboursRule("SW"),
        FiveNeighboursRule("NES"),
        FiveNeighboursRule("ESW"),
        FiveNeighboursRule("SWN"),
        FiveNeighboursRule("WNE"),
        FiveNeighboursRule("NESW"),
        FiveNeighboursRule("X"),
        FiveNeighboursRule("XN"),
        FiveNeighboursRule("XE"),
        FiveNeighboursRule("XS"),
        FiveNeighboursRule("XW"),
        FiveNeighboursRule("XNE"),
        FiveNeighboursRule("XNS"),
        FiveNeighboursRule("XNW"),
        FiveNeighboursRule("XES"),
        FiveNeighboursRule("XEW"),
        FiveNeighboursRule("XSW"),
        FiveNeighboursRule("XNES"),
        FiveNeighboursRule("XESW"),
        FiveNeighboursRule("XSWN"),
        FiveNeighboursRule("XWNE")
)

class FiveNeighboursRule : VertexRule {
    /**
     * XNESW
     */
    override val array = BooleanArray(5)
    override val id: Int

    constructor(id: Int) {
        this.id = id
        setArrayValues(id, array)
    }

    constructor(rule: String) {
        if (rule == "") {
            throw IllegalArgumentException("To construct an empty rule use \"-\" string")
        }
        if (rule == "-") {
            this.id = 0
            return
        }
        for (i in 0 until rule.length) {
            val c = rule[i]
            val position = positionLetters.getKey(c)!!
            val index = positionIndexes[position]!!
            array[index] = true
        }
        this.id = calcId(array)
    }

    constructor (array: BooleanArray) {
        System.arraycopy(array, 0, this.array, 0, this.array.size)
        id = calcId(array)
    }

    /**
     * Copies `rule` and toggles position
     */
    constructor(rule: FiveNeighboursRule, position: FIVE_POSITION) {
        System.arraycopy(rule.array, 0, this.array, 0, this.array.size)
        array[positionIndexes[position]!!] = !array[positionIndexes[position]!!] // toggle position
        id = calcId(array)
    }

    fun isIncluded(position: FIVE_POSITION): Boolean =
            when (position) {
                FIVE_POSITION.X -> array[0]
                FIVE_POSITION.N -> array[1]
                FIVE_POSITION.E -> array[2]
                FIVE_POSITION.S -> array[3]
                FIVE_POSITION.W -> array[4]
            }

    override fun rotate(rotationsCount: Int): FiveNeighboursRule {
        val array = array.copyOf()
        for (i in 0 until 4) {
            array[i % 4 + 1] = this.array[(i - rotationsCount + 4) % 4 + 1]
        }
        return FiveNeighboursRule(array)
    }

    override fun toHumanReadableSting(): String {
        val stringBuilder = StringBuilder()
        array.indices
                .filter { array[it] }
                .forEach { stringBuilder.append(positionLetters[positionIndexes.getKey(it)]) }
        if (stringBuilder.isEmpty()) {
            return "-"
        }
        return stringBuilder.toString()
    }
}