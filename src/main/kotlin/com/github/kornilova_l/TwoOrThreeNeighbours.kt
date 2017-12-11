package com.github.kornilova_l

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getRulePermutations
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

var count = 0

/**
 * Outputs all possible combinations of rules
 * where center has two or three neighbours
 */
fun outputTwoOrThreeNeighboursRules() {
    val rules = ArrayList<VertexRule>()
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))
    rules.addAll(getRulePermutations(2, false))
    rules.addAll(getRulePermutations(3, false))
    println(rules.size)

    BufferedWriter(FileWriter(File("lists_of_rules/two_or_three_neighbours.txt"))).use { writer ->
        outputAllRuleCombination(rules, ArrayList(rules.size), writer)
    }
    println(count)
}

fun outputAllRuleCombination(rules: List<VertexRule>, isIncludedArr: ArrayList<Boolean>, writer: BufferedWriter) {
    if (isIncludedArr.size == rules.size) {
        count++
        isIncludedArr.forEachIndexed { index, isIncluded ->
            if (isIncluded) {
                writer.write(rules[index].toString())
                writer.write(" ")
            }
        }
        writer.write("\n")
        return
    }
    val isIncludedArr2 = ArrayList(isIncludedArr)
    isIncludedArr2.add(true)
    outputAllRuleCombination(rules, isIncludedArr2, writer)

    isIncludedArr.add(false)
    outputAllRuleCombination(rules, isIncludedArr, writer)
}
