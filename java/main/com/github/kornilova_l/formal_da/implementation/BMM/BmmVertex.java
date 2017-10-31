package com.github.kornilova_l.formal_da.implementation.BMM;

import com.github.kornilova_l.formal_da.simulator.vertex.Message;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

abstract class BmmVertex extends Vertex {
    private final int id;
    Vertex pair = null; // for matched vertices
    State state = State.UNMATCHED_RUNNING;
    int k = 1; // iteration number
    int deg = 0;

    BmmVertex(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    @NotNull
    @Override
    public final Map<Vertex, Message> send() {
        Map<Vertex, Message> messages = null;
        switch (k % 2) {
            case 0:
                messages = evenIterationSend();
                break;
            case 1:
                messages = oddIterationSend();
        }
        return messages == null ? new HashMap<>() : messages;
    }

    @Override
    public void receive(@Nullable Map<Vertex, Message> messages) {
        if (messages != null) {
            Map<Vertex, BmmMessage> castedMessaged = new HashMap<>();
            for (Map.Entry<Vertex, Message> entry : messages.entrySet()) {
                if (entry.getValue() instanceof BmmMessage) {
                    castedMessaged.put(entry.getKey(), ((BmmMessage) entry.getValue()));
                }
            }
            switch (k % 2) {
                case 0:
                    evenIterationReceive(castedMessaged);
                    break;
                case 1:
                    oddIterationReceive(castedMessaged);
            }
        }
        k++;
    }

    abstract void oddIterationReceive(Map<Vertex, BmmMessage> messages);

    abstract void evenIterationReceive(Map<Vertex, BmmMessage> messages);

    @Override
    public boolean isStopped() {
        return state == State.UNMATCHED_STOPPED || state == State.MATCHED_STOPPED;
    }

    abstract Map<Vertex, Message> oddIterationSend();

    abstract Map<Vertex, Message> evenIterationSend();

    @Nullable
    Vertex getPair() {
        return pair;
    }

    public enum State {
        UNMATCHED_RUNNING,
        MATCHED_RUNNING,
        UNMATCHED_STOPPED,
        MATCHED_STOPPED
    }
}