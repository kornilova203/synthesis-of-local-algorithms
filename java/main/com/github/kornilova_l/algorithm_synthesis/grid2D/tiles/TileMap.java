package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles;

import java.util.HashMap;
import java.util.Map;

/**
 * TileMap is used to make sure that all tiles in the map has the same parameters
 *
 * @param <Val> value type in the map
 */
public class TileMap<Val> {
    private final int n;
    private final int m;
    private final int k;
    private final Map<Tile, Val> tiles = new HashMap<>();

    public TileMap(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;
    }

    public int size() {
        return tiles.size();
    }

    boolean isEmpty() {
        return tiles.isEmpty();
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

    public void put(Tile key, Val value) {
        if (key.getK() != k || key.getM() != m || key.getN() != n) {
            throw new IllegalArgumentException("Tile must have the same parameters as tile set");
        }

        tiles.put(key, value);
    }

    public Val get(Tile tile) {
        return tiles.get(tile);
    }
}
