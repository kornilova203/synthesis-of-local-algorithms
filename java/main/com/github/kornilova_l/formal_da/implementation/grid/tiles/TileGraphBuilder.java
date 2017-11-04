package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import com.github.kornilova_l.formal_da.implementation.grid.tiles.Tile.Part;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TileGraphBuilder {
    private final int n;
    private final int m;
    private final int k;

    /**
     * @param tiles1 tile.n == n + 1, tile.m == m
     * @param tiles2 tile.n == n, tile.m == m + 1
     * @param n      internal tile n
     * @param m      internal tile m
     * @param k      of all tiles
     */
    public TileGraphBuilder(Set<Tile> tiles1, Set<Tile> tiles2, int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;
        validateTilesSize(tiles1, tiles2);
        HashMap<Tile, HashSet<Tile>> graph = new HashMap<>();
        for (Tile tile : tiles1) {
            Tile top = new Tile(tile, Part.TOP);
            Tile bottom = new Tile(tile, Part.BOTTOM);
            Set<Tile> neighbours = graph.computeIfAbsent(top, t -> new HashSet<>());
            neighbours.add(bottom);
        }
        for (Tile tile : tiles2) {
            Tile left = new Tile(tile, Part.LEFT);
            Tile right = new Tile(tile, Part.RIGHT);
            Set<Tile> neighbours = graph.computeIfAbsent(left, t -> new HashSet<>());
            neighbours.add(right);
        }
    }

    /**
     * @param tiles1 tile.n == n + 1, tile.m == m
     * @param tiles2 tile.n == n, tile.m == m + 1
     */
    private void validateTilesSize(Set<Tile> tiles1, Set<Tile> tiles2) {
        if (tiles1.isEmpty() || tiles2.isEmpty()) {
            throw new IllegalArgumentException("At least one set is empty");
        }
        for (Tile tile : tiles1) {
            if (tile.getN() != n + 1 || tile.getM() != m) {
                throw new IllegalArgumentException("Size of tiles in first set should be n + 1, m");
            }
            if (tile.getK() != k) {
                throw new IllegalArgumentException("Power of graph does not match");
            }
        }
        for (Tile tile : tiles2) {
            if (tile.getN() != n || tile.getM() != m + 1) {
                throw new IllegalArgumentException("Size of tiles in second set should be n, m + 1");
            }
            if (tile.getK() != k) {
                throw new IllegalArgumentException("Power of graph does not match");
            }
        }
    }

    public static void main(String[] args) {
        new TileGraphBuilder(new TileGenerator(3, 2, 1).getTiles(),
                new TileGenerator(2, 3, 1).getTiles(), 2, 2, 1);
    }
}
