package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public final class Tile implements Comparable<Tile> {
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

    /**
     * Clone tile and change grid[x][y]
     */
    Tile(Tile tile, int x, int y) {
        m = tile.m;
        n = tile.n;
        k = tile.k;
        grid = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            System.arraycopy(tile.grid[i], 0, grid[i], 0, m);
        }
        grid[x][y] = true;
    }

    /**
     * Clone and expand tile
     */
    Tile(Tile tile) {
        k = tile.k;
        n = tile.n + k * 2;
        m = tile.m + k * 2;
        grid = new boolean[n][m];
        for (int i = k; i < n - k; i++) {
            System.arraycopy(tile.grid[i - k], 0, grid[i], k, tile.m);
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
        int endX = Math.min(n - 1, x + k);
        int endY = Math.min(m - 1, y + k);
        for (int i = Math.max(0, x - k); i <= endX; i++) {
            for (int j = Math.max(0, y - k); j <= endY; j++) {
                if (Math.abs(i - x) + Math.abs(j - y) <= k) {
                    if (grid[i][j]) {
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                stringBuilder.append(grid[i][j] ? 1 : 0).append(" ");
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    /**
     * Find all cells which can be in I
     * Check is it possible to cover all this cells
     * with I set outside the tile
     */
    boolean isMaximal() {
        Set<Coordinate> canBeAddedToIS = new HashSet<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!grid[i][j] && canBeI(i, j)) {
                    canBeAddedToIS.add(new Coordinate(i, j));
                }
            }
        }
        if (canBeAddedToIS.isEmpty()) {
            return true;
        }
        return isPossibleToCoverAll(canBeAddedToIS);
    }

    private boolean isPossibleToCoverAll(Set<Coordinate> canBeAddedToIS) {
        canBeAddedToIS = changeCoordinatesForExpanded(canBeAddedToIS, k);
        TreeSet<Tile> expandedTiles = new TreeSet<>();
        expandedTiles.add(new Tile(this));
        int eN = n + 2 * k;
        int eM = m + 2 * k;
        for (int i = 0; i < eN; i++) {
            for (int j = 0; j < eM; j++) {
                if (i >= k && i < eN - k && j >= k && j < eM - k) {
                    continue;
                }
                TreeSet<Tile> newExpandedTiles = new TreeSet<>();
                for (Tile expandedTile : expandedTiles) {
                    if (expandedTile.canBeI(i, j) && expandedTile.doesCover(i, j, canBeAddedToIS)) {
                        Tile newTile = new Tile(expandedTile, i, j);
                        if (newTile.coversAll(canBeAddedToIS)) { // if we can generate expanded tile which covers all
                            return true;
                        }
                        newExpandedTiles.add(newTile);
                    }
                }
                expandedTiles.addAll(newExpandedTiles);
                newExpandedTiles.clear();
            }
        }
        return false;
    }

    /**
     * Returns true if (x, y) covers at least one point in points
     */
    private boolean doesCover(int x, int y, Set<Coordinate> points) {
        for (Coordinate point : points) {
            if (Math.abs(x - point.x) + Math.abs(y - point.y) <= k) {
                return true;
            }
        }
        return false;
    }

    private Set<Coordinate> changeCoordinatesForExpanded(Set<Coordinate> canBeAddedToIS, int k) {
        Set<Coordinate> res = new HashSet<>();
        for (Coordinate coordinate : canBeAddedToIS) {
            res.add(new Coordinate(coordinate.x + k, coordinate.y + k));
        }
        return res;
    }

    /**
     * For tests
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Tile && compareTo((Tile) obj) == 0;
    }

    private boolean coversAll(Set<Coordinate> canBeAddedToIS) {
        for (Coordinate point : canBeAddedToIS) {
            if (canBeI(point.x, point.y)) {
                return false;
            }
        }
        return true;
    }

    private class Coordinate {
        final int x;
        final int y;

        Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return x + " " + y;
        }
    }
}
