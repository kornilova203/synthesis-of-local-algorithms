package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import java.util.HashMap

/**
 * TileMap is used to make sure that all tiles in the map has the same parameters
 *
 * @param <Val> value type in the map
</Val> */
class TileMap<Val>(val n: Int, val m: Int, val k: Int) {
    private val tiles = HashMap<Tile, Val>()

    internal val isEmpty: Boolean
        get() = tiles.isEmpty()

    fun size(): Int {
        return tiles.size
    }

    fun put(key: Tile, value: Val) {
        if (key.k != k || key.getM() != m || key.getN() != n) {
            throw IllegalArgumentException("Tile must have the same parameters as tile set")
        }

        tiles.put(key, value)
    }

    operator fun get(tile: Tile): Val {
        return tiles[tile]!!
    }
}
