package com.github.kornilova_l.algorithm_synthesis.grid2D.grid

import java.io.File
import java.util.*

/**
 * Toroidal 2-dimensional grid
 */
class Grid2D(file: File) {
    val grid: Array<IntArray>
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
}
