package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.Grid2D
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileSet
import java.io.File

private val tiles67 = TileSet(File("generated_tiles/6-7-3-1509826576982.txt"))
private val tiles58 = TileSet(File("generated_tiles/5-8-3-1509826268928.txt"))

fun getFourColouring(file: File) {
    val grid2D = Grid2D(file)
    val tileGraph = TileGraph(tiles67, tiles58)
    val colouringFunction = ColouringProblem(tileGraph, 4).colouringFunction
    val graphColoured = colouringFunction?.getGraphColoured(grid2D)
    graphColoured?.forEach { arr ->
        arr.forEach { colour ->
            print(colour)
            print(" ")
        }
        println()
    }
}

fun main(args: Array<String>) {
    getFourColouring(File("java/test_resources/grids/02_grid_8-8.txt"))
}
