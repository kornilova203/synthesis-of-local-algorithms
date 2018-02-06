package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem

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
enum class FIVE_POSITION {
    X,
    N,
    E,
    S,
    W
}

class FourPositions {
    companion object {
        val positionIndexes: BidiMap<FIVE_POSITION, Int> = DualHashBidiMap()
        val positionLetters: BidiMap<FIVE_POSITION, Char> = DualHashBidiMap()

        init {
            positionIndexes[FIVE_POSITION.X] = 0
            positionIndexes[FIVE_POSITION.N] = 1
            positionIndexes[FIVE_POSITION.E] = 2
            positionIndexes[FIVE_POSITION.S] = 3
            positionIndexes[FIVE_POSITION.W] = 4

            positionLetters[FIVE_POSITION.X] = 'X'
            positionLetters[FIVE_POSITION.N] = 'N'
            positionLetters[FIVE_POSITION.E] = 'E'
            positionLetters[FIVE_POSITION.S] = 'S'
            positionLetters[FIVE_POSITION.W] = 'W'
        }
    }
}
