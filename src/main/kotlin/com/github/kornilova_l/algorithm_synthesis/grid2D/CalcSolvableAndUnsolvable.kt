package com.github.kornilova_l.algorithm_synthesis.grid2D


fun main(args: Array<String>) {
    println(parseLongs(solvableFile).size())
    val unsolvable = parseLongs(unsolvableFile)
    val solvable = parseLongs(solvableFile)

    var unsolvableCount = 0L
    var solvableCount = 0L

    for (i in 1..totalNumberOfCombination) {
        if (isUnsolvable(i, unsolvable)) {
            unsolvableCount++
        } else if (isSolvable(i, solvable)) {
            solvableCount++
        }
        if (i % 10_000_000 == 0L) {
            println("$i unsolvable: $unsolvableCount solvable: $solvableCount")
        }
    }
    println("unsolvable: $unsolvableCount")
    println("2 ^ ${Math.log(unsolvableCount.toDouble()) / Math.log(2.0)}")

    println("solvable $solvableCount")
    println("2 ^ ${Math.log(solvableCount.toDouble()) / Math.log(2.0)}")
}