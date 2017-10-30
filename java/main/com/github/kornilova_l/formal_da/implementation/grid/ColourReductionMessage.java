package com.github.kornilova_l.formal_da.implementation.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ColourReductionMessage {
    private final int colour;
    /**
     * Path of message. Maximum size of list is a power of the graph
     */
    private final List<GridVertex> path;

    ColourReductionMessage(int colour, GridVertex gridVertex) {
        this.colour = colour;
        path = new ArrayList<>();
        path.add(gridVertex);
    }

    ColourReductionMessage(ColourReductionMessage colourReductionMessage, GridVertex gridVertex) {
        colour = colourReductionMessage.colour;
        path = new ArrayList<>(colourReductionMessage.getPath());
        path.add(gridVertex);
    }

    int getColour() {
        return colour;
    }

    List<GridVertex> getPath() {
        return path;
    }

    @Override
    public String toString() {
        return colour + " (" + path.stream()
                .map(vertex -> Integer.toString(vertex.getId()))
                .collect(Collectors.joining(", ")) + ")";
    }
}
