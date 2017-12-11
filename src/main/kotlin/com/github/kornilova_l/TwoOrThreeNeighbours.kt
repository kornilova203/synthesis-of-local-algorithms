package com.github.kornilova_l

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.getLabelingFunction
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getRulePermutations
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.util.*
import kotlin.collections.HashSet

var count = 0

fun main(args: Array<String>) {
    val rules = ArrayList<VertexRule>()
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))
    rules.addAll(getRulePermutations(2, false))
    rules.addAll(getRulePermutations(3, false))
    println(rules.size)

    val res = HashSet<Set<VertexRule>>()
    getAllRuleCombination(rules, ArrayList(rules.size), res)
    println(res)
}

fun scanFromFile() {
    Scanner(FileInputStream(File("lists_of_rules/two_or_three_neighbours.txt"))).use { scanner ->
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            if (line == "" || line.contains("FOUND")) {
                continue
            }
            val startTime = System.currentTimeMillis()
            val rules = line.split(" ").filter { ruleString -> ruleString != "" }
                    .map { ruleString -> VertexRule(ruleString) }.toSet()
            println(rules)
            val labelingFunction = getLabelingFunction(rules)
            if (labelingFunction == null) {
                println("NOT FOUND ${System.currentTimeMillis() - startTime}")
            } else {
                println("FOUND ${System.currentTimeMillis() - startTime}")
            }
        }
    }
}

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

fun getAllRuleCombination(rules: List<VertexRule>, isIncludedArr: ArrayList<Boolean>, res: MutableSet<Set<VertexRule>>) {
    if (isIncludedArr.size == rules.size) {
        count++
        if (count % 100000 == 0) {
            println(count)
        }
        val set = HashSet<VertexRule>()
        isIncludedArr.forEachIndexed { index, isIncluded ->
            if (isIncluded) {
                set.add(rules[index])
            }
        }
        res.add(set)
        return
    }
    val isIncludedArr2 = ArrayList(isIncludedArr)
    isIncludedArr2.add(true)
    getAllRuleCombination(rules, isIncludedArr2, res)

    isIncludedArr.add(false)
    getAllRuleCombination(rules, isIncludedArr, res)
}

