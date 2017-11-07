package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColouringProblemTest {
    private final Set<Tile> tiles32 = new TileGenerator(3, 2, 1).getTiles();
    private final Set<Tile> tiles23 = new TileGenerator(2, 3, 1).getTiles();

    @Test
    void toDimacs() throws IOException {
        String expected = String.join("\n",
                FileUtils.readLines(new File("java/test_resources/dimacs_4-colouring_2-2-1.txt"), (String) null)) + "\n";
        HashMap<Tile, HashSet<Tile>> graph = new TileGraphBuilder(tiles32, tiles23, 2, 2, 1).getGraph();
        Map<Tile, Integer> ids = ColouringProblem.Companion.assignIds(graph);
        String actual = ColouringProblem.Companion.toDimacs(
                graph, ids, 4
        );
        assertEquals(expected, actual);
    }

}