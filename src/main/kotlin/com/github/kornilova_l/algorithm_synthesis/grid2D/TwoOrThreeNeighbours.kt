package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.*
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.tryToFindSolution
import java.io.File
import java.util.regex.Pattern

val tilesFilePattern = Pattern.compile("\\d+-\\d+-\\d+\\.txt")!!

fun main(args: Array<String>) {
    val solvable = parseInts(solvableFile)
    val unsolvable = parseInts(unsolvableFile)
    val rules = atLeastOneIncludedAndOneExcluded()
    var combinationNum: Int? = problemToId(rules)

    val currentIteration = ArrayList<Set<VertexRule>>()
    var i = 0
    while (combinationNum != null) {
        i++
        if (i % 1_000_000 == 0) {
            println(combinationNum)
        }
        if (!isUnsolvable(combinationNum, unsolvable) && !isSolvable(combinationNum, solvable)) {
            println("add $combinationNum")
            currentIteration.add(idToProblem(combinationNum))
        } else {
//            println("Solution exist")
        }
        if (currentIteration.size == 10) {
            val newSolvable = tryToFindSolutionForEachRulesSet(currentIteration)
            updateSolvableAndUnsolvable(solvable, unsolvable, newSolvable, currentIteration)
            currentIteration.clear()
        }
        combinationNum = getNextProblemId(combinationNum, rules)
    }
    if (currentIteration.size != 0) {
        val newSolvable = tryToFindSolutionForEachRulesSet(currentIteration)
        updateSolvableAndUnsolvable(solvable, unsolvable, newSolvable, currentIteration)
    }
}

fun tryToFindSolutionForEachRulesSet(rulesCombinations: List<Set<VertexRule>>): Set<Set<VertexRule>> {
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
            useFileToFindSolutions(rulesCombinations, file, solvable, n, m, k)
        }
    }
    println("COMPLETE")
    return solvable
}

fun useFileToFindSolutions(rulesCombinations: List<Set<VertexRule>>, file: File,
                           solutions: MutableSet<Set<VertexRule>>, n: Int, m: Int, k: Int) {
    println("Try n=$n m=$m k=$k")
    try {
        val tileSet = TileSet(file)
        val graph = DirectedGraph(tileSet)

        for (rulesCombination in rulesCombinations) {
            if (solutions.contains(rulesCombination)) { // if solution was found
                continue
            }
            var function = tryToFindSolution(rulesCombination, graph)
            if (function == null && n != m) {
                function = tryToFindSolution(rotateProblem(rulesCombination), graph)
            }
            if (function != null) {
                println("Found solution for $rulesCombination")
                solutions.add(rulesCombination)
            }
        }
//        println("Checked parameters n=$n m=$m k=$k")
    } catch (e: OutOfMemoryError) {
        System.err.println("OutOfMemoryError n=$n m=$m k=$k")
    }
}

fun tooBig(n: Int, m: Int, k: Int): Boolean {
    return k == 2
//            n == 6 && m == 7 && k == 1 ||
//            n == 7 && m == 7 && k == 1 ||
//            n == 7 && m == 7 && k == 2 ||
//            n == 5 && m == 8 && k == 1 ||
//            n == 6 && m == 8 && k == 2 ||
//            n == 6 && m == 8 && k == 1 ||
//            n == 7 && m == 8 && k == 2 ||
//            n == 8 && m == 8 && k == 3 ||
//
//            n == 5 && m == 8 && k == 2 ||
//            n == 6 && m == 8 && k == 3 ||
//            n == 8 && m == 8 && k == 4 ||
//            n == 7 && m == 8 && k == 3
}

/**
 * @return all possible combinations of rules
 * where center has two or three neighbours
 */
fun getTwoOrThreeNeighboursRules(): Set<VertexRule> {
    val rules = HashSet<VertexRule>()
    rules.addAll(getRulePermutations(1, true))
    rules.addAll(getRulePermutations(2, true))
    rules.addAll(getRulePermutations(3, true))
    rules.addAll(getRulePermutations(2, false))
    rules.addAll(getRulePermutations(3, false))

    return rules
}

