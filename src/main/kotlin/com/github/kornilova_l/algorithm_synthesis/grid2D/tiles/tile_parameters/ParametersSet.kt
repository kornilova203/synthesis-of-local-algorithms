package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters

import java.util.*

/**
 * IndependentSetTile parameters are: n, m and k
 * All of them are proportional to computational difficulty
 * This class generated disjoint sets of parameters based of given difficulty
 */
fun getParametersSet(difficulty: Int = 1): Set<Parameters> {
    if (difficulty == 1) {
        val parameters = TreeSet<Parameters>()
        for (n in 3..8) {
            for (m in n..8) {
                (1..5)
                        .filter { k -> k >= (m * n) / 5 - 10 } // if not too difficult
                        .mapTo(parameters) { Parameters(n, m, it) }
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

class Parameters(val n: Int, val m: Int, val k: Int) : Comparable<Parameters> {
    override fun compareTo(other: Parameters): Int {
        val bigger = Math.max(n, m)
        val smaller = Math.min(n, m)
        val oBigger = Math.max(other.n, other.m)
        val oSmaller = Math.min(other.n, other.m)
        if (bigger == oBigger) {
            if (smaller == oSmaller) {
                if (k == other.k) {
                    return 0
                }
                return if (k > other.k) -1 else 1
            }
            return if (smaller < oSmaller) -1 else 1
        }
        return if (bigger < oBigger) -1 else 1
    }

    override fun toString(): String = "n: $n, m: $m, k: $k"
}