package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.colouring.ColouringProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGraphBuilder
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileSet
import java.io.File

private val tiles67 = TileSet(File("generated_tiles/6-7-3-1509826576982.txt"))
private val tiles58 = TileSet(File("generated_tiles/5-8-3-1509826268928.txt"))

fun main(args: Array<String>) {
    //        ColouringProblem.Companion.exportDimacs(
    //                new TileGraphBuilder(tiles67, tiles58, 5, 7, 3).getGraph(),
    //                4,
    //                new File("dimacs/")
    //        );
    val colouringFunction = ColouringProblem(
            TileGraphBuilder(tiles67, tiles58).graph, 4
    ).colouringFunction
    println(colouringFunction)
}

