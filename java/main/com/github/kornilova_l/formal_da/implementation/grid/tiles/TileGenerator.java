package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import com.github.kornilova_l.util.ProgressBar;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Generates all possible combinations of tiles n x m in kth power of grid
 */
@SuppressWarnings("WeakerAccess")
public class TileGenerator {
    private int n;
    private int m;
    private int k;

    private final HashSet<Tile> tiles = new HashSet<>();

    TileGenerator(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;

        HashSet<Tile> candidateTiles = new HashSet<>();

        candidateTiles.add(new Tile(n, m, k));

        // TODO: make this recursive
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                HashSet<Tile> newTiles = new HashSet<>();
                for (Tile tile : candidateTiles) {
                    if (tile.canBeI(i, j)) {
                        newTiles.add(new Tile(tile, i, j));
                    }
                }
                candidateTiles.addAll(newTiles);
                newTiles.clear();
            }
        }
        printCandidatesFound(candidateTiles);
        try {
            removeNotMaximal(candidateTiles);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static Set<Tile> importFromFile(@NotNull File file) {
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File does not exist or it is not a file");
        }

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void printCandidatesFound(HashSet<Tile> candidateTiles) {
        int candidatesCount = candidateTiles.size();
        System.out.println("Found " + candidatesCount + " possible tile" + (candidatesCount == 1 ? "" : "s"));
        System.out.println("Remove tiles which cannot contain maximal independent set...");
    }

    /**
     * Remove all tiles which does not have maximal IS
     */
    private void removeNotMaximal(HashSet<Tile> candidateTiles) throws InterruptedException {
        ProgressBar progressBar = new ProgressBar(candidateTiles.size());
        int processorsCount = Runtime.getRuntime().availableProcessors();
        int partitionsCount = processorsCount * 8;
        ExecutorService executorService = Executors.newFixedThreadPool(processorsCount);
        Set<TileValidator> tileValidators = new HashSet<>();
        for (List<Tile> partition : Iterables.partition(candidateTiles, partitionsCount)) {
            TileValidator validator = new TileValidator(partition, progressBar);
            tileValidators.add(validator);
            executorService.submit(validator);
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(24L, TimeUnit.HOURS)) {
            System.out.println("Not yet. Still waiting for termination");
        }
        for (TileValidator tileValidator : tileValidators) {
            tiles.addAll(tileValidator.getValidTiles());
        }
        progressBar.finish();
    }

    public HashSet<Tile> getTiles() {
        return tiles;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(n).append(" ").append(m).append(" ").append(k).append("\n")
                .append(tiles.size()).append("\n");
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
        new TileGenerator(5, 8, 3).exportToFile(new File("generated_tiles"));
    }

    private class TileValidator implements Runnable {
        private final HashSet<Tile> valid = new HashSet<>();
        private final List<Tile> candidateTiles;
        private ProgressBar progressBar;

        TileValidator(List<Tile> candidateTiles, ProgressBar progressBar) {
            this.candidateTiles = candidateTiles;
            this.progressBar = progressBar;
        }

        public HashSet<Tile> getValidTiles() {
            return valid;
        }

        @Override
        public void run() {
            synchronized (this) {
                for (Tile tile : candidateTiles) {
                    if (tile.isTileValid()) {
                        valid.add(tile);
                    }
                    progressBar.updateProgress(1);
                }
            }
        }
    }
}
