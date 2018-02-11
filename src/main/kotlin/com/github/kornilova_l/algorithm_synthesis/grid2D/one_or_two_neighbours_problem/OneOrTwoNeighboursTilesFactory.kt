package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TilesIterator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TilesParserFactory
import java.io.File


class OneOrTwoNeighboursTilesFactory : TilesParserFactory<OneOrTwoNeighboursTile>() {
    override fun createParser(file: File): Iterable<OneOrTwoNeighboursTile> = TilesParser(file)

    class TilesParser(val file: File) : Iterable<OneOrTwoNeighboursTile> {
        val n = BinaryTile.parseNumber(file.name, 1)
        val m = BinaryTile.parseNumber(file.name, 2)
        val size = BinaryTile.parseNumber(file.name, 3)

        override fun iterator(): Iterator<OneOrTwoNeighboursTile> = ISTilesIterator()

        inner class ISTilesIterator : TilesIterator<OneOrTwoNeighboursTile>(file, size) {

            override fun next(): OneOrTwoNeighboursTile {
                val grid = BinaryTile.parseBitSet(reader.readLine())
                parsedTiles++
                return OneOrTwoNeighboursTile(n, m, grid)
            }
        }
    }

}