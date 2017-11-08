package com.github.kornilova_l.algorithm_synthesis.tiles;

import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.Grid2D;
import com.github.kornilova_l.algorithm_synthesis.grid2D.grid.IndependentSetAlgorithm;
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile;
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Coordinate;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {
    @Test
    void canBeI() {
        Tile tile = new Tile(5, 6, 2);

        assertTrue(tile.canBeI(4, 4));
        tile = new Tile(tile, 4, 3);
        assertFalse(tile.canBeI(4, 4));

        Tile testThrowsTile = new Tile(5, 6, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> testThrowsTile.canBeI(5, 4));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> testThrowsTile.canBeI(4, 6));

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
    void isValidTest() { // may take some time
        Tile tile = new Tile(5, 6, 2);
        assertFalse(tile.isValid());

        tile = new Tile(5, 7, 3);
        assertTrue(tile.isValid());
    }

    @Test
    void expandingConstructor() {
        Tile tile = new Tile(3, 4, 3);
        tile = new Tile(tile, 0, 1);
        Tile expected = new Tile(9, 10, 3);

        expected = new Tile(expected, 3, 4);
        assertEquals(expected, new Tile(tile));
    }

    @Test
    void getNextBorderCoordinateTest() {
        Tile tile = new Tile(6, 8, 2);
        assertEquals(new Coordinate(1, 0),
                tile.getNextBorderCoordinate(new Coordinate(0, 0)));

        assertEquals(new Coordinate(0, 1),
                tile.getNextBorderCoordinate(new Coordinate(5, 0)));

        assertEquals(new Coordinate(4, 2),
                tile.getNextBorderCoordinate(new Coordinate(1, 2)));

        assertEquals(new Coordinate(2, 0),
                tile.getNextBorderCoordinate(new Coordinate(1, 0)));

        assertNull(tile.getNextBorderCoordinate(new Coordinate(tile.getN() - 1, tile.getM() - 1)));
    }

    @Test
    void tileFromArrayTest() {
        Grid2D grid2D = new Grid2D(new File("java/test_resources/grids/01_grid_5-6.txt"));
        boolean[][] independentSet = new IndependentSetAlgorithm(grid2D, 2).getIndependentSet();

        Tile tile = new Tile(independentSet, 1, 3, 3, 5, 2);

        assertEquals("0 0 0 0 0\n" +
                "0 0 0 1 0\n" +
                "0 1 0 0 0\n", tile.toString());

        tile = new Tile(independentSet, 0, 0, 3, 5, 2);

        assertEquals("0 0 0 0 0\n" +
                "0 0 1 0 0\n" +
                "1 0 0 0 0\n", tile.toString());
    }
}