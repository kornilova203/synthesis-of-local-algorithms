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
        assertTrue(tile.canBeI(0, 3));
        /* On diagonal and on opposite side: */
        assertTrue(tile.canBeI(0, 2));

        assertTrue(tile.canBeI(1, 2));
        assertTrue(tile.canBeI(0, 1));
    }

    @Test
    void isMaximalTest() {
        Tile tile = new Tile(5, 6, 2);
        assertFalse(tile.isMaximal());

        tile = new Tile(5, 7, 3);
        assertFalse(tile.isMaximal());
    }

    @Test
    void expandingConstructor() {
        Tile tile = new Tile(3, 4, 3);
        tile.getGrid()[0][1] = true;
        Tile expected = new Tile(9, 10, 3);
        expected.getGrid()[3][4] = true;
        assertEquals(expected, new Tile(tile));

    }

}