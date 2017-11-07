package com.github.kornilova_l.algorithm_sinthesis.tiles;

import com.github.kornilova_l.algorithm_sinthesis.tiles.Tile.Coordinate;
import com.github.kornilova_l.util.ProgressBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Generates all possible combinations of validTiles n x m in kth power of grid
 */
@SuppressWarnings("WeakerAccess")
public class TileGenerator {
    private int n;
    private int m;
    private int k;

    private final Set<Tile> validTiles = ConcurrentHashMap.newKeySet();

    TileGenerator(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;

        ConcurrentLinkedQueue<Tile> candidateTiles = new ConcurrentLinkedQueue<>();

        candidateTiles.add(new Tile(n, m, k));

        // TODO: make this recursive
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Set<Tile> newTiles = new HashSet<>();
                for (Tile tile : candidateTiles) {
                    if (tile.canBeI(i, j)) {
                        newTiles.add(new Tile(tile, i, j));
                    }
                }
                candidateTiles.addAll(newTiles);
                newTiles.clear();
            }
        }
        printCandidatesFound(candidateTiles.size());
        try {
            removeNotMaximal(candidateTiles);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static HashSet<Tile> importFromFile(@NotNull File file) {
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File does not exist or it is not a file");
        }

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            int k = scanner.nextInt();
            int size = scanner.nextInt();
            HashSet<Tile> tiles = new HashSet<>();
            for (int i = 0; i < size; i++) {
                Set<Coordinate> is = new HashSet<>();
                for (int row = 0; row < n; row++) {
                    for (int column = 0; column < m; column++) {
                        if (scanner.nextInt() == 1) {
                            is.add(new Coordinate(row, column));
                        }
                    }
                }
                tiles.add(new Tile(n, m, k, is));
            }
            return tiles;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void printCandidatesFound(int candidatesCount) {
        System.out.println("Found " + candidatesCount + " possible tile" + (candidatesCount == 1 ? "" : "s"));
        System.out.println("Remove validTiles which cannot contain maximal independent set...");
    }

    /**
     * Remove all validTiles which does not have maximal IS
     */
    private void removeNotMaximal(ConcurrentLinkedQueue<Tile> candidateTiles) throws InterruptedException {
        ProgressBar progressBar = new ProgressBar(candidateTiles.size());
        int processorsCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(processorsCount);

        for (int i = 0; i < processorsCount; i++) {
            executorService.submit(new TileValidator(candidateTiles, progressBar));
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(24L, TimeUnit.HOURS)) {
            System.out.println("Not yet. Still waiting for termination");
        }
        progressBar.finish();
        System.out.println(validTiles.size() + " valid tiles");
    }

    public Set<Tile> getTiles() {
        return validTiles;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(n).append(" ").append(m).append(" ").append(k).append("\n")
                .append(validTiles.size()).append("\n");
        for (Tile tile : validTiles) {
            stringBuilder.append(tile).append("\n");
        }
        return stringBuilder.toString();
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

    public static void main(String[] args) {
        new TileGenerator(2, 2, 2).exportToFile(new File("generated_tiles"));
        System.out.println(importFromFile(new File("generated_tiles/2-2-2-1509827230493.txt")));
    }

    private class TileValidator implements Runnable {
        private ConcurrentLinkedQueue<Tile> candidateTiles;
        private ProgressBar progressBar;

        TileValidator(ConcurrentLinkedQueue<Tile> candidateTiles, ProgressBar progressBar) {
            this.candidateTiles = candidateTiles;
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
