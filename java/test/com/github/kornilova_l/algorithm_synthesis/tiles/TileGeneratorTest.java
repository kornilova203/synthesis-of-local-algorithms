package com.github.kornilova_l.algorithm_synthesis.tiles;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TileGeneratorTest {
    @Test
    void test() {
        TileGenerator tileGenerator = new TileGenerator(7, 5, 3);
        assertEquals(2079, tileGenerator.getTileSet().size());

        tileGenerator = new TileGenerator(5, 7, 3); // set of tiles should not depend on orientation
        assertEquals(2079, tileGenerator.getTileSet().size());
    }

    @Test
    void exportAndImportTest() {
        TileGenerator tileGenerator = new TileGenerator(5, 7, 3);
        File file = tileGenerator.exportToFile(new File("."));
        assertNotNull(file);

        Set<Tile> tiles = new TileSet(file).getTiles();
        assertNotNull(tiles);

        assertTrue(CollectionUtils.isEqualCollection(tileGenerator.getTileSet().getTiles(), tiles));

        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }
}