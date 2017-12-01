package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles


class TilesIntersection(biggerTile: Tile, smallerTile: Tile) {
    private val leftEnd: Int
    private val rightStart: Int
    private val topEnd: Int
    private val bottomStart: Int

    init {
        if (biggerTile.m == smallerTile.m &&
                biggerTile.n == smallerTile.n) {
            throw IllegalArgumentException("Tiles have the same size")
        }
        if (biggerTile.m < smallerTile.m || biggerTile.n < smallerTile.n) {
            throw IllegalArgumentException("Change order of tiles")
        }
        when (biggerTile.m) {
            smallerTile.m -> {
                leftEnd = 0
                rightStart = biggerTile.m - 1
            }
            smallerTile.m + 1 -> {
                leftEnd = 0
                rightStart = smallerTile.m
            }
            else -> {
                leftEnd = (biggerTile.m - smallerTile.m) / 2
                rightStart = biggerTile.m - Math.ceil((biggerTile.m - smallerTile.m) / 2.toDouble()).toInt()
            }
        }

        when (biggerTile.n) {
            smallerTile.n -> {
                topEnd = 0
                bottomStart = biggerTile.n - 1
            }
            smallerTile.n + 1 -> {
                topEnd = 0
                bottomStart = smallerTile.n
            }
            else -> {
                topEnd = (biggerTile.n - smallerTile.n) / 2
                bottomStart = biggerTile.n - Math.ceil((biggerTile.n - smallerTile.n) / 2.toDouble()).toInt()
            }
        }
    }

    fun isInside(x: Int, y: Int): Boolean {
        return x in topEnd until bottomStart &&
                y in leftEnd until rightStart
    }
}