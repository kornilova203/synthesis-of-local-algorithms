package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters

/**
 * Tile parameters are: n, m and k
 * All of them are proportional to computational difficulty
 * This class generated disjoint sets of parameters based of given difficulty
 */
fun getParametersSet(difficulty: Int): Set<Parameters> {
    if (difficulty == 1) {
        val parameters = HashSet<Parameters>()
        for(n in 1..8) {
            for(m in n..8) {
                for (k in 1..5) {
                    if (k < (m * n) / 5 - 4) { // if too difficult
                        continue
                    }
                    parameters.add(Parameters(n, m, k))
                }
            }
        }
        return parameters
    }
    throw IllegalArgumentException("Method getParametersSet() supports only difficulty == 1")
}

fun printParameters(parametersSet: Set<Parameters>) {
    for (parameters in parametersSet) {
        println("power: ${parameters.k}, n: ${parameters.n}, m: ${parameters.m}")
    }
}

fun main(args: Array<String>) {
    printParameters(getParametersSet(1))
}

data class Parameters(val n: Int, val m: Int, val k: Int)