package com.github.kornilova_l.algorithm_synthesis.grid2D.grid

class IndependentSetAlgorithm(grid2d: Grid2D, power: Int) {
    public val independentSet: Array<BooleanArray>
    val n: Int = grid2d.n
    val m: Int = grid2d.m

    init {
        /* Copy grid2d and perform colour reduction */
        val gridCopy = Array(n) { i -> grid2d.grid[i].clone() }
        while (!allStop(gridCopy)) {
            doColourReductionIteration(gridCopy, power)
        }
        independentSet = Array(n) { i -> BooleanArray(m, { j -> gridCopy[i][j] == 1 }) }
    }

    private fun doColourReductionIteration(gridCopy: Array<IntArray>, power: Int) {
        gridCopy.forEachIndexed { i, array ->
            array.forEachIndexed { j, id ->
                if (id > 1 && isLocalMaxima(gridCopy, i, j, power)) { // if cell did not stop and is a local maxima
                    if (canBeIS(gridCopy, i, j, power)) {
                        gridCopy[i][j] = 1
                    } else {
                        gridCopy[i][j] = 0
                    }
                }
            }
        }
    }

    private fun canBeIS(grid: Array<IntArray>, x: Int, y: Int, power: Int): Boolean {
        for (i in x - power..x + power) {
            @Suppress("LoopToCallChain")
            for (j in y - power..y + power) {
                if (Math.abs(x - i) + Math.abs(y - j) <= power) {
                    if (grid[(i + n) % n][(j + m) % m] == 1) {
                        return false
                    }
                }

            }
        }
        return true
    }

    private fun isLocalMaxima(grid: Array<IntArray>, x: Int, y: Int, power: Int): Boolean {
        for (i in x - power..x + power) {
            @Suppress("LoopToCallChain")
            for (j in y - power..y + power) {
                if (Math.abs(x - i) + Math.abs(y - j) <= power) {
                    if (grid[(i + n) % n][(j + m) % m] > grid[x][y]) {
                        return false
                    }
                }

            }
        }
        return true
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
