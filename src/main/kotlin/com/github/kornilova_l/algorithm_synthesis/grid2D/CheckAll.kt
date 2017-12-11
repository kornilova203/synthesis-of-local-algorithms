package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.allRulesExceptTrivial
import gnu.trove.list.array.TLongArrayList
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/* it is 2 ^ 30 because all-zeros and all-one are always solvable */
val totalNumberOfCombination = (Math.pow(2.toDouble(), 30.toDouble())).toLong() - 1
val solvableFile = File("results/solvable.txt")
val unsolvableFile = File("results/unsolvable.txt")
/* it is more efficient to search solution for several rules combination at one time
 * because graph creating takes a lot of time */
val iterationSize = 1000

val skipFirst = 9000

fun main(args: Array<String>) {
    val solvable = parseLongs(solvableFile)
    val unsolvable = parseLongs(unsolvableFile)

    val currentIteration = ArrayList<Set<VertexRule>>()
    val rulesToId = HashMap<Set<VertexRule>, Long>()
    for (combinationNum in totalNumberOfCombination - skipFirst downTo 0) {
        if (isSolvable(combinationNum, solvable)) {
//            println("$combinationNum is solvable")
            continue
        }
        if (isUnsolvable(combinationNum, unsolvable)) {
//            println("$combinationNum is unsolvable")
            continue
        }
        // here we do not know if it is solvable or not
        val rules = toSetOfVertexRules(combinationNum)
        currentIteration.add(rules)
        rulesToId.put(rules, combinationNum)
        if (currentIteration.size == iterationSize) {
            val newSolvable = tryToFindSolutionForEachRulesSet(currentIteration)
            for (entry in rulesToId.entries) {
                if (newSolvable.contains(entry.key)) {
                    solvable.add(entry.value)
                } else {
                    unsolvable.add(entry.value)
                }
            }
            updateSolvable(solvable)
            updateUnsolvable(unsolvable)
            currentIteration.clear()
            rulesToId.clear()
            println("Checked ${totalNumberOfCombination - combinationNum + 1}")
        }
    }
}

/**
 * Removes values that can be check to be solvable
 */
fun updateSolvable(solvable: TLongArrayList) {
    val doNotAddInformation = TLongArrayList()
    for (s in solvable) {
        val solvableWithoutOneValue = TLongArrayList(solvable)
        solvableWithoutOneValue.remove(s)
        if (isSolvable(s, solvableWithoutOneValue)) {
            doNotAddInformation.add(s)
        }
    }
    solvable.removeAll(doNotAddInformation)
    updateFile(solvableFile, solvable)
}

fun updateUnsolvable(unsolvable: TLongArrayList) {
    val doNotAddInformation = TLongArrayList()
    for (s in unsolvable) {
        val unsolvableWithoutOneValue = TLongArrayList(unsolvable)
        unsolvableWithoutOneValue.remove(s)
        if (isSolvable(s, unsolvableWithoutOneValue)) {
            doNotAddInformation.add(s)
        }
    }
    unsolvable.removeAll(doNotAddInformation)
    updateFile(unsolvableFile, unsolvable)
}

fun updateFile(file: File, numbers: TLongArrayList) {
    BufferedWriter(FileWriter(file)).use { writer ->
        for (n in numbers) {
            writer.write("$n ")
        }
    }
}

fun toSetOfVertexRules(combinationNum: Long): Set<VertexRule> {
    val rules = HashSet<VertexRule>()
    (0..31).forEach { shift ->
        if (combinationNum.and(1.toLong().shl(shift)) > 0) { // if rule is included
            rules.add(allRulesExceptTrivial[shift])
        }
    }
    return rules
}

fun isUnsolvable(combinationNum: Long, unsolvableCombinations: TLongArrayList): Boolean {
    for (unsolvable in unsolvableCombinations) {
        val xor = unsolvable.xor(combinationNum) // find different bits
        if (xor.and(combinationNum) == 0.toLong()) { // if all different bits in `combinationNum` are zeros
            return true
        }
    }
    return false
}

fun isSolvable(combinationNum: Long, solvableCombinations: TLongArrayList): Boolean {
    for (solvable in solvableCombinations) {
        val xor = solvable.xor(combinationNum) // find different bits
        if (xor.and(solvable) == 0.toLong()) { // if all different bits in `solvable` are zeros
            return true
        }
    }
    return false
}

fun parseLongs(file: File): TLongArrayList {
    val solvable = TLongArrayList()
    Scanner(file).use { scanner ->
        while (scanner.hasNextLong()) {
            solvable.add(scanner.nextLong())
        }
    }
    return solvable
}