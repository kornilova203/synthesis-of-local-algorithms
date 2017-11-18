package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileDirectedGraph.Node
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
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
        val file1 = File("generated_tiles/${n + 1}-$m-$k.txt")
        val file2 = File("generated_tiles/$n-${m + 1}-$k.txt")
        if (file1.exists() && file2.exists()) {
            val tileSet1 = TileSet(file1)
            val tileSet2 = TileSet(file2)
            val graph = TileDirectedGraph(tileSet1, tileSet2)
            val clauses = toDimacs(graph, vertexRules)
            val solution = solveWithSatSolver(clauses, graph.size)
            println(solution)
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
        while (scanner.hasNextLine()) {
            println(scanner.nextLine())
        }
//        if (scanner.nextLine() == "OK") {
//            return parseResult(scanner)
//        } else {
//            println("Something went wrong while running python script")
//        }
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

fun toDimacs(graph: TileDirectedGraph, rules: Set<VertexRule>): Set<Set<Int>> {
    val reversedRules = reverseRules(rules)
    val clauses = HashSet<Set<Int>>()
    val simplifiedGraph = getSimplifiedGraph(graph) // each node has at most 1 neighbour on each side
    for (tile in graph.graph.keys) {
        val node = graph.graph[tile]!!

        val clausesForEqualNeighbours = requireNeighboursEqual(node, graph, simplifiedGraph[tile]!!)
        clauses.addAll(clausesForEqualNeighbours)
        for (reversedRule in reversedRules) {
            val clause = formClause(simplifiedGraph[tile]!!, reversedRule, graph)
            if (clause != null) {
                clauses.add(clause)
            }
        }
    }
    return clauses
}

/**
 * For each node select a leader among neighbours on each side
 * This is needed to simplify clauses
 */
internal fun getSimplifiedGraph(graph: TileDirectedGraph): HashMap<Tile, Map<POSITION, Tile>> {
    val simplifiedGraph = HashMap<Tile, Map<POSITION, Tile>>()
    for (node in graph.graph.values) {
        val simplifiedNeighbours = HashMap<POSITION, Tile>()
        for (position in positions) {
            try {
                val leader = node.neighbours[position]!!.first() // get any node
                simplifiedNeighbours[position] = leader
            } catch (ignored: NoSuchElementException) {
                // that is okay
            }
        }
        simplifiedGraph[node.neighbours[POSITION.X]!!.first()] = simplifiedNeighbours
    }
    return simplifiedGraph
}

/**
 * For each set of neighbours (N, E, S, W) require that they all are equal to
 * leader neighbour in simplifiedNode
 */
fun requireNeighboursEqual(node: Node, graph: TileDirectedGraph, simplifiedNode: Map<POSITION, Tile>): Set<Set<Int>> {
    val clausesForEqualNeighbours = HashSet<Set<Int>>()
    for (position in positions) {
        val k = node.neighbours[position]!!.size
        if (k > 1) {
            val leader = simplifiedNode[position]!!
            /* for all neighbours except leader: */
            for (neighbour in node.neighbours[position]!!.filter { it != leader }) {
                clausesForEqualNeighbours.addAll(twoNotEqual(graph.getId(leader)!!, graph.getId(neighbour)!!))
            }
        }
    }
    return clausesForEqualNeighbours
}

fun twoNotEqual(id1: Int, id2: Int): Set<Set<Int>> {
    val clause1 = hashSetOf(id1, -id2)
    val clause2 = hashSetOf(-id1, id2)
    return hashSetOf(clause1, clause2)
}

/**
 * @return null if reversedRule can never be true for this node
 */
private fun formClause(node: Map<POSITION, Tile>, reversedRule: VertexRule, graph: TileDirectedGraph): Set<Int>? {
    val clause = HashSet<Int>()
    for (position in positions) {
        var mul = 1
        if (reversedRule.isIncluded(position)) { // if a position is included by reversed rule then it should be excluded
            mul = -1
        }
        val neighbour = node[position]!!
        val newConstraint = mul * graph.getId(neighbour)!!
        if (clause.contains(-newConstraint)) { // if current clause will always be true
            return null
        }
        clause.add(newConstraint)
    }
    return clause
}
