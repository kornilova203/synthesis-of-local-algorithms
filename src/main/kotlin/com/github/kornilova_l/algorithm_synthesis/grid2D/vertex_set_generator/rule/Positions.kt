package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule

import org.apache.commons.collections4.BidiMap
import org.apache.commons.collections4.bidimap.DualHashBidiMap

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

val positions = hashSetOf(POSITION.X, POSITION.N, POSITION.E, POSITION.S, POSITION.W)

class Positions {
    companion object {
        val positionIndexes: BidiMap<POSITION, Int> = DualHashBidiMap()
        val positionLetters: BidiMap<POSITION, Char> = DualHashBidiMap()

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
    }
}
