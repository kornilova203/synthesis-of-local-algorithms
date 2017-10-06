package com.github.kornilova_l.formal_da.implementation.BMM;

import com.github.kornilova_l.formal_da.simulator.vertex.Input;
import com.github.kornilova_l.formal_da.simulator.vertex.Message;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static com.github.kornilova_l.formal_da.implementation.BMM.BmmMessage.BmmMessageContent.ACCEPT;
import static com.github.kornilova_l.formal_da.implementation.BMM.BmmVertex.State.*;

public class BmmBlackVertex extends BmmVertex {
    private final TreeSet<Integer> unmatchedNeighbours = new TreeSet<>(); // X
    private final TreeSet<Integer> offeringProposal = new TreeSet<>(); // M

    BmmBlackVertex(int id) {
        super(id);
    }

    /**
     * Add all neighbours to set of unmatched neighbours
     */
    @Override
    public void init(@Nullable Input input) {
        unmatchedNeighbours.addAll(connections.keySet());
        deg = connections.size();
    }

    @Override
    void oddIterationReceive(Map<Vertex, BmmMessage> messages) {
        if (state == UNMATCHED_RUNNING) {
            for (Map.Entry<Vertex, BmmMessage> entry : messages.entrySet()) {
                switch (entry.getValue().getMessageContent()) {
                    case MATCHED:
                        unmatchedNeighbours.remove(getPort(entry.getKey()));
                        break;
                    case PROPOSAL:
                        offeringProposal.add(getPort(entry.getKey()));
                }
            }
        }
    }

    @NotNull
    private Integer getPort(Vertex vertex) {
        for (Map.Entry<Integer, Vertex> entry : connections.entrySet()) {
            if (entry.getValue() == vertex) {
                return entry.getKey();
            }
        }
        throw new AssertionError("Cannot find vertex");
    }

    @Override
    void evenIterationReceive(Map<Vertex, BmmMessage> messages) {

    }

    @Nullable
    @Override
    Map<Vertex, Message> oddIterationSend() {
        return null;
    }

    @Nullable
    @Override
    Map<Vertex, Message> evenIterationSend() {
        Map<Vertex, Message> messages = new HashMap<>();
        if (state == UNMATCHED_RUNNING) {
            if (offeringProposal.size() != 0) {
                Vertex pair = connections.get(offeringProposal.first());
                messages.put(pair, new BmmMessage(ACCEPT));
                state = MATCHED_STOPPED;
                this.pair = pair;
            } else if (unmatchedNeighbours.size() == 0) {
                state = UNMATCHED_STOPPED;
            }
        }
        return messages;
    }
}
