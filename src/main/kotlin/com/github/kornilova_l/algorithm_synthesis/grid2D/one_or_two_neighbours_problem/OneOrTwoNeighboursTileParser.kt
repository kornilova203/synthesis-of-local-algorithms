package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.parseBitSet
import com.github.kornilova_l.util.FileNameCreator
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


class OneOrTwoNeighboursTileParser(private val file: File) : Iterable<OneOrTwoNeighboursTile> {
    val n: Int
    val m: Int
    val size: Int

    init {
        if (!file.exists() || !file.isFile) {
            throw IllegalArgumentException("File does not exist or it is not a file")
        }
        if (FileNameCreator.getExtension(file.name) != "tiles") {
            throw IllegalArgumentException("File extension must be 'tiles': ${file.name}")
        }
        n = FileNameCreator.getIntParameter(file.name, "n")!!
        m = FileNameCreator.getIntParameter(file.name, "m")!!
        size = FileNameCreator.getIntParameter(file.name, "size")!!
    }

    override fun iterator(): Iterator<OneOrTwoNeighboursTile> = OneOrTwoNeighboursTileIterator()

    private inner class OneOrTwoNeighboursTileIterator : Iterator<OneOrTwoNeighboursTile> {
        private var tilesParsed = 0
        private val reader: BufferedReader = BufferedReader(FileReader(file))

        override fun hasNext(): Boolean {
            return if (tilesParsed < size) {
                true
            } else {
                reader.close()
                false
            }
        }

        override fun next(): OneOrTwoNeighboursTile {
            val grid = parseBitSet(reader.readLine())
            tilesParsed++
            return OneOrTwoNeighboursTile(n, m, grid)
        }
    }
}