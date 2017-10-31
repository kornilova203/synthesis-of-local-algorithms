package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import org.jetbrains.annotations.NotNull;

public class Tile implements Comparable<Tile> {
    private final boolean[][] grid;
    private final int n;
    private final int m;
    private final int k;

    Tile(int n, int m, int k) {
        grid = new boolean[n][m];
        this.n = n;
        this.m = m;
        this.k = k;
    }

    Tile(Tile tile) {
        m = tile.m;
        n = tile.n;
        k = tile.k;
        grid = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            System.arraycopy(tile.grid[i], 0, grid[i], 0, m);
        }
    }

    public boolean[][] getGrid() {
        return grid;
    }

    /**
     * @return true if grid[x][y] can be an element of an independent set
     */
    public boolean canBeI(int x, int y) {
        validate(x, y);
        if (grid[x][y]) { // if already I
            return true;
        }
        for (int i = x - k; i <= x + k; i++) {
            for (int j = y - k; j <= y + k; j++) {
                int currentX = (i + n) % n;
                int currentY = (j + m) % m;
                if (Math.abs(i - x) + Math.abs(j - y) <= k) {
                    if (grid[currentX][currentY]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void validate(int x, int y) {
        if (x < 0 || x >= n ||
                y < 0 || y >= m) {
            throw new IllegalArgumentException("Coordinates out of range");
        }
    }

    @Override
    public int compareTo(@NotNull Tile tile) {
        if (n != tile.n || m != tile.m) {
            throw new IllegalArgumentException("Grid size is different");
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] != tile.grid[i][j]) {
                    return i == 0 ? -1 : 1;
                }
            }
        }
        return 0;
    }
}
