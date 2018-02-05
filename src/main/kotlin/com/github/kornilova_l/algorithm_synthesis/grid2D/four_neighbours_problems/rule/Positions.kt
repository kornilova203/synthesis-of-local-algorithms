package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.rule

import org.apache.commons.collections4.BidiMap
import org.apache.commons.collections4.bidimap.DualHashBidiMap

/**
 * TL TR
 * BL BR
 */
enum class FOUR_POSITION {
    TL,
    TR,
    BR,
    BL
}

class FourPositions {
    companion object {
        val positionIndexes: BidiMap<FOUR_POSITION, Int> = DualHashBidiMap()
        val positionLetters: BidiMap<FOUR_POSITION, String> = DualHashBidiMap()

        init {
            positionIndexes[FOUR_POSITION.TL] = 0
            positionIndexes[FOUR_POSITION.TR] = 1
            positionIndexes[FOUR_POSITION.BR] = 2
            positionIndexes[FOUR_POSITION.BL] = 3

            positionLetters[FOUR_POSITION.TL] = "TL"
            positionLetters[FOUR_POSITION.TR] = "TR"
            positionLetters[FOUR_POSITION.BR] = "BR"
            positionLetters[FOUR_POSITION.BL] = "BL"
        }
    }
}
