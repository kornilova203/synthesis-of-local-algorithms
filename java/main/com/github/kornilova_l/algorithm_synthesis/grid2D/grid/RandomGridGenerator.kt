package com.github.kornilova_l.algorithm_synthesis.grid2D.grid

import java.util.*

fun generateGrid(n: Int, m: Int): Grid2D {
    if (n <= 0 || m <= 0) {
        throw IllegalArgumentException("N and M must be positive")
    }
    val size = n * m
    val ids = LinkedList<Int>()
    ids += 0..size
    Collections.shuffle(ids)

    val grid = Array(n) { IntArray(m) }
    for (i in 0 until n) {
        for (j in 0 until m) {
            grid[i][j] = ids.remove()
        }
    }
    return Grid2D(grid)
}