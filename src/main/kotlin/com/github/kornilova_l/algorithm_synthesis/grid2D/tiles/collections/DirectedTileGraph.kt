package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.POSITION
import org.apache.commons.collections4.bidimap.DualHashBidiMap

/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges
 */
class DirectedTileGraph(tileSet1: TileSet, tileSet2: TileSet) : TileGraph(tileSet1, tileSet2) {
    val graph: HashMap<Tile, Node> = HashMap()
    private val ids = DualHashBidiMap<Tile, Int>()
    override val size: Int
        get() = graph.size
    override val edgeCount: Int
        get() {
            val count = graph.values.sumBy { it.edgeCount }
            assert(count % 2 == 0)
            return count / 2
        }


    init {
        for (tile in this.tileSet1.validTiles) { // get vertical neighbours
            val north = Tile(tile, Tile.Part.N)
            val south = Tile(tile, Tile.Part.S)
            graph.computeIfAbsent(north) { Node(north) }.neighbours[POSITION.S]!!.add(south)
            graph.computeIfAbsent(south) { Node(south) }.neighbours[POSITION.N]!!.add(north)
        }
        for (tile in this.tileSet2.validTiles) { // get horizontal neighbours
            val west = Tile(tile, Tile.Part.W)
            val east = Tile(tile, Tile.Part.E)
            graph.computeIfAbsent(west) { Node(west) }.neighbours[POSITION.E]!!.add(east)
            graph.computeIfAbsent(east) { Node(east) }.neighbours[POSITION.W]!!.add(west)
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

    @Suppress("PropertyName")
    class Node(tile: Tile) {
        val neighbours = HashMap<POSITION, HashSet<Tile>>()
        val edgeCount: Int
            get() = neighbours.values.sumBy { it.size } - 1 // remove itself

        init {
            neighbours[POSITION.N] = HashSet()
            neighbours[POSITION.E] = HashSet()
            neighbours[POSITION.S] = HashSet()
            neighbours[POSITION.W] = HashSet()
            neighbours[POSITION.X] = HashSet()
            neighbours[POSITION.X]!!.add(tile)
        }

        override fun toString(): String = neighbours[POSITION.X]?.toString()?:""
    }
}