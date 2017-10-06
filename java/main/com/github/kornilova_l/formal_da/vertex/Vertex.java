package com.github.kornilova_l.formal_da.vertex;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Vertex {
    private final TreeMap<Integer, Vertex> connections = new TreeMap<>();

    public abstract void init(Input input);

    /**
     * @return map where key is a port number
     * and value is a message
     */
    public abstract Map<Vertex, Message> send();

    public abstract void receive(List<Message> messages);

    public abstract boolean isStopped();

    public TreeMap<Integer, Vertex> getConnections() {
        return connections;
    }

    public void addReceiver(Vertex vertex, int port) {
        connections.put(port, vertex);
    }
}
