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
        val file = File("generated_tiles/$n-$m-$k.txt")
        val tileSet = TileSet(file)
        val graph = DirectedTileGraph(tileSet)
        val clauses = toDimacs(graph, vertexRules)
        val solution = solveWithSatSolver(clauses, graph.size)
        if (solution != null) { // solution found
            return LabelingFunction(solution, graph)
        }
    }
    return null
}

/**
 * @return null if not satisfiable
 */
private fun solveWithSatSolver(clauses: Set<Set<Int>>, varCount: Int): List<Int>? {
    val builder = ProcessBuilder("python", File("python_sat/sat/start_sat.py").toString())

    builder.redirectErrorStream(true)
    try {
        val process = builder.start()
        val writer = BufferedWriter(OutputStreamWriter(process.outputStream))
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
        if (scanner.nextLine() == "OK") {
            return parseResult(scanner)
        }
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
    return res
}

fun toDimacs(graph: DirectedTileGraph, rules: Set<VertexRule>): Set<Set<Int>> {
    val reversedRules = reverseRules(rules)
    val clauses = HashSet<Set<Int>>()
    graph.graph.values
            .forEach { it.forEach { clauses.addAll(formClause(it, reversedRules, graph)) } }
    return clauses
}

private fun formClause(neighbourhood: Neighbourhood, reversedRules: Set<VertexRule>, graph: DirectedTileGraph): Set<Set<Int>> {
    val clauses = HashSet<Set<Int>>()
    for (reversedRule in reversedRules) {
        val clause = HashSet<Int>()
        var isAlwaysTrue = false
        for (position in positions) {
            val id = graph.getId(neighbourhood.neighbours[position]!!)!!
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
    return clauses
}
