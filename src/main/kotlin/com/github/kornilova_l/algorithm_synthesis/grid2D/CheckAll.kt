package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.idToProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.problemToId
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.tryToFindSolutionForEachRulesSet
import gnu.trove.list.array.TIntArrayList
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

/* it is 2 ^ 30 because all-zeros and all-one are always solvable */
val totalNumberOfCombination = (Math.pow(2.toDouble(), 30.toDouble())).toInt() - 1
val solvableFile = File("results/solvable.txt")
val unsolvableFile = File("results/unsolvable.txt")
/* it is more efficient to search solution for several rules combination at one time
 * because graph creating takes a lot of time */
val iterationSize = 100

val skipFirst = 18900

val random = Random(System.currentTimeMillis())

fun main(args: Array<String>) {
    val solvable = parseInts(solvableFile)
    val unsolvable = parseInts(unsolvableFile)

    val currentIteration = ArrayList<Set<VertexRule>>()
    for (combinationNum in totalNumberOfCombination - skipFirst downTo 0) {
//        val combinationNum = Math.abs(random.nextLong()) % totalNumberOfCombination
        if (isSolvable(combinationNum, solvable)) {
//            println("$combinationNum is solvable")
            continue
        }
        if (isUnsolvable(combinationNum, unsolvable)) {
//            println("$combinationNum is unsolvable")
            continue
        }
        // here we do not know if it is solvable or not
        val rules = idToProblem(combinationNum)
        currentIteration.add(rules)
        if (currentIteration.size == iterationSize) {
            val newSolvable = tryToFindSolutionForEachRulesSet(currentIteration)
            updateSolvableAndUnsolvable(solvable, unsolvable, newSolvable, currentIteration)
            currentIteration.clear()
            println("Checked ${totalNumberOfCombination - combinationNum + 1}")
        }
    }
}

fun updateSolvableAndUnsolvable(solvable: TIntArrayList, unsolvable: TIntArrayList,
                                newSolvable: Set<Set<VertexRule>>, allCheckedProblems: List<Set<VertexRule>>) {
    println("Solvable size before: ${solvable.size()}")
    println("Unsolvable size before: ${unsolvable.size()}")
    for (problem in allCheckedProblems) {
        if (newSolvable.contains(problem)) {
            solvable.add(problemToId(problem))
        } else {
            unsolvable.add(problemToId(problem))
        }
    }
    updateSolvable(solvable)
    updateUnsolvable(unsolvable)
    println("Solvable size: ${solvable.size()}")
    println("Unsolvable size: ${unsolvable.size()}")
}

/**
 * Removes values that can be check to be solvable
 */
fun updateSolvable(solvable: TIntArrayList) {
    val doNotAddInformation = TIntArrayList()
    for (s in solvable) {
        val solvableWithoutOneValue = TIntArrayList(solvable)
        solvableWithoutOneValue.remove(s)
        if (isSolvable(s, solvableWithoutOneValue)) {
            doNotAddInformation.add(s)
        }
    }
    solvable.removeAll(doNotAddInformation)
    updateFile(solvableFile, solvable)
}

fun updateUnsolvable(unsolvable: TIntArrayList) {
    val doNotAddInformation = TIntArrayList()
    for (s in unsolvable) {
        val unsolvableWithoutOneValue = TIntArrayList(unsolvable)
        unsolvableWithoutOneValue.remove(s)
        if (isUnsolvable(s, unsolvableWithoutOneValue)) {
            doNotAddInformation.add(s)
        }
    }
    unsolvable.removeAll(doNotAddInformation)
    updateFile(unsolvableFile, unsolvable)
}

fun updateFile(file: File, numbers: TIntArrayList) {
    BufferedWriter(FileWriter(file)).use { writer ->
        for (n in numbers) {
            writer.write("$n\n")
        }
    }
}

fun isUnsolvable(combinationNum: Int, unsolvableCombinations: TIntArrayList): Boolean {
    for (unsolvable in unsolvableCombinations) {
        val xor = unsolvable.xor(combinationNum) // find different bits
        if (xor.and(combinationNum) == 0) { // if all different bits in `combinationNum` are zeros
            return true
        }
    }
    return false
}

fun isSolvable(combinationNum: Int, solvableCombinations: TIntArrayList): Boolean {
    for (solvable in solvableCombinations) {
        val xor = solvable.xor(combinationNum) // find different bits
        if (xor.and(solvable) == 0) { // if all different bits in `solvable` are zeros
            return true
        }
    }
    return false
}

fun parseInts(file: File): TIntArrayList {
    val solvable = TIntArrayList()
    Scanner(file).use { scanner ->
        while (scanner.hasNextLong()) {
            solvable.add(scanner.nextInt())
        }
    }
    return solvable
}
