package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Tile {
    private final boolean[][] grid;
    private final int k;

    Tile(int n, int m, int k, Set<Coordinate> is) {
        this.k = k;
        grid = new boolean[n][m];
        for (Coordinate coordinate : is) {
            grid[coordinate.x][coordinate.y] = true;
        }
    }

    public Tile(Tile tile, Part part) {
        k = tile.k;
        int n, m;
        switch (part) {
            case BOTTOM:
            case TOP:
                n = tile.getN() - 1;
                m = tile.getM();
                break;
            case LEFT:
            case RIGHT:
                n = tile.getN();
                m = tile.getM() - 1;
                break;
            default:
                throw new IllegalArgumentException("Not known part");
        }
        grid = new boolean[n][m];
        switch (part) {
            case TOP:
            case LEFT:
                for (int i = 0; i < n; i++) {
                    System.arraycopy(tile.grid[i], 0, grid[i], 0, m);
                }
                break;
            case BOTTOM:
                for (int i = 0; i < n; i++) {
                    System.arraycopy(tile.grid[i + 1], 0, grid[i], 0, m);
                }
                break;
            case RIGHT:
                for (int i = 0; i < n; i++) {
                    System.arraycopy(tile.grid[i], 1, grid[i], 0, m);
                }
        }
    }

    /**
     * Create an empty tile
     *
     * @param n size
     * @param m size
     * @param k power of graph
     */
    public Tile(int n, int m, int k) {
        grid = new boolean[n][m];
        this.k = k;
    }

    /**
     * Clone tile and change grid[x][y]
     */
    public Tile(Tile tile, int x, int y) {
        k = tile.k;
        grid = new boolean[tile.getN()][tile.getM()];
        for (int i = 0; i < tile.getN(); i++) {
            System.arraycopy(tile.grid[i], 0, grid[i], 0, tile.getM());
        }
        grid[x][y] = true;
    }

    /**
     * Clone and expand tile by k
     */
    public Tile(Tile tile) {
        k = tile.k;
        int n = tile.getN() + k * 2;
        int m = tile.getM() + k * 2;
        grid = new boolean[n][m];
        for (int i = k; i < n - k; i++) {
            System.arraycopy(tile.grid[i - k], 0, grid[i], k, tile.getM());
        }
    }

    public int getN() {
        return grid.length;
    }

    public int getM() {
        return grid[0].length;
    }

    /**
     * @return true if grid[x][y] can be an element of an independent set
     */
    public boolean canBeI(int x, int y) {
        if (grid[x][y]) { // if already I
            return true;
        }
        int endX = Math.min(getN() - 1, x + k);
        int endY = Math.min(getM() - 1, y + k);
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
        for (int i = 0; i < getN(); i++) {
            for (int j = 0; j < getM(); j++) {
                stringBuilder.append(grid[i][j] ? 1 : 0).append(j == getM() - 1 ? "" : " ");
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
    public boolean isValid() {
        Set<Coordinate> canBeAddedToIS = new HashSet<>();
        for (int i = 0; i < getN(); i++) {
            for (int j = 0; j < getM(); j++) {
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
    public Coordinate getNextBorderCoordinate(@NotNull Coordinate curCoordinate) {
        int x = curCoordinate.x;
        int y = curCoordinate.y;
        if (x == getN() - 1 && y == getM() - 1) { // if last coordinate
            return null;
        }
        if (x < getN() - 1) { // if not the last in this row
            x++;
            if (y >= k && y < getM() - k) {
                if (x >= k && x < getN() - k) { // if inside internal tile
                    x = getN() - k;
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
        if (tile.getN() != getN() || tile.getM() != getM()) {
            throw new IllegalArgumentException("Size of tile is different");
        }
        for (int i = 0; i < getN(); i++) {
            for (int j = 0; j < getM(); j++) {
                if (grid[i][j] != tile.grid[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }

    public int getK() {
        return k;
    }

    public enum Part {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public static class Coordinate {
        final int x;
        final int y;

        public Coordinate(int x, int y) {
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
