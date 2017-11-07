package com.github.kornilova_l.algorithm_sinthesis.tiles;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TileGeneratorTest {
    @Test
    void test() {
        TileGenerator tileGenerator = new TileGenerator(7, 5, 3);
        assertEquals(2079, tileGenerator.getTiles().size());

        tileGenerator = new TileGenerator(5, 7, 3); // set of tiles should not depend on orientation
        assertEquals(2079, tileGenerator.getTiles().size());
    }

    @Test
    void exportAndImportTest() {
        TileGenerator tileGenerator = new TileGenerator(5, 7, 3);
        File file = tileGenerator.exportToFile(new File("."));
        assertNotNull(file);

        HashSet<Tile> tiles = TileGenerator.importFromFile(file);
        assertNotNull(tiles);

        assertTrue(CollectionUtils.isEqualCollection(tileGenerator.getTiles(), tiles));

        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }
}