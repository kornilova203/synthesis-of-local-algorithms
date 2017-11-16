package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.tile_parameters.getParametersSet
import java.io.File
import java.util.*

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

fun toDimacs(graph: TileDirectedGraph, vertexRules: Set<VertexRule>): List<List<Int>> {
    val clauses = LinkedList<List<Int>>()
    for (node in graph.graph.values) {
        for (rule in vertexRules) {
            val clause = LinkedList<Int>()
            for (position in positions) {
                if (rule.isIncluded(position)) {
                    node.neighbours[position]!!.mapTo(clause) { graph.getId(it)!! }
                } else {
                    node.neighbours[position]!!.mapTo(clause) { -graph.getId(it)!! }
                }
            }
            clauses.add(clause)
        }
    }
    return clauses
}
