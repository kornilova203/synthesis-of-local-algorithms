package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import org.apache.commons.collections4.BidiMap
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import kotlin.experimental.and

/**
 * ____1
 * _4__0__2
 * ____3
 *
 * =>
 *
 * ____N
 * _W__X__E
 * ____S
 */

enum class POSITION {
    X,
    N,
    E,
    S,
    W
}

val positionIndexes: BidiMap<POSITION, Int> = DualHashBidiMap()
val positionLetters: BidiMap<POSITION, Char> = DualHashBidiMap()
val positions = hashSetOf(POSITION.X, POSITION.N, POSITION.E, POSITION.S, POSITION.W)

class VertexRule(id: Int) {
    val array = Array(32, { false })

    init {
        if (id >= 32) {
            throw IllegalArgumentException("Id must be smaller than 32")
        }
        (0..4).filter { getBit(id, it) }
                .forEach { array[it] = true }
    }

    fun isIncluded(position: POSITION): Boolean = array[positionIndexes[position]!!]

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

    companion object {
        init {
            positionIndexes[POSITION.X] = 0
            positionIndexes[POSITION.N] = 1
            positionIndexes[POSITION.E] = 2
            positionIndexes[POSITION.S] = 3
            positionIndexes[POSITION.W] = 4

            positionLetters[POSITION.X] = 'X'
            positionLetters[POSITION.N] = 'N'
            positionLetters[POSITION.E] = 'E'
            positionLetters[POSITION.S] = 'S'
            positionLetters[POSITION.W] = 'W'
        }

        private fun getBit(num: Int, position: Int): Boolean = (num shr position).toByte() and 1 == 1.toByte()
    }
}