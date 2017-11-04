package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TileGeneratorTest {
    @Test
    void test() {
        TileGenerator tileGenerator = new TileGenerator(7, 5, 3);
        assertEquals(2079, tileGenerator.getTiles().size());

        tileGenerator = new TileGenerator(5, 7, 3); // set of tiles should not depend on orientation
        assertEquals(2079, tileGenerator.getTiles().size());
    }
}