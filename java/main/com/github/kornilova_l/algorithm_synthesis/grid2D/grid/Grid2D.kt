package com.github.kornilova_l.algorithm_synthesis.grid2D.grid

import java.io.File
import java.util.*

/**
 * Toroidal 2-dimensional grid
 */
class Grid2D {
    val grid: Array<IntArray>
    val n: Int
    val m: Int

    constructor(file: File) {
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

    constructor(grid: Array<IntArray>) {
        this.grid = grid
        n = grid.size
        m = grid[0].size
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until grid.size) {
            for (j in 0 until grid[0].size) {
                stringBuilder.append(grid[i][j]).append("\t")
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }
}
