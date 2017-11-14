package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.util.ProgressBar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Generates all possible combinations of tileSet n x m in kth power of grid
 */
class TileGenerator(private val n: Int, private val m: Int, private val k: Int) {
    val tileSet: TileSet

    init {

        val candidateTileIS = ConcurrentLinkedQueue<Tile>() // get concurrently from here

        val candidateTilesSet = TileSet(n, m, k)
        candidateTileIS.addAll(candidateTilesSet.possiblyValidTiles)

        printCandidatesFound(candidateTileIS.size)
        val validTileIS = ConcurrentHashMap.newKeySet<Tile>() // put concurrently here
        try {
            removeNotMaximal(candidateTileIS, validTileIS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        if (validTileIS.isEmpty()) {
            throw IllegalArgumentException("Cannot produce valid set of tiles")
        } else {
            this.tileSet = TileSet(validTileIS)
        }
    }

    private fun printCandidatesFound(candidatesCount: Int) {
        println("Found " + candidatesCount + " possible tile" + if (candidatesCount == 1) "" else "s")
        println("Remove tiles which cannot contain maximal independent set...")
    }

    /**
     * Remove all tileSet which does not have maximal IS
     */
    @Throws(InterruptedException::class)
    private fun removeNotMaximal(candidateTileIS: ConcurrentLinkedQueue<Tile>,
                                 validTileIS: MutableSet<Tile>) {
        val progressBar = ProgressBar(candidateTileIS.size)
        val processorsCount = Runtime.getRuntime().availableProcessors()
        val executorService = Executors.newFixedThreadPool(processorsCount)

        for (i in 0 until processorsCount) {
            executorService.submit(TileValidator(candidateTileIS, validTileIS, progressBar))
        }

        executorService.shutdown()
        while (!executorService.awaitTermination(24L, TimeUnit.HOURS)) {
            println("Not yet. Still waiting for termination")
        }
        progressBar.finish()
        println(validTileIS.size.toString() + " valid tiles")
    }

    override fun toString(): String {
        return n.toString() + " " + m + " " + k + "\n" +
                tileSet.size() + "\n" +
                tileSet.toString()
    }

    fun exportToFile(dir: File, addTimestampToFileName: Boolean?): File? {
        if (!dir.exists() || !dir.isDirectory) {
            throw IllegalArgumentException("Argument is not a directory or does not exist")
        }
        val filePath = Paths.get(dir.toString(), getFileName(addTimestampToFileName))
        val file = filePath.toFile()
        try {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(toString().toByteArray())
                return file
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun getFileName(addTimestamp: Boolean?): String {
        return if (addTimestamp!!) {
            String.format("%d-%d-%d-%d.txt", n, m, k, System.currentTimeMillis())
        } else String.format("%d-%d-%d.txt", n, m, k)
    }

    private inner class TileValidator internal constructor(private val candidateTileIS: ConcurrentLinkedQueue<Tile>, private val validTileIS: MutableSet<Tile>, private val progressBar: ProgressBar) : Runnable {

        override fun run() {
            while (!candidateTileIS.isEmpty()) {
                val tile = candidateTileIS.poll()
                if (tile != null) {
                    if (tile.isValid) {
                        validTileIS.add(tile)
                    }
                    progressBar.updateProgress(1)
                }
            }
        }
    }
}
