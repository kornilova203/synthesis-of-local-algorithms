package com.github.kornilova_l.formal_da.algorithm;

import com.github.kornilova_l.formal_da.vertex.Input;
import com.github.kornilova_l.formal_da.vertex.Message;
import com.github.kornilova_l.formal_da.vertex.Vertex;

import java.util.*;

public abstract class AlgorithmRunner {
    /**
     * maps vertex id to vertex object
     */
    private Map<Integer, Vertex> vertices;

    public AlgorithmRunner(Map<Integer, Vertex> vertices) {
        this.vertices = vertices;
    }

    public final void initVertices() {
        for (Vertex vertex : vertices.values()) {
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
        for (Vertex sender : vertices.values()) {
            Map<Vertex, Message> newMessages = sender.send();

            for (Map.Entry<Vertex, Message> newMessage : newMessages.entrySet()) {
                Vertex receiver = newMessage.getKey();
                Map<Integer, Message> receiverMessages = incomingMessages.computeIfAbsent(
                        receiver,
                        k -> new TreeMap<>()
                );
                receiverMessages.put(getPortNumber(receiver, sender), newMessage.getValue());
            }
        }
        return incomingMessages;
    }

    private Integer getPortNumber(Vertex sender, Vertex receiver) {
        for (Map.Entry<Integer, Vertex> connection : sender.getConnections().entrySet()) {
            if (connection.getValue() == receiver) {
                return connection.getKey();
            }
        }
        throw new AssertionError("Cannot find neighbour which must exist");
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
}
