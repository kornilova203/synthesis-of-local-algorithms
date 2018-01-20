package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph.Neighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.positions
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.reverseRules
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.rotateProblem
import gnu.trove.list.array.TIntArrayList
import gnu.trove.set.hash.TIntHashSet
import java.io.File
import java.util.regex.Pattern

val tilesFilePattern = Pattern.compile("\\d+-\\d+-\\d+\\.txt")!!
val graphFilePattern = Pattern.compile("\\d+-\\d+-\\d+\\.graph")!!

/**
 * Try to find tile size such that it is possible to get labels so
 * each vertex has 1-neighbourhood in combinations Set.
 *
 * To use this function all tile sets must be precalculated and stored in generated_tiles directory
 */
fun getLabelingFunction(vertexRules: Set<VertexRule>): LabelingFunction? {
    val files = File("directed_graphs").listFiles()
    for (i in 0 until files.size) {
        val file = files[i]
        if (graphFilePattern.matcher(file.name).matches()) {
            val graph = DirectedGraph.createInstance(file)
            println("n = ${graph.n} m = ${graph.m} k = ${graph.k}")
            val function = getLabelingFunction(vertexRules, graph)

            if (function != null) {
                println("Found")
                return function
            }
        }
    }
    return null
}

private fun getLabelingFunction(vertexRules: Set<VertexRule>, graph: DirectedGraph): LabelingFunction? {
    var solution = tryToFindSolution(vertexRules, graph)
    if (solution != null) { // solution found
        return LabelingFunction(solution,
                DirectedGraphWithTiles.createInstance(File("directed_graphs/${graph.n}-${graph.m}-${graph.k}.tiles"), graph))
    }

    solution = tryToFindSolution(rotateProblem(vertexRules), graph)
    if (solution != null) { // solution found
        return LabelingFunction(solution,
                DirectedGraphWithTiles.createInstance(File("directed_graphs/${graph.n}-${graph.m}-${graph.k}.tiles"), graph))
                .rotate()
    }
    return null
}

fun tryToFindSolution(vertexRules: Set<VertexRule>, graph: DirectedGraph): List<Int>? {
    val clauses = toDimacs(graph, vertexRules)
    return solve(clauses, graph.size)
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
            if (k == 2) {
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
        val graph = DirectedGraphWithTiles.createInstance(tileSet)

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
    } catch (e: OutOfMemoryError) {
        System.err.println("OutOfMemoryError n=$n m=$m k=$k")
    }
}

fun toDimacs(graph: DirectedGraph, rules: Set<VertexRule>): List<TIntArrayList> {
    val reversedRules = reverseRules(rules)
    val clauses = ArrayList<TIntArrayList>()
    clauses.add(TIntArrayList())
    for (neighbourhood in graph.neighbourhoods) {
        formClause(neighbourhood, reversedRules, clauses)
    }
    return clauses
}

private fun formClause(neighbourhood: Neighbourhood, reversedRules: Set<VertexRule>,
                       clauses: MutableList<TIntArrayList>) {
    val currentArrayList = clauses.last()
    for (reversedRule in reversedRules) {
        val clause = TIntHashSet()
        var isAlwaysTrue = false
        for (position in positions) {
            val id = neighbourhood.get(position)
            if (id == 0) {
                throw AssertionError("id must be bigger than 0")
            }
            var value = id
            if (reversedRule.isIncluded(position)) {
                value = -id
            }
            if (clause.contains(-value)) { // if clause is always true
                isAlwaysTrue = true
                break
            }
            clause.add(value)
        }
        if (!isAlwaysTrue) {
            currentArrayList.addAll(clause)
            currentArrayList.add(0)
            if (currentArrayList.size() > 1_000_000) {
                clauses.add(TIntArrayList(500_000))
            }
        }
    }
}

