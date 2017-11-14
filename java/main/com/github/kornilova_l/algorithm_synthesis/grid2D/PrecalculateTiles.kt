package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import java.io.File

val maxTileSize = 8
val maxDimension = 5

fun main(args: Array<String>) {
    for (n in 1 until maxTileSize) {
        for (m in n until maxTileSize) {
            for (k in 1..maxDimension) {
                if (File("generated_tiles/$n-$m-$k.txt").exists()) { // if was precalculated
                    continue
                }
                println("Calculate $n x $m tile in power $k")
                TileGenerator(n, m, k).exportToFile(File("generated_tiles"), false)
            }
        }
    }
}