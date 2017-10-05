package com.github.kornilova_l.formal_da.algorithm;

import com.github.kornilova_l.formal_da.vertex.Input;
import com.github.kornilova_l.formal_da.vertex.Message;
import com.github.kornilova_l.formal_da.vertex.Vertex;

import java.util.*;

public abstract class AlgorithmRunner {
    private Map<Vertex, List<Vertex>> graph;

    public AlgorithmRunner(Map<Vertex, List<Vertex>> vertices) {
        this.graph = vertices;
    }

    public final void initVertices() {
        for (Vertex vertex : graph.keySet()) {
            vertex.init(getInput(vertex));
        }
    }

    abstract Input getInput(Vertex vertex);

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
        for (Map.Entry<Vertex, List<Vertex>> vertexAndNeighbours : graph.entrySet()) {
            Vertex sender = vertexAndNeighbours.getKey();
            List<Vertex> receivers = vertexAndNeighbours.getValue();
            List<Message> newMessages = sender.send(receivers);
            for (int i = 0; i < newMessages.size(); i++) {
                Vertex receiver = receivers.get(i);
                Message message = newMessages.get(i);
                Map<Integer, Message> receiverMessages = incomingMessages.computeIfAbsent(
                        receiver,
                        k -> new TreeMap<>()
                );
                receiverMessages.put(getPortNumber(receiver, sender), message);
            }
        }
        return incomingMessages;
    }

    private Integer getPortNumber(Vertex receiver, Vertex sender) {
        int i = 0;
        for (Vertex neighbour : graph.get(receiver)) {
            if (neighbour == sender) {
                return i;
            }
            i++;
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
}
