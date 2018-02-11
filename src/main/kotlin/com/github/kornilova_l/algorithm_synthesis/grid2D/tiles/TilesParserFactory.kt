package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import java.io.BufferedReader
import java.io.File
import java.io.FileReader


abstract class TilesParserFactory<out T : BinaryTile> {
    abstract fun createParser(file: File): Iterable<T>
}

abstract class TilesIterator<out T : BinaryTile>(file: File, val size: Int) : Iterator<T> {
    var parsedTiles = 0
    protected val reader: BufferedReader = BufferedReader(FileReader(file))

    override fun hasNext(): Boolean = parsedTiles < size
}
