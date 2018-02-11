package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TilesIterator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TilesParserFactory
import java.io.File


class ISTilesParserFactory : TilesParserFactory<IndependentSetTile>() {
    override fun createParser(file: File): Iterable<IndependentSetTile> = ISTilesParser(file)

    class ISTilesParser(val file: File) : Iterable<IndependentSetTile> {
        val n = BinaryTile.parseNumber(file.name, 1)
        val m = BinaryTile.parseNumber(file.name, 2)
        val k = BinaryTile.parseNumber(file.name, 3)
        val size = BinaryTile.parseNumber(file.name, 4)

        override fun iterator(): Iterator<IndependentSetTile> = ISTilesIterator()

        inner class ISTilesIterator : TilesIterator<IndependentSetTile>(file, size) {

            override fun next(): IndependentSetTile {
                val grid = BinaryTile.parseBitSet(reader.readLine())
                parsedTiles++
                return IndependentSetTile(n, m, k, grid)
            }
        }
    }
}