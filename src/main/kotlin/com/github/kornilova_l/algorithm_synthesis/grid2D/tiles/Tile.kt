package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles


abstract class Tile(val n: Int, val m: Int) {

    abstract fun isValid(): Boolean

    abstract fun rotate(): BinaryTile
}