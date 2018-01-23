package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import org.apache.lucene.util.OpenBitSet


abstract class Tile(val n: Int,
                    val m: Int,
                    protected val grid: OpenBitSet) {

    abstract fun isValid(): Boolean

    protected fun getIndex(x: Int, y: Int): Long {
        return (x * m + y).toLong()
    }

    protected fun getIndex(x: Long, y: Long): Long {
        return x * m + y
    }
}