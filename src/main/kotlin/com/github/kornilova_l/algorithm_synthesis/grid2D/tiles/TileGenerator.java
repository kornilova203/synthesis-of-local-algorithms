package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles;

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

        ConcurrentLinkedQueue<Tile> candidateTileIS = new ConcurrentLinkedQueue<>(); // get concurrently from here

        TileSet candidateTilesSet = new TileSet(n, m, k);
        candidateTileIS.addAll(candidateTilesSet.getPossiblyValidTiles());

        printCandidatesFound(candidateTileIS.size());
        Set<Tile> validTileIS = ConcurrentHashMap.newKeySet(); // put concurrently here
        try {
            removeNotMaximal(candidateTileIS, validTileIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (validTileIS.isEmpty()) {
            throw new IllegalArgumentException("Cannot produce valid set of tiles");
        } else {
            this.tileSet = new TileSet(validTileIS);
        }
    }

    private void printCandidatesFound(int candidatesCount) {
        System.out.println("Found " + candidatesCount + " possible tile" + (candidatesCount == 1 ? "" : "s"));
        System.out.println("Remove tiles which cannot contain maximal independent set...");
    }

    /**
     * Remove all tileSet which does not have maximal IS
     */
    private void removeNotMaximal(ConcurrentLinkedQueue<Tile> candidateTileIS,
                                  Set<Tile> validTileIS) throws InterruptedException {
        ProgressBar progressBar = new ProgressBar(candidateTileIS.size());
        int processorsCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(processorsCount);

        for (int i = 0; i < processorsCount; i++) {
            executorService.submit(new TileValidator(candidateTileIS, validTileIS, progressBar));
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(24L, TimeUnit.HOURS)) {
            System.out.println("Not yet. Still waiting for termination");
        }
        progressBar.finish();
        System.out.println(validTileIS.size() + " valid tiles");
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
    public File exportToFile(@NotNull File dir, Boolean addTimestampToFileName) {
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Argument is not a directory or does not exist");
        }
        Path filePath = Paths.get(dir.toString(), getFileName(addTimestampToFileName));
        File file = filePath.toFile();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(toString().getBytes());
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileName(Boolean addTimestamp) {
        if (addTimestamp) {
            return String.format("%d-%d-%d-%d.txt", n, m, k, System.currentTimeMillis());
        }
        return String.format("%d-%d-%d.txt", n, m, k);
    }

    private class TileValidator implements Runnable {
        private ConcurrentLinkedQueue<Tile> candidateTileIS;
        private Set<Tile> validTileIS;
        private ProgressBar progressBar;

        TileValidator(ConcurrentLinkedQueue<Tile> candidateTileIS, Set<Tile> validTileIS, ProgressBar progressBar) {
            this.candidateTileIS = candidateTileIS;
            this.validTileIS = validTileIS;
            this.progressBar = progressBar;
        }

        @Override
        public void run() {
            while (!candidateTileIS.isEmpty()) {
                Tile tile = candidateTileIS.poll();
                if (tile != null) {
                    if (tile.isValid()) {
                        validTileIS.add(tile);
                    }
                    progressBar.updateProgress(1);
                }
            }
        }
    }
}
