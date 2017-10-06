package com.github.kornilova_l.formal_da.simulator.vertex;

import java.util.Map;
import java.util.TreeMap;

public abstract class Vertex {
    protected final TreeMap<Integer, Vertex> connections = new TreeMap<>();

    public abstract void init(Input input);

    /**
     * @return map where key is a port number
     * and value is a message
     */
    public abstract Map<Vertex, Message> send();

    public abstract void receive(Map<Vertex, Message> messages);

    public abstract boolean isStopped();

    public TreeMap<Integer, Vertex> getConnections() {
        return connections;
    }

    public void addReceiver(Vertex vertex, int port) {
        connections.put(port, vertex);
    }
}
