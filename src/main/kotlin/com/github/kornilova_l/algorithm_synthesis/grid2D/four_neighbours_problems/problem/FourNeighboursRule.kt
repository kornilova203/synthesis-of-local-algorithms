package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem.VertexRule


class FourNeighboursRule : VertexRule {
    /* top left, top right, bottom right, bottom left */
    override val array = BooleanArray(4)

    override val id: Int

    constructor(id: Int) {
        this.id = id
        setArrayValues(id, array)
    }

    constructor(array: BooleanArray) {
        if (array.size != 4) {
            throw IllegalArgumentException("Size of array must be 4. Array: $array")
        }
        System.arraycopy(array, 0, this.array, 0, this.array.size)
        id = calcId(array)
    }

    constructor(string: String) {
        val parts = string.split(" ").filter { it != "" }
        val array = BooleanArray(4)
        for (part in parts) {
            val position = FourPositions.positionLetters.getKey(part)!!
            array[FourPositions.positionIndexes[position]!!] = true
        }
        id = calcId(array)
    }

    private fun calcId(array: BooleanArray): Int {
        var tempId = 0
        (0 until 4).forEach { if (array[it]) tempId += Math.pow(2.toDouble(), it.toDouble()).toInt() }
        return tempId
    }

    override fun rotate(rotationsCount: Int): FourNeighboursRule {
        val rotatedArray = BooleanArray(4)
        for (i in 0 until 4) {
            rotatedArray[(i + rotationsCount) % 4] = array[i]
        }
        return FourNeighboursRule(rotatedArray)
    }

    override fun toHumanReadableSting(): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until array.size) {
            if (array[i]) {
                stringBuilder.append(FourPositions.positionIndexes.getKey(i))
                if (i != array.size - 1) {
                    stringBuilder.append(" ")
                }
            }
        }
        return "[" + stringBuilder.toString() + "]"
    }

    fun isIncluded(position: FOUR_POSITION): Boolean = array[FourPositions.positionIndexes[position]!!]
}