package com.github.kornilova_l.algorithm_sinthesis.tiles;

import com.github.kornilova_l.util.ProgressBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Generates all possible combinations of tileSet n x m in kth power of grid
 */
@SuppressWarnings("WeakerAccess")
public class TileGenerator {
    @NotNull
    private final TileSet tileSet;
    private int n;
    private int m;
    private int k;

    public TileGenerator(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;

        ConcurrentLinkedQueue<Tile> candidateTiles = new ConcurrentLinkedQueue<>(); // get concurrently from here

        TileSet candidateTilesSet = new TileSet(n, m, k);
        candidateTiles.addAll(candidateTilesSet.getTiles());

        printCandidatesFound(candidateTiles.size());
        Set<Tile> validTiles = ConcurrentHashMap.newKeySet(); // put concurrently here
        try {
            removeNotMaximal(candidateTiles, validTiles);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (validTiles.isEmpty()) {
            throw new IllegalArgumentException("Cannot produce valid set of tiles");
        } else {
            this.tileSet = new TileSet(validTiles);
        }
    }

    private void printCandidatesFound(int candidatesCount) {
        System.out.println("Found " + candidatesCount + " possible tile" + (candidatesCount == 1 ? "" : "s"));
        System.out.println("Remove tiles which cannot contain maximal independent set...");
    }

    /**
     * Remove all tileSet which does not have maximal IS
     */
    private void removeNotMaximal(ConcurrentLinkedQueue<Tile> candidateTiles,
                                  Set<Tile> validTiles) throws InterruptedException {
        ProgressBar progressBar = new ProgressBar(candidateTiles.size());
        int processorsCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(processorsCount);

        for (int i = 0; i < processorsCount; i++) {
            executorService.submit(new TileValidator(candidateTiles, validTiles, progressBar));
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(24L, TimeUnit.HOURS)) {
            System.out.println("Not yet. Still waiting for termination");
        }
        progressBar.finish();
        System.out.println(validTiles.size() + " valid tiles");
    }

    @NotNull
    public TileSet getTileSet() {
        return tileSet;
    }

    @Override
    public String toString() {
        return String.valueOf(n) + " " + m + " " + k + "\n" +
                tileSet.size() + "\n" +
                tileSet.toString();
    }

    @Nullable
    public File exportToFile(@NotNull File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Argument is not a directory or does not exist");
        }
        Path filePath = Paths.get(dir.toString(), getFileName());
        File file = filePath.toFile();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(toString().getBytes());
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileName() {
        return String.format("%d-%d-%d-%d.txt", n, m, k, System.currentTimeMillis());
    }

    private class TileValidator implements Runnable {
        private ConcurrentLinkedQueue<Tile> candidateTiles;
        private Set<Tile> validTiles;
        private ProgressBar progressBar;

        TileValidator(ConcurrentLinkedQueue<Tile> candidateTiles, Set<Tile> validTiles, ProgressBar progressBar) {
            this.candidateTiles = candidateTiles;
            this.validTiles = validTiles;
            this.progressBar = progressBar;
        }

        @Override
        public void run() {
            while (!candidateTiles.isEmpty()) {
                Tile tile = candidateTiles.poll();
                if (tile != null) {
                    if (tile.isValid()) {
                        validTiles.add(tile);
                    }
                    progressBar.updateProgress(1);
                }
            }
        }
    }
}
