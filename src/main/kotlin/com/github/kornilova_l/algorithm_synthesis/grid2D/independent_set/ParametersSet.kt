package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import java.util.*

/**
 * IndependentSetTile parameters are: n, m and k
 * All of them are proportional to computational difficulty
 * This class generated disjoint sets of parameters based of given difficulty
 */
fun getParametersSet(difficulty: Int = 1): List<Parameters> {
    val maxSideLength: Int
    val offset: Int // used to check if parameters set is too difficult
    when (difficulty) {
        1 -> {
            maxSideLength = 8
            offset = 10
        }
        2 -> {
            maxSideLength = 10
            offset = 12
        }
        3 -> {
            maxSideLength = 12
            offset = 14
        }
        4 -> {
            maxSideLength = 14
            offset = 16
        }
        else -> throw IllegalArgumentException("Difficulty is unsupported: $difficulty")
    }
    val parameters = ArrayList<Parameters>()
    for (n in 3..maxSideLength) {
        for (m in n..maxSideLength) {
            (1..5)
                    .filter { k -> k >= (m * n) / 5 - offset } // if not too difficult
                    .mapTo(parameters) { Parameters(n, m, it) }
        }
    }
    parameters.sortWith(kotlin.Comparator { p1, p2 ->
        when {
            p1.n != p1.n -> Integer.compare(p1.n, p2.n)
            p1.m != p1.m -> Integer.compare(p1.m, p2.m)
            else -> Integer.compare(p1.k, p2.k)
        }
    })
    return parameters
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