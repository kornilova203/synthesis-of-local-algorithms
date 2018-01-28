package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.parseSet
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
        var n: Int = -1
        var m: Int = -1
        var tilesCount: Int = -1
        BufferedReader(FileReader(file)).use { reader ->
            val firstLine = reader.readLine()
            val parts = firstLine.split(" ")
            n = Integer.parseInt(parts[0])
            m = Integer.parseInt(parts[1])
            tilesCount = Integer.parseInt(reader.readLine())
        }
        if (n != -1 && m != -1 && tilesCount != -1) {
            this.n = n
            this.m = m
            this.size = tilesCount
        } else {
            throw IllegalArgumentException("Cannot read n, m and size from file")
        }
    }

    override fun iterator(): Iterator<OneOrTwoNeighboursTile> = OneOrTwoNeighboursTileIterator()

    private inner class OneOrTwoNeighboursTileIterator : Iterator<OneOrTwoNeighboursTile> {
        private var tilesParsed = 0
        private val reader: BufferedReader = BufferedReader(FileReader(file))

        init {
            /* skip lines with n, m and size */
            reader.readLine()
            reader.readLine()
            // do not close stream
        }

        override fun hasNext(): Boolean {
            return if (tilesParsed < size) {
                true
            } else {
                reader.close()
                false
            }
        }

        override fun next(): OneOrTwoNeighboursTile {
            val grid = parseSet(reader, n, m)
            tilesParsed++
            return OneOrTwoNeighboursTile(n, m, grid)
        }
    }
}