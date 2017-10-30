package com.github.kornilova_l.formal_da.implementation.grid;

import com.github.kornilova_l.formal_da.simulator.vertex.Input;
import com.github.kornilova_l.formal_da.simulator.vertex.Message;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GridVertex extends Vertex {
    private int id;
    private int currentColour;
    private int k;
    /* each iteration of colour reduction takes k steps */
    private int time = 0;
    private final Set<ColourReductionMessage> colourMessages = new HashSet<>();
    private final Set<ColourReductionMessage> newColourMessages = new HashSet<>();

    @Override
    public void init(@Nullable Input input) {
        if (input instanceof GridInput) {
            this.id = ((GridInput) input).getId();
            this.currentColour = id;
            this.k = ((GridInput) input).getK();
        } else {
            throw new IllegalArgumentException("Input must be a GridInput instance");
        }
    }

    @Override
    public @NotNull Map<Vertex, Message> send() {
        Map<Vertex, Message> messages = new HashMap<>();
        if (time % k == 0) { // if first iteration of colour reduction
            colourMessages.clear();
            for (Vertex vertex : connections.values()) {
                messages.put(vertex, new ColourMessagesSet(currentColour, this));
            }
        } else {
            for (ColourReductionMessage colourMessage : newColourMessages) {
                List<GridVertex> path = colourMessage.getPath();
                if (path.size() == k) {
                    continue;
                }
                for (Vertex vertex : connections.values()) {
                    if (path.get(path.size() - 1) != vertex) { // if it was not received from this colour
                        Message messagesSet = messages.computeIfAbsent(vertex, v -> new ColourMessagesSet());
                        if (messagesSet instanceof ColourMessagesSet) {
                            ((ColourMessagesSet) messagesSet).addMessage(
                                    new ColourReductionMessage(colourMessage, this)
                            );
                        }
                    }
                }
            }
            newColourMessages.clear();
        }
        return messages;
    }

    @Override
    public void receive(@NotNull Map<Vertex, Message> messages) {
        for (Map.Entry<Vertex, Message> entry : messages.entrySet()) {
            Message message = entry.getValue();
            if (message instanceof ColourMessagesSet) {
                for (ColourReductionMessage m : ((ColourMessagesSet) message).getMessages()) {
                    colourMessages.add(m);
                    newColourMessages.add(m);
                }
            }
        }
        time++;
        if (time % k == 0) {
            doColourReduction();
        }
    }

    private void doColourReduction() {
        if (isLocalMaxima()) {
            if (hasIndependentNeighbour()) {
                currentColour = 0;
            } else {
                currentColour = 1;
            }
        }
    }

    private boolean hasIndependentNeighbour() {
        for (ColourReductionMessage colourMessage : colourMessages) {
            if (colourMessage.getColour() == 1) {
                return true;
            }
        }
        return false;
    }

    private boolean isLocalMaxima() {
        for (ColourReductionMessage colourMessage : colourMessages) {
            if (currentColour < colourMessage.getColour()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isStopped() {
        return currentColour == 1 || currentColour == 0;
    }

    @Override
    public String toString() {
        return Integer.toString(currentColour);
//                + " " +
//                colourMessages.stream().map(ColourReductionMessage::toString).collect(Collectors.joining(" : "));
    }

    int getId() {
        return id;
    }
}
