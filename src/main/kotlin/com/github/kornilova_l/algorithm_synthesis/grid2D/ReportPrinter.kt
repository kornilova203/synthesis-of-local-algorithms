package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.util.ProgressBar
import gnu.trove.list.array.TLongArrayList

private val rulesCount = 30
private val combinationsCount = Math.pow(2.toDouble(), rulesCount.toDouble()).toLong()

fun main(args: Array<String>) {
    ReportPrinter(parseLongs(solvableFile), parseLongs(unsolvableFile)).printReport()
}


class ReportPrinter(private val solvable: TLongArrayList, private val unsolvable: TLongArrayList) {
    fun printReport() {
        val progressBar = ProgressBar(combinationsCount, "Count solvable and unsolvable")
        var solvableCount = 0
        var unsolvableCount = 0
        for (i in 0 until combinationsCount) {
            if (isUnsolvable(i, unsolvable)) {
                unsolvableCount++
            } else if (isSolvable(i, solvable)) {
                solvableCount++
            }
            progressBar.updateProgress(1)
        }
        progressBar.finish()
        println("Solvable problems count: $solvableCount")
        println("Unsolvable problems count: $unsolvableCount")
        println("Unknown: ${combinationsCount - solvableCount - unsolvableCount}")
    }
}