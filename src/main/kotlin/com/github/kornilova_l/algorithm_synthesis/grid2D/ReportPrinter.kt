package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getNextProblemId
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.problemToId
import com.github.kornilova_l.util.ProgressBar
import gnu.trove.list.array.TLongArrayList

private val rulesCount = 30
private val combinationsCount = Math.pow(2.toDouble(), rulesCount.toDouble()).toLong()

fun main(args: Array<String>) {
    ReportPrinter(parseLongs(solvableFile), parseLongs(unsolvableFile)).printReport(getTwoOrThreeNeighboursRules())
//    ReportPrinter(parseLongs(solvableFile), parseLongs(unsolvableFile)).printReport()
}


class ReportPrinter(private val solvable: TLongArrayList, private val unsolvable: TLongArrayList) {
    /**
     * Checks all 2 ^ 30 problems
     */
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

    /**
     * Checks all possible problems that can be constructed by removing
     * set of rules from original problem
     */
    fun printReport(problem: Set<VertexRule>) {
        val combinationsCount = Math.pow(2.toDouble(), problem.size.toDouble()).toLong()
        val progressBar = ProgressBar(combinationsCount, "Count solvable and unsolvable")
        var solvableCount = 0
        var unsolvableCount = 0
        var combinationNum: Long? = problemToId(problem)

        while (combinationNum != null) {
            if (isUnsolvable(combinationNum, unsolvable)) {
                unsolvableCount++
            } else if (isSolvable(combinationNum, solvable)) {
                solvableCount++
            }
            progressBar.updateProgress(1)
            combinationNum = getNextProblemId(combinationNum, problem)
        }
        progressBar.finish()
        println("Problem $problem:")
        println("Solvable problems count: $solvableCount")
        println("Unsolvable problems count: $unsolvableCount")
        println("Unknown: ${combinationsCount - solvableCount - unsolvableCount}")
    }
}