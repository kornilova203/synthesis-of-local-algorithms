package com.github.kornilova_l.algorithm_synthesis.tiles;

import com.github.kornilova_l.algorithm_synthesis.tiles.Tile.Part;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TileGraphBuilder {
    private final HashMap<Tile, HashSet<Tile>> graph = new HashMap<>();

    @SuppressWarnings("WeakerAccess")
    public TileGraphBuilder(TileSet tileSet1, TileSet tileSet2) {
        validateTileSets(tileSet1, tileSet2);

        for (Tile tile : tileSet1.getTiles()) { // get vertical neighbours
            Tile top = new Tile(tile, Part.TOP);
            Tile bottom = new Tile(tile, Part.BOTTOM);
            graph.computeIfAbsent(top, t -> new HashSet<>()).add(bottom);
            graph.computeIfAbsent(bottom, t -> new HashSet<>()).add(top);
        }
        for (Tile tile : tileSet2.getTiles()) { // get horizontal neighbours
            Tile left = new Tile(tile, Part.LEFT);
            Tile right = new Tile(tile, Part.RIGHT);
            graph.computeIfAbsent(left, t -> new HashSet<>()).add(right);
            graph.computeIfAbsent(right, t -> new HashSet<>()).add(left);
        }
        if (graph.size() == 0) {
            throw new IllegalArgumentException("Cannot construct graph");
        }
    }

    private void validateTileSets(TileSet tileSet1, TileSet tileSet2) {
        if (tileSet1.getK() != tileSet2.getK()) {
            throw new IllegalArgumentException("Graph power is different in two sets");
        }
        if (tileSet1.isEmpty() || tileSet2.isEmpty()) {
            throw new IllegalArgumentException("At least one set is empty");
        }

        int n1 = tileSet1.getN();
        int m1 = tileSet1.getM();
        int n2 = tileSet2.getN();
        int m2 = tileSet2.getM();
        if (n1 > n2) {
            if (n1 != n2 + 1 || m2 != m1 + 1) {
                throw new IllegalArgumentException("If size of kern tile is n*m then size of two tiles set must be n+1*m and n*m+1 (order does not matter)");
            }
        } else {
            if (n1 + 1 != n2 || m2 + 1 != m1) {
                throw new IllegalArgumentException("If size of kern tile is n*m then size of two tiles set must be n+1*m and n*m+1 (order does not matter)");
            }
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
}
