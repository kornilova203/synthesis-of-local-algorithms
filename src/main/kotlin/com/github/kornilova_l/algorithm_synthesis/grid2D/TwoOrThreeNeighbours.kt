package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getRulePermutations
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.rotateRuleSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.tryToFindSolution
import java.io.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

var count = 0
val tilesFilePattern = Pattern.compile("\\d+-\\d+-\\d+\\.txt")!!

fun main(args: Array<String>) {
    val rulesCount = Math.pow(2.toDouble(), 20.toDouble())
    val step = 100
    var from = 0
    var to = step
    while (to < rulesCount) {
        if (wasChecked(from, to)) {
            from += step
            to += step
            continue
        }
        val startTime = System.currentTimeMillis()
        val logFile = File("log/$from-$to-${System.currentTimeMillis()}.txt")

        val rulesCombinations = scanFromFile(from, to)
        FileWriter(logFile).use { writer ->
            tryToFindSolutionForEachRulesSet(rulesCombinations, writer)
        }
        System.err.println("Time: ${(System.currentTimeMillis() - startTime) / 1000}s")
        from += step
        to += step
    }
}

fun wasChecked(from: Int, to: Int): Boolean {
    val logDir = File("log")
    logDir.listFiles()!!.forEach { file ->
        val parts = file.name.split("-")
        val fileFrom = Integer.parseInt(parts[0])
        val fileTo = Integer.parseInt(parts[1])
        if (from >= fileFrom && to <= fileTo) {
            Scanner(FileInputStream(file)).use { scanner ->
                while (scanner.hasNextLine()) {
                    if (scanner.nextLine() == "COMPLETE") {
                        return true
                    }
                }
            }
        }
    }
    return false
}

fun tryToFindSolutionForEachRulesSet(rulesCombinations: List<Set<VertexRule>>,
                                     writer: Writer? = null): Set<Set<VertexRule>> {
    val solvable = HashSet<Set<VertexRule>>()
    val files = File("generated_tiles").listFiles()
    for (i in 0 until files.size) {
        if (solvable.size == rulesCombinations.size) { // if everything is solved
            return solvable
        }
        val file = files[i]
        if (tilesFilePattern.matcher(file.name).matches()) {
            val parts = file.name.split("-")
            val n = Integer.parseInt(parts[0])
            val m = Integer.parseInt(parts[1])
            val k = Integer.parseInt(parts[2].split(".")[0])
            if (n < 3 || m < 3 ||
                    n > m) {
                continue
            }
            if (tooBig(n, m, k)) {
                continue
            }
            useFileToFindSolutions(rulesCombinations, file, writer, solvable, n, m, k)
        }
    }
    if (!solvable.isEmpty()) {
        writer?.write("SOLUTION FOUND")
    }
    writer?.write("COMPLETE\n")
    println("COMPLETE")
    return solvable
}

fun useFileToFindSolutions(rulesCombinations: List<Set<VertexRule>>, file: File, writer: Writer?,
                           solutions: MutableSet<Set<VertexRule>>, n: Int, m: Int, k: Int) {
//    println("Try n=$n m=$m k=$k")
    try {
        val tileSet = TileSet(file)
        val graph = DirectedGraph(tileSet)

        for (rulesCombination in rulesCombinations) {
            if (solutions.contains(rulesCombination)) { // if solution was found
                continue
            }
            var function = tryToFindSolution(rulesCombination, graph)
            if (function == null && n != m) {
                function = tryToFindSolution(rotateRuleSet(rulesCombination), graph)
            }
            if (function != null) {
                writer?.write("Found solution for $rulesCombination\n")
//                println("Found solution for $rulesCombination")
                solutions.add(rulesCombination)
            }
        }
        writer?.write("Checked parameters n=$n m=$m k=$k\n")
//        println("Checked parameters n=$n m=$m k=$k")
    } catch (e: OutOfMemoryError) {
        writer?.write("OutOfMemoryError n=$n m=$m k=$k\n")
        System.err.println("OutOfMemoryError n=$n m=$m k=$k")
    }
}

fun tooBig(n: Int, m: Int, k: Int): Boolean {
    return n == 6 && m == 7 && k == 1 ||
            n == 7 && m == 7 && k == 1 ||
            n == 7 && m == 7 && k == 2 ||
            n == 5 && m == 8 && k == 1 ||
            n == 6 && m == 8 && k == 2 ||
            n == 6 && m == 8 && k == 1 ||
            n == 7 && m == 8 && k == 2 ||
            n == 8 && m == 8 && k == 3 ||

            n == 5 && m == 8 && k == 2 ||
            n == 6 && m == 8 && k == 3 ||
            n == 8 && m == 8 && k == 4 ||
            n == 7 && m == 8 && k == 3
}

fun scanFromFile(from: Int, to: Int): List<Set<VertexRule>> {
    val rulesCombinations = ArrayList<Set<VertexRule>>()
    Scanner(FileInputStream(File("lists_of_rules/two_or_three_neighbours.txt"))).use { scanner ->
        (0 until from).forEach { scanner.nextLine() } // skip first `from` rules
        (0 until to - from).forEach {
            if (scanner.hasNextLine()) {
                val line = scanner.nextLine()
                if (line != "") {
                    val rules = line.split(" ").filter { ruleString -> ruleString != "" }
                            .map { ruleString -> VertexRule(ruleString) }.toSet()
                    rulesCombinations.add(rules)
                }
            }
        }
    }
    return rulesCombinations
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

