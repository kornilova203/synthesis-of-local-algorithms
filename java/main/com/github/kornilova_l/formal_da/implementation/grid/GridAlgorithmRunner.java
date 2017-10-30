package com.github.kornilova_l.formal_da.implementation.grid;

import com.github.kornilova_l.formal_da.simulator.AlgorithmRunner;
import com.github.kornilova_l.formal_da.simulator.vertex.Input;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GridAlgorithmRunner extends AlgorithmRunner {
    /**
     * Save original stricture for {@link #toString()} method
     */
    private final Vertex[][] verticesArr;
    private final Map<Vertex, Integer> ids; // for getInput method. Because this sim is for PN model
    private final int n;
    private final int m;
    /**
     * power of the grid
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int k = 3;


    private GridAlgorithmRunner(Map<Integer, Vertex> vertices,
                                Vertex[][] verticesArr,
                                int n, int m, Map<Vertex, Integer> ids) {
        super(vertices);
        this.verticesArr = verticesArr;
        this.n = n;
        this.m = m;
        this.ids = ids;
    }

    public static GridAlgorithmRunner createRunner(int n, int m) {
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
                verticesArr[i][j].addReceiver(verticesArr[(i - 1 + n) % n][j], 3);
                verticesArr[i][j].addReceiver(verticesArr[i][(j - 1 + m) % m], 4);
            }
        }
        Map<Integer, Vertex> vertices = new TreeMap<>();
        Map<Vertex, Integer> ids = new HashMap<>(); // for getInput method. Because this sim is for PN model
        List<Integer> allowedIds = getAllowedIds(n * m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int id = allowedIds.remove(0);
                ids.put(verticesArr[i][j], id);
                vertices.put(id, verticesArr[i][j]);
            }
        }
        return new GridAlgorithmRunner(vertices, verticesArr, n, m, ids);
    }

    /**
     * Generate list of n * m unique ids and shuffle
     */
    private static List<Integer> getAllowedIds(int nodesCount) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < nodesCount; i++) {
            ids.add(i);
        }
        Collections.shuffle(ids);
        return ids;
    }

    @Override
    protected @Nullable Input getInput(Vertex vertex) {
        Integer id = ids.get(vertex);
        assert id != null;
        return new GridInput(id, k);
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
