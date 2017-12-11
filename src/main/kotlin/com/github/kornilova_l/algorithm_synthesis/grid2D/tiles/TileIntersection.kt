package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles


class TileIntersection(biggerN: Int, biggerM: Int, smallerN: Int, smallerM: Int) {
    private val leftEnd: Int
    private val rightStart: Int
    private val topEnd: Int
    private val bottomStart: Int

    init {
        if (biggerM == smallerM &&
                biggerN == smallerN) {
            throw IllegalArgumentException("Tiles have the same size")
        }
        if (biggerM < smallerM || biggerN < smallerN) {
            throw IllegalArgumentException("Change order of tiles")
        }
        when (biggerM) {
            smallerM -> {
                leftEnd = 0
                rightStart = biggerM - 1
            }
            smallerM + 1 -> {
                leftEnd = 0
                rightStart = smallerM
            }
            else -> {
                leftEnd = (biggerM - smallerM) / 2
                rightStart = biggerM - Math.ceil((biggerM - smallerM) / 2.toDouble()).toInt()
            }
        }

        when (biggerN) {
            smallerN -> {
                topEnd = 0
                bottomStart = biggerN - 1
            }
            smallerN + 1 -> {
                topEnd = 0
                bottomStart = smallerN
            }
            else -> {
                topEnd = (biggerN - smallerN) / 2
                bottomStart = biggerN - Math.ceil((biggerN - smallerN) / 2.toDouble()).toInt()
            }
        }
    }

    fun isInside(x: Int, y: Int): Boolean {
        return x in topEnd until bottomStart &&
                y in leftEnd until rightStart
    }
}