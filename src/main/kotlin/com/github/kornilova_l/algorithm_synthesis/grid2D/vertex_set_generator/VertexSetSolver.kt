package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileDirectedGraph.Node
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import java.io.File
import kotlin.collections.ArrayList

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
            val dimacs = toDimacs(graph, vertexRules)
            println(dimacs)
        }
    }
    return null
}

fun toDimacs(graph: TileDirectedGraph, rules: Set<VertexRule>): List<List<Int>> {
    val revertedRules = reverseRules(rules)
    val clauses = ArrayList<List<Int>>()
    for (node in graph.graph.values) {
        revertedRules.mapTo(clauses) { formClause(node, it, graph) }
    }
    return clauses
}

private fun formClause(node: Node, reversedRule: VertexRule, graph: TileDirectedGraph): List<Int> {
    val clause = ArrayList<Int>()
    for (position in positions) {
        if (reversedRule.isIncluded(position)) { // if a position is included by reversed rule then it should be excluded
            node.neighbours[position]!!.mapTo(clause) { -graph.getId(it)!! }
        } else { // and vise versa
            node.neighbours[position]!!.mapTo(clause) { graph.getId(it)!! }
        }
    }
    return clause
}
