package com.github.kornilova_l.formal_da.simulator;

import com.github.kornilova_l.formal_da.simulator.vertex.Input;
import com.github.kornilova_l.formal_da.simulator.vertex.Message;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class AlgorithmRunner {
    /**
     * maps vertex id to vertex object
     */
    protected Map<Integer, Vertex> vertices;

    public AlgorithmRunner(Map<Integer, Vertex> vertices) {
        this.vertices = vertices;
    }

    public void initVertices() {
        for (Vertex vertex : vertices.values()) {
            vertex.init(getInput(vertex));
        }
    }

    @Nullable
    protected Input getInput(Vertex vertex) {
        return null;
    }

    public final void doIteration() {
        Map<Vertex, Map<Vertex, Message>> incomingMessages = sendMessages();
        receiveMessages(incomingMessages);
    }

    private void receiveMessages(Map<Vertex, Map<Vertex, Message>> incomingMessages) {
        for (Vertex vertex : vertices.values()) {
            vertex.receive(incomingMessages.getOrDefault(vertex, null));
        }
    }

    /**
     * Call send method of all vertexes
     *
     * @return map where key is to whom messages are addressed
     * and value is map from sender to it's message
     */
    private Map<Vertex, Map<Vertex, Message>> sendMessages() {
        Map<Vertex, Map<Vertex, Message>> incomingMessages = new HashMap<>();
        // send messages
        for (Vertex sender : vertices.values()) {
            Map<Vertex, Message> newMessages = sender.send();

            for (Map.Entry<Vertex, Message> newMessage : newMessages.entrySet()) {
                Vertex receiver = newMessage.getKey();
                Map<Vertex, Message> receiverMessages = incomingMessages.computeIfAbsent(
                        receiver,
                        k -> new HashMap<>()
                );
                receiverMessages.put(sender, newMessage.getValue());
            }
        }
        return incomingMessages;
    }

    public final boolean areAllNodesStopped() {
        for (Vertex vertex : vertices.values()) {
            if (!vertex.isStopped()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method will be called after all nodes stopped
     */
    public abstract void outputResult();

    protected static void readConnections(Map<Integer, Vertex> vertices, int connectionsCount, Scanner scanner) {
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
    }

    /**
     * Runs algorithm until all nodes stopped
     */
    public void run() {
        initVertices();
        while (!areAllNodesStopped()) {
            doIteration();
        }
    }

    @Nullable
    protected Integer getId(@Nullable Vertex vertex) {
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            if (entry.getValue() == vertex) {
                return entry.getKey();
            }
        }
        return null;
    }
}
