package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.generateGrid
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.getLabelingFunction
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization.drawLabels

fun main(args: Array<String>) {
    /* independent set */
    val labelingFunction = getLabelingFunction(hashSetOf(
            VertexRule("X"),
            VertexRule("N"),
            VertexRule("E"),
            VertexRule("S"),
            VertexRule("W"),
            VertexRule("NE"),
            VertexRule("NS"),
            VertexRule("NW"),
            VertexRule("ES"),
            VertexRule("EW"),
            VertexRule("SW"),
            VertexRule("NES"),
            VertexRule("NEW"),
            VertexRule("NSW"),
            VertexRule("ESW"),
            VertexRule("NESW")
    ))
    if (labelingFunction == null) {
        println("not found")
    } else {
        println("found")
        val grid = generateGrid(10, 10)
        val labeledGrid = labelingFunction.getLabels(grid)
        println(grid)
        for (i in 0 until labeledGrid!!.size) {
            (0 until labeledGrid[0].size)
                    .map { if (labeledGrid[i][it]) 1 else 0 }
                    .forEach { print("$it ") }
            println()
        }
        drawLabels(labeledGrid)
    }
}

