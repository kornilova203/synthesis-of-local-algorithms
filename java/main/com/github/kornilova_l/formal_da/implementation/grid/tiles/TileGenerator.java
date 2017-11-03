package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import com.github.kornilova_l.util.ProgressBar;

import java.util.HashSet;

/**
 * Generates all possible combinations of tiles n x m in kth power of grid
 */
@SuppressWarnings("WeakerAccess")
public class TileGenerator {
    private final HashSet<Tile> tiles = new HashSet<>();

    TileGenerator(int n, int m, int k) {
        tiles.add(new Tile(n, m, k));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                HashSet<Tile> newTiles = new HashSet<>();
                for (Tile tile : tiles) {
                    if (tile.canBeI(i, j)) {
                        newTiles.add(new Tile(tile, i, j));
                    }
                }
                tiles.addAll(newTiles);
                newTiles.clear();
            }
        }
        printCandidatesFound();
        removeNotMaximal();
    }

    private void printCandidatesFound() {
        int candidatesCount = tiles.size();
        System.out.println("Found " + candidatesCount + " possible tile" + (candidatesCount == 1 ? "" : "s"));
        System.out.println("Remove tiles which cannot contain maximal independent set...");
    }

    /**
     * Remove all tiles which does not have maximal IS
     */
    private void removeNotMaximal() {
        int candidatesCount = tiles.size();
        ProgressBar progressBar = new ProgressBar(candidatesCount);
        HashSet<Tile> notMaximalTiles = new HashSet<>();
        int i = 0;
        int prevPercent = 0;
        for (Tile tile : tiles) {
            if (!tile.isTileValid()) {
                notMaximalTiles.add(tile);
            }
            prevPercent = printPercent(++i, candidatesCount, prevPercent, progressBar);
        }
        progressBar.finish();
        tiles.removeAll(notMaximalTiles);
    }

    private int printPercent(int i, int candidatesCount, int prevPercent, ProgressBar progressBar) {
        int percent = ((i * 100) / candidatesCount);
        if (percent != 0 && percent != prevPercent && percent % 2 == 0) {
            progressBar.printProgress(i);
            return percent;
        }
        return prevPercent;
    }

    public HashSet<Tile> getTiles() {
        return tiles;
    }

    public static void main(String[] args) {
        new TileGenerator(5, 7, 3);
    }
}
