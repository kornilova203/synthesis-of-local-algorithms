package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles


abstract class Tile(val n: Int, val m: Int) {

    abstract fun isValid(): Boolean

    abstract fun rotate(): BinaryTile
}

abstract class TileFileNameCreator {
    fun getFileName(n: Int, m: Int, size: Int): String = "${getFileNameInner(n, m, size)}.tiles"

    protected abstract fun getFileNameInner(n: Int, m: Int, size: Int): String
}