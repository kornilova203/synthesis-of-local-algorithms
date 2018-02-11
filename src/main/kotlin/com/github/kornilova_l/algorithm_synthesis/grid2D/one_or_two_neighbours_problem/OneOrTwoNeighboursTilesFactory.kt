package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TilesIterator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TilesParserFactory
import com.github.kornilova_l.util.FileNameCreator
import java.io.File


class OneOrTwoNeighboursTilesFactory : TilesParserFactory<OneOrTwoNeighboursTile>() {
    override fun createParser(file: File): Iterable<OneOrTwoNeighboursTile> = TilesParser(file)

    class TilesParser(val file: File) : Iterable<OneOrTwoNeighboursTile> {
        val n = FileNameCreator.getIntParameter(file.name, "n")!!
        val m = FileNameCreator.getIntParameter(file.name, "m")!!
        val size = FileNameCreator.getIntParameter(file.name, "size")!!

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