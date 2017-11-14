package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

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
private val positionLetters: Array<Char> = arrayOf('X', 'N', 'E', 'S', 'W')

fun toHumanReadableSting(combinationId: Int): String {
    if (combinationId >= 32) {
        throw IllegalArgumentException("CombinationId must be smaller than 2^5")
    }
    val stringBuilder = StringBuilder()
    @Suppress("LoopToCallChain")
    for (i in 0..4) {
        if (getBit(combinationId, i))
            stringBuilder.append(positionLetters[i])
    }
    if (stringBuilder.isEmpty()) {
        return "-"
    }
    return stringBuilder.toString()
}

private fun getBit(num: Int, position: Int): Boolean {
    return (num shr position).toByte() and 1 == 1.toByte()
}
