package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

val maxTileSize = 8
val maxDimension = 5

/**
 * Try to find tile size such that it is possible to get labels so
 * each vertex has 1-neighbourhood in combinations Set.
 *
 * To use this function all tile sets must be precalculated and stored in generated_tiles directory
 */
//fun getLabelingFunction(combinations: Set<Int>): LabelingFunction? {
//    for (n in 0 until  maxTileSize) {
//        for(m in n until  maxTileSize) {
//            for (k in 0..maxDimension) {
//                val tileSet1 = TileSet(File("generated_tiles/${n + 1}-$m-$k.txt"))
//                val tileSet2 = TileSet(File("generated_tiles/$n-${m + 1}-$k.txt"))
//                val tileGraph = TileGraph(tileSet1, tileSet2)
//
//            }
//        }
//    }
//}