package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleTileGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.parseTiles
import java.io.File

private val tiles67 = parseTiles(File("generated_tiles/6-7-3.txt"))
private val tiles58 = parseTiles(File("generated_tiles/5-8-3.txt"))

fun getRandomFourColouring() {
    val grid2D = generateGrid(7, 10)
    println("Grid:")
    println(grid2D)
    val tileGraph = SimpleTileGraph(tiles67, tiles58)
    val colouringFunction = ColouringProblem(tileGraph, 4, 3).colouringFunction
    val graphColoured = colouringFunction?.getGraphColoured(grid2D)
    graphColoured!! // must not be null
    for (i in 0 until graphColoured.size) {
        for (j in 0 until graphColoured[0].size) {
            print("${graphColoured[i][j]} ")
        }
        println()
    }
}

fun main(args: Array<String>) {
    getRandomFourColouring()
}
