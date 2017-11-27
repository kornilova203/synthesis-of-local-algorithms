package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile.Coordinate
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.generatePossiblyValidTiles
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.findAllSolutionsWithSatSolver
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.solveWithSatSolver
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths

/**
 * Generates all possible combinations of tileSet n x m in kth power of grid
 */
class TileGenerator(private val n: Int, private val m: Int, private val k: Int) {
    val tileSet: TileSet

    init {
        var currentN = if (n < k) n else k
        var currentM = if (m < k) m else k

        var candidateTilesSet = generatePossiblyValidTiles(currentN, currentM, k)

        while (currentM < m || currentN < n) {
            candidateTilesSet = expandTileSet(candidateTilesSet, currentN, currentM)
            currentN = candidateTilesSet.first().getN()
            currentM = candidateTilesSet.first().getM()
            println("Found $currentN x $currentM tiles")
        }

        val maximalTiles = removeNotMaximal(candidateTilesSet, n, m)

        if (maximalTiles.isEmpty()) {
            throw IllegalArgumentException("Cannot produce valid set of tiles")
        } else {
            this.tileSet = TileSet(maximalTiles)
        }
    }

    private fun expandTileSet(candidateTilesSet: Set<Tile>, currentN: Int, currentM: Int): Set<Tile> {
        val newN = if (currentN + 2 * k < n) currentN + 2 * k else n
        val newM = if (currentM + 2 * k < m) currentM + 2 * k else m
        val expandedTiles = HashSet<Tile>()
        for (tile in candidateTilesSet) {
            val clauses = toDimacs(tile, newN, newM)
            findAllSolutionsWithSatSolver(clauses, newN * newM)
                    ?.mapTo(expandedTiles) { Tile(tile, newN, newM, it) }
            println("expanded")
        }
        return expandedTiles
    }

    private fun toDimacs(tile: Tile, newN: Int, newM: Int): Set<Set<Int>> {
        val biggerTile = Tile(newN, newM, tile)
        var borderCoordinate: Coordinate? = Coordinate(0, 0)

        val clauses = HashSet<Set<Int>>()

        while (borderCoordinate != null) {
            val x = borderCoordinate.x
            val y = borderCoordinate.y
            if (biggerTile.canBeI(x, y)) {
                for (i in x - k..x + k) {
                    (y - k..y + k)
                            .filter { j ->
                                i >= 0 && j >= 0 && i < newN && j < newM && !(i == x && j == y) && // not center
                                        Math.abs(x - i) + Math.abs(y - j) <= k
                            }
                            .mapTo(clauses) { j -> hashSetOf(-(x * newM + y + 1), -(i * newM + j + 1)) }
                }
            } else {
                clauses.add(hashSetOf(-(x * newM + y + 1))) // must be zero
            }
            /* at least one should be one: */
            val clause = HashSet<Int>()
            for (i in x - k..x + k) {
                (y - k..y + k)
                        .filter { j ->
                            i >= 0 && j >= 0 && i < newN && j < newM && !(i == x && j == y) && // not center
                                    Math.abs(x - i) + Math.abs(y - j) <= k
                        }
                        .mapTo(clause) { j -> i * newM + j + 1 }
            }
            borderCoordinate = biggerTile.getNextBorderCoordinate(borderCoordinate)
        }
        return clauses
    }

    /**
     * Remove all tileSet which does not have maximal IS
     */
    private fun removeNotMaximal(tiles: Set<Tile>, n: Int, m: Int): Set<Tile> {
        val newN = n + 2 * k
        val newM = m + 2 * k
        val maximalTiles = HashSet<Tile>()
        for (tile in tiles) {
            val clauses = toDimacs(tile, newN, newM)
            val solution = solveWithSatSolver(clauses, newN * newM)
            if (solution != null) {
                maximalTiles.add(tile)
            }
        }
        return maximalTiles
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
}
