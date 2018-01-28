package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem.OneOrTwoNeighboursTile.Companion.oneOrTwoNeighboursTilesFilePattern
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.parseBitSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.parseNumber
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
        if (!oneOrTwoNeighboursTilesFilePattern.matcher(file.name).matches()) {
            throw IllegalArgumentException("File name does not match pattern: ${file.name}")
        }
        n = parseNumber(file.name, 1)
        m = parseNumber(file.name, 2)
        size = parseNumber(file.name, 3)
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