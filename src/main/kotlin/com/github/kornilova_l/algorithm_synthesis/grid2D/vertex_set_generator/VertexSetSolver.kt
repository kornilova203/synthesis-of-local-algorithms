package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedTileGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedTileGraph.Neighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.positions
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.reverseRules
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


/**
 * Try to find tile size such that it is possible to get labels so
 * each vertex has 1-neighbourhood in combinations Set.
 *
 * To use this function all tile sets must be precalculated and stored in generated_tiles directory
 */
fun getLabelingFunction(vertexRules: Set<VertexRule>): LabelingFunction? {
    val parametersSet = getParametersSet(1)
    for (parameters in parametersSet) {
        val n = parameters.n
        val m = parameters.m
        val k = parameters.k
        val function = tryToFindSolution(vertexRules, n, m, k)
        if (function != null) {
            return function
        }
    }
    return null
}

private fun tryToFindSolution(vertexRules: Set<VertexRule>, n: Int, m: Int, k: Int): LabelingFunction? {
    println("n $n  m $m  k $k")
    val file = File("generated_tiles/$n-$m-$k.txt")
    if (!file.exists()) { // if was not precalculated
        return null
    }
    val tileSet = TileSet(file)
    val graph = DirectedTileGraph(tileSet)
    val clauses = toDimacs(graph, vertexRules)
    val solution = solve(clauses, graph.size)
    if (solution != null) { // solution found
        return LabelingFunction(solution, graph)
    }
    return null
}

/**
 * @return null if not satisfiable
 */
fun findAllSolutionsWithSatSolver(clauses: Set<Set<Int>>, varCount: Int): HashSet<Set<Int>>? {
    val builder = ProcessBuilder("python", File("python_sat/sat/start_sat.py").toString())

    builder.redirectErrorStream(true)
    try {
        val process = builder.start()
        val writer = BufferedWriter(OutputStreamWriter(process.outputStream))
        writer.write("Find all solutions\n")
        writer.write("p cnf $varCount ${clauses.size}\n")
        for (clause in clauses) {
            for (variable in clause) {
                writer.write("$variable ")
            }
            writer.write("\n")
        }
        writer.flush()
        writer.close()
        val scanner = Scanner(process.inputStream)
        process!!.waitFor()
        val solutions = HashSet<Set<Int>>()
        var hasSolution = false
//        while (scanner.hasNextLine()) {
//            println(scanner.nextLine())
//        }
        while (scanner.hasNextLine() && scanner.nextLine() == "OK") {
            hasSolution = true
            solutions.add(parseLineResult(scanner))
        }
        if (hasSolution) {
            return solutions
        }
        return null
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    return null
}

fun parseResult(scanner: Scanner): List<Int> {
    val res = ArrayList<Int>()
    while (scanner.hasNextInt()) {
        res.add(scanner.nextInt())
    }
    scanner.nextLine() // end of line
    val end = scanner.nextLine()
    if (end != "END") {
        throw RuntimeException("Cannot find end of result")
    }
    return res
}

fun parseLineResult(scanner: Scanner): Set<Int> {
    val res = HashSet<Int>()
    val line = scanner.nextLine()
    val numbers = line.split(" ")
    numbers.mapTo(res) { Integer.parseInt(it) }
    return res
}

fun toDimacs(graph: DirectedTileGraph, rules: Set<VertexRule>): Collection<Set<Int>> {
    val reversedRules = reverseRules(rules)
    val clauses = ArrayList<Set<Int>>()
    graph.graph.values
            .forEach { it.forEach { formClause(it, reversedRules, graph, clauses) } }
    return clauses
}

private fun formClause(neighbourhood: Neighbourhood, reversedRules: Set<VertexRule>,
                       graph: DirectedTileGraph, clauses: ArrayList<Set<Int>>) {
    for (reversedRule in reversedRules) {
        val clause = HashSet<Int>()
        var isAlwaysTrue = false
        for (position in positions) {
            val id = neighbourhood.neighbours[position]!!.id
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
            clauses.add(clause)
        }
    }
}
