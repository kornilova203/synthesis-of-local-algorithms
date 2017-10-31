package com.github.kornilova_l.formal_da.implementation.grid.tiles;

import java.util.TreeSet;

/**
 * Generates all possible combinations of tiles n x m in kth power of grid
 */
public class TileGenerator {
    private final TreeSet<Tile> tiles = new TreeSet<>();
    private final int n;
    private final int m;
    private final int k;

    TileGenerator(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;
        tiles.add(new Tile(n, m, k));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                TreeSet<Tile> newTiles = new TreeSet<>();
                for (Tile tile : tiles) {
                    if (tile.canBeI(i, j)) {
                        newTiles.add(new Tile(tile, i, j));
                    }
                }
                tiles.addAll(newTiles);
                newTiles.clear();
            }
        }
        removeNotMaximal();

    }

    /**
     * Remove all tiles which does not have maximal IS
     */
    private void removeNotMaximal() {
        TreeSet<Tile> notMaximalTiles = new TreeSet<>();
        int i = 0;
//        tiles.remove(new Tile(n, m, k));
//        Tile removeTile = null;
//        for (Tile tile : tiles) {
//            if (tile.equals(emptyTile)) {
//                removeTile = tile;
//                break;
//            }
//        }
        Tile emptyTile = new Tile(n, m, k);
        Tile cornerTile1 = new Tile(n, m, k);
        cornerTile1.getGrid()[0][0] = true;
        Tile cornerTile2 = new Tile(n, m, k);
        cornerTile1.getGrid()[n - 1][0] = true;
        Tile cornerTile3 = new Tile(n, m, k);
        cornerTile1.getGrid()[0][m - 1] = true;
        Tile cornerTile4 = new Tile(n, m, k);
        cornerTile1.getGrid()[n - 1][m - 1] = true;
        for (Tile tile : tiles) {
            if (tile.equals(emptyTile) || tile.equals(cornerTile1) || tile.equals(cornerTile2) ||
                    tile.equals(cornerTile3) || tile.equals(cornerTile4)) {
                continue;
            }
            System.out.println(i);
//            if (i == 2230) {
//                System.out.println(tile);
//            }
            i++;
            if (!tile.isMaximal()) {
                notMaximalTiles.add(tile);
                System.out.println("not maximal");
            }
        }
        System.out.println(":" + notMaximalTiles.size());
        tiles.removeAll(notMaximalTiles);
    }

    public TreeSet<Tile> getTiles() {
        return tiles;
    }
}
