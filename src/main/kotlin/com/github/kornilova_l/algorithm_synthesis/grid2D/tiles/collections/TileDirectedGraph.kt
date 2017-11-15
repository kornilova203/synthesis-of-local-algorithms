package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import java.util.HashSet
import kotlin.collections.HashMap

/**
 * Constructs graph of tiles.
 * This implementation saves orientation of edges
 */
class TileDirectedGraph(tileSet1: TileSet, tileSet2: TileSet) : TileGraph(tileSet1, tileSet2) {
    private val graph: HashMap<Tile, Node> = HashMap()
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
            graph.computeIfAbsent(north) { Node() }.S.add(south)
            graph.computeIfAbsent(south) { Node() }.N.add(north)
        }
        for (tile in this.tileSet2.validTiles) { // get horizontal neighbours
            val west = Tile(tile, Tile.Part.W)
            val east = Tile(tile, Tile.Part.E)
            graph.computeIfAbsent(west) { Node() }.E.add(east)
            graph.computeIfAbsent(east) { Node() }.W.add(west)
        }
        if (graph.size == 0) {
            throw IllegalArgumentException("Cannot construct graph")
        }

//        assignIds()
    }

    @Suppress("PropertyName")
    private class Node {
        val N: HashSet<Tile> = HashSet()
        val E: HashSet<Tile> = HashSet()
        val S: HashSet<Tile> = HashSet()
        val W: HashSet<Tile> = HashSet()
        val edgeCount: Int
            get() = N.size + E.size + S.size + W.size
    }
}