package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import com.github.kornilova_l.util.ProgressBar;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

/**
 * Generates all possible combinations of tiles n x m in kth power of grid
 */
@SuppressWarnings("WeakerAccess")
public class TileGenerator {
    private final HashSet<Tile> tiles = new HashSet<>();
    private int n;
    private int m;
    private int k;

    TileGenerator(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;
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
                System.out.println(tile);
            }
            prevPercent = printPercent(++i, candidatesCount, prevPercent, progressBar);
        }
        progressBar.finish();
        tiles.removeAll(notMaximalTiles);
        System.out.println(tiles.size() + " tiles was found");
    }

    private int printPercent(int i, int candidatesCount, int prevPercent, ProgressBar progressBar) {
        int percent = ((i * 100) / candidatesCount);
        if (percent != 0 && percent != prevPercent) {
            progressBar.printProgress(i);
            return percent;
        }
        return prevPercent;
    }

    public HashSet<Tile> getTiles() {
        return tiles;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Tile tile : tiles) {
            stringBuilder.append(tile).append("\n");
        }
        return stringBuilder.toString();
    }

    public void exportToFile(@NotNull File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Argument is not a directory or does not exist");
        }
        Path filePath = Paths.get(dir.toString(), getFileName());
        File file = filePath.toFile();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileName() {
        return String.format("%d-%d-%d-%d.txt", n, m, k, System.currentTimeMillis());
    }

    public static void main(String[] args) {
        new TileGenerator(6, 7, 3).exportToFile(new File("generated_tiles"));
    }
}
