package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleGraphWithTiles
import java.io.File

private const val k = 3
private val tiles67 = IndependentSetTile.parseTiles(IndependentSetTile.getTilesFile(6, 5, k, File("independent_set_tiles"))!!)
private val tiles58 = IndependentSetTile.parseTiles(IndependentSetTile.getTilesFile(5, 8, k, File("independent_set_tiles"))!!)

fun getRandomFourColouring() {
    val grid2D = generateGrid(7, 10)
    println("Grid:")
    println(grid2D)
    val tileGraph = SimpleGraphWithTiles.createInstance(tiles67, tiles58)
    val colouringFunction = ColouringProblem(tileGraph, 4).colouringFunction
    val graphColoured = colouringFunction?.getGraphColoured(IndependentSetAlgorithm(grid2D, k).independentSet)
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
