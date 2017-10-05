package com.github.kornilova_l.formal_da.vertex;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Vertex {
    private final Set<Integer> portNumbers;

    protected Vertex(Set<Integer> portNumbers) {
        this.portNumbers = portNumbers;
    }

    public abstract void init(Input input);

    /**
     * @return map where key is a port number
     * and value is a message
     */
    public abstract Map<Integer, Message> send();

    public abstract void receive(List<Message> messages);

    public abstract boolean isStopped();

    /**
     * This method will be called after each send() method
     */
    public void validateOutgoingMessages(Map<Integer, Message> messages) {
        for (Integer port : messages.keySet()) {
            if (!portNumbers.contains(port)) {
                throw new AssertionError("Vertex tried to send message through non-existing port");
            }
        }
    }
}
