package com.github.kornilova_l.algorithm_synthesis.grid2D.colouring

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleGraphWithTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.SatSolver
import java.io.File
import java.nio.file.Paths

/**
 * Solves colouring problem
 * Converts graph to dimacs format to solve n-colouring problem
 * Starts python script which starts SAT solver
 * @param dir directory where tiles are stored.
 * If dir is null then [ColouringProblem.colouringFunction] will be null
 */
class ColouringProblem(graph: SimpleGraph, coloursCount: Int, dir: File? = null) {
    /**
     * Value is null if there is no proper colouring
     */
    val colouringFunction: ColouringFunction? // it is public because it is value
    val solutionExists: Boolean

    init {
        val satSolver = SatSolver()
        addClausesToSatSolver(graph, coloursCount, satSolver)
        val solution = satSolver.solve(graph.size * coloursCount)
        if (solution == null) {
            colouringFunction = null
            solutionExists = false
        } else {
            solutionExists = true
            colouringFunction = if (dir != null)
                createColouringFunction(solution, coloursCount, dir, graph) else null
        }
    }

    private fun createColouringFunction(solution: List<Int>, coloursCount: Int, dir: File, graph: SimpleGraph): ColouringFunction? {
        val tileColours = HashMap<BinaryTile, Int>()
        val tilesFile = Paths.get(dir.toString(), "${graph.n}-${graph.m}.txt").toFile()
        if (!tilesFile.exists()) {
            return null
        }
        val graphWithTiles = SimpleGraphWithTiles.createInstance(tilesFile, graph)
        for (index in solution) {
            if (index > 0) { // if colour was assigned
                val tileColourId = Math.abs(index)
                val tileId = getTileId(tileColourId, coloursCount)
                tileColours[graphWithTiles.getTile(tileId) as BinaryTile] = getColourId(tileColourId, coloursCount)
            }
        }
        return ColouringFunction(tileColours)
    }

    /**
     * @return one if {0, 1, .., coloursCount - 1}
     */
    private fun getColourId(tileColourId: Int, coloursCount: Int): Int = (tileColourId - 1) % coloursCount

    private fun getTileId(tileColourId: Int, coloursCount: Int): Int = (tileColourId - 1) / coloursCount

    companion object {
        fun addClausesToSatSolver(graph: SimpleGraph, coloursCount: Int, satSolver: SatSolver) {
            val visitedEdges = HashMap<Int, HashSet<Int>>() // to count each edge only ones

            for (tileId in graph.graph.keys) {
                tileMustHaveAtLeastOneColour(tileId, coloursCount, satSolver)
                val neighbours = graph.graph[tileId]!! // null value should never happen. If it throws NPE -> find mistake
                for (neighbourId in neighbours) {
                    if (neighbourId < tileId) {
                        val neighbourhood = visitedEdges.computeIfAbsent(neighbourId) { HashSet() }
                        if (neighbourhood.contains(tileId)) { // if this edge was visited
                            continue
                        }
                        neighbourhood.add(tileId)
                    } else {
                        val neighbourhood = visitedEdges.computeIfAbsent(tileId) { HashSet() }
                        if (neighbourhood.contains(neighbourId)) { // if this edge was visited
                            continue
                        }
                        neighbourhood.add(neighbourId)
                    }
                    addEdgeClauses(tileId, neighbourId, coloursCount, satSolver)
                }
            }
        }

        private fun addEdgeClauses(vertex1Id: Int, vertex2Id: Int, coloursCount: Int, satSolver: SatSolver) {
            for (i in 0 until coloursCount) {
                satSolver.addClause(-getVarId(vertex1Id, coloursCount, i), -getVarId(vertex2Id, coloursCount, i))
            }
        }

        /**
         * Id of any variable must not be 0
         */
        private fun getVarId(vertexId: Int, coloursCount: Int, currentVar: Int): Int =
                vertexId * coloursCount + currentVar + 1

        private fun tileMustHaveAtLeastOneColour(id: Int, coloursCount: Int, satSolver: SatSolver) {
            val clause = IntArray(coloursCount)
            for (i in 0 until coloursCount) {
                clause[i] = getVarId(id, coloursCount, i)
            }
            satSolver.addClause(clause)
        }
    }
}