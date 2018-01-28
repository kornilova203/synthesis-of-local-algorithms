package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.colouring.ColouringProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleGraphIterator
import java.io.File


fun main(args: Array<String>) {
    val graphs = SimpleGraphIterator(File("one_or_two_neighbours_tiles/simple_graphs"))
    for (graph in graphs) {
        println(ColouringProblem(graph, 8).solutionExists)
    }
}