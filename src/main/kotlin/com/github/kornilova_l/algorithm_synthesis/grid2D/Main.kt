package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileSet
import java.io.File

private val tiles67 = TileSet(File("generated_tiles/6-7-3.txt"))
private val tiles58 = TileSet(File("generated_tiles/5-8-3.txt"))

fun main(args: Array<String>) {
    val tileGenerator = TileGenerator(6, 7, 3)
}

