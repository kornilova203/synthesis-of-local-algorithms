package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Contains set of possiblyValidTiles of one size
 */
public class TileSet {
    private final int n;
    private final int m;
    private final int k;
    private final Set<Tile> possiblyValidTiles = new HashSet<>();

    /**
     * Generates set of possibly-valid possiblyValidTiles
     */
    TileSet(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;

        /* It is meaningless to make following piece of code recursive
         * because all candidate tiles must be placed in possiblyValidTiles set
         * and it is not possible to reduce memory consumption using recursive method
         */
        possiblyValidTiles.add(new Tile(n, m, k));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Set<Tile> newTileIS = new HashSet<>();
                for (Tile tile : this.possiblyValidTiles) {
                    if (tile.canBeI(i, j)) {
                        newTileIS.add(new Tile(tile, i, j));
                    }
                }
                possiblyValidTiles.addAll(newTileIS);
                newTileIS.clear();
            }
        }
    }

    public TileSet(@NotNull File file) {
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File does not exist or it is not a file");
        }
        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            n = scanner.nextInt();
            m = scanner.nextInt();
            k = scanner.nextInt();
            int size = scanner.nextInt();
            for (int i = 0; i < size; i++) {
                Set<Tile.Coordinate> is = new HashSet<>();
                for (int row = 0; row < n; row++) {
                    for (int column = 0; column < m; column++) {
                        if (scanner.nextInt() == 1) {
                            is.add(new Tile.Coordinate(row, column));
                        }
                    }
                }
                possiblyValidTiles.add(new Tile(n, m, k, is));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("File was not found");
        }
    }

    TileSet(Collection<Tile> possiblyValidTiles) {
        Tile someTile = possiblyValidTiles.iterator().next();
        n = someTile.getN();
        m = someTile.getM();
        k = someTile.getK();
        for (Tile tile : possiblyValidTiles) {
            addTile(tile); // check that all tile have same size
        }
    }

    public int size() {
        return possiblyValidTiles.size();
    }

    boolean isEmpty() {
        return possiblyValidTiles.isEmpty();
    }

    int getN() {
        return n;
    }

    int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

    /**
     * Return clone of original set so
     * it is not possible to add to set a tile of different size
     */
    public Set<Tile> getPossiblyValidTiles() {
        return new HashSet<>(possiblyValidTiles);
    }

    private void addTile(Tile tile) {
        if (tile.getK() != k || tile.getM() != m || tile.getN() != n) {
            throw new IllegalArgumentException("Tile must have the same parameters as tile set");
        }

        this.possiblyValidTiles.add(tile);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Tile tile : this.possiblyValidTiles) {
            stringBuilder.append(tile).append("\n");
        }
        return stringBuilder.toString();
    }
}
