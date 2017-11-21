package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import org.apache.commons.collections4.bidimap.DualHashBidiMap

/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges
 */
class DirectedTileGraph(tileSet: TileSet) : TileGraph() {
    override val n: Int = tileSet.n
    override val m: Int = tileSet.m
    override val k: Int = tileSet.k
    val graph: HashMap<Tile, Neighbourhood> = HashMap()
    private val ids = DualHashBidiMap<Tile, Int>()
    override val size: Int
        get() = graph.size

    init {
        for (tile in tileSet.validTiles) {
            graph[Tile(tile, POSITION.X)] = Neighbourhood(
                    Tile(tile, POSITION.N),
                    Tile(tile, POSITION.E),
                    Tile(tile, POSITION.W),
                    Tile(tile, POSITION.S)
            )
        }
        if (graph.size == 0) {
            throw IllegalArgumentException("Cannot construct graph")
        }

        assignIds()
    }

    private fun assignIds() {
        for ((i, tile) in graph.keys.withIndex()) {
            ids.put(tile, i + 1) // ids must be positive
        }
    }

    fun getId(tile: Tile): Int? = ids[tile]

    fun getTile(id: Int): Tile? = ids.getKey(id)

    class Neighbourhood(north: Tile, east: Tile, south: Tile, west: Tile) {
        val neighbours = HashMap<POSITION, Tile>()

        init {
            neighbours[POSITION.N] = north
            neighbours[POSITION.E] = east
            neighbours[POSITION.W] = west
            neighbours[POSITION.S] = south
        }
    }
}