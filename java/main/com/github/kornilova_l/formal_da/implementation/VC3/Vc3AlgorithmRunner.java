package com.github.kornilova_l.formal_da.implementation.VC3;

import com.github.kornilova_l.formal_da.implementation.BMM.BmmAlgorithmRunner;
import com.github.kornilova_l.formal_da.simulator.AlgorithmRunner;
import com.github.kornilova_l.formal_da.simulator.vertex.Input;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Vc3AlgorithmRunner extends AlgorithmRunner {
    private Set<Pair<Integer, Integer>> matching;
    private Set<BmmAlgorithmRunner.Connection> connections = new HashSet<>();

    private Vc3AlgorithmRunner(Map<Integer, Vertex> vertices,
                               Set<BmmAlgorithmRunner.Connection> connections) {
        super(vertices);
        this.connections = connections;
    }

    /**
     * @param file which contains graph structure:
     *             6 // number of vertices
     *             4 // number of connections
     *             1 2 1 1 // first vertex, second vertex, first port id, second port id
     *             2 3 2 1
     *             3 6 2 1
     *             5 6 1 2
     */
    public static Vc3AlgorithmRunner createRunner(File file) {
        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            Map<Integer, Vertex> vertices = new HashMap<>();
            scanner.nextInt(); // read number of vertices
            int connectionsCount = scanner.nextInt();
            Set<BmmAlgorithmRunner.Connection> connections = new HashSet<>();

            for (int i = 0; i < connectionsCount; i++) {
                int vertexId1 = scanner.nextInt();
                int vertexId2 = scanner.nextInt();
                Vertex vertex1 = vertices.computeIfAbsent(vertexId1, k -> new Vc3Vertex());
                Vertex vertex2 = vertices.computeIfAbsent(vertexId2, k -> new Vc3Vertex());
                int port1 = scanner.nextInt();
                int port2 = scanner.nextInt();
                connections.add(new BmmAlgorithmRunner.Connection(vertexId1, vertexId2, port1, port2));
                vertex1.addReceiver(vertex2, port1);
                vertex2.addReceiver(vertex1, port2);
            }

            return new Vc3AlgorithmRunner(vertices, connections);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Cannot open file");
        }
    }

    /**
     * Create bipartite graph from original graph
     * Perform BMM
     */
    @Override
    public void initVertices() {
        BmmAlgorithmRunner bmmAlgorithmRunner = createBmmRunner();
        bmmAlgorithmRunner.run();
        matching = bmmAlgorithmRunner.getMatching();
        super.initVertices();
    }

    private BmmAlgorithmRunner createBmmRunner() {
        Set<Integer> whiteVertices = new HashSet<>();
        Set<Integer> blackVertices = new HashSet<>();
        Set<BmmAlgorithmRunner.Connection> connections = new HashSet<>();
        int verticesCount = vertices.size();
        for (Map.Entry<Integer, Vertex> idAndVertex : vertices.entrySet()) {
            int vertex1 = idAndVertex.getKey();
            int vertex2 = verticesCount + idAndVertex.getKey();
            whiteVertices.add(vertex1);
            blackVertices.add(vertex2);
        }
        for (BmmAlgorithmRunner.Connection originalConnection : this.connections) {
            connections.add(new BmmAlgorithmRunner.Connection(
                    originalConnection.getVertex1(),
                    originalConnection.getVertex2() + verticesCount,
                    originalConnection.getPort1(),
                    originalConnection.getPort2()
            ));
            connections.add(new BmmAlgorithmRunner.Connection(
                    originalConnection.getVertex1() + verticesCount,
                    originalConnection.getVertex2(),
                    originalConnection.getPort1(),
                    originalConnection.getPort2()
            ));
        }
        return BmmAlgorithmRunner.createRunner(whiteVertices, blackVertices, connections);
    }

    /**
     * @return two vertices from bipartite graph
     */
    @Override
    protected @Nullable Input getInput(Vertex vertex) {
        return new Vc3Input(hasMatching(vertex));
    }

    private boolean hasMatching(@NotNull Vertex vertex) {
        Integer idObject = getId(vertex);
        assert idObject != null;
        int id = idObject;
        for (Pair<Integer, Integer> pair : matching) {
            if (pair.getValue() == id || pair.getKey() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void outputResult() {
        System.out.println("3-approximation of a minimum vertex cover:");
        for (Map.Entry<Integer, Vertex> vertexEntry : vertices.entrySet()) {
            if (vertexEntry.getValue() instanceof Vc3Vertex) {
                if (((Vc3Vertex) vertexEntry.getValue()).isCover()) {
                    System.out.print(vertexEntry.getKey() + " ");
                }
            }
        }
        System.out.println("");
    }
}
