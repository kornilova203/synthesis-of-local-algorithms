package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.github.kornilova_l.formal_da.implementation.grid.tiles.TileGraphBuilder.countEdges;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TileGraphBuilderTest {
    private final Set<Tile> tiles32 = new TileGenerator(3, 2, 1).getTiles();
    private final Set<Tile> tiles23 = new TileGenerator(2, 3, 1).getTiles();

    @Test
    void getGraph() {
        HashMap<Tile, HashSet<Tile>> graph = new TileGraphBuilder(tiles32, tiles23, 2, 2, 1).getGraph();
        assertEquals(7, graph.size());

        assertEquals(15, countEdges(graph));
    }

}