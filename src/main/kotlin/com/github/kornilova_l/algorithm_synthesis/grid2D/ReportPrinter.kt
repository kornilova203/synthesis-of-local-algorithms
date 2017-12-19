package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getNextProblemId
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.idToProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.problemToId
import com.github.kornilova_l.util.ProgressBar
import gnu.trove.list.array.TLongArrayList

private val rulesCount = 30
private val combinationsCount = Math.pow(2.toDouble(), rulesCount.toDouble()).toLong()

fun main(args: Array<String>) {
    ReportPrinter(parseLongs(solvableFile), parseLongs(unsolvableFile))
            .printReport(getTwoOrThreeNeighboursRules(), true)
//    ReportPrinter(parseLongs(solvableFile), parseLongs(unsolvableFile)).printReport()
}


class ReportPrinter(private val solvable: TLongArrayList, private val unsolvable: TLongArrayList) {
    /**
     * Checks all 2 ^ 30 problems
     */
    fun printReport() {
        val progressBar = ProgressBar(combinationsCount, "Count solvable and unsolvable")
        var solvableCount = 0L
        var unsolvableCount = 0L
        for (i in 0 until combinationsCount) {
            if (isUnsolvable(i, unsolvable)) {
                unsolvableCount++
            } else if (isSolvable(i, solvable)) {
                solvableCount++
            }
            progressBar.updateProgress(1)
        }
        progressBar.finish()
        printReport(solvableCount, unsolvableCount, combinationsCount)
    }

    /**
     * Checks all possible problems that can be constructed by removing
     * set of rules from original problem
     */
    fun printReport(problem: Set<VertexRule>, printSolvable: Boolean = false) {
        val combinationsCount = Math.pow(2.toDouble(), problem.size.toDouble()).toLong()
        val progressBar = ProgressBar(combinationsCount, "Count solvable and unsolvable")
        var solvableCount = 0L
        var unsolvableCount = 0L
        var combinationNum: Long? = problemToId(problem)
        val foundSolvable = ArrayList<Long>()

        while (combinationNum != null) {
            if (isUnsolvable(combinationNum, unsolvable)) {
                unsolvableCount++
            } else if (isSolvable(combinationNum, solvable)) {
                solvableCount++
                if (printSolvable) {
                    foundSolvable.add(combinationNum)
                }
            }
            progressBar.updateProgress(1)
            combinationNum = getNextProblemId(combinationNum, problem)
        }
        progressBar.finish()
        println("Problem $problem:")
        printReport(solvableCount, unsolvableCount, combinationsCount)
        if (printSolvable) {
            for (s in foundSolvable) {
                println(idToProblem(s))
            }
        }
    }

    private fun printReport(solvableCount: Long, unsolvableCount: Long, combinationsCount: Long) {
        println("Solvable problems count: $solvableCount")
        println("Unsolvable problems count: $unsolvableCount")
        println("Unknown: ${combinationsCount - solvableCount - unsolvableCount}")
    }
}