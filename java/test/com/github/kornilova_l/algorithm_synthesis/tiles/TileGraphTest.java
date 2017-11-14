package com.github.kornilova_l.algorithm_synthesis.tiles;

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator;
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGraph;
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileSet;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TileGraphTest {
    private final TileSet tiles32 = new TileGenerator(3, 2, 1).getTileSet();
    private final TileSet tiles23 = new TileGenerator(2, 3, 1).getTileSet();
    private final TileSet tiles67 = new TileSet(new File("generated_tiles/6-7-3.txt"));
    private final TileSet tiles58 = new TileSet(new File("generated_tiles/5-8-3.txt"));

    @Test
    void getGraph() {
        TileGraph tileGraph = new TileGraph(tiles32, tiles23);
        assertEquals(7, tileGraph.getSize());

        assertEquals(15, tileGraph.getEdgeCount());

        tileGraph = new TileGraph(tiles67, tiles58);
        assertEquals(2079, tileGraph.getSize());
    }

}