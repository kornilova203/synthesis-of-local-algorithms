package com.github.kornilova_l.formal_da.algorithm;

import com.github.kornilova_l.formal_da.vertex.Input;
import com.github.kornilova_l.formal_da.vertex.Message;
import com.github.kornilova_l.formal_da.vertex.Vertex;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AlgorithmRunner {
    /**
     * Connections of each vertex is sorted
     * accordingly to port id
     */
    private Map<Vertex, TreeSet<Connection>> graph;

    public AlgorithmRunner(Map<Vertex, TreeSet<Connection>> vertices) {
        this.graph = vertices;
    }

    public final void initVertices() {
        for (Vertex vertex : graph.keySet()) {
            vertex.init(getInput(vertex));
        }
    }

    protected abstract Input getInput(Vertex vertex);

    public final void doIteration() {
        Map<Vertex, TreeMap<Integer, Message>> incomingMessages = sendMessages();
        receiveMessages(incomingMessages);
    }

    private void receiveMessages(Map<Vertex, TreeMap<Integer, Message>> incomingMessages) {
        for (Map.Entry<Vertex, TreeMap<Integer, Message>> receiverAndMessages : incomingMessages.entrySet()) {
            Vertex receiver = receiverAndMessages.getKey();
            List<Message> messages = new LinkedList<>(receiverAndMessages.getValue().values());
            receiver.receive(messages);
        }
    }

    /**
     * Call send method of all vertexes
     *
     * @return map where key is to whom messages are addressed
     * and value is ordered map from id of sender to it's message
     */
    private Map<Vertex, TreeMap<Integer, Message>> sendMessages() {
        Map<Vertex, TreeMap<Integer, Message>> incomingMessages = new HashMap<>();
        // send messages
        for (Map.Entry<Vertex, TreeSet<Connection>> senderAndConnections : graph.entrySet()) {
            Vertex sender = senderAndConnections.getKey();
            TreeSet<Connection> connections = senderAndConnections.getValue();
            Map<Integer, Message> newMessages = sender.send();

            for (Map.Entry<Integer, Message> newMessage : newMessages.entrySet()) {
                Vertex receiver = getReceiver(connections, newMessage.getKey());
                Map<Integer, Message> receiverMessages = incomingMessages.computeIfAbsent(
                        receiver,
                        k -> new TreeMap<>()
                );
                receiverMessages.put(getPortNumber(receiver, sender), newMessage.getValue());
            }
        }
        return incomingMessages;
    }

    private Vertex getReceiver(TreeSet<Connection> connections, Integer portNumber) {
        for (Connection connection : connections) {
            if (connection.portId == portNumber) {
                return connection.receiver;
            }
        }
        throw new AssertionError("Cannot find port");
    }

    private Integer getPortNumber(Vertex sender, Vertex receiver) {
        for (Connection connection : graph.get(sender)) {
            if (connection.receiver == receiver) {
                return connection.portId;
            }
        }
        throw new AssertionError("Cannot find neighbour which must exist");
    }

    public final boolean areAllNodesStopped() {
        Set<Vertex> vertices = graph.keySet();
        for (Vertex vertex : vertices) {
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

    protected class Connection implements Comparable<Connection> {
        private Vertex receiver;
        private int portId;

        Connection(Vertex receiver, int portId) {
            this.receiver = receiver;
            this.portId = portId;
        }

        @Override
        public int compareTo(@NotNull Connection connection) {
            return Integer.compare(this.portId, connection.portId);
        }
    }
}
