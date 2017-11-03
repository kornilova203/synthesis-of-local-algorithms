package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Liudmila Kornilova
 * on 31.10.17.
 */
class TileGeneratorTest {
    @Test
    void test() {
        TileGenerator tileGenerator = new TileGenerator(7, 5, 3);
        assertEquals(2079, tileGenerator.getTiles().size());
    }
}