package com.github.kornilova_l.formal_da.implementation.BMM;

import com.github.kornilova_l.formal_da.algorithm.AlgorithmRunner;
import com.github.kornilova_l.formal_da.vertex.Input;
import com.github.kornilova_l.formal_da.vertex.Vertex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class BmmAlgorithmRunner extends AlgorithmRunner {
    private BmmAlgorithmRunner(Map<Integer, Vertex> vertexSet) {
        super(vertexSet);
    }

    /**
     * @param file which contains graph structure:
     *             3 3 // number of white and black vertexes
     *             1 2 3 // white vertexes
     *             4 5 6 // black vertexes
     *             5 // number of connections
     *             1 4 1 1 // first vertex, second vertex, first port id, second port id
     *             1 5 2 1
     *             2 5 1 2
     *             3 5 1 3
     *             3 6 2 1
     */
    public static BmmAlgorithmRunner createRunner(File file) {
        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            Map<Integer, Vertex> vertices = new HashMap<>();
            // read vertices:
            int whiteCount = scanner.nextInt();
            int blackCount = scanner.nextInt();
            for (int i = 0; i < whiteCount; i++) {
                int id = scanner.nextInt();
                vertices.put(id, new BmmWhiteVertex());
            }
            for (int i = 0; i < blackCount; i++) {
                int id = scanner.nextInt();
                vertices.put(id, new BmmBlackVertex());
            }

            // read connections:
            int connectionsCount = scanner.nextInt();
            for (int i = 0; i < connectionsCount; i++) {
                Vertex vertex1 = vertices.get(scanner.nextInt());
                Vertex vertex2 = vertices.get(scanner.nextInt());
                if (vertex1 == null || vertex2 == null) {
                    throw new AssertionError("Cannot find vertex");
                }
                int port1 = scanner.nextInt();
                int port2 = scanner.nextInt();
                vertex1.addReceiver(vertex2, port1);
                vertex2.addReceiver(vertex1, port2);

            }
            return new BmmAlgorithmRunner(vertices);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Cannot open file");
        }
    }

    @Override
    protected Input getInput(Vertex vertex) {
        return null;
    }

    @Override
    public void outputResult() {
        System.out.println("Maximal matching");
        Set<Vertex> outputtedVertices = new HashSet<>();
        for (Vertex vertex : vertices.values()) {
            if (outputtedVertices.contains(vertex)) {
                continue;
            }
            if (!(vertex instanceof BmmVertex)) {
                continue;
            }
            Vertex pair = ((BmmVertex) vertex).getPair();
            if (pair != null) {
                outputtedVertices.add(vertex);
                outputtedVertices.add(pair);
                System.out.println(getId(vertex) + " - " + getId(pair));
            }
        }
    }

    private int getId(Vertex pair) {
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            if (entry.getValue() == pair) {
                return entry.getKey();
            }
        }
        throw new AssertionError("Cannot find vertex");
    }
}
