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

    constructor (array: BooleanArray) {
        System.arraycopy(array, 0, this.array, 0, this.array.size)
        var tempId = 0
        (0 until 4).forEach { if (array[it]) tempId += Math.pow(2.toDouble(), it.toDouble()).toInt() }
        id = tempId
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
            }
        }
        return stringBuilder.toString()
    }
}