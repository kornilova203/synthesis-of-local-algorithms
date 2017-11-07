package com.github.kornilova_l.algorithm_sinthesis.tiles;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.github.kornilova_l.algorithm_sinthesis.tiles.TileGraphBuilder.countEdges;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TileGraphBuilderTest {
    private final Set<Tile> tiles32 = new TileGenerator(3, 2, 1).getTiles();
    private final Set<Tile> tiles23 = new TileGenerator(2, 3, 1).getTiles();
    private final Set<Tile> tiles67 = TileGenerator.importFromFile(new File("generated_tiles/6-7-3-1509826576982.txt"));
    private final Set<Tile> tiles58 = TileGenerator.importFromFile(new File("generated_tiles/5-8-3-1509826268928.txt"));

    @Test
    void getGraph() {
        HashMap<Tile, HashSet<Tile>> graph = new TileGraphBuilder(tiles32, tiles23, 2, 2, 1).getGraph();
        assertEquals(7, graph.size());

        assertEquals(15, countEdges(graph));

        graph = new TileGraphBuilder(tiles67, tiles58, 5, 7, 3).getGraph();
        assertEquals(2079, graph.size());
    }

}