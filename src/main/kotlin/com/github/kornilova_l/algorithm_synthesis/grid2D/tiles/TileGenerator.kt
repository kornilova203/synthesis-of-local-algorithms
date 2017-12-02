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
 * Generates all possible combinations of tileSet finalN x finalM in kth power of grid
 */
class TileGenerator(private val finalN: Int, private val finalM: Int, private val k: Int) {
    val tileSet: TileSet

    init {
        var currentN = if (finalN < k * 2) finalN else k * 2
        var currentM = if (finalM < k * 2) finalM else k * 2

        var candidateTilesSet = generatePossiblyValidTiles(currentN, currentM, k)

        while (currentM < finalM || currentN < finalN) {
            candidateTilesSet = expandTileSet(candidateTilesSet, currentN, currentM)
            currentN = candidateTilesSet.first().n
            currentM = candidateTilesSet.first().m
            println("Found $currentN x $currentM tiles")
        }

        val maximalTiles = removeNotMaximal(candidateTilesSet, finalN, finalM)

        if (maximalTiles.isEmpty()) {
            throw IllegalArgumentException("Cannot produce valid set of tiles")
        } else {
            this.tileSet = TileSet(maximalTiles)
        }
    }

    private fun expandTileSet(candidateTilesSet: Set<Tile>, currentN: Int, currentM: Int): Set<Tile> {
        val newN = if (currentN + 2 * k < finalN) currentN + 2 * k else finalN
        val newM = if (currentM + 2 * k < finalM) currentM + 2 * k else finalM
        val expandedTiles = HashSet<Tile>()
        for (tile in candidateTilesSet) {
            val clauses = toDimacs(tile, newN, newM)
            findAllSolutionsWithSatSolver(clauses, newN * newM)
                    ?.mapTo(expandedTiles) { solution -> Tile(tile, newN, newM, solution) }
            println("expanded")
        }
        return expandedTiles
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
        return finalN.toString() + " " + finalM + " " + k + "\n" +
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
            String.format("%d-%d-%d-%d.txt", finalN, finalM, k, System.currentTimeMillis())
        } else String.format("%d-%d-%d.txt", finalN, finalM, k)
    }

    companion object {

        internal fun toDimacs(tile: Tile, newN: Int, newM: Int): Set<Set<Int>> {
            val k = tile.k
            val biggerTile = Tile(newN, newM, tile)
            val intersection = TilesIntersection(biggerTile, tile)

            val clauses = HashSet<Set<Int>>()

            for (x in 0 until newN) {
                for (y in 0 until newM) {
                    if (intersection.isInside(x, y)) {
                        clauses.add(cellMustStayTheSame(x, y, biggerTile))
                        if (biggerTile.isI(x, y)) {
                            clauses.addAll(allNeighboursMustBeZero(x, y, biggerTile, newN, newM, k))
                        } else if (biggerTile.canBeI(x, y) && neighbourhoodIsInsideTile(x, y, newN, newM, k)) {
                            clauses.add(atLeastOneNeighbourMustBeOne(x, y, biggerTile, newN, newM, k))
                        }

                    } else {
                        if (biggerTile.canBeI(x, y)) {
                            ifCenterIsOneAllOtherAreNot(x, y, biggerTile, clauses, newN, newM, k)
                            if (neighbourhoodIsInsideTile(x, y, newN, newM, k)) {
                                val clause = atLeastOneNeighbourMustBeOne(x, y, biggerTile, newN, newM, k)
                                clause.add(biggerTile.getId(x, y)) // center may also be in IS
                                clauses.add(clause)
                            }
                        } else {
                            clauses.add(hashSetOf(-biggerTile.getId(x, y))) // must be zero
                        }
                    }
                }
            }
            return clauses
        }

        private fun neighbourhoodIsInsideTile(x: Int, y: Int, n: Int, m: Int, k: Int): Boolean {
            if (x - k < 0 || y - k < 0) {
                return false
            }
            if (x + k >= n || y + k >= m) {
                return false
            }
            return true
        }

        private fun cellMustStayTheSame(x: Int, y: Int, biggerTile: Tile): Set<Int> {
            return if (biggerTile.isI(x, y)) {
                hashSetOf(biggerTile.getId(x, y))
            } else {
                hashSetOf(-biggerTile.getId(x, y))
            }
        }

        private fun allNeighboursMustBeZero(x: Int, y: Int, biggerTile: Tile, newN: Int, newM: Int, k: Int): Set<Set<Int>> {
            val clauses = HashSet<Set<Int>>()
            for (i in x - k..x + k) {
                (y - k..y + k)
                        .filter { j ->
                            i >= 0 && j >= 0 && i < newN && j < newM &&
                                    !(i == x && j == y) && // not center
                                    Math.abs(x - i) + Math.abs(y - j) <= k
                        }
                        .mapTo(clauses) { j -> hashSetOf(-biggerTile.getId(i, j)) } // must be zero
            }
            return clauses
        }

        /**
         * If (x, y) is 1 then non of it's neighbours is 1
         */
        private fun ifCenterIsOneAllOtherAreNot(x: Int, y: Int, biggerTile: Tile,
                                                clauses: HashSet<Set<Int>>, newN: Int, newM: Int, k: Int) {
            for (i in x - k..x + k) {
                (y - k..y + k)
                        .filter { j ->
                            i >= 0 && j >= 0 && i < newN && j < newM && !(i == x && j == y) && // not center
                                    Math.abs(x - i) + Math.abs(y - j) <= k
                        }
                        .mapTo(clauses) { j -> hashSetOf(-biggerTile.getId(x, y), -biggerTile.getId(i, j)) }
            }
        }

        private fun atLeastOneNeighbourMustBeOne(x: Int, y: Int, biggerTile: Tile, newN: Int, newM: Int, k: Int): HashSet<Int> {
            val clause = HashSet<Int>()
            for (i in x - k..x + k) {
                (y - k..y + k)
                        .filter { j ->
                            i >= 0 && j >= 0 && i < newN && j < newM &&
                                    !(i == x && j == y) && // not center
                                    Math.abs(x - i) + Math.abs(y - j) <= k
                        }
                        .mapTo(clause) { j -> biggerTile.getId(i, j) }
            }
            return clause
        }
    }
}
