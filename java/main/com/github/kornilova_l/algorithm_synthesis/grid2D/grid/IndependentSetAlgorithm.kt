package com.github.kornilova_l.algorithm_synthesis.grid2D.grid

class IndependentSetAlgorithm(grid2d: Grid2D) {
    public val independentSet: Array<BooleanArray>
    val n: Int = grid2d.n
    val m: Int = grid2d.m

    init {
        /* Copy grid2d and perform colour reduction */
        val gridCopy = Array(n) { i -> grid2d.grid[i].clone() }
        while (!allStop(gridCopy)) {
            doColourReductionIteration(gridCopy)
        }
        independentSet = Array(n) { i -> BooleanArray(m, { j -> gridCopy[i][j] == 1 }) }
    }

    private fun doColourReductionIteration(gridCopy: Array<IntArray>) {
        gridCopy.forEachIndexed { i, array ->
            array.forEachIndexed { j, id ->
                if (id > 1 && isLocalMaxima(gridCopy, i, j)) { // if cell did not stop and is a local maxima
                    if (canBeIS(gridCopy, i, j)) {
                        gridCopy[i][j] = 1
                    } else {
                        gridCopy[i][j] = 0
                    }
                }
            }
        }
    }

    private fun canBeIS(grid: Array<IntArray>, i: Int, j: Int): Boolean {
        return grid[(i + 1) % n][j] != 1 &&
                grid[(i + n - 1) % n][j] != 1 &&
                grid[i][(j + 1) % m] != 1 &&
                grid[i][(j + m - 1) % m] != 1
    }

    private fun isLocalMaxima(grid: Array<IntArray>, i: Int, j: Int): Boolean {
        return grid[i][j] > grid[(i + 1) % n][j] &&
                grid[i][j] > grid[(i + n - 1) % n][j] &&
                grid[i][j] > grid[i][(j + 1) % m] &&
                grid[i][j] > grid[i][(j + m - 1) % m]
    }

    private fun allStop(gridCopy: Array<IntArray>): Boolean {
        return !gridCopy.any { it.any { it > 1 } } // return true if all cells <= 1
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        independentSet.forEach { array ->
            array.forEachIndexed { index, cell ->
                stringBuilder.append(if (cell) 1 else 0)
                if (index < m - 1) {
                    stringBuilder.append(" ")
                }
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }
}
