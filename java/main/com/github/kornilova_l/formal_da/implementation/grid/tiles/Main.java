package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import java.io.File;
import java.util.Set;

public class Main {
    private static final Set<Tile> tiles67 = TileGenerator.importFromFile(new File("generated_tiles/6-7-3-1509826576982.txt"));
    private static final Set<Tile> tiles58 = TileGenerator.importFromFile(new File("generated_tiles/5-8-3-1509826268928.txt"));

    public static void main(String[] args) {
        ColouringProblem.Companion.exportDimacs(
                new TileGraphBuilder(tiles67, tiles58, 5, 7, 3).getGraph(),
                4,
                new File("dimacs/")
        );
    }
}
