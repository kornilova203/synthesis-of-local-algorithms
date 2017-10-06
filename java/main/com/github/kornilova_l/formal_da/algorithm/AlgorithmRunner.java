package com.github.kornilova_l.formal_da.algorithm;

import com.github.kornilova_l.formal_da.vertex.Input;
import com.github.kornilova_l.formal_da.vertex.Message;
import com.github.kornilova_l.formal_da.vertex.Vertex;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class AlgorithmRunner {
    /**
     * maps vertex id to vertex object
     */
    protected Map<Integer, Vertex> vertices;

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
        Map<Vertex, Map<Vertex, Message>> incomingMessages = sendMessages();
        receiveMessages(incomingMessages);
    }

    private void receiveMessages(Map<Vertex, Map<Vertex, Message>> incomingMessages) {
        for (Map.Entry<Vertex, Map<Vertex, Message>> receiverAndMessages : incomingMessages.entrySet()) {
            Vertex receiver = receiverAndMessages.getKey();
            receiver.receive(receiverAndMessages.getValue());
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
                        k -> new TreeMap<>()
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
}
