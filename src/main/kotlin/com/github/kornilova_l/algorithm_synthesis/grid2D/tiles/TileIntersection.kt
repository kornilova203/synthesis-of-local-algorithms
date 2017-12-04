package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles


class TileIntersection(n1: Int, m1: Int, n2: Int, m2: Int) {
    private val leftEnd: Int
    private val rightStart: Int
    private val topEnd: Int
    private val bottomStart: Int

    init {
        if (m1 == m2 &&
                n1 == n2) {
            throw IllegalArgumentException("Tiles have the same size")
        }
        if (m1 < m2 || n1 < n2) {
            throw IllegalArgumentException("Change order of tiles")
        }
        when (m1) {
            m2 -> {
                leftEnd = 0
                rightStart = m1 - 1
            }
            m2 + 1 -> {
                leftEnd = 0
                rightStart = m2
            }
            else -> {
                leftEnd = (m1 - m2) / 2
                rightStart = m1 - Math.ceil((m1 - m2) / 2.toDouble()).toInt()
            }
        }

        when (n1) {
            n2 -> {
                topEnd = 0
                bottomStart = n1 - 1
            }
            n2 + 1 -> {
                topEnd = 0
                bottomStart = n2
            }
            else -> {
                topEnd = (n1 - n2) / 2
                bottomStart = n1 - Math.ceil((n1 - n2) / 2.toDouble()).toInt()
            }
        }
    }

    fun isInside(x: Int, y: Int): Boolean {
        return x in topEnd until bottomStart &&
                y in leftEnd until rightStart
    }
}