package com.github.kornilova_l.formal_da.implementation.BMM;

import com.github.kornilova_l.formal_da.simulator.vertex.Input;
import com.github.kornilova_l.formal_da.simulator.vertex.Message;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.github.kornilova_l.formal_da.implementation.BMM.BmmMessage.BmmMessageContent.*;
import static com.github.kornilova_l.formal_da.implementation.BMM.BmmVertex.State.*;

public class BmmWhiteVertex extends BmmVertex {

    BmmWhiteVertex(int id) {
        super(id);
    }

    /**
     * This method does nothing because color of
     * vertex is defined when vertex is created
     */
    @Override
    public void init(Input input) {
        deg = connections.size();
    }

    @Override
    void oddIterationReceive(Map<Vertex, BmmMessage> messages) {
    }

    @Override
    void evenIterationReceive(Map<Vertex, BmmMessage> messages) {
        if (state == UNMATCHED_RUNNING) {
            for (Map.Entry<Vertex, BmmMessage> entry : messages.entrySet()) {
                if (entry.getValue().getMessageContent() == ACCEPT) {
                    state = MATCHED_RUNNING;
                    pair = entry.getKey();
                }
            }
        }
    }

    @Nullable
    @Override
    Map<Vertex, Message> oddIterationSend() {
        Map<Vertex, Message> messages = new HashMap<>();
        if (state == UNMATCHED_RUNNING) {
            if ((k + 1) / 2 <= deg) {
                // Send ‘proposal’ to port k
                Vertex receiver = connections.get((k + 1) / 2);
                assert receiver != null;
                messages.put(receiver, new BmmMessage(PROPOSAL));
            } else {
                state = UNMATCHED_STOPPED;
            }
        } else if (state == MATCHED_RUNNING) {
            // Send ‘matched’ to all ports
            for (Vertex receiver : connections.values()) {
                messages.put(receiver, new BmmMessage(MATCHED));
            }
            state = MATCHED_STOPPED;
        }

        return messages;
    }

    @Nullable
    @Override
    Map<Vertex, Message> evenIterationSend() {
        return null;
    }
}
