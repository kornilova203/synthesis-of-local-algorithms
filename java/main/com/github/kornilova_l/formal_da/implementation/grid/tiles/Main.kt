package com.github.kornilova_l.formal_da.implementation.grid.tiles

import java.io.File

private val tiles67 = TileGenerator.importFromFile(File("generated_tiles/6-7-3-1509826576982.txt"))
private val tiles58 = TileGenerator.importFromFile(File("generated_tiles/5-8-3-1509826268928.txt"))

fun main(args: Array<String>) {
    //        ColouringProblem.Companion.exportDimacs(
    //                new TileGraphBuilder(tiles67, tiles58, 5, 7, 3).getGraph(),
    //                4,
    //                new File("dimacs/")
    //        );
    val colouringProblem = ColouringProblem(TileGraphBuilder(tiles67, tiles58, 5, 7, 3).graph, 4)
}

