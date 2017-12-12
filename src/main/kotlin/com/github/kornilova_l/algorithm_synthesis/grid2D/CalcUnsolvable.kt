package com.github.kornilova_l.algorithm_synthesis.grid2D


fun main(args: Array<String>) {
    val unsolvable = parseLongs(unsolvableFile)

    var unsolvableCount = 0L

    (0..totalNumberOfCombination).forEach { i ->
        if (isUnsolvable(i, unsolvable)) {
            unsolvableCount++
        }
        if (i % 10_000_000 == 0L) {
            println("$i - $unsolvableCount")
        }
    }
    println(unsolvableCount)
    println("2 ^ ${Math.log(unsolvableCount.toDouble()) / Math.log(2.0)}")
}