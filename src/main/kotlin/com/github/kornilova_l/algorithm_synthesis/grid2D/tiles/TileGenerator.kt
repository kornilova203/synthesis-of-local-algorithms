package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand.HEIGHT
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand.WIDTH
import com.github.kornilova_l.util.FileNameCreator
import com.github.kornilova_l.util.ProgressBar
import com.github.kornilova_l.util.Util
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ForkJoinPool

/**
 * Generates all possible combinations of tileSet finalN x finalM in kth power of grid.
 * On each step this generator expands set of tiles by 1 row or 1 column.
 * Two different tiles cannot produce equal expanded tiles so it is possible to not store all tiles in memory
 * therefore tiles are parsed from file one by one, expanded and stored to temporal file
 * @param fileWithInitialTiles file with initial tiles. File name must contain n, m and size
 */
abstract class TileGenerator<T : BinaryTile>(private val finalN: Int, private val finalM: Int,
                                             fileWithInitialTiles: File,
                                             outputDir: File,
                                             private val tilesParserFactory: TilesParserFactory<T>,
                                             private val tilesFileNameCreator: FileNameCreator) {
    val size: Int
    val file: File
    private val tempDir = File("TileGenerator_temp_dir")
    val tiles: Set<T>
        get() {
            val tiles = HashSet<T>()
            for (tile in tilesParserFactory.createParser(file)) {
                tiles.add(tile)
            }
            return tiles
        }

    init {
        Util.deleteDir(tempDir.toPath())
        tempDir.mkdir()
        var currentTilesFile = fileWithInitialTiles
        var currentN = FileNameCreator.getIntParameter(currentTilesFile.name, "n")!!
        var currentM = FileNameCreator.getIntParameter(currentTilesFile.name, "m")!!
        var currentSize = FileNameCreator.getIntParameter(currentTilesFile.name, "size")!!
        while (currentM < finalM || currentN < finalN) {
            val pair = expandTileSet(currentTilesFile, currentN, currentM)
            if (currentTilesFile.absoluteFile.parent == tempDir.name) { // if inside temp file
                currentTilesFile.delete()
            }
            currentTilesFile = pair.first
            currentSize = pair.second
            currentN = FileNameCreator.getIntParameter(currentTilesFile.name, "n")!!
            currentM = FileNameCreator.getIntParameter(currentTilesFile.name, "m")!!
        }
        size = currentSize
        file = Paths.get(outputDir.toString(),
                tilesFileNameCreator.getFileName(finalN, finalM, size)).toFile()
        val res = currentTilesFile.renameTo(file.absoluteFile)
        if (res) {
            println("Output file: $file")
        } else {
            System.err.println("Cannot rename file to $file")
        }
        Util.deleteDir(tempDir.toPath())
    }

    /**
     * Expand each tile by 1 row/column
     * Method processes tiles concurrently by small group of size [TilesSetsIterator.size]
     */
    private fun expandTileSet(currentTilesFile: File, currentN: Int, currentM: Int): Pair<File, Int> {
        val side = if (currentN < currentM && currentN < finalN || currentM == finalM) HEIGHT else WIDTH
        val currentTilesCount = FileNameCreator.getIntParameter(currentTilesFile.name, "size")!!
        println("Expand tiles $currentN x $currentM. Side: $side. Tiles count: $currentTilesCount")
        val progressBar = ProgressBar(currentTilesCount)
        val tilesSetsIterator = TilesSetsIterator(tilesParserFactory.createParser(currentTilesFile)) // process tiles by small groups

        /* we cannot create temp file with valid name because we do not know how many tiles will be there */
        val tempOutputFile = Paths.get(tempDir.toString(), "${System.currentTimeMillis()}.tiles").toFile()
        var expandedTilesCount = 0
        for (tilesSet in tilesSetsIterator) {
            val createdTilesCount = processPackOfTilesConcurrently(tilesSet, tempOutputFile, side, progressBar)
            expandedTilesCount += createdTilesCount
        }
        println()
        return Pair(renameToValidFile(tempOutputFile, currentN, currentM, expandedTilesCount, side), expandedTilesCount)
    }

    private fun renameToValidFile(tempOutputFile: File, currentN: Int, currentM: Int, tilesCount: Int, side: Expand): File {
        val newN = if (side == HEIGHT) currentN + 1 else currentN
        val newM = if (side == WIDTH) currentM + 1 else currentM
        val outputFile = Paths.get(tempDir.toString(), tilesFileNameCreator.getFileName(newN, newM, tilesCount)).toFile()
        tempOutputFile.renameTo(outputFile)
        return outputFile
    }

    private fun processPackOfTilesConcurrently(tiles: Set<T>, tempNewOutputFile: File, side: Expand, progressBar: ProgressBar): Int {
        val expandedTiles: MutableSet<T> = ConcurrentHashMap.newKeySet()
        /* limit number of threads to 4 because sat solver allocates memory outside java heap and
         * therefore sat solver may consume all memory and cause java process to be killed.
         * the number of threads needs to be adjusted to each computer separately */
        val forkJoinPool = ForkJoinPool(4)
        forkJoinPool.submit {
            tiles.parallelStream().forEach { tile ->
                addValidExtensionsToSet(tile, expandedTiles, side)
                progressBar.updateProgress()
            }
        }.get()
        forkJoinPool.shutdown()
        appendToFile(expandedTiles, tempNewOutputFile)
        return expandedTiles.size
    }

    private fun appendToFile(expandedTiles: MutableSet<T>, newOutputFile: File) {
        FileOutputStream(newOutputFile, true).use { stream ->
            for (expandedTile in expandedTiles) {
                stream.write(expandedTile.longsToString().toByteArray())
                stream.write("\n".toByteArray())
            }
        }
    }

    protected abstract fun addValidExtensionsToSet(tile: T,
                                                   expandedTiles: MutableSet<T>,
                                                   side: Expand)

    inner class TilesSetsIterator(val tilesParser: Iterable<T>) : Iterable<Set<T>> {
        val size = 100_000
        override fun iterator(): Iterator<Set<T>> = MyIterator()

        inner class MyIterator : Iterator<Set<T>> {
            val iterator = tilesParser.iterator()

            override fun hasNext(): Boolean = iterator.hasNext()

            override fun next(): Set<T> {
                val tiles = HashSet<T>()
                for (i in 0 until size) {
                    if (iterator.hasNext()) {
                        tiles.add(iterator.next())
                    }
                }
                return tiles
            }
        }
    }
}
