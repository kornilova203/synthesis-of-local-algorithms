package com.github.kornilova_l.algorithm_synthesis.grid2D.grid

import com.github.kornilova_l.algorithm_synthesis.grid2D.colouring.ColouringFunction
import java.io.File
import java.util.*

/**
 * Toroidal 2-dimensional grid
 */
class Grid2D(file: File) {
    val grid: Array<IntArray>
    private var independentSet: Array<BooleanArray>? = null
    val n: Int
    val m: Int

    init {
        if (!file.isFile || !file.exists()) {
            throw IllegalArgumentException("File is not a file or does not exist")
        }
        val scanner = Scanner(file)
        n = scanner.nextInt()
        m = scanner.nextInt()
        grid = Array(n) { IntArray(m) }
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                grid[i][j] = scanner.nextInt()
            }
        }
    }

    fun getIS(): Array<BooleanArray> {
        if (independentSet == null) {
            independentSet = IndependentSetAlgorithm(this).independentSet
        }
        return independentSet ?: throw IllegalArgumentException("Cannot find IS on this grid")
    }

    /* TODO: find more convenient way to return colouring */
    fun getColouring(colouringFunction: ColouringFunction): Array<IntArray> {
        val colourGrid = Array(n) { IntArray(m) }
        for (i in colourGrid.indices) {
            for (j in colourGrid[0].indices) {
//                colourGrid[i][j] = colouringFunction.getColour(this, i, j)
            }
        }
        return colourGrid
    }
}
