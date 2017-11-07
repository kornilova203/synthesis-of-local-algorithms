package com.github.kornilova_l.algorithm_sinthesis.tiles;

import com.github.kornilova_l.algorithm_sinthesis.tiles.Tile.Part;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TileGraphBuilder {
    private final int n;
    private final int m;
    private final int k;
    private final HashMap<Tile, HashSet<Tile>> graph = new HashMap<>();

    /**
     * @param tiles1 tile.n == n + 1, tile.m == m
     * @param tiles2 tile.n == n, tile.m == m + 1
     * @param n      internal tile n
     * @param m      internal tile m
     * @param k      of all tiles
     */
    @SuppressWarnings("WeakerAccess")
    public TileGraphBuilder(Set<Tile> tiles1, Set<Tile> tiles2, int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;
        validateTilesSize(tiles1, tiles2);
        for (Tile tile : tiles1) {
            Tile top = new Tile(tile, Part.TOP);
            Tile bottom = new Tile(tile, Part.BOTTOM);
            graph.computeIfAbsent(top, t -> new HashSet<>()).add(bottom);
            graph.computeIfAbsent(bottom, t -> new HashSet<>()).add(top);
        }
        for (Tile tile : tiles2) {
            Tile left = new Tile(tile, Part.LEFT);
            Tile right = new Tile(tile, Part.RIGHT);
            graph.computeIfAbsent(left, t -> new HashSet<>()).add(right);
            graph.computeIfAbsent(right, t -> new HashSet<>()).add(left);
        }
        if (graph.size() == 0) {
            throw new IllegalArgumentException("Cannot construct graph");
        }
    }

    @SuppressWarnings("WeakerAccess")
    public HashMap<Tile, HashSet<Tile>> getGraph() {
        return graph;
    }

    public static int countEdges(Map<Tile, HashSet<Tile>> graph) {
        int res = 0;
        for (Set<Tile> neighbours : graph.values()) {
            res += neighbours.size();
        }
        assert res % 2 == 0;

        return res / 2;
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
