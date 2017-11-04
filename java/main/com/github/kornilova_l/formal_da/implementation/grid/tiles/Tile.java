package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Tile {
    private final boolean[][] grid;

    int getN() {
        return n;
    }

    int getM() {
        return m;
    }

    private final int n;
    private final int m;
    private final int k;

    /**
     * Create an empty tile
     *
     * @param n size
     * @param m size
     * @param k power of graph
     */
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
     * Clone and expand tile by k
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

    /**
     * @return true if grid[x][y] can be an element of an independent set
     */
    boolean canBeI(int x, int y) {
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                stringBuilder.append(grid[i][j] ? 1 : 0).append(j == m - 1 ? "" : " ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Check if this tile is valid
     * 1. Expand tile by k cells on each side
     * 2. Try to generate IS in extended tile
     * such that it will cover all uncovered cells in original tile.
     * This proves that internal tile exist but does not tell us that this particular
     * extended tile also exist.
     */
    boolean isValid() {
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
        return isTileValidRecursive(new Tile(this),
                changeCoordinatesForExpanded(canBeAddedToIS, k),
                new Coordinate(0, 0));
    }

    /**
     * @return true if it is possible to generate expanded tile
     * which cover all in uncovered cells in internal tile.
     */
    private boolean isTileValidRecursive(Tile expandedTile,
                                         Set<Coordinate> canBeAddedToIS,
                                         @Nullable Coordinate curCoordinate) {
        if (curCoordinate == null) { // if on previous step there was last coordinate
            return false;
        }
        if (isTileValidRecursive(expandedTile, canBeAddedToIS, expandedTile.getNextBorderCoordinate(curCoordinate))) {
            return true;
        }
        if (expandedTile.canBeI(curCoordinate.x, curCoordinate.y)) { // it cell can be added to the tile
            Set<Coordinate> covered = expandedTile.getCovered(curCoordinate, canBeAddedToIS);
            if (covered != null) {
                canBeAddedToIS.removeAll(covered);
                if (canBeAddedToIS.isEmpty()) { // if covers all
                    return true;
                }
                Tile newTile = new Tile(expandedTile, curCoordinate.x, curCoordinate.y);
                boolean res = isTileValidRecursive(newTile, canBeAddedToIS, newTile.getNextBorderCoordinate(curCoordinate));
                canBeAddedToIS.addAll(covered);
                return res;
            }
        }
        return false;
    }

    /**
     * @return next coordinate which does not belong to internal tile
     * or null if it was last tile
     */
    @Nullable
    Coordinate getNextBorderCoordinate(@NotNull Coordinate curCoordinate) {
        int x = curCoordinate.x;
        int y = curCoordinate.y;
        if (x == n - 1 && y == m - 1) { // if last coordinate
            return null;
        }
        if (x < n - 1) { // if not the last in this row
            x++;
            if (y >= k && y < m - k) {
                if (x >= k && x < n - k) { // if inside internal tile
                    x = n - k;
                }
            }
        } else {
            y++;
            x = 0;
        }
        return new Coordinate(x, y);
    }

    /**
     * Returns true if (x, y) covers at least one point in cells
     */
    @Nullable
    private Set<Coordinate> getCovered(Coordinate coordinate, Set<Coordinate> cells) {
        int x = coordinate.x;
        int y = coordinate.y;
        Set<Coordinate> covered = null;
        for (Coordinate cell : cells) {
            if (Math.abs(x - cell.x) + Math.abs(y - cell.y) <= k) {
                if (covered == null) {
                    covered = new HashSet<>();
                }
                covered.add(cell);
            }
        }
        return covered;
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
        if (obj instanceof String) {
            return Objects.equals(obj, toString());
        }
        if (!(obj instanceof Tile)) {
            return false;
        }
        Tile tile = ((Tile) obj);
        if (tile.n != n || tile.m != m) {
            throw new IllegalArgumentException("Size of tile is different");
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] != tile.grid[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    static class Coordinate {
        final int x;
        final int y;

        Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Coordinate)) {
                return false;
            }
            return x == ((Coordinate) obj).x && y == ((Coordinate) obj).y;
        }
    }
}
