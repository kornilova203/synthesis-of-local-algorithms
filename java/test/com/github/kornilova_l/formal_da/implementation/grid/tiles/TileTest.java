package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {
    @Test
    void canBeI() {
        Tile tile = new Tile(5, 6, 2);
        assertTrue(tile.canBeI(4, 4));
        tile.getGrid()[4][3] = true;
        assertFalse(tile.canBeI(4, 4));
        assertThrows(IllegalArgumentException.class, () -> tile.canBeI(5, 4));
        assertThrows(IllegalArgumentException.class, () -> tile.canBeI(4, 6));
        /* On diagonal: */
        assertFalse(tile.canBeI(3, 2));
        /* On opposite side: */
        assertFalse(tile.canBeI(0, 3));
        /* On diagonal and on opposite side: */
        assertFalse(tile.canBeI(0, 2));

        assertTrue(tile.canBeI(1, 2));
        assertTrue(tile.canBeI(0, 1));
    }

}