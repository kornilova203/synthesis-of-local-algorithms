package com.github.kornilova_l.algorithm_synthesis.tiles;

import com.github.kornilova_l.algorithm_synthesis.grid2D.colouring.ColouringProblem;
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator;
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGraph;
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileSet;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColouringProblemTest {
    private final TileSet tiles32 = new TileGenerator(3, 2, 1).getTileSet();
    private final TileSet tiles23 = new TileGenerator(2, 3, 1).getTileSet();

    @Test
    void toDimacs() throws IOException {
        // TODO: compare set of strings
        /* note: this test may fail because order of lines matters */
        String expected = String.join("\n",
                FileUtils.readLines(new File("java/test_resources/dimacs_4-colouring_2-2-1.txt"), (String) null)) + "\n";
        TileGraph graph = new TileGraph(tiles32, tiles23);
        String actual = ColouringProblem.Companion.toDimacs(
                graph, 4
        );
        assertEquals(expected, actual);
    }

}