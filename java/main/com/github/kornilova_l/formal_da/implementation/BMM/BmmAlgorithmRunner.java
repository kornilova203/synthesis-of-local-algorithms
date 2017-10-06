package com.github.kornilova_l.formal_da.implementation.BMM;

import com.github.kornilova_l.formal_da.simulator.AlgorithmRunner;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;
import javafx.util.Pair;

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
     *             3 3 // number of white and black vertices
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
                vertices.put(id, new BmmWhiteVertex(id));
            }
            for (int i = 0; i < blackCount; i++) {
                int id = scanner.nextInt();
                vertices.put(id, new BmmBlackVertex(id));
            }

            // read connections:
            int connectionsCount = scanner.nextInt();
            readConnections(vertices, connectionsCount, scanner);
            return new BmmAlgorithmRunner(vertices);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Cannot open file");
        }
    }

    public static BmmAlgorithmRunner createRunner(Set<Integer> whiteVertices,
                                                  Set<Integer> blackVertices,
                                                  Set<Connection> connections) {
        Map<Integer, Vertex> vertices = new HashMap<>();
        for (Integer whiteVertex : whiteVertices) {
            vertices.put(whiteVertex, new BmmWhiteVertex(whiteVertex));
        }
        for (Integer blackVertex : blackVertices) {
            vertices.put(blackVertex, new BmmBlackVertex(blackVertex));
        }
        for (Connection connection : connections) {
            Vertex vertex1 = vertices.get(connection.vertex1);
            Vertex vertex2 = vertices.get(connection.vertex2);
            vertex1.addReceiver(vertex2, connection.port1);
            vertex2.addReceiver(vertex1, connection.port2);
        }
        return new BmmAlgorithmRunner(vertices);
    }

    @Override
    public void outputResult() {
        System.out.println("Maximal matching:");
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
                System.out.println(vertex + " - " + pair);
            }
        }
    }

    public Set<Pair<Integer, Integer>> getMatching() {
        Set<Pair<Integer, Integer>> pairs = new HashSet<>();
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            boolean contains = false;
            for (Pair<Integer, Integer> pair : pairs) {
                if (Objects.equals(pair.getKey(), entry.getKey()) ||
                        Objects.equals(pair.getValue(), entry.getKey())) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                continue;
            }
            if (!(entry.getValue() instanceof BmmVertex)) {
                continue;
            }
            Vertex pair = ((BmmVertex) entry.getValue()).getPair();
            if (pair != null) {
                pairs.add(new Pair<>(entry.getKey(), getId(pair)));
            }
        }
        return pairs;

    }

    public static final class Connection {
        private final int port1;
        private final int port2;
        private final int vertex1;
        private final int vertex2;

        public Connection(int vertex1, int vertex2, int port1, int port2) {
            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
            this.port1 = port1;
            this.port2 = port2;
        }

        public int getPort1() {
            return port1;
        }

        public int getPort2() {
            return port2;
        }

        public int getVertex1() {
            return vertex1;
        }

        public int getVertex2() {
            return vertex2;
        }
    }
}
