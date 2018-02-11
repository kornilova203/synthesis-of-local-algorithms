package com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.Neighbourhood
import com.github.kornilova_l.util.FileNameCreator
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.io.File
import java.nio.file.Paths


/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges.
 * Also it contains actual tiles ([DirectedGraph] contains only ids)
 * This class should be used when
 * [com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.LabelingFunction] must be created
 */
abstract class DirectedGraphWithTiles<out N : Neighbourhood>(n: Int,
                                                             m: Int,
                                                             k: Int,
                                                             neighbourhoods: Set<N>,
                                                             private val ids: DualHashBidiMap<IndependentSetTile, Int>) : IndependentSetDirectedGraph<N>(n, m, k, neighbourhoods) {
    override val size: Int
        get() = ids.size

    fun getId(tile: IndependentSetTile): Int = ids[tile]!!

    fun getTile(id: Int): IndependentSetTile? = ids.getKey(id)

    override fun createGraphWithTiles(tilesFile: File): DirectedGraphWithTiles<N> = this

    /**
     * Format:
     * for each tile:
     * <id>
     * <tile's array of longs>
     */
    fun exportTiles(dir: File) {
        val file = Paths.get(dir.toString(), FileNameCreator.getFileName(
                IndependentSetTile.name,
                mapOf(Pair("n", n), Pair("m", m), Pair("k", k), Pair("size", size)),
                graphTilesFileExtension
        )).toFile()
        file.outputStream().use { outputStream ->
            for (tileAndId in ids) {
                outputStream.write(tileAndId.value.toString().toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(tileAndId.key.longsToString().toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("\n".toByteArray())
            }
        }
    }
}