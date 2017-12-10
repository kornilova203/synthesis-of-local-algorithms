package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import java.util.HashMap

/**
 * TileMap is used to make sure that all tiles in the map has the same parameters
 *
 * @param <Val> value type in the map
</Val> */
class TileMap<Val>(val n: Int, val m: Int, val k: Int) {
    private val tiles = HashMap<Tile, Val>()

    fun size(): Int = tiles.size

    fun put(key: Tile, value: Val) {
        if (key.k != k || key.m != m || key.n != n) {
            throw IllegalArgumentException("Tile must have the same parameters as tile set")
        }

        tiles.put(key, value)
    }

    operator fun get(tile: Tile): Val = tiles[tile]!!

    fun rotate(): TileMap<Val> {
        val rotatedTileMap = TileMap<Val>(m, n, k)
        for (entry in tiles.entries) {
            rotatedTileMap.put(entry.key.rotate(), entry.value)
        }
        return rotatedTileMap
    }
}
