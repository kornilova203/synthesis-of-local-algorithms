package com.github.kornilova_l.formal_da.implementation.grid;

import com.github.kornilova_l.formal_da.simulator.vertex.Message;

import java.util.HashSet;
import java.util.Set;

class ColourMessagesSet extends Message {
    private Set<ColourReductionMessage> messages = new HashSet<>();

    ColourMessagesSet(int currentColour, GridVertex gridVertex) {
        messages.add(new ColourReductionMessage(currentColour, gridVertex));
    }

    ColourMessagesSet() {
    }

    void addMessage(ColourReductionMessage message) {
        messages.add(message);
    }

    Set<ColourReductionMessage> getMessages() {
        return messages;
    }
}
