package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles

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
        var currentN = if (n < k * 2) n else k * 2
        var currentM = if (m < k * 2) m else k * 2

        var candidateTilesSet = generatePossiblyValidTiles(currentN, currentM, k)

        while (currentM < m || currentN < n) {
            candidateTilesSet = expandTileSet(candidateTilesSet, currentN, currentM)
            currentN = candidateTilesSet.first().n
            currentM = candidateTilesSet.first().m
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
                    ?.mapTo(expandedTiles) { solution -> Tile(tile, newN, newM, solution) }
            println("expanded")
        }
        return expandedTiles
    }

    private fun toDimacs(tile: Tile, newN: Int, newM: Int): Set<Set<Int>> {
        val biggerTile = Tile(newN, newM, tile)
        val intersection = TilesIntersection(biggerTile, tile)

        val clauses = HashSet<Set<Int>>()

        for (x in 0 until n) {
            for (y in 0 until m) {
                if (intersection.isInside(x, y)) {
                    clauses.add(cellMustNotChange(x, y, biggerTile))
                } else {
                    if (biggerTile.canBeI(x, y)) {
                        for (i in x - k..x + k) {
                            (y - k..y + k)
                                    .filter { j ->
                                        i >= 0 && j >= 0 && i < newN && j < newM && !(i == x && j == y) && // not center
                                                Math.abs(x - i) + Math.abs(y - j) <= k
                                    }
                                    .mapTo(clauses) { j -> hashSetOf(-biggerTile.getId(x, y), -biggerTile.getId(i, j)) }
                        }
                    } else {
                        clauses.add(hashSetOf(-biggerTile.getId(x, y))) // must be zero
                    }
                    /* at least one should be one: */
                    val clause = HashSet<Int>()
                    for (i in x - k..x + k) {
                        (y - k..y + k)
                                .filter { j ->
                                    i >= 0 && j >= 0 && i < newN && j < newM && !(i == x && j == y) && // not center
                                            Math.abs(x - i) + Math.abs(y - j) <= k
                                }
                                .mapTo(clause) { j -> biggerTile.getId(i, j) }
                    }
                }
            }
        }
        return clauses
    }

    private fun cellMustNotChange(x: Int, y: Int, biggerTile: Tile): Set<Int> {
        return if (biggerTile.isI(x, y)) {
            hashSetOf(biggerTile.getId(x, y))
        } else {
            hashSetOf(-biggerTile.getId(x, y))
        }
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
