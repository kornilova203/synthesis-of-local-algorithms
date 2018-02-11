package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TilesIterator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TilesParserFactory
import com.github.kornilova_l.util.FileNameCreator
import java.io.File


class ISTilesParserFactory : TilesParserFactory<IndependentSetTile>() {
    override fun createParser(file: File): Iterable<IndependentSetTile> = ISTilesParser(file)

    class ISTilesParser(val file: File) : Iterable<IndependentSetTile> {
        val n = FileNameCreator.getIntParameter(file.name, "n")!!
        val m = FileNameCreator.getIntParameter(file.name, "m")!!
        val k = FileNameCreator.getIntParameter(file.name, "k")!!
        val size = FileNameCreator.getIntParameter(file.name, "size")!!

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