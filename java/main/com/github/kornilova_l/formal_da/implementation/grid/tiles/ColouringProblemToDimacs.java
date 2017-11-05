package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.github.kornilova_l.formal_da.implementation.grid.tiles.TileGraphBuilder.countEdges;

@SuppressWarnings("WeakerAccess")
public class ColouringProblemToDimacs {

    @SuppressWarnings("WeakerAccess")
    public static String toDimacs(Map<Tile, HashSet<Tile>> graph, int coloursCount) {
        StringBuilder stringBuilder = new StringBuilder();
        int clausesCount = graph.size() + countEdges(graph) * coloursCount;
        stringBuilder.append("p cnf ").append(graph.size() * coloursCount).append(" ").append(clausesCount).append("\n");
        Map<Tile, Integer> ids = assignIds(graph);

        Map<Tile, HashSet<Tile>> visitedEdges = new HashMap<>(); // to count each edge only ones

        for (Tile tile : graph.keySet()) {
            addTileClause(stringBuilder, ids.get(tile), coloursCount);
            Set<Tile> neighbours = graph.get(tile);
            for (Tile neighbour : neighbours) {
                if (visitedEdges.computeIfAbsent(neighbour, t -> new HashSet<>()).contains(tile)) {
                    continue;
                }
                visitedEdges.computeIfAbsent(tile, t -> new HashSet<>()).add(neighbour);
                addEdgeClauses(stringBuilder, ids.get(tile), ids.get(neighbour), coloursCount);
            }
        }
        return stringBuilder.toString();
    }

    private static void addEdgeClauses(StringBuilder stringBuilder, Integer id1, Integer id2, int coloursCount) {
        for (int i = 0; i < coloursCount; i++) {
            stringBuilder.append("-").append(id1 * coloursCount + i).append(" -").append(id2 * coloursCount + i).append("\n");
        }
    }

    private static void addTileClause(StringBuilder stringBuilder, Integer id, int coloursCount) {
        for (int i = 0; i < coloursCount; i++) {
            stringBuilder.append(id * coloursCount + i).append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("\n");
    }

    private static Map<Tile, Integer> assignIds(Map<Tile, HashSet<Tile>> graph) {
        Map<Tile, Integer> ids = new HashMap<>();
        int id = 0;
        for (Tile tile : graph.keySet()) {
            ids.put(tile, id++);
        }
        return ids;
    }
}