package com.github.kornilova_l.formal_da.implementation.grid;

import com.github.kornilova_l.formal_da.simulator.AlgorithmRunner;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;

import java.util.Map;
import java.util.TreeMap;

public class GridAlgorithmRunner extends AlgorithmRunner {
    /**
     * Save original stricture for {@link #toString()} method
     */
    private final Vertex[][] verticesArr;
    private final int n;
    private final int m;


    private GridAlgorithmRunner(Map<Integer, Vertex> vertices,
                                Vertex[][] verticesArr,
                                int n, int m) {
        super(vertices);
        this.verticesArr = verticesArr;
        this.n = n;
        this.m = m;
    }

    public static GridAlgorithmRunner createGridAR(int n, int m) {
        Vertex[][] verticesArr = new Vertex[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                verticesArr[i][j] = new GridVertex();
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                verticesArr[i][j].addReceiver(verticesArr[(i + 1) % n][j], 1);
                verticesArr[i][j].addReceiver(verticesArr[i][(j + 1) % m], 2);
            }
        }
        Map<Integer, Vertex> vertices = new TreeMap<>();
        int id = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                vertices.put(id++, verticesArr[i][j]);
            }
        }
        return new GridAlgorithmRunner(vertices, verticesArr, n, m);
    }

    @Override
    public void outputResult() {

    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                stringBuilder.append(verticesArr[i][j]).append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
