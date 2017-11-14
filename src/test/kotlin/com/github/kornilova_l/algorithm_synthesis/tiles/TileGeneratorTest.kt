package com.github.kornilova_l.algorithm_synthesis.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileGenerator
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.TileSet
import org.apache.commons.collections4.CollectionUtils
import org.junit.Assert.*
import org.junit.Test
import java.io.File

internal class TileGeneratorTest {
    @Test
    fun test() {
        var tileGenerator = TileGenerator(7, 5, 3)
        assertEquals(2079, tileGenerator.tileSet.size())

        tileGenerator = TileGenerator(5, 7, 3) // set of tiles should not depend on orientation
        assertEquals(2079, tileGenerator.tileSet.size())
    }

    @Test
    fun exportAndImportTest() {
        val tileGenerator = TileGenerator(5, 7, 3)
        val file = tileGenerator.exportToFile(File("."), true)
        assertNotNull(file)

        val tileIS = TileSet(file!!).getPossiblyValidTiles()
        assertNotNull(tileIS)

        assertTrue(CollectionUtils.isEqualCollection(tileGenerator.tileSet.getPossiblyValidTiles(), tileIS))


        file.delete()
    }
}