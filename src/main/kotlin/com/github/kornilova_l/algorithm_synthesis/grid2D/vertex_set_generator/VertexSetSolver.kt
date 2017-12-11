package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph.Neighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.positions
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.reverseRules
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.rotateRuleSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tilesFilePattern
import com.github.kornilova_l.algorithm_synthesis.grid2D.tooBig
import gnu.trove.list.array.TIntArrayList
import gnu.trove.set.hash.TIntHashSet
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
    val files = File("generated_tiles").listFiles()
    for (i in 0 until files.size) {
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
            print("n $n  m $m  k $k ")
            val tileSet = TileSet(file)
            val graph = DirectedGraph(tileSet)
            println("graph constructed")

            var function = tryToFindSolution(vertexRules, graph)
            if (function != null) {
                return function
            }

            function = tryToFindSolution(rotateRuleSet(vertexRules), graph)
            if (function != null) {
                println("Found rotated")
                return function.rotate()
            }
        }
    }
    return null
}

fun tryToFindSolution(vertexRules: Set<VertexRule>, graph: DirectedGraph): LabelingFunction? {

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

fun toDimacs(graph: DirectedGraph, rules: Set<VertexRule>): List<TIntArrayList> {
    val reversedRules = reverseRules(rules)
    val clauses = ArrayList<TIntArrayList>()
    clauses.add(TIntArrayList())
    graph.neighbourhoods.forEach { neighbourhood ->
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
            currentArrayList.addAll(clause)
            currentArrayList.add(0)
            if (currentArrayList.size() > 1_000_000) {
                clauses.add(TIntArrayList(500_000))
            }
        }
    }
}

